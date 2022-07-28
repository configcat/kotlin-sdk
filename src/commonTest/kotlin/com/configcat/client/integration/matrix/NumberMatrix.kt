package com.configcat.client.integration.matrix

object NumberMatrix : DataMatrix {
    override val sdkKey = "PKDVCLf-Hq-h-kCzMp-L7Q/uGyK3q9_ckmdxRyI7vjwCw"
    override val data = """Identifier;Email;Country;Custom1;numberWithPercentage;number
##null##;;;;Default;Default
id1;;;0;<2.1;<>5
id1;;;0.0;<2.1;<>5
id1;;;0,0;<2.1;<>5
id1;;;0.2;<2.1;<>5
id2;;;0,2;<2.1;<>5
id3;;;1;<2.1;<>5
id4;;;1.0;<2.1;<>5
id5;;;1,0;<2.1;<>5
id6;;;1.5;<2.1;<>5
id7;;;1,5;<2.1;<>5
id8;;;2.1;<=2,1;<>5
id9;;;2,1;<=2,1;<>5
id10;;;3.50;=3.5;<>5
id11;;;3,50;=3.5;<>5
id12;;;5;>=5;Default
id13;;;5.0;>=5;Default
id14;;;5,0;>=5;Default
id13;;;5.76;>5;<>5
id14;;;5,76;>5;<>5
id15;;;4;<>4.2;<>5
id16;;;4.0;<>4.2;<>5
id17;;;4,0;<>4.2;<>5
id18;;;4.2;80%;<>5
id19;;;4,2;20%;<>5"""
    override val remoteJson =
        """{"p":{"u":"https://cdn-global.configcat.com","r":0},"f":{"numberWithPercentage":{"v":"Default","t":1,"i":"642bbb26","p":[{"o":0,"v":"80%","p":80,"i":"ad5f05a7"},{"o":1,"v":"20%","p":20,"i":"786b696f"}],"r":[{"o":0,"a":"Custom1","t":10,"c":"sajt","v":"=sajt","i":"216987c8"},{"o":1,"a":"Custom1","t":12,"c":"2.1","v":"<2.1","i":"a900bc23"},{"o":2,"a":"Custom1","t":13,"c":"2,1","v":"<=2,1","i":"2c85f73d"},{"o":3,"a":"Custom1","t":10,"c":"3.5","v":"=3.5","i":"ae86baf5"},{"o":4,"a":"Custom1","t":14,"c":"5","v":">5","i":"c6924001"},{"o":5,"a":"Custom1","t":15,"c":"5","v":">=5","i":"8090543a"},{"o":6,"a":"Custom1","t":11,"c":"4.2","v":"<>4.2","i":"2691fade"}]},"number":{"v":"Default","t":1,"i":"5ced27a9","p":[],"r":[{"o":0,"a":"Custom1","t":11,"c":"5","v":"<>5","i":"a41938c5"}]}}}"""
}
