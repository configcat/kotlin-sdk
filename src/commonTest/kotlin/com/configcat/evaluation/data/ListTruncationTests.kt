package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

object ListTruncationTests : TestSet {
    override val sdkKey = "configcat-sdk-test-key/0000000000000000000001"
    override val baseUrl = null
    override val jsonOverride = """{
  "p": {
    "u": "https://cdn-global.configcat.com",
    "r": 0,
    "s": "W8tBvwwMoeP6Ht74jMCI7aPNTc\u002B1W6rtwob18ojXQ9U="
  },
  "f": {
    "booleanKey1": {
      "t": 0,
      "v": { "b": false },
      "r": [
        {
          "c": [
            {
              "t": {
                "a": "Identifier",
                "c": 2,
                "l": [ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" ]
              }
            },
            {
              "t": {
                "a": "Identifier",
                "c": 2,
                "l": [ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11" ]
              }
            },
            {
              "t": {
                "a": "Identifier",
                "c": 2,
                "l": [ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" ]
              }
            }
          ],
          "s": { "v": { "b": true } }
        }
      ]
    }
  }
}
"""
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "booleanKey1",
            defaultValue = false,
            returnValue = true,
            expectedLog = """INFO [5000] Evaluating 'booleanKey1' for User '{"Identifier":"12"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Identifier CONTAINS ANY OF ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10'] => true
    AND User.Identifier CONTAINS ANY OF ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', ... <1 more value>] => true
    AND User.Identifier CONTAINS ANY OF ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', ... <2 more values>] => true
    THEN 'true' => MATCH, applying rule
  Returning 'true'.""",
            user = ConfigCatUser("12")
        )
    )
}
