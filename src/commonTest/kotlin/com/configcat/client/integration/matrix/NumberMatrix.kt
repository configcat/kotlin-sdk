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
}