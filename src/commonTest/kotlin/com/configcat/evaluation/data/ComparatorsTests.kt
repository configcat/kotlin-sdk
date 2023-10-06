package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

object ComparatorsTests : TestSet {
    override val sdkKey = "configcat-sdk-1/JcPbCGl_1E-K9M-fJOyKyQ/OfQqcTjfFUGBwMKqtyEOrQ"
    override val baseUrl = null
    override val jsonOverride = null
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "allinone",
            defaultValue = "",
            returnValue = "default",
            user = ConfigCatUser(
                "12345",
                "joe@example.com",
                "USA",
                custom = mapOf("Version" to "1.0.0", "Number" to "1.0", "Date" to "1693497500")
            ),
            expectedLog = """INFO [5000] Evaluating 'allinone' for User '{"Identifier":"12345","Email":"joe@example.com","Country":"USA","Version":"1.0.0","Number":"1.0","Date":"1693497500"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email EQUALS '<hashed value>' => true
    AND User.Email NOT EQUALS '<hashed value>' => false, skipping the remaining AND conditions
    THEN '1' => no match
  - IF User.Email IS ONE OF [<1 hashed value>] => true
    AND User.Email IS NOT ONE OF [<1 hashed value>] => false, skipping the remaining AND conditions
    THEN '2' => no match
  - IF User.Email STARTS WITH ANY OF [<1 hashed value>] => true
    AND User.Email NOT STARTS WITH ANY OF [<1 hashed value>] => false, skipping the remaining AND conditions
    THEN '3' => no match
  - IF User.Email ENDS WITH ANY OF [<1 hashed value>] => true
    AND User.Email NOT ENDS WITH ANY OF [<1 hashed value>] => false, skipping the remaining AND conditions
    THEN '4' => no match
  - IF User.Email CONTAINS ANY OF ['e@e'] => true
    AND User.Email NOT CONTAINS ANY OF ['e@e'] => false, skipping the remaining AND conditions
    THEN '5' => no match
  - IF User.Version IS ONE OF ['1.0.0'] => true
    AND User.Version IS NOT ONE OF ['1.0.0'] => false, skipping the remaining AND conditions
    THEN '6' => no match
  - IF User.Version < '1.0.1' => true
    AND User.Version >= '1.0.1' => false, skipping the remaining AND conditions
    THEN '7' => no match
  - IF User.Version > '0.9.9' => true
    AND User.Version <= '0.9.9' => false, skipping the remaining AND conditions
    THEN '8' => no match
  - IF User.Number = '1' => true
    AND User.Number != '1' => false, skipping the remaining AND conditions
    THEN '9' => no match
  - IF User.Number < '1.1' => true
    AND User.Number >= '1.1' => false, skipping the remaining AND conditions
    THEN '10' => no match
  - IF User.Number > '0.9' => true
    AND User.Number <= '0.9' => false, skipping the remaining AND conditions
    THEN '11' => no match
  - IF User.Date BEFORE '1693497600' (2023-08-31T16:00:00.000Z UTC) => true
    AND User.Date AFTER '1693497600' (2023-08-31T16:00:00.000Z UTC) => false, skipping the remaining AND conditions
    THEN '12' => no match
  - IF User.Country ARRAY CONTAINS ANY OF [<1 hashed value>] => true
    AND User.Country ARRAY NOT CONTAINS ANY OF [<1 hashed value>] => false, skipping the remaining AND conditions
    THEN '13' => no match
  Returning 'default'.
""".trimIndent(),
        )
    )
}