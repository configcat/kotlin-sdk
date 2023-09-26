package com.configcat.evaluation.data

interface TestSet {
    val sdkKey: String?
    val baseUrl: String?
    val jsonOverride: String?
    val tests: Array<TestCase>?
}