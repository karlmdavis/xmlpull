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

    public void testSimpleWriter() throws Exception {
        XmlSerializer ser = factory.newSerializer();

        //assert there is error if trying to write

        //assert there is error if trying to write
        try {
	    ser.startTag("", "foo");
	    fail("exception was expected of serializer if no input was set on parser");
        } catch(Exception ex) {}

        ser.setOutput(null);

        //assert there is error if trying to write
        try {
	    ser.startTag("", "foo");
	    fail("exception was expected of serializer if no input was set on parser");
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

        //TODO: check that startTag(null, ...) is not allowed

        ser.endTag("", "foo");
        ser.endDocument();


        //xpp.setInput(new StringReader("<foo></foo>"));
        String serialized = sw.toString();
        xpp.setInput(new StringReader(serialized));

        assertEquals(null, xpp.getInputEncoding());
        checkParserStateNs(xpp, 0, xpp.START_DOCUMENT, null, 0, null, null, null, false, -1);
        xpp.next();
        checkParserStateNs(xpp, 1, xpp.START_TAG, null, 0, "", "foo", null, xpp.isEmptyElementTag() /*empty*/, 0);
        xpp.next();
        checkParserStateNs(xpp, 1, xpp.END_TAG, null, 0, "", "foo", null, false, -1);
        xpp.next();
        checkParserStateNs(xpp, 0, xpp.END_DOCUMENT, null, 0, null, null, null, false, -1);
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
        checkParserStateNs(xpp, 0, xpp.START_DOCUMENT, null, 0, null, null, null, false, -1);
        xpp.next();
        checkParserStateNs(xpp, 1, xpp.START_TAG, null, 0, "", "foo", null, false, 0);
        xpp.next();
        checkParserStateNs(xpp, 1, xpp.TEXT, null, 0, null, null, "test", false, -1);
        xpp.next();
        checkParserStateNs(xpp, 1, xpp.END_TAG, null, 0, "", "foo", null, false, -1);
        xpp.next();
        checkParserStateNs(xpp, 0, xpp.END_DOCUMENT, null, 0, null, null, null, false, -1);



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

	    //        byte[] binput = ("<foo xmlns='n' xmlns:ns1='n1' xmlns:ns2='n2'>"+
	    //                             "<ns1:bar xmlns:ns1='x1' xmlns:ns3='n3' xmlns='n1'>"+
	    //                             "<ns2:gugu a1='v1' ns2:a2='v2' xml:lang='en' ns1:a3=\"v3\"/>"+
	    //                             "<baz xmlns:ns1='y1'></baz>"+
	    //                             "</ns1:bar></foo>").getBytes("US-ASCII");
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ser.setOutput(baos, "UTF8");

	    ser.startDocument(null, null);
	    ser.setPrefix("", "n");
	    ser.setPrefix("ns1", "n1");
	    ser.setPrefix("ns2", "n2");
	    ser.startTag("", "foo");

	    ser.setPrefix("ns1", "x1");
	    ser.setPrefix("ns3", "n3");
	    ser.setPrefix("", "n1");
	    ser.startTag("x1", "bar");

	    ser.startTag("n2", "gugu");
	    ser.attribute("", "a1", "v1");
	    ser.attribute("n2", "a2", "v2");
	    ser.attribute("http://www.w3.org/XML/1998/namespace", "lang", "en");
	    ser.attribute("x1", "a3", "v3");

	    ser.endTag("n2", "gugu");

	    ser.setPrefix("ns1", "y1");
	    ser.startTag("n1", "baz");

	    ser.endTag("n1", "baz");

	    ser.endTag("x1", "bar");

	    ser.endTag("", "foo");
	    ser.endDocument();


	    byte[] binput = baos.toByteArray();

	    //System.out.println("serialized="+new String(binput, "US-ASCII"));

	    xpp.setInput(new ByteArrayInputStream( binput ), "US-ASCII" );
	    assertEquals("US-ASCII", xpp.getInputEncoding());

	    checkParserStateNs(xpp, 0, xpp.START_DOCUMENT, null, 0, null, null, null, false, -1);

	    xpp.next();
	    checkParserStateNs(xpp, 1, xpp.START_TAG, null, 3, "n", "foo", null, false, 0);
	    assertEquals(0, xpp.getNamespaceCount(0));
	    assertEquals(3, xpp.getNamespaceCount(1));
	    checkNamespace(xpp, 0, null, "n", true);
	    checkNamespace(xpp, 1, "ns1", "n1", true);
	    checkNamespace(xpp, 2, "ns2", "n2", true);

	    xpp.next();
	    checkParserStateNs(xpp, 2, xpp.START_TAG, "ns1", 6, "x1", "bar", null, false, 0);
	    assertEquals(0, xpp.getNamespaceCount(0));
	    assertEquals(3, xpp.getNamespaceCount(1));
	    assertEquals(6, xpp.getNamespaceCount(2));
	    checkNamespace(xpp, 3, "ns1", "x1", true);
	    checkNamespace(xpp, 4, "ns3", "n3", true);
	    checkNamespace(xpp, 5, null, "n1", true);

	    xpp.next();
	    checkParserStateNs(xpp, 3, xpp.START_TAG, "ns2", 6, "n2", "gugu", null, true, 4);
	    assertEquals(6, xpp.getNamespaceCount(2));
	    assertEquals(6, xpp.getNamespaceCount(3));
	    assertEquals("x1", xpp.getNamespace("ns1"));
	    assertEquals("n2", xpp.getNamespace("ns2"));
	    assertEquals("n3", xpp.getNamespace("ns3"));
	    checkAttribNs(xpp, 0, null, "", "a1", "v1");
	    checkAttribNs(xpp, 1, "ns2", "n2", "a2", "v2");
	    checkAttribNs(xpp, 2, "xml", "http://www.w3.org/XML/1998/namespace", "lang", "en");
	    checkAttribNs(xpp, 3, "ns1", "x1", "a3", "v3");

	    xpp.next();
	    checkParserStateNs(xpp, 3, xpp.END_TAG, "ns2", 6, "n2", "gugu", null, false, -1);

	    xpp.next();
	    checkParserStateNs(xpp, 3, xpp.START_TAG, null, 7, "n1", "baz", null, xpp.isEmptyElementTag(), 0);
	    assertEquals(0, xpp.getNamespaceCount(0));
	    assertEquals(3, xpp.getNamespaceCount(1));
	    assertEquals(6, xpp.getNamespaceCount(2));
	    assertEquals(7, xpp.getNamespaceCount(3));
	    checkNamespace(xpp, 6, "ns1", "y1", true);
	    assertEquals("y1", xpp.getNamespace("ns1"));
	    assertEquals("n2", xpp.getNamespace("ns2"));
	    assertEquals("n3", xpp.getNamespace("ns3"));

	    xpp.next();
	    checkParserStateNs(xpp, 3, xpp.END_TAG, null, 7, "n1", "baz", null, false, -1);
	    assertEquals("y1", xpp.getNamespace("ns1"));
	    assertEquals("n2", xpp.getNamespace("ns2"));
	    assertEquals("n3", xpp.getNamespace("ns3"));

	    // check that declared namespaces can be accessed for current end tag
	    assertEquals(3, xpp.getDepth());
	    assertEquals(6, xpp.getNamespaceCount(2));
	    assertEquals(7, xpp.getNamespaceCount(3));

	    // chekc that namespace is accessible by direct addresssing
	    assertEquals(null, xpp.getNamespacePrefix(0));
	    assertEquals("n", xpp.getNamespaceUri(0));
	    assertEquals("ns1", xpp.getNamespacePrefix(1));
	    assertEquals("n1", xpp.getNamespaceUri(1));
	    assertEquals("ns1", xpp.getNamespacePrefix(3));
	    assertEquals("x1", xpp.getNamespaceUri(3));
	    assertEquals("ns1", xpp.getNamespacePrefix(6));
	    assertEquals("y1", xpp.getNamespaceUri(6));


	    xpp.next();
	    checkParserStateNs(xpp, 2, xpp.END_TAG, "ns1", 6, "x1", "bar", null, false, -1);
	    // check that namespace is undelcared
	    assertEquals("x1", xpp.getNamespace("ns1"));

	    xpp.next();
	    checkParserStateNs(xpp, 1, xpp.END_TAG, null, 3, "n", "foo", null, false, -1);

	    assertEquals("n1", xpp.getNamespace("ns1"));
	    assertEquals("n2", xpp.getNamespace("ns2"));
	    assertEquals(null, xpp.getNamespace("ns3"));

	    xpp.next();
	    checkParserStateNs(xpp, 0, xpp.END_DOCUMENT, null, 0, null, null, null, false, -1);
	    assertEquals(null, xpp.getNamespace("ns1"));
	    assertEquals(null, xpp.getNamespace("ns2"));
	    assertEquals(null, xpp.getNamespace("ns3"));


	}

	public static void main (String[] args) {
	    junit.textui.TestRunner.run (new TestSuite(TestSerializeWithNs.class));
	}

    }

