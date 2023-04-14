package com.configcat.integration.matrix

object SemanticMatrix2 : DataMatrix {
    override val sdkKey = "PKDVCLf-Hq-h-kCzMp-L7Q/q6jMCFIp-EmuAfnmZhPY7w"
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
        """{"p":{"u":"https://cdn-global.configcat.com","r":0},"f":{"precedenceTests":{"v":"DEFAULT-FROM-CC-APP","t":1,"i":"53940653","p":[],"r":[{"o":0,"a":"AppVersion","t":6,"c":"1.9.1-2","v":"< 1.9.1-2","i":"92a04969"},{"o":1,"a":"AppVersion","t":6,"c":"1.9.1-10","v":"< 1.9.1-10","i":"c651eba2"},{"o":2,"a":"AppVersion","t":6,"c":"1.9.1-10a","v":"< 1.9.1-10a","i":"237dedc5"},{"o":3,"a":"AppVersion","t":6,"c":"1.9.1-1a","v":"< 1.9.1-1a","i":"154a319b"},{"o":4,"a":"AppVersion","t":6,"c":"1.9.1-alpha","v":"< 1.9.1-alpha","i":"33f59c5e"},{"o":5,"a":"AppVersion","t":6,"c":"1.9.99-alpha","v":"< 1.9.99-alpha","i":"9b6c24f1"},{"o":6,"a":"AppVersion","t":4,"c":"1.9.99-alpha","v":"= 1.9.99-alpha","i":"c08a99de"},{"o":7,"a":"AppVersion","t":6,"c":"1.9.99-beta","v":"< 1.9.99-beta","i":"4c9d7eb1"},{"o":8,"a":"AppVersion","t":6,"c":"1.9.99-rc","v":"< 1.9.99-rc","i":"e5aa7655"},{"o":9,"a":"AppVersion","t":6,"c":"1.9.99-rc.1","v":"< 1.9.99-rc.1","i":"c9075e5b"},{"o":10,"a":"AppVersion","t":6,"c":"1.9.99-rc.2","v":"< 1.9.99-rc.2","i":"97465d24"},{"o":11,"a":"AppVersion","t":6,"c":"1.9.99-rc.20","v":"< 1.9.99-rc.20","i":"32d20254"},{"o":12,"a":"AppVersion","t":6,"c":"1.9.99-rc.20a","v":"< 1.9.99-rc.20a","i":"c4843bfb"},{"o":13,"a":"AppVersion","t":6,"c":"1.9.99-rc.2a","v":"< 1.9.99-rc.2a","i":"11b96c5a"},{"o":14,"a":"AppVersion","t":6,"c":"1.9.99","v":"< 1.9.99","i":"dc5a0ed1"},{"o":15,"a":"AppVersion","t":6,"c":"1.9.100","v":"< 1.9.100","i":"8ce0bff8"},{"o":16,"a":"AppVersion","t":6,"c":"1.10.0-alpha","v":"< 1.10.0-alpha","i":"9ff0cadc"},{"o":17,"a":"AppVersion","t":7,"c":"1.10.0-alpha","v":"<= 1.10.0-alpha","i":"7a24a0f6"},{"o":18,"a":"AppVersion","t":6,"c":"1.10.0","v":"< 1.10.0","i":"03a85e10"},{"o":19,"a":"AppVersion","t":7,"c":"1.10.0","v":"<= 1.10.0","i":"b37d5427"},{"o":20,"a":"AppVersion","t":7,"c":"1.10.1","v":"<= 1.10.1","i":"b402f112"},{"o":21,"a":"AppVersion","t":7,"c":"1.10.3","v":"<= 1.10.3","i":"da563c51"},{"o":22,"a":"AppVersion","t":6,"c":"2.0.0","v":"< 2.0.0","i":"c64645a1"},{"o":23,"a":"AppVersion","t":4,"c":"2.0.0","v":"= 2.0.0","i":"b0008e97"},{"o":24,"a":"AppVersion","t":4,"c":"3.0.0+build3","v":"= 3.0.0+build3","i":"67ceff4e"},{"o":25,"a":"AppVersion","t":4,"c":"4.0.0+001","v":"= 4.0.0+001","i":"da6dd7ab"},{"o":26,"a":"AppVersion","t":4,"c":"5.0.0+20130313144700","v":"= 5.0.0+20130313144700","i":"673b3fd5"},{"o":27,"a":"AppVersion","t":4,"c":"6.0.0+exp.sha.5114f85","v":"= 6.0.0+exp.sha.5114f85","i":"e3bcafe6"},{"o":28,"a":"AppVersion","t":4,"c":"7.0.0-patch","v":"= 7.0.0-patch","i":"04e2949b"},{"o":29,"a":"AppVersion","t":4,"c":"8.0.0-patch+anothermetadata","v":"= 8.0.0-patch+anothermetadata","i":"505e8efa"},{"o":30,"a":"AppVersion","t":4,"c":"9.0.0-patch+metadata","v":"= 9.0.0-patch+metadata","i":"ca4c9dcc"},{"o":31,"a":"AppVersion","t":8,"c":"103.0.0","v":"> 103.0.0","i":"9428e733"},{"o":32,"a":"AppVersion","t":9,"c":"103.0.0","v":">= 103.0.0","i":"c448abb8"},{"o":33,"a":"AppVersion","t":9,"c":"101.0.0","v":">= 101.0.0","i":"9980c03a"},{"o":34,"a":"AppVersion","t":8,"c":"90.103.0","v":"> 90.103.0","i":"04259f0b"},{"o":35,"a":"AppVersion","t":9,"c":"90.103.0","v":">= 90.103.0","i":"4817782c"},{"o":36,"a":"AppVersion","t":9,"c":"90.101.0","v":">= 90.101.0","i":"2e9be278"},{"o":37,"a":"AppVersion","t":8,"c":"80.0.103","v":"> 80.0.103","i":"d7058d3e"},{"o":38,"a":"AppVersion","t":9,"c":"80.0.103","v":">= 80.0.103","i":"0da87e6b"},{"o":39,"a":"AppVersion","t":9,"c":"80.0.101","v":">= 80.0.101","i":"8e71aa24"},{"o":40,"a":"AppVersion","t":9,"c":"73.0.0-beta.2","v":">= 73.0.0-beta.2","i":"26a443e3"},{"o":41,"a":"AppVersion","t":8,"c":"72.0.0-beta.2","v":"> 72.0.0-beta.2","i":"0705710a"},{"o":42,"a":"AppVersion","t":8,"c":"72.0.0-beta.1","v":"> 72.0.0-beta.1","i":"7d6cf793"},{"o":43,"a":"AppVersion","t":8,"c":"72.0.0-beta","v":"> 72.0.0-beta","i":"f9ef6e83"},{"o":44,"a":"AppVersion","t":8,"c":"72.0.0-alpha","v":"> 72.0.0-alpha","i":"cf17c939"},{"o":45,"a":"AppVersion","t":8,"c":"72.0.0-1a","v":"> 72.0.0-1a","i":"650640fd"},{"o":46,"a":"AppVersion","t":8,"c":"72.0.0-10a","v":"> 72.0.0-10a","i":"508dd0b2"},{"o":47,"a":"AppVersion","t":8,"c":"72.0.0-2","v":"> 72.0.0-2","i":"142e6d61"},{"o":48,"a":"AppVersion","t":8,"c":"72.0.0-1","v":"> 72.0.0-1","i":"d969006a"},{"o":49,"a":"AppVersion","t":9,"c":"71.0.0+anothermetadata","v":">= 71.0.0+anothermetadata","i":"6f74dc87"},{"o":50,"a":"AppVersion","t":9,"c":"71.0.0-patch3+anothermetadata","v":">= 71.0.0-patch3+anothermetadata","i":"8061734b"},{"o":51,"a":"AppVersion","t":9,"c":"71.0.0-patch2","v":">= 71.0.0-patch2","i":"0615c726"},{"o":52,"a":"AppVersion","t":9,"c":"71.0.0-patch1+metadata","v":">= 71.0.0-patch1+metadata","i":"910b79b5"},{"o":53,"a":"AppVersion","t":9,"c":"60.73.0-beta.2","v":">= 60.73.0-beta.2","i":"32e2a4ea"},{"o":54,"a":"AppVersion","t":8,"c":"60.72.0-beta.2","v":"> 60.72.0-beta.2","i":"9017539e"},{"o":55,"a":"AppVersion","t":8,"c":"60.72.0-beta.1","v":"> 60.72.0-beta.1","i":"74de4704"},{"o":56,"a":"AppVersion","t":8,"c":"60.72.0-beta","v":"> 60.72.0-beta","i":"b61af046"},{"o":57,"a":"AppVersion","t":8,"c":"60.72.0-alpha","v":"> 60.72.0-alpha","i":"419eb18d"},{"o":58,"a":"AppVersion","t":8,"c":"60.72.0-1a","v":"> 60.72.0-1a","i":"7574c707"},{"o":59,"a":"AppVersion","t":8,"c":"60.72.0-10a","v":"> 60.72.0-10a","i":"5b3949e6"},{"o":60,"a":"AppVersion","t":8,"c":"60.72.0-2","v":"> 60.72.0-2","i":"9ff17692"},{"o":61,"a":"AppVersion","t":8,"c":"60.72.0-1","v":"> 60.72.0-1","i":"3027451d"},{"o":62,"a":"AppVersion","t":9,"c":"60.71.0+anothermetadata","v":">= 60.71.0+anothermetadata","i":"613d3642"},{"o":63,"a":"AppVersion","t":9,"c":"60.71.0-patch3+anothermetadata","v":">= 60.71.0-patch3+anothermetadata","i":"e45ffb06"},{"o":64,"a":"AppVersion","t":9,"c":"60.71.0-patch2","v":">= 60.71.0-patch2","i":"db50de0a"},{"o":65,"a":"AppVersion","t":9,"c":"60.71.0-patch1+metadata","v":">= 60.71.0-patch1+metadata","i":"5f9acaf7"},{"o":66,"a":"AppVersion","t":9,"c":"50.60.73-beta.2","v":">= 50.60.73-beta.2","i":"701ac6b2"},{"o":67,"a":"AppVersion","t":8,"c":"50.60.72-beta.2","v":"> 50.60.72-beta.2","i":"da09daf8"},{"o":68,"a":"AppVersion","t":8,"c":"50.60.72-beta.1","v":"> 50.60.72-beta.1","i":"8f7e54d5"},{"o":69,"a":"AppVersion","t":8,"c":"50.60.72-beta","v":"> 50.60.72-beta","i":"93e245a5"},{"o":70,"a":"AppVersion","t":8,"c":"50.60.72-alpha","v":"> 50.60.72-alpha","i":"356c8279"},{"o":71,"a":"AppVersion","t":8,"c":"50.60.72-1a","v":"> 50.60.72-1a","i":"6131df16"},{"o":72,"a":"AppVersion","t":8,"c":"50.60.72-10a","v":"> 50.60.72-10a","i":"3f1a3aa4"},{"o":73,"a":"AppVersion","t":8,"c":"50.60.72-2","v":"> 50.60.72-2","i":"7534cc57"},{"o":74,"a":"AppVersion","t":8,"c":"50.60.72-1","v":"> 50.60.72-1","i":"24d6f0ab"},{"o":75,"a":"AppVersion","t":9,"c":"50.60.71+anothermetadata","v":">= 50.60.71+anothermetadata","i":"fdd36d82"},{"o":76,"a":"AppVersion","t":9,"c":"50.60.71-patch3+anothermetadata","v":">= 50.60.71-patch3+anothermetadata","i":"709780e6"},{"o":77,"a":"AppVersion","t":9,"c":"50.60.71-patch2","v":">= 50.60.71-patch2","i":"7649322d"},{"o":78,"a":"AppVersion","t":9,"c":"50.60.71-patch1+metadata","v":">= 50.60.71-patch1+metadata","i":"25d5c70d"},{"o":79,"a":"AppVersion","t":9,"c":"40.0.0-patch","v":">= 40.0.0-patch","i":"271370ff"},{"o":80,"a":"AppVersion","t":9,"c":"30.0.0-alpha","v":">= 30.0.0-alpha","i":"af29c39d"}]}}}"""
}