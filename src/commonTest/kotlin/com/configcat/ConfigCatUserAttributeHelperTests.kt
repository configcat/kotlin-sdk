package com.configcat

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ConfigCatUserAttributeHelperTests {

    @Test
    fun testDouble1() = runTest {
        runAttributeValueFrom("double", "3d", "3.0")
    }

    @Test
    fun testDouble2() = runTest {
        runAttributeValueFrom("double", "3.14", "3.14")
    }

    @Test
    fun testDouble3() = runTest {
        runAttributeValueFrom("double", "-1.23E-100", "-1.23E-100")
    }

    @Test
    fun testInt() = runTest {
        runAttributeValueFrom("int", "3", "3")
    }

    @Test
    fun testArray() = runTest {
        runAttributeValueFrom("stringlist", "a,,b,c", "[\"a\",\"\",\"b\",\"c\"]")
    }

    private suspend fun runAttributeValueFrom(type: String, input: String, expected: String) {
        val result =
//            if ("datetime" == type) {
//            val sdf: java.text.SimpleDateFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
//            User.attributeValueFrom(sdf.parse(input as String))
//        } else
            when (type) {
                "double" -> {
                    val doubleInput = input.toDouble()
                    ConfigCatUserHelper.attributeValueFrom(doubleInput)
                }

                "int" -> {
                    val intInput = input.toInt()
                    ConfigCatUserHelper.attributeValueFrom(intInput)
                }

                else -> {
                    val splitInput = input.split(",".toRegex()).toTypedArray()
                    ConfigCatUserHelper.attributeValueFrom(splitInput)
                }
            }
        assertEquals(expected, result, "Formatted user attribute is not matching.")

    }
}