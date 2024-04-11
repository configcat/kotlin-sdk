package com.configcat.userattribute.data

interface ConvertData {
    val sdkKey: String
    val flagKey: String
    val defaultValue: Any
    val remoteJson: String
}
