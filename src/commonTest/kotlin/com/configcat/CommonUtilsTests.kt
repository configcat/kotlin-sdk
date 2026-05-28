package com.configcat

import com.configcat.model.Config
import com.configcat.model.Preferences
import com.configcat.model.Segment
import com.configcat.model.Setting
import com.configcat.model.SettingValue
import kotlinx.coroutines.test.runTest
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
            var result: Any = settingValue.validateType(-1)
            assertEquals(3.14, result)
            result = settingValue.validateType(0)
            assertEquals(true, result)
            result = settingValue.validateType(1)
            assertEquals("stringValue", result)
            result = settingValue.validateType(2)
            assertEquals(1, result)
            result = settingValue.validateType(3)
            assertEquals(3.14, result)

            // test setting value not matching type - bool value & string type
            val invalidSettingValue = SettingValue(true, null, null, null)
            val invalidSettingResultException =
                assertFailsWith(
                    exceptionClass = IllegalArgumentException::class,
                    block = { invalidSettingValue.validateType(1) },
                )
            assertEquals("Setting value is not of the expected type String.", invalidSettingResultException.message)

            // test invalid type - 99
            val invalidTypeException =
                assertFailsWith(
                    exceptionClass = IllegalArgumentException::class,
                    block = { settingValue.validateType(99) },
                )
            assertEquals("Setting is of an unsupported type (null).", invalidTypeException.message)

            // test null setting value
            val invalidSettingValueException =
                assertFailsWith(
                    exceptionClass = IllegalArgumentException::class,
                    block = { null.validateType(1) },
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
            config.addConfigSaltAndSegmentsToSettings()

            assertEquals("test-salt", config.settings?.get("test-setting")?.configSalt)
            assertEquals("test-segment", config.settings?.get("test-setting")?.segments?.get(0)?.name)
        }

    @Test
    fun testParseConfigJson() =
        runTest {
            val configResult = Data.formatJsonBodyWithString("fake").parseConfigJson()
            assertNotNull(configResult)
            assertEquals("test-salt", configResult.preferences?.salt)
            assertEquals("https://cdn-global.configcat.com", configResult.preferences?.baseUrl)
            assertEquals(1, configResult.settings?.size)
            assertEquals("fake", configResult.settings?.get("fakeKey")?.settingValue?.stringValue)
        }
}
