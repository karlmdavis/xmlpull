/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license see accompanying LICENSE_TESTS.txt file (available also at http://www.xmlpull.org)

package org.xmlpull.v1.tests;

//import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.StringReader;
import java.io.StringWriter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Simple test for minimal XML tokenizing
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 */
public class TestSimpleToken extends UtilTestCase {
    private XmlPullParserFactory factory;

    private static final String FEATURE_XML_ROUNDTRIP=
        "http://xmlpull.org/v1/doc/features.html#xml-roundtrip";
    private static final String MISC_XML =
        "\n \r\n \n\r<!DOCTYPE titlepage SYSTEM \"http://www.foo.bar/dtds/typo.dtd\""+
        "[<!ENTITY % active.links \"INCLUDE\">"+
        "  <!ENTITY   test \"This is test! Do NOT Panic!\" >"+
        "]>"+
        "<!--c-->  \r\n<foo attrName='attrVal'>bar<!--comment-->"+
        "&test;&test;&lt;&#32;"+
        "&amp;&gt;&apos;&quot;&#x20;"+
        "<?pi ds?><![CDATA[ vo<o ]]></foo> \r\n";

    public TestSimpleToken(String name) {
        super(name);
    }

    protected void setUp() throws XmlPullParserException {
        factory = factoryNewInstance();
        factory.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        assertEquals(true, factory.getFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES));
    }

    protected void tearDown() {
    }


    public void testSimpleToken() throws Exception {
        XmlPullParser xpp = factory.newPullParser();
        assertEquals(true, xpp.getFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES));

        // check setInput semantics
        assertEquals(XmlPullParser.START_DOCUMENT, xpp.getEventType());
        try {
            xpp.nextToken();
            fail("exception was expected of nextToken() if no input was set on parser");
        } catch(XmlPullParserException ex) {}

        xpp.setInput(null);
        assertEquals(XmlPullParser.START_DOCUMENT, xpp.getEventType());
        try {
            xpp.nextToken();
            fail("exception was expected of next() if no input was set on parser");
        } catch(XmlPullParserException ex) {}

        xpp.setInput(null); //reset parser
        // attempt to set roundtrip
        try {
            xpp.setFeature(FEATURE_XML_ROUNDTRIP, true);
        } catch(Exception ex) {
        }
        // did we succeeded?
        boolean roundtripSupported = xpp.getFeature(FEATURE_XML_ROUNDTRIP);


        // check the simplest possible XML document - just one root element
        for(int i = 1; i <= 2; ++i) {
            xpp.setInput(new StringReader(i == 1 ? "<foo/>" : "<foo></foo>"));
            boolean empty = (i == 1);
            checkParserStateNs(xpp, 0, xpp.START_DOCUMENT, null, 0, null, null, null, false, -1);
            xpp.nextToken();
            checkParserStateNs(xpp, 1, xpp.START_TAG, null, 0, "", "foo", null, empty, 0);
            if(roundtripSupported) {
                if(empty) {
                    //              System.out.println("tag='"+xpp.getText()+"'");
                    //              String foo ="<foo/>";
                    //              String foo2 = xpp.getText();
                    //              System.out.println(foo.equals(foo2));
                    assertEquals("empty tag roundtrip",
                                 printable("<foo/>"),
                                 printable(xpp.getText()));
                } else {
                    assertEquals("start tag roundtrip",
                                 printable("<foo>"),
                                 printable(xpp.getText()));
                }
            }
            xpp.nextToken();
            checkParserStateNs(xpp, 0, xpp.END_TAG, null, 0, "", "foo", null, false, -1);
            if(roundtripSupported) {
                if(empty) {
                    assertEquals("empty tag roundtrip",
                                 printable("<foo/>"),
                                 printable(xpp.getText()));
                } else {
                    assertEquals("end tag roundtrip",
                                 printable("</foo>"),
                                 printable(xpp.getText()));
                }
            }
            xpp.nextToken();
            checkParserStateNs(xpp, 0, xpp.END_DOCUMENT, null, 0, null, null, null, false, -1);
        }
    }

    public void testTokenEventEquivalency() throws Exception {
        XmlPullParser xpp = factory.newPullParser();
        assertEquals(true, xpp.getFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES));
        xpp.setInput(new StringReader(MISC_XML));
        boolean processDocdecl = xpp.getFeature(xpp.FEATURE_PROCESS_DOCDECL);

        // make sure entity "test" can be resolved even when parser is not parsing DOCDECL
        if(!processDocdecl) {
            xpp.defineEntityReplacementText("test", "This is test! Do NOT Panic!");
        }

        checkParserStateNs(xpp, 0, xpp.START_DOCUMENT, null, 0, null, null, null, false, -1);
        xpp.next();
        checkParserStateNs(xpp, 1, xpp.START_TAG, null, 0, "", "foo", null, false, 1);
        checkAttribNs(xpp, 0, null, "", "attrName", "attrVal");
        xpp.next();
        checkParserStateNs(xpp, 1, xpp.TEXT, null, 0, null, null,
                           "barThis is test! Do NOT Panic!This is test! Do NOT Panic!< &>'\"  vo<o ",
                           false, -1);
        xpp.next();
        checkParserStateNs(xpp, 0, xpp.END_TAG, null, 0, "", "foo", null, false, -1);
        xpp.next();
        checkParserStateNs(xpp, 0, xpp.END_DOCUMENT, null, 0, null, null, null, false, -1);

    }

    // one step further - it has content ...
    public void testTokenTypes() throws Exception {
        XmlPullParser xpp = factory.newPullParser();
        assertEquals(true, xpp.getFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES));

        xpp.setInput(new StringReader(MISC_XML));
        // attempt to set roundtrip
        try {
            xpp.setFeature(FEATURE_XML_ROUNDTRIP, true);
        } catch(Exception ex) {
        }
        // did we succeeded?
        boolean roundtripSupported = xpp.getFeature(FEATURE_XML_ROUNDTRIP);

        checkParserStateNs(xpp, 0, xpp.START_DOCUMENT, null, 0, null, null, null, false, -1);
        try {
            xpp.isWhitespace();
            fail("whitespace function must fail for START_DOCUMENT");
        } catch(XmlPullParserException ex) {
        }

        xpp.nextToken();
        checkParserStateNs(xpp, 0, xpp.IGNORABLE_WHITESPACE, null, 0, null, null,
                           "\n \r\n \n\r", false, -1);
        assertTrue(xpp.isWhitespace());

        xpp.nextToken();
        checkParserStateNs(xpp, 0, xpp.DOCDECL, null, 0, null, null,
                           " titlepage SYSTEM \"http://www.foo.bar/dtds/typo.dtd\""+
                               "[<!ENTITY % active.links \"INCLUDE\">"+
                               "  <!ENTITY   test \"This is test! Do NOT Panic!\" >]", false, -1);
        try {
            xpp.isWhitespace();
            fail("whitespace function must fail for START_DOCUMENT");
        } catch(XmlPullParserException ex) {
        }

        xpp.nextToken();
        checkParserStateNs(xpp, 0, xpp.COMMENT, null, 0, null, null, "c", false, -1);
        try {
            xpp.isWhitespace();
            fail("whitespace function must fail for START_DOCUMENT");
        } catch(XmlPullParserException ex) {
        }

        xpp.nextToken();
        checkParserStateNs(xpp, 0, xpp.IGNORABLE_WHITESPACE, null, 0, null, null, "  \r\n", false, -1);
        assertTrue(xpp.isWhitespace());

        xpp.nextToken();
        checkParserStateNs(xpp, 1, xpp.START_TAG, null, 0, "", "foo", null, false, 1);
        if(roundtripSupported) {
            assertEquals("start tag roundtrip", "<foo attrName='attrVal'>", xpp.getText());
        }
        checkAttribNs(xpp, 0, null, "", "attrName", "attrVal");
        try {
            xpp.isWhitespace();
            fail("whitespace function must fail for START_DOCUMENT");
        } catch(XmlPullParserException ex) {
        }

        xpp.nextToken();
        checkParserStateNs(xpp, 1, xpp.TEXT, null, 0, null, null, "bar", false, -1);
        assertEquals(false, xpp.isWhitespace());

        xpp.nextToken();
        checkParserStateNs(xpp, 1, xpp.COMMENT, null, 0, null, null, "comment", false, -1);
        try {
            xpp.isWhitespace();
            fail("whitespace function must fail for START_DOCUMENT");
        } catch(XmlPullParserException ex) {
        }

        boolean processDocdecl = xpp.getFeature(xpp.FEATURE_PROCESS_DOCDECL);

        // uresolved entity must be reurned as null by nextToken()
        xpp.nextToken();
        if(!processDocdecl) {
            checkParserStateNs(xpp, 1, xpp.ENTITY_REF, null, 0, null,
                               "test", null, false, -1);
        } else {
            checkParserStateNs(xpp, 1, xpp.ENTITY_REF, null, 0, null,
                               "test", "This is test! Do NOT Panic!", false, -1);

        }

        // now we check if we can resolve entity
        if(!processDocdecl) {
            xpp.defineEntityReplacementText("test", "This is test! Do NOT Panic!");
        }
        xpp.nextToken();
        checkParserStateNs(xpp, 1, xpp.ENTITY_REF, null, 0, null,
                           "test", "This is test! Do NOT Panic!", false, -1);
        try {
            xpp.isWhitespace();
            fail("whitespace function must fail for ENTITY_RED");
        } catch(XmlPullParserException ex) {
        }

        // check standard entities and char refs
        xpp.nextToken();
        checkParserStateNs(xpp, 1, xpp.ENTITY_REF, null, 0, null, "lt", "<", false, -1);
        try {
            xpp.isWhitespace();
            fail("whitespace function must fail for ENTITY_REF");
        } catch(XmlPullParserException ex) {
        }

        xpp.nextToken();
        checkParserStateNs(xpp, 1, xpp.ENTITY_REF, null, 0, null, "#32", " ", false, -1);
        try {
            xpp.isWhitespace();
            fail("whitespace function must fail for ENTITY_REF");
        } catch(XmlPullParserException ex) {
        }

        xpp.nextToken();
        checkParserStateNs(xpp, 1, xpp.ENTITY_REF, null, 0, null, "amp", "&", false, -1);

        xpp.nextToken();
        checkParserStateNs(xpp, 1, xpp.ENTITY_REF, null, 0, null, "gt", ">", false, -1);

        xpp.nextToken();
        checkParserStateNs(xpp, 1, xpp.ENTITY_REF, null, 0, null, "apos", "'", false, -1);

        xpp.nextToken();
        checkParserStateNs(xpp, 1, xpp.ENTITY_REF, null, 0, null, "quot", "\"", false, -1);

        xpp.nextToken();
        checkParserStateNs(xpp, 1, xpp.ENTITY_REF, null, 0, null, "#x20", " ", false, -1);

        xpp.nextToken();
        checkParserStateNs(xpp, 1, xpp.PROCESSING_INSTRUCTION, null, 0, null, null, "pi ds", false, -1);
        try {
            xpp.isWhitespace();
            fail("whitespace function must fail for START_DOCUMENT");
        } catch(XmlPullParserException ex) {
        }

        xpp.nextToken();
        checkParserStateNs(xpp, 1, xpp.CDSECT, null, 0, null, null, " vo<o ", false, -1);
        assertEquals(false, xpp.isWhitespace());

        xpp.nextToken();
        checkParserStateNs(xpp, 0, xpp.END_TAG, null, 0, "", "foo", null, false, -1);
        if(roundtripSupported) {
            assertEquals("end tag roundtrip", "</foo>", xpp.getText());
        }
        try {
            xpp.isWhitespace();
            fail("whitespace function must fail for START_DOCUMENT");
        } catch(XmlPullParserException ex) {
        }

        xpp.nextToken();
        checkParserStateNs(xpp, 0, xpp.IGNORABLE_WHITESPACE, null, 0, null, null,
                           " \r\n", false, -1);
        assertTrue(xpp.isWhitespace());

        xpp.nextToken();
        checkParserStateNs(xpp, 0, xpp.END_DOCUMENT, null, 0, null, null, null, false, -1);
        try {
            xpp.isWhitespace();
            fail("whitespace function must fail for START_DOCUMENT");
        } catch(XmlPullParserException ex) {
        }

    }

    // one step further - it has content ...
    public void testXmlRoundtrip() throws Exception {
        XmlPullParser xpp = factory.newPullParser();
        assertEquals(true, xpp.getFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES));


        // reset parser
        xpp.setInput(null);
        // attempt to set roundtrip
        try {
            xpp.setFeature(FEATURE_XML_ROUNDTRIP, true);
        } catch(Exception ex) {
        }
        // did we succeeded?
        boolean roundtripSupported = xpp.getFeature(FEATURE_XML_ROUNDTRIP);

        if(!roundtripSupported) {
            return;
        }
        PackageTests.addNote("* optional feature "+FEATURE_XML_ROUNDTRIP+" is supported\n");

        StringWriter sw = new StringWriter();
        String s;
        //StringWriter st = new StringWriter();
        xpp.setInput(new StringReader(MISC_XML));
        int[] holderForStartAndLength = new int[2];
        char[] buf;
        while(xpp.nextToken() != xpp.END_DOCUMENT) {
            switch(xpp.getEventType()) {
                case XmlPullParser.START_TAG:
                    buf = xpp.getTextCharacters(holderForStartAndLength);
                    s = new String(buf, holderForStartAndLength[0], holderForStartAndLength[1]);
                    assertEquals("roundtrip START_TAG", xpp.getText(), s);
                    sw.write(s);
                    break;
                case XmlPullParser.END_TAG:
                    buf = xpp.getTextCharacters(holderForStartAndLength);
                    s = new String(buf, holderForStartAndLength[0], holderForStartAndLength[1]);
                    assertEquals("roundtrip END_TAG", xpp.getText(), s);
                    sw.write(s);
                    break;
                case XmlPullParser.TEXT:
                    buf = xpp.getTextCharacters(holderForStartAndLength);
                    s = new String(buf, holderForStartAndLength[0], holderForStartAndLength[1]);
                    assertEquals("roundtrip TEXT", xpp.getText(), s);
                    sw.write(s);
                    break;
                case XmlPullParser.IGNORABLE_WHITESPACE:
                    buf = xpp.getTextCharacters(holderForStartAndLength);
                    s = new String(buf, holderForStartAndLength[0], holderForStartAndLength[1]);
                    assertEquals("roundtrip IGNORABLE_WHITESPACE", xpp.getText(), s);
                    sw.write(s);
                    break;
                case XmlPullParser.CDSECT:
                    sw.write("<![CDATA[");
                    buf = xpp.getTextCharacters(holderForStartAndLength);
                    s = new String(buf, holderForStartAndLength[0], holderForStartAndLength[1]);
                    assertEquals("roundtrip CDSECT", xpp.getText(), s);
                    sw.write(s);
                    sw.write("]]>");
                    break;
                case XmlPullParser.PROCESSING_INSTRUCTION:
                    sw.write("<?");
                    buf = xpp.getTextCharacters(holderForStartAndLength);
                    s = new String(buf, holderForStartAndLength[0], holderForStartAndLength[1]);
                    assertEquals("roundtrip PROCESSING_INSTRUCTION", xpp.getText(), s);
                    sw.write(s);
                    sw.write("?>");
                    break;
                case XmlPullParser.COMMENT:
                    sw.write("<!--");
                    buf = xpp.getTextCharacters(holderForStartAndLength);
                    s = new String(buf, holderForStartAndLength[0], holderForStartAndLength[1]);
                    assertEquals("roundtrip COMMENT", xpp.getText(), s);
                    sw.write(s);
                    sw.write("-->");
                    break;
                case XmlPullParser.ENTITY_REF:
                    sw.write("&");
                    buf = xpp.getTextCharacters(holderForStartAndLength);
                    s = new String(buf, holderForStartAndLength[0], holderForStartAndLength[1]);
                    assertEquals("roundtrip ENTITY_REF", xpp.getName(), s);
                    sw.write(s);
                    sw.write(";");
                    break;
                case XmlPullParser.DOCDECL:
                    sw.write("<!DOCTYPE");
                    buf = xpp.getTextCharacters(holderForStartAndLength);
                    s = new String(buf, holderForStartAndLength[0], holderForStartAndLength[1]);
                    assertEquals("roundtrip DOCDECL", xpp.getText(), s);
                    sw.write(s);
                    sw.write(">");
                    break;
                default:
                    throw new RuntimeException("unknown token type");
            }
        }
        sw.close();
        String RESULT_XML_BUF = sw.toString();
        assertEquals("rountrip XML", printable(MISC_XML), printable(RESULT_XML_BUF));
    }


    public static void main (String[] args) {
        junit.textui.TestRunner.run (new TestSuite(TestSimpleToken.class));
    }

}

