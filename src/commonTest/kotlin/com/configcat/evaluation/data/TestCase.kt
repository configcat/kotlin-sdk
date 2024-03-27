package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

data class TestCase(
    val key: String,
    val defaultValue: Any,
    val returnValue: Any,
    val expectedLog: String?,
    val user: ConfigCatUser?
)
