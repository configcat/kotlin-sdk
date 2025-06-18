package com.configcat

import com.configcat.model.Config
import com.configcat.model.Entry
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EntrySerializationTests {
    @AfterTest
    fun tearDown() {
        Services.reset()
    }

    @Test
    fun testSerialize() =
        runTest {
            val json: String = Data.formatJsonBodyWithString("test")
            val config: Config = Constants.json.decodeFromString(json)

            val fetchTimeNow: Instant = Clock.System.now()
            val entry = Entry(config, "fakeTag", json, fetchTimeNow)

            val fetchTimeNowUnixSecond = fetchTimeNow.toEpochMilliseconds()

            val expected = "$fetchTimeNowUnixSecond\nfakeTag\n$json"
            assertEquals(expected, entry.cacheString)
        }

    @Test
    fun testPayloadSerializationPlatformIndependent() {
        val payloadTestConfigJson =
            "{\"p\":{\"u\":\"https://cdn-global.configcat.com\",\"r\":0,\"s\":\"test-slat\"}," +
                "\"f\":{\"testKey\":{\"v\":{\"s\":\"testValue\"},\"t\":1,\"p\":[],\"r\":[], \"a\":\"\"}}, \"s\":[] }"
        val config: Config = Constants.json.decodeFromString(payloadTestConfigJson)

        val timestamp = Instant.fromEpochMilliseconds(1686756435844L)
        val entry = Entry(config, "test-etag", payloadTestConfigJson, timestamp)

        assertEquals("1686756435844\ntest-etag\n$payloadTestConfigJson", entry.cacheString)

    }

    @Test
    fun testDeserialize() =
        runTest {
            val json: String = Data.formatJsonBodyWithString("test")
            val dateTimeNow = Clock.System.now()
            val dateTimeNowUnixSeconds: Long = dateTimeNow.toEpochMilliseconds()

            val cacheValue = "$dateTimeNowUnixSeconds\nfakeTag\n$json"

            val entry: Entry = Entry.fromString(cacheValue)
            assertNotNull(entry)
            assertEquals(dateTimeNow, entry.fetchTime)
            assertEquals("fakeTag", entry.eTag)
            assertEquals(json, entry.configJson)
            assertEquals(1, entry.config.settings?.size)
        }

    @Test
    fun testDeserializeMissingValue() =
        runTest {
            val deserializeNull = Entry.fromString(null)
            assertTrue(deserializeNull.isEmpty())
            val deserializeEmpty = Entry.fromString("")
            assertTrue(deserializeEmpty.isEmpty())
        }

    @Test
    fun testDeserializeWrongFormat() =
        runTest {
            val exception =
                assertFailsWith<IllegalArgumentException> {
                    Entry.fromString("value with no new line")
                }
            assertEquals("Number of values is fewer than expected.", exception.message)
            val exception2 =
                assertFailsWith<IllegalArgumentException> {
                    Entry.fromString("value with one \n new line")
                }
            assertEquals("Number of values is fewer than expected.", exception2.message)
        }

    @Test
    fun testDeserializeInvalidDate() =
        runTest {
            val cacheValue = "Invalid\nfakeTag\nTestjson"

            val exception =
                assertFailsWith<IllegalArgumentException> {
                    Entry.fromString(cacheValue)
                }

            assertEquals("Invalid fetch time: Invalid", exception.message)
        }

    @Test
    fun testDeserializeInvalidETag() =
        runTest {
            val fetchTimeTest = Clock.System.now().toEpochMilliseconds()
            val cacheValue = "${fetchTimeTest}\n\nTestjson"

            val exception =
                assertFailsWith<IllegalArgumentException> {
                    Entry.fromString(cacheValue)
                }
            assertEquals("Empty eTag value.", exception.message)
        }

    @Test
    fun testDeserializeInvalidJson() =
        runTest {
            val fetchTimeTest = Clock.System.now().toEpochMilliseconds()
            val cacheValueEmptyJson = "${fetchTimeTest}\nTestETag\n"

            val exception =
                assertFailsWith<IllegalArgumentException> {
                    Entry.fromString(cacheValueEmptyJson)
                }
            assertEquals("Empty config jsom value.", exception.message)

            val cacheValueWrongJson = "${fetchTimeTest}\nTestETag\nwrongjson"

            val exception2 =
                assertFailsWith<IllegalArgumentException> {
                    Entry.fromString(cacheValueWrongJson)
                }
            assertEquals("Invalid config JSON content: wrongjson", exception2.message)
        }
}
