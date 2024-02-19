package com.configcat.integration.matrix

object SegmentMatrix : DataMatrix {
    override val sdkKey: String = "configcat-sdk-1/JcPbCGl_1E-K9M-fJOyKyQ/h99HYXWWNE2bH8eWyLAVMA"
    override val data =
        """Identifier;Email;Country;Custom1;developerAndBetaUserSegment;developerAndBetaUserCleartextSegment;notDeveloperAndNotBetaUserSegment;notDeveloperAndNotBetaUserCleartextSegment
##null##;;;;False;False;False;False
;;;;False;False;False;False
john@example.com;john@example.com;##null##;##null##;False;False;False;False
jane@example.com;jane@example.com;##null##;##null##;False;False;False;False
kate@example.com;kate@example.com;##null##;##null##;True;True;True;True"""
    override val remoteJson =
        """{
   "p":{
      "u":"https://cdn-global.configcat.com",
      "r":0,
      "s":"AfP/HFxenWYu4mTtHLlrNSQTV6DIAVnqRoNiaF7fLGQ="
   },
   "s":[
      {
         "n":"Beta Users",
         "r":[
            {
               "a":"Email",
               "c":16,
               "l":[
                  "6cba762cb8633edb821b0d053b889078a7196dfeaff76bd7093db1405540fce0",
                  "41c849db4599b0ebcedecd6a64904401ffcd4d9c26137907cc0ef16525daa665"
               ]
            }
         ]
      },
      {
         "n":"Developers",
         "r":[
            {
               "a":"Email",
               "c":16,
               "l":[
                  "aae4b03d832f66cfe06b716e2af6ea063ce71408728814fe78fbfec8fbc20d76",
                  "2ebeb92c2192e27005ac0a954adc38a1986639976acf61c77a852b86200d67f4"
               ]
            }
         ]
      },
      {
         "n":"Not Beta Users",
         "r":[
            {
               "a":"Email",
               "c":17,
               "l":[
                  "15ac2079d47e7d0c94f2d85327b2c262014abbfbbcbb200c932a6bcce5933ea1",
                  "e24dd5274f7b82521e19b19f4e9fe5c586bfc9ceb09683d7e7ba948d3f8dc023"
               ]
            }
         ]
      },
      {
         "n":"Not Developers",
         "r":[
            {
               "a":"Email",
               "c":17,
               "l":[
                  "2c9f86d42b5e5206a3192bb66f086456ac2641affa1c3422deb4f36923711e13",
                  "c78bf0398d5e526fc63c2853107211fc66ec692cccd69c5610cf19a30fb8c828"
               ]
            }
         ]
      },
      {
         "n":"Not States",
         "r":[
            {
               "a":"Country",
               "c":3,
               "l":[
                  "States"
               ]
            }
         ]
      },
      {
         "n":"United",
         "r":[
            {
               "a":"Country",
               "c":2,
               "l":[
                  "United"
               ]
            }
         ]
      },
      {
         "n":"Beta Users (cleartext)",
         "r":[
            {
               "a":"Email",
               "c":0,
               "l":[
                  "jane@example.com",
                  "john@example.com"
               ]
            }
         ]
      },
      {
         "n":"Not Beta Users (cleartext)",
         "r":[
            {
               "a":"Email",
               "c":1,
               "l":[
                  "jane@example.com",
                  "john@example.com"
               ]
            }
         ]
      }
   ],
   "f":{
      "countrySegment":{
         "t":1,
         "r":[
            {
               "c":[
                  {
                     "s":{
                        "s":5,
                        "c":0
                     }
                  },
                  {
                     "s":{
                        "s":4,
                        "c":0
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"A"
                  },
                  "i":"9b7e6414"
               }
            }
         ],
         "v":{
            "s":"Z"
         },
         "i":"f71b6d96"
      },
      "developerAndBetaUserCleartextSegment":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "s":{
                        "s":6,
                        "c":1
                     }
                  },
                  {
                     "s":{
                        "s":1,
                        "c":0
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"586d85a7"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"80c95c76"
      },
      "developerAndBetaUserSegment":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "s":{
                        "s":1,
                        "c":0
                     }
                  },
                  {
                     "s":{
                        "s":0,
                        "c":1
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"ddc50638"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"6427f4b8"
      },
      "notDeveloperAndNotBetaUserCleartextSegment":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "s":{
                        "s":3,
                        "c":1
                     }
                  },
                  {
                     "s":{
                        "s":7,
                        "c":0
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"46b767da"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"3af487b7"
      },
      "notDeveloperAndNotBetaUserSegment":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "s":{
                        "s":2,
                        "c":0
                     }
                  },
                  {
                     "s":{
                        "s":3,
                        "c":1
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"77081d42"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"a14eaf13"
      }
   }
}"""
}