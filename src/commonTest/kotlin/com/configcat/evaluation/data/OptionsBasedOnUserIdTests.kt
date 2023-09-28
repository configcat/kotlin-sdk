package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

object OptionsBasedOnUserIdTests : TestSet {
    override val sdkKey = "PKDVCLf-Hq-h-kCzMp-L7Q/psuH7BGHoUmdONrzzUOY7A"
    override val baseUrl = null
    override val jsonOverride = null
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "string75Cat0Dog25Falcon0Horse",
            defaultValue = "default",
            returnValue = "Chicken",
            user = null,
            expectedLog = """WARNING [3001] Cannot evaluate targeting rules and % options for setting 'string75Cat0Dog25Falcon0Horse' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()`/`getValueAsync()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'string75Cat0Dog25Falcon0Horse'
  Skipping % options because the User Object is missing.
  Returning 'Chicken'.""".trimIndent(),
        ),
        TestCase(
            key = "string75Cat0Dog25Falcon0Horse",
            defaultValue = "default",
            returnValue = "Cat",
            user = ConfigCatUser("12345"),
            expectedLog = """INFO [5000] Evaluating 'string75Cat0Dog25Falcon0Horse' for User '{"Identifier":"12345"}'
  Evaluating % options based on the User.Identifier attribute:
  - Computing hash in the [0..99] range from User.Identifier => 21 (this value is sticky and consistent across all SDKs)
  - Hash value 21 selects % option 1 (75%), 'Cat'.
  Returning 'Cat'.
""".trimIndent(),
        ),
    )
}