
//package org.xmlpull.v1.samples;

import java.io.*;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import org.xmlpull.v1.serializer.XmlSerializer;

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

    public void writeStartTag () throws XmlPullParserException, IOException {
        if (!parser.getFeature (parser.FEATURE_REPORT_NAMESPACE_ATTRIBUTES)) {
            for (int i = parser.getNamespaceCount (parser.getDepth ()-1);
                 i < parser.getNamespaceCount (parser.getDepth ())-1; i++) {
                serializer.setPrefix
                    (parser.getNamespacePrefix (i),
                     parser.getNamespaceUri (i));
            }
        }
        serializer.startTag
            (parser.getNamespace (), parser.getName ());

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
                break;

            case XmlPullParser.END_DOCUMENT:
                serializer.flush();
                break;

            case XmlPullParser.START_TAG:
                writeStartTag ();
                break;

            case XmlPullParser.END_TAG:
                serializer.endTag
                    (parser.getNamespace (), parser.getName ());
                break;

            case XmlPullParser.IGNORABLE_WHITESPACE:
                //comment it to remove ignorable whtespaces from XML infoset
                serializer.ignorableWhitespace (parser.getText ());
                break;

            case XmlPullParser.TEXT:
                serializer.text (parser.getText ());
                break;

            case XmlPullParser.ENTITY_REF:
                serializer.entityRef (parser.getName ());
                break;

            case XmlPullParser.CDSECT:
                serializer.cdsect( parser.getText () );
                break;

            case XmlPullParser.PROCESSING_INSTRUCTION:
                serializer.processingInstruction( parser.getText ());
                break;

            case XmlPullParser.COMMENT:
                serializer.comment (parser.getText ());
                break;

            case XmlPullParser.DOCDECL:
                serializer.docdecl (parser.getText ());
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



