/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license see accompanying LICENSE_TESTS.txt file (available also at http://www.xmlpull.org)

package org.xmlpull.v1.tests;

//import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Tests checking miscellaneous features.
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 */
public class TestMisc extends UtilTestCase {
    private XmlPullParserFactory factory;

    public TestMisc(String name) {
        super(name);
    }

    protected void setUp() throws XmlPullParserException {
        factory = factoryNewInstance();
        factory.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        assertEquals(true, factory.getFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES));
    }

    protected void tearDown() {
    }

    public void testNextTag() throws Exception {
        final String INPUT_XML = "<t>  <test1>foo</test1>\n<test2>  </test2>\n </t>";
        XmlPullParser pp = factory.newPullParser();
        pp.setInput( new StringReader( INPUT_XML ) );
        pp.nextTag();
        pp.require( pp.START_TAG, null, "t");
        pp.nextTag();
        pp.require( pp.START_TAG, null, "test1");
        assertEquals( "foo", pp.nextText() );
        pp.require( pp.END_TAG, null, "test1");

        pp.nextTag();
        pp.require( pp.START_TAG, null, "test2");
        pp.nextTag();
        pp.require( pp.END_TAG, null, "test2");


        pp.nextTag();
        pp.require( pp.END_TAG, null, "t");
        pp.next();
        pp.require( pp.END_DOCUMENT, null, null);

    }

    //    public void testReadText() throws Exception {
    //        final String INPUT_XML = "<test>foo</test>";
    //        XmlPullParser pp = factory.newPullParser();
    //        pp.setInput( new StringReader( INPUT_XML ) );
    //        assertEquals( "", pp.readText() );
    //        pp.next();
    //        assertEquals( "", pp.readText() );
    //        pp.next();
    //        assertEquals( "foo", pp.readText() );
    //        assertEquals( pp.TYPES[ pp.END_TAG ], pp.TYPES[ pp.getEventType() ]);
    //    }

    public void testNextText() throws Exception {
        final String INPUT_XML =
            "<t><test1>foo</test1><test2></test2><test3/><test4>bar</test4></t>";
        XmlPullParser pp = factory.newPullParser();
        pp.setInput( new StringReader( INPUT_XML ) );
        pp.next();
        pp.require( pp.START_TAG, null, "t");
        pp.next();
        pp.require( pp.START_TAG, null, "test1");
        assertEquals( "foo", pp.nextText() );
        pp.require( pp.END_TAG, null, "test1");

        pp.next();
        pp.require( pp.START_TAG, null, "test2");
        assertEquals( "", pp.nextText() );
        pp.require( pp.END_TAG, null, "test2");

        pp.next();
        pp.require( pp.START_TAG, null, "test3");
        assertEquals( "", pp.nextText() );
        pp.require( pp.END_TAG, null, "test3");

        pp.next();
        pp.require( pp.START_TAG, null, "test4");
        //pp.next();
        //pp.require( pp.TEXT, null, null);
        assertEquals( "bar", pp.nextText() );
        pp.require( pp.END_TAG, null, "test4");

        pp.next();
        pp.require( pp.END_TAG, null, "t");
        pp.next();
        pp.require( pp.END_DOCUMENT, null, null);

        // now check for error conditions
        pp.setInput( new StringReader( INPUT_XML ) );
        pp.next();
        pp.require( pp.START_TAG, null, "t");
        pp.next();
        pp.require( pp.START_TAG, null, "test1");
        pp.next();
        pp.require( pp.TEXT, null, null);
        try {
            pp.nextText();
            fail("if current tag is TEXT no next text content can be returned!");
        } catch(XmlPullParserException ex) {}

        pp.setInput( new StringReader( INPUT_XML ) );
        pp.next();
        pp.require( pp.START_TAG, null, "t");
        try {
            pp.nextText();
            fail("if next tag is START_TAG no text content can be returned!");
        } catch(XmlPullParserException ex) {}

        pp.setInput( new StringReader( INPUT_XML ) );
        pp.next();
        pp.require( pp.START_TAG, null, "t");
        pp.next();
        pp.require( pp.START_TAG, null, "test1");
        pp.next();
        pp.next();
        pp.require( pp.END_TAG, null, "test1");
        try {
            pp.nextText();
            fail("if current tag is END_TAG no text content can be returned!");
        } catch(XmlPullParserException ex) {}

    }

    public void testRequire() throws Exception {
        //public void require (int type, String namespace, String name)
        final String INPUT_XML = "<test><t>foo</t><m:s xmlns:m='URI'>\t</m:s></test>";
        XmlPullParser pp = factory.newPullParser();
        pp.setInput( new StringReader( INPUT_XML ) );
        pp.require( pp.START_DOCUMENT, null, null);
        pp.next();
        pp.require( pp.START_TAG, null, "test");
        pp.require( pp.START_TAG, "", null);
        pp.require( pp.START_TAG, "", "test");
        pp.next();
        pp.require( pp.START_TAG, "", "t");
        pp.next();
        pp.require( pp.TEXT, null, null);
        pp.next();
        pp.require( pp.END_TAG, "", "t");

        pp.next();
        pp.require( pp.START_TAG, "URI", "s");

        pp.next();
        pp.require( pp.TEXT, null, null);
        assertEquals("\t", pp.getText());
        pp.next();
        pp.require( pp.END_TAG, "URI", "s");

        pp.next();
        pp.require( pp.END_TAG, "", "test");
        pp.next();
        pp.require( pp.END_DOCUMENT, null, null);

        //now check that require will NOT skip white space
        pp = factory.newPullParser();
        pp.setInput( new StringReader( "<m:s xmlns:m='URI'>\t</m:s>" ) );
        pp.require( pp.START_DOCUMENT, null, null);
        pp.next();
        pp.require( pp.START_TAG, "URI", "s");
        pp.next();
        try {
            pp.require( pp.END_TAG, "URI", "s");
            fail("require() MUST NOT skip white spaces");
        } catch(XmlPullParserException ex){}

    }

    public void testXmlDecl() throws Exception {
        XmlPullParser pp = factory.newPullParser();

        pp.setInput( new StringReader( "<?xml version='1.0'?><foo/>" ) );
        pp.require( pp.START_DOCUMENT, null, null);
        pp.next();
        pp.require( pp.START_TAG, null, "foo");
        pp.next();
        pp.require( pp.END_TAG, null, "foo");
        pp.next();
        pp.require( pp.END_DOCUMENT, null, null);

        pp.setInput( new StringReader(
                        "<?xml  version=\"1.0\" \t encoding='UTF-8' \nstandalone='yes' ?><foo/>" ));
        pp.require( pp.START_DOCUMENT, null, null);
        pp.next();
        pp.require( pp.START_TAG, null, "foo");

        pp.setInput( new StringReader(
                        "<?xml  version=\"1.0\" \t encoding='UTF-8' \nstandalone='yes' ?><foo/>" ));
        pp.require( pp.START_DOCUMENT, null, null);
        assertNull(pp.getProperty(PROPERTY_XMLDECL_VERSION));
        assertNull(pp.getProperty(PROPERTY_XMLDECL_STANDALONE));
        assertNull(pp.getProperty(PROPERTY_XMLDECL_CONTENT));

        pp.next();
        pp.require( pp.START_TAG, null, "foo");
        String xmlDeclVersion = (String) pp.getProperty(PROPERTY_XMLDECL_VERSION);
        if(xmlDeclVersion != null) {
            assertEquals("XMLDecl version","1.0", xmlDeclVersion);
            PackageTests.addNote("* optional property "+PROPERTY_XMLDECL_VERSION+" is supported\n");
        }
        Boolean xmlDeclStandalone = (Boolean) pp.getProperty(PROPERTY_XMLDECL_STANDALONE);
        if(xmlDeclStandalone != null) {
            assertTrue("XMLDecl standalone",xmlDeclStandalone.booleanValue());
            PackageTests.addNote("* optional property "+PROPERTY_XMLDECL_STANDALONE+" is supported\n");
        }
        String xmlDeclContent = (String) pp.getProperty(PROPERTY_XMLDECL_CONTENT);
        if(xmlDeclContent != null) {
            String expected = "  version=\"1.0\" \t encoding='UTF-8' \nstandalone='yes' ";
            assertEquals("XMLDecl content", printable(expected), printable(xmlDeclContent));
            PackageTests.addNote("* optional property "+PROPERTY_XMLDECL_CONTENT+" is supported\n");
        }


        // XML decl without required verion
        pp.setInput( new StringReader( "<?xml test?><foo/>" ) );
        pp.require( pp.START_DOCUMENT, null, null);

        // check that proerties are reset
        assertNull(pp.getProperty(PROPERTY_XMLDECL_VERSION));
        assertNull(pp.getProperty(PROPERTY_XMLDECL_STANDALONE));
        assertNull(pp.getProperty(PROPERTY_XMLDECL_CONTENT));

        try {
            pp.next();
            fail("expected exception for invalid XML declaration without version");
        } catch(XmlPullParserException ex){}


        pp.setInput( new StringReader(
                        "<?xml version=\"1.0\" standalone='yes' encoding=\"UTF-8\" ?>\n<foo/>" ));
        pp.require( pp.START_DOCUMENT, null, null);
        try {
            pp.next();
            fail("expected exception for invalid XML declaration with standalone at wrong position");
        } catch(XmlPullParserException ex){}


        pp.setInput( new StringReader(
                        "<?xml  version=\"1.0\" \t encoding='UTF-8' \nstandalone='yes' ?>"
                            +"<!--comment--><foo/>" ));
        pp.require( pp.START_DOCUMENT, null, null);
        // make sure that XMLDecl is not reported
        pp.nextToken();
        pp.require( pp.COMMENT, null, null);
        pp.nextToken();
        pp.require( pp.START_TAG, null, "foo");
        pp.next();
        pp.require( pp.END_TAG, null, "foo");
        pp.next();
        pp.require( pp.END_DOCUMENT, null, null);

    }

    public void testComments() throws Exception {
        XmlPullParser pp = factory.newPullParser();
        pp.setInput( new StringReader( "<foo><!-- B+, B or B---></foo>" ) );
        pp.require( pp.START_DOCUMENT, null, null);
        pp.next();
        pp.require( pp.START_TAG, null, "foo");
        try {
            pp.next();
            fail("expected eception for invalid comment ending with --->");
        } catch(XmlPullParserException ex){}
        testContentMerging(pp, "<foo><!-- this is a comment --></foo>", null);
        testContentMerging(pp, "<foo>\n<!-- this is a comment -->\n</foo>", "\n\n");
        testContentMerging(pp, "<foo><!-- this is a comment -->\n</foo>", "\n");
    }

    public void testContentMerging(final XmlPullParser pp, final String xml, final String expected)
        throws Exception {
        pp.setInput( new StringReader( xml ) );
        pp.next();
        pp.require( pp.START_TAG, null, "foo");
        if(expected != null) {
            pp.next();
            pp.require( pp.TEXT, null, null);
            assertEquals(printable(expected), printable(pp.getText()));
            assertEquals(expected, pp.getText());
        }
        pp.next();
        pp.require( pp.END_TAG, null, "foo");
    }


    public void testPI() throws Exception {
        XmlPullParser pp = factory.newPullParser();
        //System.out.println(getClass()+"-pp="+pp);

        pp.setInput( new StringReader( "<foo><?XmLsdsd test?></foo>" ) );
        pp.require( pp.START_DOCUMENT, null, null);
        pp.next();
        pp.require( pp.START_TAG, null, "foo");
        pp.next();
        pp.require( pp.END_TAG, null, "foo");

        pp.setInput( new StringReader( "<foo><?XmL test?></foo>" ) );
        pp.require( pp.START_DOCUMENT, null, null);
        pp.next();
        pp.require( pp.START_TAG, null, "foo");
        try {
            pp.next();
            fail("expected exception for invalid PI starting with xml");
        } catch(XmlPullParserException ex){}

        pp.setInput( new StringReader( "<foo><?pi test?></foo>" ) );
        pp.require( pp.START_DOCUMENT, null, null);
        pp.next();
        pp.require( pp.START_TAG, null, "foo");
        pp.nextToken();
        pp.require( pp.PROCESSING_INSTRUCTION, null, null);
        assertEquals("PI", "pi test", pp.getText());
        pp.next();
        pp.require( pp.END_TAG, null, "foo");

        testContentMerging(pp, "<foo><? this is PI ?></foo>", null);
        testContentMerging(pp, "<foo>\n<? this is PI ?>\n</foo>", "\n\n");
        testContentMerging(pp, "<foo><? this is PI ?>\n</foo>", "\n");

    }


    public void testReportNamespaceAttributes() throws Exception {
        XmlPullParser pp = factory.newPullParser();
        assertEquals(true, pp.getFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES));

        try {
            pp.setFeature(XmlPullParser.FEATURE_REPORT_NAMESPACE_ATTRIBUTES, true);
        }catch(XmlPullParserException ex) {
            // skip rest of test if parser does not support reporting
            return;
        }
        PackageTests.addNote("* optional feature  "+pp.FEATURE_REPORT_NAMESPACE_ATTRIBUTES+" is supported\n");
        // see XML Namespaces spec for namespace URIs for 'xml' and 'xmlns'
        //   xml is bound to http://www.w3.org/XML/1998/namespace
        //   "(...) The prefix xmlns is used only for namespace bindings
        //     and is not itself bound to any namespace name. (...)
        // however it is typically bound to "http://www.w3.org/2000/xmlns/"
        //   in some contexts such as DOM
        // http://www.w3.org/TR/REC-xml-names/#ns-using
        final String XML_MISC_ATTR =
            "<test xmlns='Some-Namespace-URI' xmlns:n='Some-Other-URI'"+
            " a='a' b='b' xmlns:m='Another-URI' m:a='c' n:b='d' n:x='e' xml:lang='en'"+
            "/>\n"+
            "";
        pp.setInput(new StringReader(XML_MISC_ATTR));
        pp.next();
        //pp.readStartTag(stag);
        assertEquals("test", pp.getName());
        assertEquals("Some-Namespace-URI", pp.getNamespace());

        assertEquals("a", pp.getAttributeValue("","a"));
        assertEquals("b", pp.getAttributeValue("","b"));
        assertEquals(null, pp.getAttributeValue("", "m:a"));
        assertEquals(null, pp.getAttributeValue("", "n:b"));
        assertEquals(null, pp.getAttributeValue("", "n:x"));

        assertEquals("c", pp.getAttributeValue("Another-URI", "a"));
        assertEquals("d", pp.getAttributeValue("Some-Other-URI", "b"));
        assertEquals("e", pp.getAttributeValue("Some-Other-URI", "x"));
        assertEquals("en", pp.getAttributeValue("http://www.w3.org/XML/1998/namespace", "lang"));


        checkAttribNs(pp, 0, null, "", "xmlns", "Some-Namespace-URI");
        checkAttribNs(pp, 1, "xmlns", "http://www.w3.org/2000/xmlns/","n","Some-Other-URI");
        checkAttribNs(pp, 2, null, "", "a", "a");
        checkAttribNs(pp, 3, null, "", "b", "b");
        checkAttribNs(pp, 4, "xmlns", "http://www.w3.org/2000/xmlns/","m","Another-URI");
        checkAttribNs(pp, 5, "m", "Another-URI","a","c");
        checkAttribNs(pp, 6, "n", "Some-Other-URI","b","d");
        checkAttribNs(pp, 7, "n", "Some-Other-URI","x","e");
        checkAttribNs(pp, 8, "xml", "http://www.w3.org/XML/1998/namespace", "lang", "en");
    }


    public void testRoundtripNext() throws Exception {
        // check that entiy reference is reported in start tag as unexpanded when roundtrip is enabled
        final String SAMPLE_XML =
            "<foo attEol='a\r\nv\n\ra\n\r\r\nio\rn\nica' attr=\"baz &amp; bar\"/>";

        XmlPullParser pp = factory.newPullParser();
        assertEquals(true, pp.getFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES));
        pp.setInput(new StringReader(SAMPLE_XML));

        try {
            pp.setFeature(FEATURE_XML_ROUNDTRIP, true);
        } catch(Exception ex) {
        }
        // did we succeeded?
        boolean roundtripSupported = pp.getFeature(FEATURE_XML_ROUNDTRIP);


        pp.next();
        String attrValue = pp.getAttributeValue(pp.NO_NAMESPACE, "attr");
        assertEquals("baz & bar", attrValue);
        String attEolValue = pp.getAttributeValue(pp.NO_NAMESPACE, "attEol");
        assertEquals("a v  a   io n ica", attEolValue);
        if(roundtripSupported) {
            String text = pp.getText();
            assertEquals(SAMPLE_XML, text);
        }

    }

    public static void main (String[] args) {
        junit.textui.TestRunner.run (new TestSuite(TestMisc.class));
    }

}
