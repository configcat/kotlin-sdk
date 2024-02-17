package com.configcat.userattribute

import com.configcat.ConfigCatClient
import com.configcat.ConfigCatUser
import com.configcat.getValue
import com.configcat.log.LogLevel
import com.configcat.manualPoll
import com.configcat.userattribute.data.*
import com.soywiz.klock.DateTime
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.util.date.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class UserAttributeConvertTest {

    @Test
    fun testStringConvert() = runTest {
        runConvertTest(StringConvertData, 42, true)
    }

    @Test
    fun testSemverConvert() = runTest {
        runConvertTest(SemVerConvertData, "0.0", "20%")
        runConvertTest(SemVerConvertData, "0.9.9", "< 1.0.0")
        runConvertTest(SemVerConvertData, "1.0.0", "20%")
        runConvertTest(SemVerConvertData, "1.1", "20%")
        runConvertTest(SemVerConvertData, 0, "20%")
        runConvertTest(SemVerConvertData, 0.9, "20%")
        runConvertTest(SemVerConvertData, 2, "20%")
    }

    @Test
    fun testStringArrayConvert() = runTest {
        runConvertTest(StringArrayConvertData, arrayOf("x", "read"), "Dog")
        runConvertTest(StringArrayConvertData, arrayOf("x", "Read"), "Cat")
        runConvertTest(StringArrayConvertData, mutableListOf("x", "read"), "Dog")
        runConvertTest(StringArrayConvertData, mutableListOf("x", "Read"), "Cat")
        runConvertTest(StringArrayConvertData, "[\"x\", \"read\"]", "Dog")
        runConvertTest(StringArrayConvertData, "[\"x\", \"Read\"]", "Cat")
        runConvertTest(StringArrayConvertData, "x, read", "Cat")
    }

    @Test
    fun testDateConvert() = runTest {
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
    fun testNumberConvert() = runTest {
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
    }

    private suspend fun runConvertTest(data: ConvertData, customAttributeValue: Any, expectedValue: Any) {
        val mockEngine = MockEngine {
            respond(content = data.remoteJson, status = HttpStatusCode.OK)
        }
        val client = ConfigCatClient(data.sdkKey) {
            pollingMode = manualPoll()
            httpEngine = mockEngine
            logLevel = LogLevel.ERROR
        }
        client.forceRefresh()

        val customAttributes = mutableMapOf<String, Any>()
        customAttributes["Custom1"] = customAttributeValue

        val configCatUser = ConfigCatUser(identifier = "12345", custom = customAttributes)

        val value = client.getValue(key = data.flagKey, defaultValue = data.defaultValue, user = configCatUser)

        assertEquals(expectedValue, value)

        ConfigCatClient.closeAll()
    }
}
