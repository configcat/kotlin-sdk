package com.configcat.userattribute

import com.configcat.ConfigCatClient
import com.configcat.ConfigCatUser
import com.configcat.log.LogLevel
import com.configcat.manualPoll
import com.configcat.userattribute.data.ConvertData
import com.configcat.userattribute.data.DateConvertData
import com.configcat.userattribute.data.NumberConvertData
import com.configcat.userattribute.data.SemVerConvertData
import com.configcat.userattribute.data.StringArrayConvertData
import com.configcat.userattribute.data.StringConvertData
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import korlibs.time.DateTime
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UserAttributeConvertTest {
    @Test
    fun testStringConvert() =
        runTest {
            runConvertTest(StringConvertData, 42, true)
        }

    @Test
    fun testSemverConvert() =
        runTest {
            runConvertTest(SemVerConvertData, "0.0", "20%")
            runConvertTest(SemVerConvertData, "0.9.9", "< 1.0.0")
            runConvertTest(SemVerConvertData, "1.0.0", "20%")
            runConvertTest(SemVerConvertData, "1.1", "20%")
            runConvertTest(SemVerConvertData, 0, "20%")
            runConvertTest(SemVerConvertData, 0.9, "20%")
            runConvertTest(SemVerConvertData, 2, "20%")
        }

    @Test
    fun testStringArrayConvert() =
        runTest {
            runConvertTest(StringArrayConvertData, arrayOf("x", "read"), "Dog")
            runConvertTest(StringArrayConvertData, arrayOf("x", "Read"), "Cat")
            runConvertTest(StringArrayConvertData, mutableListOf("x", "read"), "Dog")
            runConvertTest(StringArrayConvertData, mutableListOf("x", "Read"), "Cat")
            runConvertTest(StringArrayConvertData, "[\"x\", \"read\"]", "Dog")
            runConvertTest(StringArrayConvertData, "[\"x\", \"Read\"]", "Cat")
            runConvertTest(StringArrayConvertData, "x, read", "Cat")
        }

    @Test
    fun testDateConvert() =
        runTest {
            runConvertTest(DateConvertData, "1680307200.001", true)
            runConvertTest(DateConvertData, "1680307199.999", false)
            runConvertTest(DateConvertData, 1680307201L, true)
            runConvertTest(DateConvertData, 1680307199L, false)
            runConvertTest(DateConvertData, 1680307200.001, true)
            runConvertTest(DateConvertData, 1680307199.999, false)
            runConvertTest(DateConvertData, DateTime(1680307200001L), true)
            runConvertTest(DateConvertData, DateTime(1680307199999L), false)
        }

    @Test
    fun testNumberConvert() =
        runTest {
            runConvertTest(NumberConvertData, -1, "<2.1")
            runConvertTest(NumberConvertData, 2, "<2.1")
            runConvertTest(NumberConvertData, 3, "<>4.2")
            runConvertTest(NumberConvertData, 5, ">=5")
            runConvertTest(NumberConvertData, -1L, "<2.1")
            runConvertTest(NumberConvertData, 2L, "<2.1")
            runConvertTest(NumberConvertData, 3L, "<>4.2")
            runConvertTest(NumberConvertData, 5L, ">=5")
            runConvertTest(NumberConvertData, -1.0, "<2.1")
            runConvertTest(NumberConvertData, 2.0, "<2.1")
            runConvertTest(NumberConvertData, 3.0, "<>4.2")
            runConvertTest(NumberConvertData, 5.0, ">=5")
            runConvertTest(NumberConvertData, -1.0f, "<2.1")
            runConvertTest(NumberConvertData, 2.0f, "<2.1")
            runConvertTest(NumberConvertData, 3.0f, "<>4.2")
            runConvertTest(NumberConvertData, 5.0f, ">=5")
            runConvertTest(NumberConvertData, "-1.0", "<2.1")
            runConvertTest(NumberConvertData, "2.0", "<2.1")
            runConvertTest(NumberConvertData, "3.0", "<>4.2")
            runConvertTest(NumberConvertData, "5.0", ">=5")
            runConvertTest(NumberConvertData, "-1", "<2.1")
            runConvertTest(NumberConvertData, "2", "<2.1")
            runConvertTest(NumberConvertData, "3", "<>4.2")
            runConvertTest(NumberConvertData, "5", ">=5")
            runConvertTest(NumberConvertData, Double.NaN, "<>4.2")
            runConvertTest(NumberConvertData, Double.POSITIVE_INFINITY, ">5")
            runConvertTest(NumberConvertData, Double.NEGATIVE_INFINITY, "<2.1")
            runConvertTest(NumberConvertData, Float.NaN, "<>4.2")
            runConvertTest(NumberConvertData, Float.POSITIVE_INFINITY, ">5")
            runConvertTest(NumberConvertData, Float.NEGATIVE_INFINITY, "<2.1")
            runConvertTest(NumberConvertData, Long.MAX_VALUE, ">5")
            runConvertTest(NumberConvertData, Long.MIN_VALUE, "<2.1")
            runConvertTest(NumberConvertData, Int.MAX_VALUE, ">5")
            runConvertTest(NumberConvertData, Int.MIN_VALUE, "<2.1")
            runConvertTest(NumberConvertData, "NotANumber", "80%")
            runConvertTest(NumberConvertData, "Infinity", ">5")
            runConvertTest(NumberConvertData, "NaN", "<>4.2")
            runConvertTest(NumberConvertData, "NaNa", "80%")
            runConvertTest(NumberConvertData, (-1).toByte(), "<2.1")
            runConvertTest(NumberConvertData, (2).toByte(), "<2.1")
            runConvertTest(NumberConvertData, (3).toByte(), "<>4.2")
            runConvertTest(NumberConvertData, (5).toByte(), ">=5")
            runConvertTest(NumberConvertData, (-1).toShort(), "<2.1")
            runConvertTest(NumberConvertData, (2).toShort(), "<2.1")
            runConvertTest(NumberConvertData, (3).toShort(), "<>4.2")
            runConvertTest(NumberConvertData, (5).toShort(), ">=5")
        }

    private suspend fun runConvertTest(
        data: ConvertData,
        customAttributeValue: Any,
        expectedValue: Any,
    ) {
        val mockEngine =
            MockEngine {
                respond(content = data.remoteJson, status = HttpStatusCode.OK)
            }
        val client =
            ConfigCatClient(data.sdkKey) {
                pollingMode = manualPoll()
                httpEngine = mockEngine
                logLevel = LogLevel.ERROR
            }
        client.forceRefresh()

        val customAttributes = mutableMapOf<String, Any>()
        customAttributes["Custom1"] = customAttributeValue

        val configCatUser = ConfigCatUser(identifier = "12345", custom = customAttributes)

        val value = client.getAnyValue(key = data.flagKey, defaultValue = data.defaultValue, user = configCatUser)

        assertEquals(expectedValue, value)

        ConfigCatClient.closeAll()
    }
}
