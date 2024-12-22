package com.configcat

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.test.assertNull

class ConfigCatUserTests {
    @Test
    fun testGetAttribute() {
        val user = ConfigCatUser(identifier = "test")
        assertEquals("test", user.attributeFor("Identifier"))
        assertNull(user.attributeFor("Email"))
    }

    @Test
    fun testTestToString() {
        val user = ConfigCatUser(identifier = "test", custom = mapOf(
            "a" to 1,
            "b" to 1.2,
            "c" to true,
            "d" to TestEnum.A,
            "e" to arrayOf("A", "B"),
            "f" to listOf("C", "D"),
            "g" to IllegalArgumentException("error"),
            "h" to JsonPrimitive("json"),
        ))
        assertEquals("{\"Identifier\":\"test\",\"a\":1,\"b\":1.2,\"c\":true,\"d\":\"A\",\"e\":[\"A\",\"B\"],\"f\":[\"C\",\"D\"],\"g\":\"java.lang.IllegalArgumentException: error\",\"h\":\"json\"}", user.toString())
    }

    enum class TestEnum {
        A, B
    }
}