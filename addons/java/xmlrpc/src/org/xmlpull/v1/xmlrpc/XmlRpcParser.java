/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license please see accompanying LICENSE.txt file (available also at http://www.xmlpull.org/)

package org.xmlpull.v1.xmlrpc;


import org.xmlpull.v1.*;
import java.io.*;


public class XmlRpcParser extends XmlRpcParserME {

    public XmlRpcParser(XmlPullParser parser) {
           super(parser);
       }

    protected Object parseType(String name) throws IOException, XmlPullParserException {
        if (name.equals("double"))
            return new Double(parser.nextText());
        else       
            return super.parseType(name);
    }

 
}