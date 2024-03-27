package com.configcat.integration.matrix

object SemanticMatrix2 : DataMatrix {
    override val sdkKey: String = "configcat-sdk-1/PKDVCLf-Hq-h-kCzMp-L7Q/U8nt3zEhDEO5S2ulubCopA"
    override val data = """Identifier;Email;Country;AppVersion;precedenceTests
dontcare;;;1.9.1-1;< 1.9.1-2
dontcare;;;1.9.1-2;< 1.9.1-10
dontcare;;;1.9.1-10;< 1.9.1-10a
dontcare;;;1.9.1-10a;< 1.9.1-1a
dontcare;;;1.9.1-1a;< 1.9.1-alpha
dontcare;;;1.9.1-alpha;< 1.9.99-alpha
dontcare;;;1.9.99-alpha;= 1.9.99-alpha
dontcare;;;1.9.99-alpha+build1;= 1.9.99-alpha
dontcare;;;1.9.99-alpha+build2;= 1.9.99-alpha
dontcare;;;1.9.99-alpha2;< 1.9.99-beta
dontcare;;;1.9.99-beta;< 1.9.99-rc
dontcare;;;1.9.99-rc;< 1.9.99-rc.1
dontcare;;;1.9.99-rc.1;< 1.9.99-rc.2
dontcare;;;1.9.99-rc.2;< 1.9.99-rc.20
dontcare;;;1.9.99-rc.9;< 1.9.99-rc.20
dontcare;;;1.9.99-rc.20;< 1.9.99-rc.20a
dontcare;;;1.9.99-rc.20a;< 1.9.99-rc.2a
dontcare;;;1.9.99-rc.2a;< 1.9.99
dontcare;;;1.9.99;< 1.9.100
dontcare;;;1.9.100;< 1.10.0-alpha
dontcare;;;1.10.0-alpha;<= 1.10.0-alpha
dontcare;;;1.10.0;<= 1.10.0
dontcare;;;1.10.1;<= 1.10.1
dontcare;;;1.10.2;<= 1.10.3
dontcare;;;2.0.0;= 2.0.0
dontcare;;;2.0.0+build3;= 2.0.0
dontcare;;;2.0.0+001;= 2.0.0
dontcare;;;2.0.0+20130313144700;= 2.0.0
dontcare;;;2.0.0+exp.sha.5114f85;= 2.0.0
dontcare;;;3.0.0;= 3.0.0+build3
dontcare;;;4.0.0;= 4.0.0+001
dontcare;;;5.0.0;= 5.0.0+20130313144700
dontcare;;;6.0.0;= 6.0.0+exp.sha.5114f85
dontcare;;;7.0.0-patch+metadata;= 7.0.0-patch
dontcare;;;8.0.0-patch+metadata;= 8.0.0-patch+anothermetadata
dontcare;;;9.0.0-patch;= 9.0.0-patch+metadata
dontcare;;;10.0.0;DEFAULT-FROM-CC-APP
dontcare;;;104.0.0;> 103.0.0
dontcare;;;103.0.0;>= 103.0.0
dontcare;;;102.0.0;>= 101.0.0
dontcare;;;101.0.0;>= 101.0.0
dontcare;;;90.104.0;> 90.103.0
dontcare;;;90.103.0;>= 90.103.0
dontcare;;;90.102.0;>= 90.101.0
dontcare;;;90.101.0;>= 90.101.0
dontcare;;;80.0.104;> 80.0.103
dontcare;;;80.0.103;>= 80.0.103
dontcare;;;80.0.102;>= 80.0.101
dontcare;;;80.0.101;>= 80.0.101
dontcare;;;73.0.0;>= 73.0.0-beta.2
dontcare;;;72.0.0;> 72.0.0-beta.2
dontcare;;;72.0.0-beta.2;> 72.0.0-beta.1
dontcare;;;72.0.0-beta.1;> 72.0.0-beta
dontcare;;;72.0.0-beta;> 72.0.0-alpha
dontcare;;;72.0.0-alpha;> 72.0.0-1a
dontcare;;;72.0.0-1a;> 72.0.0-10a
dontcare;;;72.0.0-10aa;> 72.0.0-10a
dontcare;;;72.0.0-10a;> 72.0.0-2
dontcare;;;72.0.0-2;> 72.0.0-1
dontcare;;;71.0.0+metadata;>= 71.0.0+anothermetadata
dontcare;;;71.0.0-patch3+metadata;>= 71.0.0-patch3+anothermetadata
dontcare;;;71.0.0-patch2+metadata;>= 71.0.0-patch2
dontcare;;;71.0.0-patch1;>= 71.0.0-patch1+metadata
dontcare;;;60.73.0;>= 60.73.0-beta.2
dontcare;;;60.72.0;> 60.72.0-beta.2
dontcare;;;60.72.0-beta.2;> 60.72.0-beta.1
dontcare;;;60.72.0-beta.1;> 60.72.0-beta
dontcare;;;60.72.0-beta;> 60.72.0-alpha
dontcare;;;60.72.0-alpha;> 60.72.0-1a
dontcare;;;60.72.0-1a;> 60.72.0-10a
dontcare;;;60.72.0-10aa;> 60.72.0-10a
dontcare;;;60.72.0-10a;> 60.72.0-2
dontcare;;;60.72.0-2;> 60.72.0-1
dontcare;;;60.71.0+metadata;>= 60.71.0+anothermetadata
dontcare;;;60.71.0-patch3+metadata;>= 60.71.0-patch3+anothermetadata
dontcare;;;60.71.0-patch2+metadata;>= 60.71.0-patch2
dontcare;;;60.71.0-patch1;>= 60.71.0-patch1+metadata
dontcare;;;50.60.73;>= 50.60.73-beta.2
dontcare;;;50.60.72;> 50.60.72-beta.2
dontcare;;;50.60.72-beta.2;> 50.60.72-beta.1
dontcare;;;50.60.72-beta.1;> 50.60.72-beta
dontcare;;;50.60.72-beta;> 50.60.72-alpha
dontcare;;;50.60.72-alpha;> 50.60.72-1a
dontcare;;;50.60.72-1a;> 50.60.72-10a
dontcare;;;50.60.72-10aa;> 50.60.72-10a
dontcare;;;50.60.72-10a;> 50.60.72-2
dontcare;;;50.60.72-2;> 50.60.72-1
dontcare;;;50.60.71+metadata;>= 50.60.71+anothermetadata
dontcare;;;50.60.71-patch3+metadata;>= 50.60.71-patch3+anothermetadata
dontcare;;;50.60.71-patch2+metadata;>= 50.60.71-patch2
dontcare;;;50.60.71-patch1;>= 50.60.71-patch1+metadata
dontcare;;;50.60.71-patch1+anothermetadata;>= 50.60.71-patch1+metadata
dontcare;;;40.0.0-patch;>= 40.0.0-patch
dontcare;;;30.0.0-beta;>= 30.0.0-alpha"""
    override val remoteJson =
        """{
   "p":{
      "u":"https://cdn-global.configcat.com",
      "r":0,
      "s":"2V43LyfkDnQoRg0q5qOcqKwg4y0GT/anDuIpG2yFEUA="
   },
   "f":{
      "precedenceTests":{
         "t":1,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":6,
                        "s":"1.9.1-2"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003C 1.9.1-2"
                  },
                  "i":"92a04969"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":6,
                        "s":"1.9.1-10"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003C 1.9.1-10"
                  },
                  "i":"c651eba2"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":6,
                        "s":"1.9.1-10a"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003C 1.9.1-10a"
                  },
                  "i":"237dedc5"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":6,
                        "s":"1.9.1-1a"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003C 1.9.1-1a"
                  },
                  "i":"154a319b"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":6,
                        "s":"1.9.1-alpha"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003C 1.9.1-alpha"
                  },
                  "i":"33f59c5e"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":6,
                        "s":"1.9.99-alpha"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003C 1.9.99-alpha"
                  },
                  "i":"9b6c24f1"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":4,
                        "l":[
                           "1.9.99-alpha"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"= 1.9.99-alpha"
                  },
                  "i":"c08a99de"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":6,
                        "s":"1.9.99-beta"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003C 1.9.99-beta"
                  },
                  "i":"4c9d7eb1"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":6,
                        "s":"1.9.99-rc"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003C 1.9.99-rc"
                  },
                  "i":"e5aa7655"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":6,
                        "s":"1.9.99-rc.1"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003C 1.9.99-rc.1"
                  },
                  "i":"c9075e5b"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":6,
                        "s":"1.9.99-rc.2"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003C 1.9.99-rc.2"
                  },
                  "i":"97465d24"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":6,
                        "s":"1.9.99-rc.20"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003C 1.9.99-rc.20"
                  },
                  "i":"32d20254"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":6,
                        "s":"1.9.99-rc.20a"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003C 1.9.99-rc.20a"
                  },
                  "i":"c4843bfb"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":6,
                        "s":"1.9.99-rc.2a"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003C 1.9.99-rc.2a"
                  },
                  "i":"11b96c5a"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":6,
                        "s":"1.9.99"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003C 1.9.99"
                  },
                  "i":"dc5a0ed1"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":6,
                        "s":"1.9.100"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003C 1.9.100"
                  },
                  "i":"8ce0bff8"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":6,
                        "s":"1.10.0-alpha"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003C 1.10.0-alpha"
                  },
                  "i":"9ff0cadc"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":7,
                        "s":"1.10.0-alpha"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003C= 1.10.0-alpha"
                  },
                  "i":"7a24a0f6"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":6,
                        "s":"1.10.0"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003C 1.10.0"
                  },
                  "i":"03a85e10"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":7,
                        "s":"1.10.0"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003C= 1.10.0"
                  },
                  "i":"b37d5427"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":7,
                        "s":"1.10.1"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003C= 1.10.1"
                  },
                  "i":"b402f112"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":7,
                        "s":"1.10.3"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003C= 1.10.3"
                  },
                  "i":"da563c51"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":6,
                        "s":"2.0.0"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003C 2.0.0"
                  },
                  "i":"c64645a1"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":4,
                        "l":[
                           "2.0.0"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"= 2.0.0"
                  },
                  "i":"b0008e97"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":4,
                        "l":[
                           "3.0.0\u002Bbuild3"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"= 3.0.0\u002Bbuild3"
                  },
                  "i":"67ceff4e"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":4,
                        "l":[
                           "4.0.0\u002B001"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"= 4.0.0\u002B001"
                  },
                  "i":"da6dd7ab"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":4,
                        "l":[
                           "5.0.0\u002B20130313144700"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"= 5.0.0\u002B20130313144700"
                  },
                  "i":"673b3fd5"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":4,
                        "l":[
                           "6.0.0\u002Bexp.sha.5114f85"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"= 6.0.0\u002Bexp.sha.5114f85"
                  },
                  "i":"e3bcafe6"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":4,
                        "l":[
                           "7.0.0-patch"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"= 7.0.0-patch"
                  },
                  "i":"04e2949b"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":4,
                        "l":[
                           "8.0.0-patch\u002Banothermetadata"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"= 8.0.0-patch\u002Banothermetadata"
                  },
                  "i":"505e8efa"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":4,
                        "l":[
                           "9.0.0-patch\u002Bmetadata"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"= 9.0.0-patch\u002Bmetadata"
                  },
                  "i":"ca4c9dcc"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"103.0.0"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 103.0.0"
                  },
                  "i":"9428e733"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":9,
                        "s":"103.0.0"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E= 103.0.0"
                  },
                  "i":"c448abb8"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":9,
                        "s":"101.0.0"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E= 101.0.0"
                  },
                  "i":"9980c03a"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"90.103.0"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 90.103.0"
                  },
                  "i":"04259f0b"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":9,
                        "s":"90.103.0"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E= 90.103.0"
                  },
                  "i":"4817782c"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":9,
                        "s":"90.101.0"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E= 90.101.0"
                  },
                  "i":"2e9be278"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"80.0.103"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 80.0.103"
                  },
                  "i":"d7058d3e"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":9,
                        "s":"80.0.103"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E= 80.0.103"
                  },
                  "i":"0da87e6b"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":9,
                        "s":"80.0.101"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E= 80.0.101"
                  },
                  "i":"8e71aa24"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":9,
                        "s":"73.0.0-beta.2"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E= 73.0.0-beta.2"
                  },
                  "i":"26a443e3"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"72.0.0-beta.2"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 72.0.0-beta.2"
                  },
                  "i":"0705710a"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"72.0.0-beta.1"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 72.0.0-beta.1"
                  },
                  "i":"7d6cf793"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"72.0.0-beta"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 72.0.0-beta"
                  },
                  "i":"f9ef6e83"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"72.0.0-alpha"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 72.0.0-alpha"
                  },
                  "i":"cf17c939"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"72.0.0-1a"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 72.0.0-1a"
                  },
                  "i":"650640fd"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"72.0.0-10a"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 72.0.0-10a"
                  },
                  "i":"508dd0b2"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"72.0.0-2"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 72.0.0-2"
                  },
                  "i":"142e6d61"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"72.0.0-1"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 72.0.0-1"
                  },
                  "i":"d969006a"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":9,
                        "s":"71.0.0\u002Banothermetadata"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E= 71.0.0\u002Banothermetadata"
                  },
                  "i":"6f74dc87"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":9,
                        "s":"71.0.0-patch3\u002Banothermetadata"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E= 71.0.0-patch3\u002Banothermetadata"
                  },
                  "i":"8061734b"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":9,
                        "s":"71.0.0-patch2"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E= 71.0.0-patch2"
                  },
                  "i":"0615c726"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":9,
                        "s":"71.0.0-patch1\u002Bmetadata"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E= 71.0.0-patch1\u002Bmetadata"
                  },
                  "i":"910b79b5"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":9,
                        "s":"60.73.0-beta.2"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E= 60.73.0-beta.2"
                  },
                  "i":"32e2a4ea"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"60.72.0-beta.2"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 60.72.0-beta.2"
                  },
                  "i":"9017539e"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"60.72.0-beta.1"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 60.72.0-beta.1"
                  },
                  "i":"74de4704"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"60.72.0-beta"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 60.72.0-beta"
                  },
                  "i":"b61af046"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"60.72.0-alpha"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 60.72.0-alpha"
                  },
                  "i":"419eb18d"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"60.72.0-1a"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 60.72.0-1a"
                  },
                  "i":"7574c707"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"60.72.0-10a"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 60.72.0-10a"
                  },
                  "i":"5b3949e6"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"60.72.0-2"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 60.72.0-2"
                  },
                  "i":"9ff17692"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"60.72.0-1"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 60.72.0-1"
                  },
                  "i":"3027451d"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":9,
                        "s":"60.71.0\u002Banothermetadata"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E= 60.71.0\u002Banothermetadata"
                  },
                  "i":"613d3642"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":9,
                        "s":"60.71.0-patch3\u002Banothermetadata"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E= 60.71.0-patch3\u002Banothermetadata"
                  },
                  "i":"e45ffb06"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":9,
                        "s":"60.71.0-patch2"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E= 60.71.0-patch2"
                  },
                  "i":"db50de0a"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":9,
                        "s":"60.71.0-patch1\u002Bmetadata"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E= 60.71.0-patch1\u002Bmetadata"
                  },
                  "i":"5f9acaf7"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":9,
                        "s":"50.60.73-beta.2"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E= 50.60.73-beta.2"
                  },
                  "i":"701ac6b2"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"50.60.72-beta.2"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 50.60.72-beta.2"
                  },
                  "i":"da09daf8"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"50.60.72-beta.1"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 50.60.72-beta.1"
                  },
                  "i":"8f7e54d5"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"50.60.72-beta"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 50.60.72-beta"
                  },
                  "i":"93e245a5"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"50.60.72-alpha"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 50.60.72-alpha"
                  },
                  "i":"356c8279"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"50.60.72-1a"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 50.60.72-1a"
                  },
                  "i":"6131df16"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"50.60.72-10a"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 50.60.72-10a"
                  },
                  "i":"3f1a3aa4"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"50.60.72-2"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 50.60.72-2"
                  },
                  "i":"7534cc57"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":8,
                        "s":"50.60.72-1"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E 50.60.72-1"
                  },
                  "i":"24d6f0ab"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":9,
                        "s":"50.60.71\u002Banothermetadata"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E= 50.60.71\u002Banothermetadata"
                  },
                  "i":"fdd36d82"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":9,
                        "s":"50.60.71-patch3\u002Banothermetadata"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E= 50.60.71-patch3\u002Banothermetadata"
                  },
                  "i":"709780e6"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":9,
                        "s":"50.60.71-patch2"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E= 50.60.71-patch2"
                  },
                  "i":"7649322d"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":9,
                        "s":"50.60.71-patch1\u002Bmetadata"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E= 50.60.71-patch1\u002Bmetadata"
                  },
                  "i":"25d5c70d"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":9,
                        "s":"40.0.0-patch"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E= 40.0.0-patch"
                  },
                  "i":"271370ff"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"AppVersion",
                        "c":9,
                        "s":"30.0.0-alpha"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"\u003E= 30.0.0-alpha"
                  },
                  "i":"af29c39d"
               }
            }
         ],
         "v":{
            "s":"DEFAULT-FROM-CC-APP"
         },
         "i":"53940653"
      }
   }
}"""
}
