/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// see LICENSE_TESTS.txt in distribution for copyright and license information

package org.xmlpull.v1.tests;

//import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Simple test for minimal XML parsing with namespaces
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 */
public class TestSimpleWithNs extends UtilTestCase {
    private XmlPullParserFactory factory;

    public TestSimpleWithNs(String name) {
        super(name);
    }

    protected void setUp() throws XmlPullParserException {
        factory = XmlPullParserFactory.newInstance(
            System.getProperty(XmlPullParserFactory.DEFAULT_PROPERTY_NAME)
        );
        factory.setNamespaceAware(true);
        assertEquals(true, factory.getFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES));
    }

    protected void tearDown() {
    }

    public void testSimpleWithNs() throws Exception {
        XmlPullParser xpp = factory.newPullParser();
        assertEquals(true, xpp.getFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES));

        // check setInput semantics
        assertEquals(XmlPullParser.START_DOCUMENT, xpp.getEventType());
        try {
            xpp.next();
            fail("exception was expected of next() if no input was set on parser");
        } catch(XmlPullParserException ex) {}

        xpp.setInput(null);
        assertEquals(XmlPullParser.START_DOCUMENT, xpp.getEventType());
        try {
            xpp.next();
            fail("exception was expected of next() if no input was set on parser");
        } catch(XmlPullParserException ex) {}


        // check the simplest possible XML document - just one root element
        for(int i = 1; i <= 2; ++i) {
            xpp.setInput(new StringReader(i == 1 ? "<foo/>" : "<foo></foo>"));
            boolean empty = (i == 1);
            checkParserStateNs(xpp, 0, xpp.START_DOCUMENT, null, 0, null, null, null, false, -1);
            xpp.next();
            checkParserStateNs(xpp, 1, xpp.START_TAG, null, 0, "", "foo", null, empty, 0);
            xpp.next();
            checkParserStateNs(xpp, 0, xpp.END_TAG, null, 0, "", "foo", null, false, -1);
            xpp.next();
            checkParserStateNs(xpp, 0, xpp.END_DOCUMENT, null, 0, null, null, null, false, -1);
        }

        // one step further - it has content ...


        xpp.setInput(new StringReader("<foo attrName='attrVal'>bar</foo>"));
        checkParserStateNs(xpp, 0, xpp.START_DOCUMENT, null, 0, null, null, null, false, -1);
        xpp.next();
        checkParserStateNs(xpp, 1, xpp.START_TAG, null, 0, "", "foo", null, false, 1);
        checkAttribNs(xpp, 0, null, "", "attrName", "attrVal");
        xpp.next();
        checkParserStateNs(xpp, 1, xpp.TEXT, null, 0, null, null, "bar", false, -1);
        xpp.next();
        checkParserStateNs(xpp, 0, xpp.END_TAG, null, 0, "", "foo", null, false, -1);
        xpp.next();
        checkParserStateNs(xpp, 0, xpp.END_DOCUMENT, null, 0, null, null, null, false, -1);


        xpp.setInput(new StringReader(
                         "<foo xmlns='n' xmlns:ns1='n1' xmlns:ns2='n2'>"+
                             "<ns1:bar xmlns:ns1='x1' xmlns:ns3='n3' xmlns='n1'>"+
                             "<ns2:gugu a1='v1' ns2:a2='v2' xml:lang='en' ns1:a3=\"v3\"/>"+
                             "<baz xmlns:ns1='y1'></baz>"+
                             "</ns1:bar></foo>"
                     ));

        checkParserStateNs(xpp, 0, xpp.START_DOCUMENT, null, 0, null, null, null, false, -1);

        xpp.next();
        checkParserStateNs(xpp, 1, xpp.START_TAG, null, 2, "n", "foo", null, false, 0);
        assertEquals(0, xpp.getNamespaceCount(0));
        assertEquals(2, xpp.getNamespaceCount(1));
        checkNamespace(xpp, 0, "ns1", "n1", true);
        checkNamespace(xpp, 1, "ns2", "n2", true);

        xpp.next();
        checkParserStateNs(xpp, 2, xpp.START_TAG, "ns1", 4, "x1", "bar", null, false, 0);
        assertEquals(0, xpp.getNamespaceCount(0));
        assertEquals(2, xpp.getNamespaceCount(1));
        assertEquals(4, xpp.getNamespaceCount(2));
        checkNamespace(xpp, 2, "ns1", "x1", true);
        checkNamespace(xpp, 3, "ns3", "n3", true);

        xpp.next();
        checkParserStateNs(xpp, 3, xpp.START_TAG, "ns2", 4, "n2", "gugu", null, true, 4);
        assertEquals(4, xpp.getNamespaceCount(2));
        assertEquals(4, xpp.getNamespaceCount(3));
        assertEquals("x1", xpp.getNamespace("ns1"));
        assertEquals("n2", xpp.getNamespace("ns2"));
        assertEquals("n3", xpp.getNamespace("ns3"));
        checkAttribNs(xpp, 0, null, "", "a1", "v1");
        checkAttribNs(xpp, 1, "ns2", "n2", "a2", "v2");
        checkAttribNs(xpp, 2, "xml", "http://www.w3.org/XML/1998/namespace", "lang", "en");
        checkAttribNs(xpp, 3, "ns1", "x1", "a3", "v3");

        xpp.next();
        checkParserStateNs(xpp, 2, xpp.END_TAG, "ns2", 4, "n2", "gugu", null, false, -1);

        xpp.next();
        checkParserStateNs(xpp, 3, xpp.START_TAG, null, 5, "n1", "baz", null, false, 0);
        assertEquals(0, xpp.getNamespaceCount(0));
        assertEquals(2, xpp.getNamespaceCount(1));
        assertEquals(4, xpp.getNamespaceCount(2));
        assertEquals(5, xpp.getNamespaceCount(3));
        checkNamespace(xpp, 4, "ns1", "y1", true);
        assertEquals("y1", xpp.getNamespace("ns1"));
        assertEquals("n2", xpp.getNamespace("ns2"));
        assertEquals("n3", xpp.getNamespace("ns3"));

        xpp.next();
        checkParserStateNs(xpp, 2, xpp.END_TAG, null, 4, "n1", "baz", null, false, -1);
        assertEquals("x1", xpp.getNamespace("ns1"));
        assertEquals("n2", xpp.getNamespace("ns2"));
        assertEquals("n3", xpp.getNamespace("ns3"));

        xpp.next();
        checkParserStateNs(xpp, 1, xpp.END_TAG, "ns1", 2, "x1", "bar", null, false, -1);
        assertEquals("n1", xpp.getNamespace("ns1"));
        assertEquals("n2", xpp.getNamespace("ns2"));
        assertEquals(null, xpp.getNamespace("ns3"));

        xpp.next();
        checkParserStateNs(xpp, 0, xpp.END_TAG, null, 0, "n", "foo", null, false, -1);

        xpp.next();
        checkParserStateNs(xpp, 0, xpp.END_DOCUMENT, null, 0, null, null, null, false, -1);
        assertEquals(null, xpp.getNamespace("ns1"));
        assertEquals(null, xpp.getNamespace("ns2"));
        assertEquals(null, xpp.getNamespace("ns3"));

    }

    public static void main (String[] args) {
        junit.textui.TestRunner.run (new TestSuite(TestSimpleWithNs.class));
    }

}

