/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// see LICENSE.txt in distribution for copyright and license information

package org.xmlpull.v1.tests;

//import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Simple test ot verify pull parser factory
 *
 * @author Aleksander Slominski [http://www.extreme.indiana.edu/~aslom/]
 */
public class TestSimple extends TestCase {
    private XmlPullParserFactory factory;

    public TestSimple(String name) {
        super(name);
    }

    protected void setUp() throws XmlPullParserException {
        factory = XmlPullParserFactory.newInstance(
            Thread.currentThread().getContextClassLoader().getClass(),
            System.getProperty(XmlPullParserFactory.DEFAULT_PROPERTY_NAME)
        );
        factory.setNamespaceAware(true);
    }

    protected void tearDown() {
    }

    public void checkParserState(
        XmlPullParser xpp,
        int depth,
        int type,
        String prefix,
        int nsCount,
        String namespace,
        String name,
        String text,
        boolean isEmpty,
        int attribCount
    ) throws Exception
    {
        assertEquals("getDepth()", depth, xpp.getDepth());
        assertEquals("TYPES[getType()]", xpp.TYPES[type], xpp.TYPES[xpp.getType()]);
        assertEquals("getType()", type, xpp.getType());
        assertEquals("getPrefix()", prefix, xpp.getPrefix());
        assertEquals("getNamespacesCount(getDepth())", nsCount, xpp.getNamespacesCount(depth));
        assertEquals("getNamespace()", namespace, xpp.getNamespace());
        assertEquals("getName()", name, xpp.getName());
        assertEquals("getText()", text, xpp.getText());
        if(type == xpp.START_TAG) {
            assertEquals("isEmptyElementTag()", isEmpty, xpp.isEmptyElementTag());
        } else {
            try {
                xpp.isEmptyElementTag();
                fail("isEmptyElementTag() must throw exception if parser not on START_TAG");
            } catch(XmlPullParserException ex) {
            }
        }
        assertEquals("getAttributeCount()", attribCount, xpp.getAttributesCount());
    }



    public void testSimple() throws Exception {
        XmlPullParser xpp = factory.newPullParser();
        assertEquals(true, xpp.getFeature(XmlPullParser.PROCESS_NAMESPACES));

        // check setInput semantics
        assertEquals(XmlPullParser.START_DOCUMENT, xpp.getType());
        try {
            xpp.next();
            fail("exception was expected of next() if no input was set on parser");
        } catch(XmlPullParserException ex) {}

        xpp.setInput(null);
        assertEquals(XmlPullParser.START_DOCUMENT, xpp.getType());
        try {
            xpp.next();
            fail("exception was expected of next() if no input was set on parser");
        } catch(XmlPullParserException ex) {}


        //      xpp.setFeature(xpp.PROCESS_NAMESPACES, false);
        //      testSimpleParsing("<foo/>", "", null, false, null, null, null);
        //      testSimpleParsing("<foo></foo>", "", null, false, null, null, null);
        //      xpp.setFeature(xpp.PROCESS_NAMESPACES, true);
        //      testSimpleParsing("<foo xmlns='ns1'>bar</foo>", "ns1", "", true, null, null, null);

        // check the simplest possible XML document - just one root element
        for(int i = 1; i <= 2; ++i) {
            xpp.setInput(new StringReader(i == 1 ? "<foo/>" : "<foo></foo>"));
            boolean empty = (i == 1);
            checkParserState(xpp, 0, xpp.START_DOCUMENT, null, 0, null, null, null, false, -1);
            xpp.next();
            checkParserState(xpp, 1, xpp.START_TAG, null, 0, "", "foo", null, empty, 0);
            xpp.next();
            checkParserState(xpp, 0, xpp.END_TAG, null, 0, "", "foo", null, false, -1);
            xpp.next();
            checkParserState(xpp, 0, xpp.END_DOCUMENT, null, 0, null, null, null, false, -1);
        }

        // one step further - it has content ...

        xpp.setInput(new StringReader("<foo xmlns:ns1='n1'><ns1:bar xmlns:ns2='n2'></foo>"));
        checkParserState(xpp, 0, xpp.START_DOCUMENT, null, 0, null, null, null, false, -1);
        xpp.next();
        checkParserState(xpp, 1, xpp.START_TAG, null, 0, "", "foo", null, false, 0);
        assertEquals(0, xpp.getNamespacesCount(0));
        xpp.next();
        checkParserState(xpp, 1, xpp.START_TAG, "ns1", 0, "n1", "bar", null, true, 0);
        xpp.next();
        checkParserState(xpp, 0, xpp.END_TAG, null, 0, "", "foo", null, false, -1);
        xpp.next();
        checkParserState(xpp, 0, xpp.END_TAG, null, 0, "", "foo", null, false, -1);
        xpp.next();
        checkParserState(xpp, 0, xpp.END_DOCUMENT, null, 0, null, null, null, false, -1);

        
        xpp.setInput(new StringReader("<foo>bar</foo>"));
        checkParserState(xpp, 0, xpp.START_DOCUMENT, null, 0, null, null, null, false, -1);
        xpp.next();
        checkParserState(xpp, 1, xpp.START_TAG, null, 0, "", "foo", null, false, 0);
        xpp.next();
        checkParserState(xpp, 1, xpp.TEXT, null, 0, null, null, "bar", false, -1);
        xpp.next();
        checkParserState(xpp, 0, xpp.END_TAG, null, 0, "", "foo", null, false, -1);
        xpp.next();
        checkParserState(xpp, 0, xpp.END_DOCUMENT, null, 0, null, null, null, false, -1);
        
        
        xpp.setInput(new StringReader("<foo xmlns='ns1'>bar</foo>"));
        assertEquals(XmlPullParser.START_DOCUMENT, xpp.getType());
        assertEquals(XmlPullParser.START_TAG, xpp.next());
        assertEquals("foo", xpp.getName());
        assertEquals("ns1", xpp.getNamespace());
        assertEquals(null, xpp.getPrefix());
        assertEquals(null, xpp.getText());
        assertEquals(XmlPullParser.TEXT, xpp.next());
        assertEquals("bar", xpp.getText());
        assertEquals(XmlPullParser.END_TAG, xpp.next());
        assertEquals("foo", xpp.getName());
        assertEquals("ns1", xpp.getNamespace());
        assertEquals(null, xpp.getPrefix());
        assertEquals(XmlPullParser.END_DOCUMENT, xpp.next());
    }

    public static void main (String[] args) {
        junit.textui.TestRunner.run (new TestSuite(TestSimple.class));
    }

}

