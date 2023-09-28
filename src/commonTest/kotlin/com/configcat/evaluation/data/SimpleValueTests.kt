package com.configcat.evaluation.data

object SimpleValueTests : TestSet {
    override val sdkKey = "PKDVCLf-Hq-h-kCzMp-L7Q/psuH7BGHoUmdONrzzUOY7A"
    override val baseUrl = null
        //"https://app.configcat.com/08d5a03c-feb7-af1e-a1fa-40b3329f8bed/08d62463-86ec-8fde-f5b5-1c5c426fc830/244cf8b0-f604-11e8-b543-f23c917f9d8d"
    override val jsonOverride = null
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "boolDefaultFalse",
            defaultValue = true,
            returnValue = false,
            expectedLog = """INFO [5000] Evaluating 'boolDefaultFalse'
  Returning 'false'.
""".trimIndent(),
            user = null
        ),
        TestCase(
            key = "boolDefaultTrue",
            defaultValue = false,
            returnValue = true,
            expectedLog = """INFO [5000] Evaluating 'boolDefaultTrue'
  Returning 'true'.
""".trimIndent(),
            user = null
        ),
        TestCase(
            key = "stringDefaultCat",
            defaultValue = "stringDefaultCat",
            returnValue = "Cat",
            expectedLog = """INFO [5000] Evaluating 'stringDefaultCat'
  Returning 'Cat'.
""".trimIndent(),
            user = null
        ),
        TestCase(
            key = "integerDefaultOne",
            defaultValue = 0,
            returnValue = 1,
            expectedLog = """INFO [5000] Evaluating 'integerDefaultOne'
  Returning '1'.
""".trimIndent(),
            user = null
        ),
        TestCase(
            key = "doubleDefaultPi",
            defaultValue = 0.0,
            returnValue = 3.1415,
            expectedLog = """INFO [5000] Evaluating 'doubleDefaultPi'
  Returning '3.1415'.
""".trimIndent(),
            user = null
        )
    )

}