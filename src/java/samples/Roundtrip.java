


package org.xmlpull.v1.samples;

import java.io.*;

import org.xmlpull.v1.*;
import org.xmlpull.v1.serializer.*;

/** WARNING: This sample is NOT part of the XmlPull API. This class is just
    contained to help evaluating the serializer interface, which is
    also NOT part of the XmlPull API (yet) */


public class Roundtrip {

    XmlPullParser parser;
    XmlSerializer serializer;

    public Roundtrip (XmlPullParser parser, XmlSerializer serializer) {
	this.parser = parser;
	this.serializer = serializer;
    }

    void writeStartTag () throws XmlPullParserException, IOException {
	if (!parser.getFeature (parser.FEATURE_REPORT_NAMESPACE_ATTRIBUTES)) {
	    for (int i = parser.getNamespaceCount (parser.getDepth ()-1);
		 i < parser.getNamespaceCount (parser.getDepth ())-1; i++) {
		serializer.setPrefix 
		    (parser.getNamespacePrefix (i),
		     parser.getNamespaceUri (i));
	    }
	}
	serializer.startTag 
	    (parser.getNamespace (), parser.getName (), false);
	
	for (int i = 0; i < parser.getAttributeCount (); i++) {
	    serializer.attribute 
		(parser.getAttributeNamespace (i),
		 parser.getAttributeName (i),
		 parser.getAttributeValue (i));
	} 
    }
    

    public void writeToken () throws XmlPullParserException, IOException {
	switch (parser.getEventType ()) {
        case XmlPullParser.START_DOCUMENT:
	case XmlPullParser.END_DOCUMENT:
	    break;

	case XmlPullParser.START_TAG:
	    writeStartTag ();
	    break;

	case XmlPullParser.END_TAG:
	    serializer.endTag 
		(parser.getNamespace (), parser.getName (), false);
	    break;

        case XmlPullParser.TEXT:
	    serializer.text (parser.getText ());
	    break;

	case XmlPullParser.ENTITY_REF:
	    serializer.legacy (serializer.ENTITY_REF, parser.getName ());
	    break;

	case XmlPullParser.CDSECT:
        case XmlPullParser.IGNORABLE_WHITESPACE:
	case XmlPullParser.PROCESSING_INSTRUCTION:
	case XmlPullParser.COMMENT:
	case XmlPullParser.DOCDECL:
	    serializer.legacy (serializer.ENTITY_REF, parser.getText ());
	    break;
	}
    }

    public void roundTrip () throws XmlPullParserException, IOException {
	do {
	    parser.nextToken ();
	    writeToken ();
	}
	while (parser.getEventType () != parser.END_DOCUMENT);
    }

	

}


