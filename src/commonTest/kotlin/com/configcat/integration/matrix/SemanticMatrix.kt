package com.configcat.integration.matrix

object SemanticMatrix : DataMatrix {
    override val sdkKey = "PKDVCLf-Hq-h-kCzMp-L7Q/BAr3KgLTP0ObzKnBTo5nhA"
    override val data =
        """Identifier;Email;Country;Custom1;isOneOf;isOneOfWithPercentage;isNotOneOf;isNotOneOfWithPercentage;lessThanWithPercentage;relations
##null##;;;;Default;Default;Default;Default;Default;Default
id1;;;0.0.0;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );< 1.0.0;< 1.0.0
id1;;;0.1.0;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );< 1.0.0;< 1.0.0
id1;;;0.2.1;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );< 1.0.0;< 1.0.0
id1;;;1;Default;80%;Default;80%;20%;Default
id2;;;1.0;Default;80%;Default;80%;80%;Default
id3;;;1.0.0;Is one of (1.0.0);is one of (1.0.0);Default;80%;80%;<=1.0.0
id4;;;1.0.0.0;Default;80%;Default;20%;20%;Default
id5;;;1.0.0.0.0;Default;80%;Default;80%;80%;Default
id6;;;1.0.1;Default;80%;Is not one of (1.0.0, 3.0.1);Is not one of (1.0.0, 3.0.1);80%;Default
id7;;;1.0.11;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );20%;Default
id8;;;1.0.111;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;Default
id9;;;1.0.2;Default;20%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;Default
id10;;;1.0.3;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;Default
id11;;;1.0.4;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;Default
id12;;;1.0.5;Default;20%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;Default
id13;;;1.1.0;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;Default
id14;;;1.1.1;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;Default
id15;;;1.1.2;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;Default
id16;;;1.1.3;Default;20%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );20%;Default
id17;;;1.1.4;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );20%;Default
id18;;;1.1.5;Default;20%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;Default
id19;;;1.9.0;Default;20%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;Default
id20;;;1.9.99;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );20%;Default
id21;;;2.0.0;Default;80%;Is not one of (1.0.0, 3.0.1);Is not one of (1.0.0, 3.0.1);20%;>=2.0.0
id22;;;2.0.1;Is one of (   , 2.0.1, 2.0.2,    );80%;Is not one of (1.0.0, 3.0.1);Is not one of (1.0.0, 3.0.1);80%;>2.0.0
id23;;;2.0.11;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );20%;>2.0.0
id24;;;2.0.2;Is one of (   , 2.0.1, 2.0.2,    );80%;Is not one of (1.0.0, 3.0.1);Is not one of (1.0.0, 3.0.1);80%;>2.0.0
id25;;;2.0.3;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;>2.0.0
id26;;;3.0.0;Is one of (3.0.0);80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;>2.0.0
id27;;;3.0.1;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );20%;>2.0.0
id28;;;3.1.0;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;>2.0.0
id28;;;3.1.1;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;>2.0.0
id29;;;5.0.0;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;>2.0.0
id30;;;5.99.999;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );20%;>2.0.0"""
    override val remoteJson =
        """{"p":{"u":"https://cdn-global.configcat.com","r":0},"f":{"isOneOf":{"v":"Default","t":1,"i":"c4ec4d53","p":[],"r":[{"o":0,"a":"Custom1","t":4,"c":"1.0.0, 2","v":"Is one of (1.0.0, 2)","i":"1e934047"},{"o":1,"a":"Custom1","t":4,"c":"1.0.0","v":"Is one of (1.0.0)","i":"44342254"},{"o":2,"a":"Custom1","t":4,"c":"   , 2.0.1, 2.0.2,    ","v":"Is one of (   , 2.0.1, 2.0.2,    )","i":"90e3ef46"},{"o":3,"a":"Custom1","t":4,"c":"3......","v":"Is one of (3......)","i":"59523971"},{"o":4,"a":"Custom1","t":4,"c":"3....","v":"Is one of (3...)","i":"2de217a1"},{"o":5,"a":"Custom1","t":4,"c":"3..0","v":"Is one of (3..0)","i":"bf943c79"},{"o":6,"a":"Custom1","t":4,"c":"3.0","v":"Is one of (3.0)","i":"3a6a8077"},{"o":7,"a":"Custom1","t":4,"c":"3.0.","v":"Is one of (3.0.)","i":"44f25fed"},{"o":8,"a":"Custom1","t":4,"c":"3.0.0","v":"Is one of (3.0.0)","i":"e77f5306"}]},"isOneOfWithPercentage":{"v":"Default","t":1,"i":"a94ff896","p":[{"o":0,"v":"20%","p":20,"i":"e25dba31"},{"o":1,"v":"80%","p":80,"i":"8c70c181"}],"r":[{"o":0,"a":"Custom1","t":4,"c":"1.0.0","v":"is one of (1.0.0)","i":"0ac4afc1"}]},"isNotOneOf":{"v":"Default","t":1,"i":"f79b763d","p":[],"r":[{"o":0,"a":"Custom1","t":5,"c":"1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    ","v":"Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    )","i":"a8d5f278"},{"o":1,"a":"Custom1","t":5,"c":"1.0.0, 3.0.1","v":"Is not one of (1.0.0, 3.0.1)","i":"54ac757f"}]},"isNotOneOfWithPercentage":{"v":"Default","t":1,"i":"b9614bad","p":[{"o":0,"v":"20%","p":20,"i":"68f652f0"},{"o":1,"v":"80%","p":80,"i":"b8d926e0"}],"r":[{"o":0,"a":"Custom1","t":5,"c":"1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    ","v":"Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    )","i":"9bf9e66f"},{"o":1,"a":"Custom1","t":5,"c":"1.0.0, 3.0.1","v":"Is not one of (1.0.0, 3.0.1)","i":"bfc1a544"}]},"lessThanWithPercentage":{"v":"Default","t":1,"i":"0081c525","p":[{"o":0,"v":"20%","p":20,"i":"3b1fde2a"},{"o":1,"v":"80%","p":80,"i":"42e92759"}],"r":[{"o":0,"a":"Custom1","t":6,"c":" 1.0.0 ","v":"< 1.0.0","i":"0c27d053"}]},"relations":{"v":"Default","t":1,"i":"c6155773","p":[],"r":[{"o":0,"a":"Custom1","t":6,"c":"1.0.0,","v":"<1.0.0,","i":"21b31b61"},{"o":1,"a":"Custom1","t":6,"c":"1.0.0","v":"< 1.0.0","i":"db3ddb7d"},{"o":2,"a":"Custom1","t":7,"c":"1.0.0","v":"<=1.0.0","i":"aa2c7493"},{"o":3,"a":"Custom1","t":8,"c":"2.0.0","v":">2.0.0","i":"5e47a1ea"},{"o":4,"a":"Custom1","t":9,"c":"2.0.0","v":">=2.0.0","i":"99482756"}]}}}"""
}
