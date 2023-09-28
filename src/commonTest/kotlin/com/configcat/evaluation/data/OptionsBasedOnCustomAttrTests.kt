package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

object OptionsBasedOnCustomAttrTests : TestSet {
    override val sdkKey = "configcat-sdk-1/XUbbCFZX_0mOU_uQ_XYGMg/x0tcrFMkl02A65D8GD20Eg"
    override val baseUrl = "https://test-cdn-eu.configcat.com"
    override val jsonOverride = null
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "string75Cat0Dog25Falcon0HorseCustomAttr",
            defaultValue = "default",
            returnValue = "Chicken",
            user = null,
            expectedLog = """WARNING [3001] Cannot evaluate targeting rules and % options for setting 'string75Cat0Dog25Falcon0HorseCustomAttr' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()`/`getValueAsync()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'string75Cat0Dog25Falcon0HorseCustomAttr'
  Skipping % options because the User Object is missing.
  Returning 'Chicken'.""".trimIndent(),
        ),
        TestCase(
            key = "string75Cat0Dog25Falcon0HorseCustomAttr",
            defaultValue = "default",
            returnValue = "Chicken",
            user = ConfigCatUser("12345"),
            expectedLog = """WARNING [3003] Cannot evaluate % options for setting 'string75Cat0Dog25Falcon0HorseCustomAttr' (the User.Country attribute is missing). You should set the User.Country attribute in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'string75Cat0Dog25Falcon0HorseCustomAttr' for User '{"Identifier":"12345"}'
  Skipping % options because the User.Country attribute is missing.
  Returning 'Chicken'.""".trimIndent(),
        ),
        TestCase(
            key = "string75Cat0Dog25Falcon0HorseCustomAttr",
            defaultValue = "default",
            returnValue = "Cat",
            user = ConfigCatUser("12345", country = "US"),
            expectedLog = """INFO [5000] Evaluating 'string75Cat0Dog25Falcon0HorseCustomAttr' for User '{"Identifier":"12345","Country":"US"}'
  Evaluating % options based on the User.Country attribute:
  - Computing hash in the [0..99] range from User.Country => 70 (this value is sticky and consistent across all SDKs)
  - Hash value 70 selects % option 1 (75%), 'Cat'.
  Returning 'Cat'.""".trimIndent(),
        ),
    )
}