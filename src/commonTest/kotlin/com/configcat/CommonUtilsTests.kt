package com.configcat

import com.configcat.model.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class CommonUtilsTests {

    @Test
    fun testValidateSettingValueType() = runTest {
        val settingValue = SettingValue(true, "stringValue", 1, 3.14)
        //test valid types -1, 0, 1, 2, 3
        var result: Any = validateSettingValueType(settingValue, -1)
        assertEquals(3.14, result)
        result = validateSettingValueType(settingValue, 0)
        assertEquals(true, result)
        result = validateSettingValueType(settingValue, 1)
        assertEquals("stringValue", result)
        result = validateSettingValueType(settingValue, 2)
        assertEquals(1, result)
        result = validateSettingValueType(settingValue, 3)
        assertEquals(3.14, result)

        // test setting value not matching type - bool value & string type
        val invalidSettingValue = SettingValue(true, null, null, null)
        val invalidSettingResultException = assertFailsWith(
            exceptionClass = IllegalArgumentException::class,
            block = { validateSettingValueType(invalidSettingValue, 1) }
        )
        assertEquals("Setting value is not of the expected type String.", invalidSettingResultException.message)

        //test invalid type - 99
        val invalidTypeException = assertFailsWith(
            exceptionClass = IllegalArgumentException::class,
            block = { validateSettingValueType(settingValue, 99) }
        )
        assertEquals("Setting is of an unsupported type (null).", invalidTypeException.message)

        // test null setting value
        val invalidSettingValueException = assertFailsWith(
            exceptionClass = IllegalArgumentException::class,
            block = { validateSettingValueType(null, 1) }
        )
        assertEquals("Setting value is missing or invalid.", invalidSettingValueException.message)
    }

    @Test
    fun testAddConfigSaltAndSegmentsToSettings() = runTest {
        var config = Config(
            Preferences("", 0, "test-salt"),
            mapOf("test-setting" to Setting(
                1,
                "",
                null,
                null,
                SettingValue(stringValue = "noRule"),
                "myVariationId"
            ),),
            arrayOf(Segment("test-segment", null))
        )
        addConfigSaltAndSegmentsToSettings(config)

        assertEquals("test-salt", config.settings?.get("test-setting")?.configSalt)
        assertEquals("test-segment", config.settings?.get("test-setting")?.segments?.get(0)?.name)
    }

    @Test
    fun testParseConfigJson() = runTest {
        val configResult = parseConfigJson(Data.formatJsonBodyWithString("fake"))
        assertNotNull(configResult)
        assertEquals("test-salt", configResult.preferences?.salt)
        assertEquals("https://cdn-global.configcat.com", configResult.preferences?.baseUrl)
        assertEquals(1, configResult.settings?.size)
        assertEquals("fake", configResult.settings?.get("fakeKey")?.settingValue?.stringValue)
    }
}
