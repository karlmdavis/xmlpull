
//package org.xmlpull.v1.samples;

import java.io.*;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import org.xmlpull.v1.XmlSerializer;

/**
 * This sample demonstrates how to roundtrip XML document
 * (roundtrip is not exact but infoset level)
 */

public class Roundtrip {
    //private static final String FEATURE_XML_ROUNDTRIP=
    //    "http://xmlpull.org/v1/doc/features.html#xml-roundtrip";
    protected final static String PROPERTY_XMLDECL_STANDALONE =
        "http://xmlpull.org/v1/doc/features.html#xmldecl-standalone";

    XmlPullParser parser;
    XmlSerializer serializer;

    public Roundtrip (XmlPullParser parser, XmlSerializer serializer) {
        this.parser = parser;
        this.serializer = serializer;
    }

    public void writeStartTag () throws XmlPullParserException, IOException {
        //check forcase when feature xml roundtrip is supported
        //if (parser.getFeature (FEATURE_XML_ROUNDTRIP)) {
        //TODO: how to do pass through string with actual start tag in getText()
        //return;
        //}
        if (!parser.getFeature (parser.FEATURE_REPORT_NAMESPACE_ATTRIBUTES)) {
            for (int i = parser.getNamespaceCount (parser.getDepth ()-1);
                 i < parser.getNamespaceCount (parser.getDepth ())-1; i++) {
                serializer.setPrefix
                    (parser.getNamespacePrefix (i),
                     parser.getNamespaceUri (i));
            }
        }
        serializer.startTag(parser.getNamespace (), parser.getName ());

        for (int i = 0; i < parser.getAttributeCount (); i++) {
            serializer.attribute
                (parser.getAttributeNamespace (i),
                 parser.getAttributeName (i),
                 parser.getAttributeValue (i));
        }
        //serializer.closeStartTag();
    }


    public void writeToken (int eventType) throws XmlPullParserException, IOException {
        switch (eventType) {
            case XmlPullParser.START_DOCUMENT:
                //use Boolean.TRUE to make it standalone
                Boolean standalone = (Boolean) parser.getProperty(PROPERTY_XMLDECL_STANDALONE);
                serializer.startDocument(parser.getInputEncoding(), standalone);
                break;

            case XmlPullParser.END_DOCUMENT:
                serializer.endDocument();
                break;

            case XmlPullParser.START_TAG:
                writeStartTag ();
                break;

            case XmlPullParser.END_TAG:
                serializer.endTag(parser.getNamespace (), parser.getName ());
                break;

            case XmlPullParser.IGNORABLE_WHITESPACE:
                //comment it to remove ignorable whtespaces from XML infoset
                String s = parser.getText ();
                serializer.ignorableWhitespace (s);
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
        parser.nextToken(); // read first token
        writeToken (parser.START_DOCUMENT);  // write optional XMLDecl if present
        while (parser.getEventType () != parser.END_DOCUMENT) {
            writeToken ( parser.getEventType () );
            parser.nextToken ();
        }
        writeToken (parser.END_DOCUMENT);
    }

    public static void main(String[] args) throws Exception {
        //for (int i = 0; i < args.length; i++)
        for (int i = 0; i < 1; i++)
        {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser pp = factory.newPullParser();
            XmlSerializer serializer = factory.newSerializer();

            pp.setInput(new java.net.URL(args[ i ]).openStream(), null);
            serializer.setOutput( System.out, null);

            (new Roundtrip(pp, serializer)).roundTrip();
        }
    }

}



