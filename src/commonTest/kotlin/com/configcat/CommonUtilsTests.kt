package com.configcat

import com.configcat.model.Config
import com.configcat.model.Preferences
import com.configcat.model.Segment
import com.configcat.model.Setting
import com.configcat.model.SettingValue
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class CommonUtilsTests {
    @Test
    fun testValidateSettingValueType() =
        runTest {
            val settingValue = SettingValue(true, "stringValue", 1, 3.14)
            // test valid types -1, 0, 1, 2, 3
            var result: Any = Helpers.validateSettingValueType(settingValue, -1)
            assertEquals(3.14, result)
            result = Helpers.validateSettingValueType(settingValue, 0)
            assertEquals(true, result)
            result = Helpers.validateSettingValueType(settingValue, 1)
            assertEquals("stringValue", result)
            result = Helpers.validateSettingValueType(settingValue, 2)
            assertEquals(1, result)
            result = Helpers.validateSettingValueType(settingValue, 3)
            assertEquals(3.14, result)

            // test setting value not matching type - bool value & string type
            val invalidSettingValue = SettingValue(true, null, null, null)
            val invalidSettingResultException =
                assertFailsWith(
                    exceptionClass = IllegalArgumentException::class,
                    block = { Helpers.validateSettingValueType(invalidSettingValue, 1) },
                )
            assertEquals("Setting value is not of the expected type String.", invalidSettingResultException.message)

            // test invalid type - 99
            val invalidTypeException =
                assertFailsWith(
                    exceptionClass = IllegalArgumentException::class,
                    block = { Helpers.validateSettingValueType(settingValue, 99) },
                )
            assertEquals("Setting is of an unsupported type (null).", invalidTypeException.message)

            // test null setting value
            val invalidSettingValueException =
                assertFailsWith(
                    exceptionClass = IllegalArgumentException::class,
                    block = { Helpers.validateSettingValueType(null, 1) },
                )
            assertEquals("Setting value is missing or invalid.", invalidSettingValueException.message)
        }

    @Test
    fun testAddConfigSaltAndSegmentsToSettings() =
        runTest {
            val config =
                Config(
                    Preferences("", 0, "test-salt"),
                    mapOf(
                        "test-setting" to
                            Setting(
                                1,
                                "",
                                null,
                                null,
                                SettingValue(stringValue = "noRule"),
                                "myVariationId",
                            ),
                    ),
                    arrayOf(Segment("test-segment", null)),
                )
            Helpers.addConfigSaltAndSegmentsToSettings(config)

            assertEquals("test-salt", config.settings?.get("test-setting")?.configSalt)
            assertEquals("test-segment", config.settings?.get("test-setting")?.segments?.get(0)?.name)
        }

    @Test
    fun testParseConfigJson() =
        runTest {
            val configResult = Helpers.parseConfigJson(Data.formatJsonBodyWithString("fake"))
            assertNotNull(configResult)
            assertEquals("test-salt", configResult.preferences?.salt)
            assertEquals("https://cdn-global.configcat.com", configResult.preferences?.baseUrl)
            assertEquals(1, configResult.settings?.size)
            assertEquals("fake", configResult.settings?.get("fakeKey")?.settingValue?.stringValue)
        }

    @Test
    fun testFlagValueSerializer() =
        runTest {
            var obj = SerializeTestClass("testWithString")
            var encoded = Json.encodeToString(obj)
            assertEquals("{\"testData\":\"testWithString\"}", encoded)
            var decoded = Json.decodeFromString<SerializeTestClass>(encoded)
            assertEquals(obj, decoded)

            obj = SerializeTestClass(1)
            encoded = Json.encodeToString(obj)
            assertEquals("{\"testData\":1}", encoded)
            decoded = Json.decodeFromString(encoded)
            assertEquals(obj, decoded)

            obj = SerializeTestClass(true)
            encoded = Json.encodeToString(obj)
            assertEquals("{\"testData\":true}", encoded)
            decoded = Json.decodeFromString(encoded)
            assertEquals(obj, decoded)

            obj = SerializeTestClass(JsonPrimitive("testJsonElement"))
            encoded = Json.encodeToString(obj)
            assertEquals("{\"testData\":\"testJsonElement\"}", encoded)
            decoded = Json.decodeFromString(encoded)
            assertEquals("testJsonElement", decoded.testData)

            // test fails
            val failObject = ConfigCatUser("test")
            val serializeException =
                assertFailsWith(
                    exceptionClass = IllegalArgumentException::class,
                    block = {
                        obj = SerializeTestClass(failObject)
                        encoded = Json.encodeToString(obj)
                    },
                )
            assertEquals("Unable to encode $failObject", serializeException.message)

            val failDecodeString = "{\"testData\":{\"testData2\":\"testJsonElement\"}}"
            val decodeException =
                assertFailsWith(
                    exceptionClass = IllegalStateException::class,
                    block = {
                        decoded = Json.decodeFromString(failDecodeString)
                    },
                )
            assertEquals("Unable to decode {\"testData2\":\"testJsonElement\"}", decodeException.message)
        }

    @Serializable
    data class SerializeTestClass(
        @Serializable(with = Constants.FlagValueSerializer::class)
        val testData: Any,
    )
}
