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


    public void testReportNamespaceAttributes() throws Exception {
        XmlPullParser pp = factory.newPullParser();
        assertEquals(true, pp.getFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES));

        try {
	    pp.setFeature(XmlPullParser.FEATURE_REPORT_NAMESPACE_ATTRIBUTES, true);
        }catch(XmlPullParserException ex) {
	    // skip rest of test if parser does nto support reporting
	    return;
        }
        PackageTests.addNote("* feature "+pp.FEATURE_REPORT_NAMESPACE_ATTRIBUTES+" is supported\n");
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


    public static void main (String[] args) {
        junit.textui.TestRunner.run (new TestSuite(TestMisc.class));
    }

}

