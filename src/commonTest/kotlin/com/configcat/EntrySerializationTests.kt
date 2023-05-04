package com.configcat

import com.configcat.DateTimeUtils.format
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
        val fetchTimeRaw = format(DateTime.now())
        val entry = Entry(config, "fakeTag", json, fetchTimeRaw)

        val serializedString = entry.serialize()

        val expected = "$fetchTimeRaw\nfakeTag\n$json"
        assertEquals(expected, serializedString)
    }

    @Test
    fun testDeserialize() = runTest {
        val json: String = Data.formatJsonBody("test")
        val dateTimeNow: DateTime = DateTime.now()
        val fetchTimeRaw = format(dateTimeNow)

        val cacheValue = "$fetchTimeRaw\nfakeTag\n$json"

        val entry: Entry = Entry.fromString(cacheValue)
        assertNotNull(entry)
        assertEquals(fetchTimeRaw, entry.fetchTimeRaw)
        assertEquals("fakeTag", entry.eTag)
        assertEquals(json, entry.configJson)
        assertEquals(1, entry.config.settings.size)
        assertEquals(dateTimeNow.date, entry.getFetchTime().date)
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
        assertFailsWith<IllegalArgumentException> {
            Entry.fromString("value with no new line")
        }
        assertFailsWith<IllegalArgumentException> {
            Entry.fromString("value with one \n new line")
        }
    }

    @Test
    fun testDeserializeInvalidDate() = runTest {
        val cacheValue = "Invalid\nfakeTag\nTestjson"

        assertFailsWith<IllegalArgumentException> {
            Entry.fromString(cacheValue)
        }
    }

    @Test
    fun testDeserializeInvalidETag() = runTest {
        val cacheValue = "Invalid\n\nTestjson"

        assertFailsWith<IllegalArgumentException> {
            Entry.fromString(cacheValue)
        }
    }

    @Test
    fun testDeserializeInvalidJson() = runTest {
        val cacheValueEmptyJson = "Invalid\n\n"

        assertFailsWith<IllegalArgumentException> {
            Entry.fromString(cacheValueEmptyJson)
        }

        val cacheValueWrongJson = "Invalid\n\nwrongjson"

        assertFailsWith<IllegalArgumentException> {
            Entry.fromString(cacheValueWrongJson)
        }
    }
}
