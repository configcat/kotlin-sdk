package com.configcat

import io.ktor.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ConfigCatUserAttributeHelperTests {

    @Test
    fun testDouble1() = runTest {
        // JS test run separately for this test
        if (PlatformUtils.IS_BROWSER || PlatformUtils.IS_NODE) {
            return@runTest
        }
        runAttributeValueFromDouble(3.0, "3.0")
    }

    @Test
    fun testDouble2() = runTest {
        // JS test run separately for this test
        if (PlatformUtils.IS_BROWSER || PlatformUtils.IS_NODE) {
            return@runTest
        }
        runAttributeValueFromDouble(3.14, "3.14")
    }

    @Test
    fun testDouble3() = runTest {
        // JS test run separately for this test
        if (PlatformUtils.IS_BROWSER || PlatformUtils.IS_NODE) {
            return@runTest
        }
        runAttributeValueFromDouble(-1.23E-100, "-1.23E-100")
    }

    @Test
    fun testInt() = runTest {
        // JS test run separately for this test
        if (PlatformUtils.IS_BROWSER || PlatformUtils.IS_NODE) {
            return@runTest
        }
        runAttributeValueFromInt(3, "3")
    }

    @Test
    fun testArray() = runTest {
        // JS test run separately for this test
        if (PlatformUtils.IS_BROWSER || PlatformUtils.IS_NODE) {
            return@runTest
        }
        runAttributeValueFromStringArray("a,,b,c", "[\"a\",\"\",\"b\",\"c\"]")
    }

    private fun runAttributeValueFromDouble(input: Double, expected: String) {
        val result = ConfigCatUser.attributeValueFrom(input)
        assertEquals(expected, result, "Formatted user attribute is not matching.")
    }

    private fun runAttributeValueFromInt(input: Int, expected: String) {
        val result = ConfigCatUser.attributeValueFrom(input)
        assertEquals(expected, result, "Formatted user attribute is not matching.")
    }

    private fun runAttributeValueFromStringArray(input: String, expected: String) {
        val splitInput = input.split(",".toRegex()).toTypedArray()
        val result = ConfigCatUser.attributeValueFrom(splitInput)
        assertEquals(expected, result, "Formatted user attribute is not matching.")
    }
}
