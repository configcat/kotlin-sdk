package com.configcat

import io.ktor.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

// This is the same test cases as the ConfigCatUserAttributeHelperTests with different expected results.
// The JS format the double differently to String from the other platforms.
@OptIn(ExperimentalCoroutinesApi::class)
class JSConfigCatUserAttributeHelperTests {

    @Test
    fun testDouble1() = runTest {
        runAttributeValueFromDouble(3.0, "3")
    }

    @Test
    fun testDouble2() = runTest {
        runAttributeValueFromDouble(3.14, "3.14")
    }

    @Test
    fun testDouble3() = runTest {
        runAttributeValueFromDouble(-1.23E-100, "-1.23e-100")
    }

    @Test
    fun testInt() = runTest {
        runAttributeValueFromInt(3, "3")
    }

    @Test
    fun testArray() = runTest {
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
