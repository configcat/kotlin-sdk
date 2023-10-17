package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

object CircularDependencyTests : TestSet {
    override val sdkKey = null
    override val baseUrl = null
    override val jsonOverride = """{
  "p": {
    "u": "https://cdn-global.configcat.com",
    "r": 0,
    "s": "W8tBvwwMoeP6Ht74jMCI7aPNTc\u002B1W6rtwob18ojXQ9U="
  },
  "f": {
    "key1": {
      "t": 1,
      "v": { "s": "value1" },
      "r": [
        {"c": [{"d": {"f": "key2", "c": 0, "v": {"s": "fourth"}}}], "s": {"v": {"s": "first"}}},
        {"c": [{"d": {"f": "key3", "c": 0, "v": {"s": "value3"}}}], "s": {"v": {"s": "second"}}}
      ]
    },
    "key2": {
      "t": 1,
      "v": { "s": "value2" },
      "r": [
        {"c": [{"d": {"f": "key1", "c": 0, "v": {"s": "value1"}}}], "s": {"v": {"s": "third"}}},
        {"c": [{"d": {"f": "key3", "c": 0, "v": {"s": "value3"}}}], "s": {"v": {"s": "fourth"}}}
      ]
    },
    "key3": {
      "t": 1,
      "v": { "s": "value3" }
    }
  }
}
"""
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "key1",
            defaultValue = "default",
            returnValue = "first",
            expectedLog = """WARNING [3005] Cannot evaluate condition (Flag 'key1' EQUALS 'value1') for setting 'key2' (circular dependency detected between the following depending flags: 'key1' -> 'key2' -> 'key1'). Please check your feature flag definition and eliminate the circular dependency.
INFO [5000] Evaluating 'key1' for User '{"Identifier":"1234"}'
  Evaluating targeting rules and applying the first match if any:
  - IF Flag 'key2' EQUALS 'fourth'
    (
      Evaluating prerequisite flag 'key2':
      Evaluating targeting rules and applying the first match if any:
      - IF Flag 'key1' EQUALS 'value1' THEN 'third' => cannot evaluate, circular dependency detected
        The current targeting rule is ignored and the evaluation continues with the next rule.
      - IF Flag 'key3' EQUALS 'value3'
        (
          Evaluating prerequisite flag 'key3':
          Prerequisite flag evaluation result: 'value3'.
          Condition (Flag 'key3' EQUALS 'value3') evaluates to true.
        )
        THEN 'fourth' => MATCH, applying rule
      Prerequisite flag evaluation result: 'fourth'.
      Condition (Flag 'key2' EQUALS 'fourth') evaluates to true.
    )
    THEN 'first' => MATCH, applying rule
  Returning 'first'.""",
            user = ConfigCatUser("1234")
        )
    )
}
