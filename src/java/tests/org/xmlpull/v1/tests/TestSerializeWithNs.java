/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license see accompanying LICENSE_TESTS.txt file (available also at http://www.xmlpull.org)

package org.xmlpull.v1.tests;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import java.io.ByteArrayInputStream;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/**
 * Simple test to verify serializer (with no namespaces)
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 */
public class TestSerializeWithNs extends UtilTestCase {
    private XmlPullParserFactory factory;
    private XmlPullParser xpp;

    public static void main (String[] args) {
        junit.textui.TestRunner.run (new TestSuite(TestSerializeWithNs.class));
    }

    public TestSerializeWithNs(String name) {
        super(name);
    }

    protected void setUp() throws XmlPullParserException {
        factory = factoryNewInstance();
        factory.setNamespaceAware(true);
        // now validate that can be deserialzied
        xpp = factory.newPullParser();
        assertEquals(true, xpp.getFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES));
    }

    protected void tearDown() {
    }


    private void checkSimpleWriterResult(String textContent) throws Exception {
        checkParserStateNs(xpp, 0, xpp.START_DOCUMENT, null, 0, null, null, null, false, -1);
        xpp.next();
        checkParserStateNs(xpp, 1, xpp.START_TAG, null, 0, "", "foo", null, xpp.isEmptyElementTag() /*empty*/, 0);
        if(textContent != null) {
            xpp.next();
            checkParserStateNs(xpp, 1, xpp.TEXT, null, 0, null, null, "test", false, -1);
        }
        xpp.next();
        checkParserStateNs(xpp, 1, xpp.END_TAG, null, 0, "", "foo", null, false, -1);
        xpp.next();
        checkParserStateNs(xpp, 0, xpp.END_DOCUMENT, null, 0, null, null, null, false, -1);
    }

    public void testSimpleWriter() throws Exception {
        XmlSerializer ser = factory.newSerializer();

        //assert there is error if trying to write

        //assert there is error if trying to write
        try {
            ser.startTag("", "foo");
            fail("exception was expected of serializer if no input was set");
        } catch(Exception ex) {}

        ser.setOutput(null);

        //assert there is error if trying to write
        try {
            ser.startTag("", "foo");
            fail("exception was expected of serializer if no input was set");
        } catch(Exception ex) {}

        StringWriter sw = new StringWriter();

        ser.setOutput(sw);

        try {
            ser.setOutput(null, null);
            fail("exception was expected of setOutput() if output stream is null");
        } catch(IllegalArgumentException ex) {}

        //check get property

        ser.setOutput(sw);

        //assertEquals(null, ser.getOutputEncoding());

        ser.startDocument("ISO-8859-1", Boolean.TRUE);
        ser.startTag("", "foo");

        //TODO: check that startTag(null, ...) is allowed

        ser.endTag("", "foo");
        ser.endDocument();


        //xpp.setInput(new StringReader("<foo></foo>"));
        String serialized = sw.toString();
        xpp.setInput(new StringReader(serialized));

        assertEquals(null, xpp.getInputEncoding());
        checkSimpleWriterResult(null);
    }


    public void testSimpleOutputStream() throws Exception {
        XmlSerializer ser = factory.newSerializer();


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ser.setOutput(baos, "UTF-8");
        ser.startDocument("UTF-8", null);
        ser.startTag("", "foo");
        ser.text("test");
        ser.endTag("", "foo");
        ser.endDocument();

        //check taking input form input stream
        //byte[] binput = "<foo>test</foo>".getBytes("UTF8");

        byte[] binput = baos.toByteArray();

        xpp.setInput(new ByteArrayInputStream( binput ), "UTF-8" );
        assertEquals("UTF-8", xpp.getInputEncoding());

        //xpp.setInput(new StringReader( "<foo/>" ) );

        checkSimpleWriterResult("test");

    }

    //TODO add test for simple namespace generation
    public void testNamespaceGeneration() throws Exception {
        XmlSerializer ser = factory.newSerializer();

        StringWriter sw = new StringWriter();
        ser.setOutput(sw);

        //assertEquals(null, ser.getOutputEncoding());

        ser.startDocument("ISO-8859-1", Boolean.TRUE);

        ser.setPrefix("soap", "http://tm");
        ser.startTag("", "foo");

        //TODO: check that startTag(null, ...) is not allowed

        ser.endTag("", "foo");
        ser.endDocument();


        //xpp.setInput(new StringReader("<foo></foo>"));
        String serialized = sw.toString();
        xpp.setInput(new StringReader(serialized));
    }

    public void testMisc() throws Exception {
        // all comemnts etc
    }

    public void testIndneting() throws Exception {
        // generate SOAP envelope
        // try to use indentation

        //check automtic namespace prefix declaration
        //test auto-generation of prefixes

        //check escaping & < > " '

        // close all unclosed tag;
        XmlSerializer ser = factory.newSerializer();

        StringWriter sw = new StringWriter();
        ser.setOutput(sw);
        ser.endDocument();
    }

    public void testSetPrefix() throws Exception {
        //setPrefix check that prefix is not duplicated ...

    }

    //TODO      redeclaring defult namespace

    public void testMultipleOverlappingNamespaces() throws Exception {
        XmlSerializer ser = factory.newSerializer();

        //<section xmlns='urn:com:books-r-us'>
        //  <!-- 2 -->   <title>Book-Signing Event</title>
        //  <!-- 3 -->   <signing>
        //  <!-- 4 -->     <author title="Mr" name="Vikram Seth" />
        //  <!-- 5 -->     <book title="A Suitable Boy" price="$22.95" />
        //               </signing>
        //             </section>


        // check namespaces generation with explicit prefixes

        //        byte[] binput = ("<foo xmlns='namesp' xmlns:ns1='namesp1' xmlns:ns2='namesp2'>"+
        //                             "<ns1:bar xmlns:ns1='x1' xmlns:ns3='namesp3' xmlns='n1'>"+
        //                             "<ns2:gugu a1='v1' ns2:a2='v2' xml:lang='en' ns1:a3=\"v3\"/>"+
        //                             "<baz xmlns:ns1='y1'></baz>"+
        //                             "</ns1:bar></foo>").getBytes("US-ASCII");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ser.setOutput(baos, "UTF8");

        ser.startDocument(null, null);
        ser.setPrefix("", "namesp");
        ser.setPrefix("ns1", "namesp1");
        ser.setPrefix("ns2", "namesp2");
        ser.startTag("", "foo");

        ser.setPrefix("ns1", "x1");
        ser.setPrefix("ns3", "namesp3");
        ser.setPrefix("", "namesp1");
        ser.startTag("x1", "bar");

        ser.startTag("namesp2", "gugu");
        ser.attribute("", "a1", "v1");
        ser.attribute("namesp2", "a2", "v2");
        ser.attribute("http://www.w3.org/XML/1998/namespace", "lang", "en");
        ser.attribute("x1", "a3", "v3");

        ser.endTag("namesp2", "gugu");

        ser.setPrefix("ns1", "y1");
        ser.startTag("namesp1", "baz");

        ser.endTag("namesp1", "baz");

        ser.endTag("x1", "bar");

        ser.endTag("", "foo");
        ser.endDocument();


        byte[] binput = baos.toByteArray();

        //System.out.println("serialized="+new String(binput, "US-ASCII"));

        xpp.setInput(new ByteArrayInputStream( binput ), "US-ASCII" );
        assertEquals("US-ASCII", xpp.getInputEncoding());

        checkParserStateNs(xpp, 0, xpp.START_DOCUMENT, null, 0, null, null, null, false, -1);

        xpp.next();
        checkParserStateNs(xpp, 1, xpp.START_TAG, null, 3, "namesp", "foo", null, false, 0);
        assertEquals(0, xpp.getNamespaceCount(0));
        assertEquals(3, xpp.getNamespaceCount(1));
        checkNamespace(xpp, 0, null, "namesp", true);
        checkNamespace(xpp, 1, "ns1", "namesp1", true);
        checkNamespace(xpp, 2, "ns2", "namesp2", true);

        xpp.next();
        checkParserStateNs(xpp, 2, xpp.START_TAG, "ns1", 6, "x1", "bar", null, false, 0);
        assertEquals(0, xpp.getNamespaceCount(0));
        assertEquals(3, xpp.getNamespaceCount(1));
        assertEquals(6, xpp.getNamespaceCount(2));
        checkNamespace(xpp, 3, "ns1", "x1", true);
        checkNamespace(xpp, 4, "ns3", "namesp3", true);
        checkNamespace(xpp, 5, null, "namesp1", true);

        xpp.next();
        checkParserStateNs(xpp, 3, xpp.START_TAG, "ns2", 6, "namesp2", "gugu", null, true, 4);
        assertEquals(6, xpp.getNamespaceCount(2));
        assertEquals(6, xpp.getNamespaceCount(3));
        assertEquals("x1", xpp.getNamespace("ns1"));
        assertEquals("namesp2", xpp.getNamespace("ns2"));
        assertEquals("namesp3", xpp.getNamespace("ns3"));
        checkAttribNs(xpp, 0, null, "", "a1", "v1");
        checkAttribNs(xpp, 1, "ns2", "namesp2", "a2", "v2");
        checkAttribNs(xpp, 2, "xml", "http://www.w3.org/XML/1998/namespace", "lang", "en");
        checkAttribNs(xpp, 3, "ns1", "x1", "a3", "v3");

        xpp.next();
        checkParserStateNs(xpp, 3, xpp.END_TAG, "ns2", 6, "namesp2", "gugu", null, false, -1);

        xpp.next();
        checkParserStateNs(xpp, 3, xpp.START_TAG, null, 7, "namesp1", "baz", null, xpp.isEmptyElementTag(), 0);
        assertEquals(0, xpp.getNamespaceCount(0));
        assertEquals(3, xpp.getNamespaceCount(1));
        assertEquals(6, xpp.getNamespaceCount(2));
        assertEquals(7, xpp.getNamespaceCount(3));
        checkNamespace(xpp, 6, "ns1", "y1", true);
        assertEquals("y1", xpp.getNamespace("ns1"));
        assertEquals("namesp2", xpp.getNamespace("ns2"));
        assertEquals("namesp3", xpp.getNamespace("ns3"));

        xpp.next();
        checkParserStateNs(xpp, 3, xpp.END_TAG, null, 7, "namesp1", "baz", null, false, -1);
        assertEquals("y1", xpp.getNamespace("ns1"));
        assertEquals("namesp2", xpp.getNamespace("ns2"));
        assertEquals("namesp3", xpp.getNamespace("ns3"));

        // check that declared namespaces can be accessed for current end tag
        assertEquals(3, xpp.getDepth());
        assertEquals(6, xpp.getNamespaceCount(2));
        assertEquals(7, xpp.getNamespaceCount(3));

        // chekc that namespace is accessible by direct addresssing
        assertEquals(null, xpp.getNamespacePrefix(0));
        assertEquals("namesp", xpp.getNamespaceUri(0));
        assertEquals("ns1", xpp.getNamespacePrefix(1));
        assertEquals("namesp1", xpp.getNamespaceUri(1));
        assertEquals("ns1", xpp.getNamespacePrefix(3));
        assertEquals("x1", xpp.getNamespaceUri(3));
        assertEquals("ns1", xpp.getNamespacePrefix(6));
        assertEquals("y1", xpp.getNamespaceUri(6));


        xpp.next();
        checkParserStateNs(xpp, 2, xpp.END_TAG, "ns1", 6, "x1", "bar", null, false, -1);
        // check that namespace is undelcared
        assertEquals("x1", xpp.getNamespace("ns1"));

        xpp.next();
        checkParserStateNs(xpp, 1, xpp.END_TAG, null, 3, "namesp", "foo", null, false, -1);

        assertEquals("namesp1", xpp.getNamespace("ns1"));
        assertEquals("namesp2", xpp.getNamespace("ns2"));
        assertEquals(null, xpp.getNamespace("ns3"));

        xpp.next();
        checkParserStateNs(xpp, 0, xpp.END_DOCUMENT, null, 0, null, null, null, false, -1);
        assertEquals(null, xpp.getNamespace("ns1"));
        assertEquals(null, xpp.getNamespace("ns2"));
        assertEquals(null, xpp.getNamespace("ns3"));


    }


}

