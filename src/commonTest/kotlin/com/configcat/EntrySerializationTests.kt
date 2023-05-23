package com.configcat

import com.soywiz.klock.DateTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.decodeFromString
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class EntrySerializationTests {
    @AfterTest
    fun tearDown() {
        Services.reset()
    }

    @Test
    fun testSerialize() = runTest {
        val json: String = Data.formatJsonBody("test")
        val config: Config = Constants.json.decodeFromString(json)
        val fetchTimeNow = DateTime.now()
        val entry = Entry(config, "fakeTag", json, fetchTimeNow)

        val serializedString = entry.serialize()
        val fetchTimeNowUnixSecond = fetchTimeNow.unixMillis / 1000
        val expected = "$fetchTimeNowUnixSecond\nfakeTag\n$json"
        assertEquals(expected, serializedString)
    }

    @Test
    fun testDeserialize() = runTest {
        val json: String = Data.formatJsonBody("test")
        val dateTimeNowUnixSeconds: Double = DateTime.now().unixMillis / 1000
        val fetchTimeUnixSeconds = DateTime(dateTimeNowUnixSeconds * 1000)

        val cacheValue = "$dateTimeNowUnixSeconds\nfakeTag\n$json"

        val entry: Entry = Entry.fromString(cacheValue)
        assertNotNull(entry)
        assertEquals(fetchTimeUnixSeconds, entry.fetchTime)
        assertEquals("fakeTag", entry.eTag)
        assertEquals(json, entry.configJson)
        assertEquals(1, entry.config.settings.size)
    }

    @Test
    fun testDeserializeMissingValue() = runTest {
        val deserializeNull = Entry.fromString(null)
        assertTrue(deserializeNull.isEmpty())
        val deserializeEmpty = Entry.fromString("")
        assertTrue(deserializeEmpty.isEmpty())
    }

    @Test
    fun testDeserializeWrongFormat() = runTest {
        val exception = assertFailsWith<IllegalArgumentException> {
            Entry.fromString("value with no new line")
        }
        assertEquals("Number of values is fewer than expected.", exception.message)
        val exception2 = assertFailsWith<IllegalArgumentException> {
            Entry.fromString("value with one \n new line")
        }
        assertEquals("Number of values is fewer than expected.", exception2.message)
    }

    @Test
    fun testDeserializeInvalidDate() = runTest {
        val cacheValue = "Invalid\nfakeTag\nTestjson"

        val exception = assertFailsWith<IllegalArgumentException> {
            Entry.fromString(cacheValue)
        }

        assertEquals("Invalid fetch time: Invalid", exception.message)
    }

    @Test
    fun testDeserializeInvalidETag() = runTest {
        val fetchTimeTest = DateTime.now().unixMillis / 1000
        val cacheValue = "${fetchTimeTest}\n\nTestjson"

        val exception = assertFailsWith<IllegalArgumentException> {
            Entry.fromString(cacheValue)
        }
        assertEquals("Empty eTag value.", exception.message)


    }

    @Test
    fun testDeserializeInvalidJson() = runTest {
        val fetchTimeTest = DateTime.now().unixMillis / 1000
        val cacheValueEmptyJson = "${fetchTimeTest}\nTestETag\n"

        val exception = assertFailsWith<IllegalArgumentException> {
            Entry.fromString(cacheValueEmptyJson)
        }
        assertEquals("Empty config jsom value.", exception.message)

        val cacheValueWrongJson = "${fetchTimeTest}\nTestETag\nwrongjson"

        val exception2 = assertFailsWith<IllegalArgumentException> {
            Entry.fromString(cacheValueWrongJson)
        }
        assertEquals("Invalid config JSON content: wrongjson", exception2.message)
    }
}
