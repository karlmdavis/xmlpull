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
 * Simple test ot verify pull parser factory
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 */
public class TestSimple extends UtilTestCase {
    private XmlPullParserFactory factory;

    public TestSimple(String name) {
        super(name);
    }

    protected void setUp() throws XmlPullParserException {
        factory = XmlPullParserFactory.newInstance(
            System.getProperty(XmlPullParserFactory.DEFAULT_PROPERTY_NAME)
        );
        assertEquals(false, factory.getFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES));
        assertEquals(false, factory.getFeature(XmlPullParser.FEATURE_VALIDATION));
    }

    protected void tearDown() {
    }

    public void testSimple() throws Exception {
        XmlPullParser xpp = factory.newPullParser();
        assertEquals(false, xpp.getFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES));

        // this SHOULD always be OK
        assertEquals("START_DOCUMENT", xpp.TYPES[xpp.START_DOCUMENT]);
        assertEquals("END_DOCUMENT", xpp.TYPES[xpp.END_DOCUMENT]);
        assertEquals("START_TAG", xpp.TYPES[xpp.START_TAG]);
        assertEquals("END_TAG", xpp.TYPES[xpp.END_TAG]);
        assertEquals("TEXT", xpp.TYPES[xpp.TEXT]);
        assertEquals("CDSECT", xpp.TYPES[xpp.CDSECT]);
        assertEquals("ENTITY_REF", xpp.TYPES[xpp.ENTITY_REF]);
        assertEquals("IGNORABLE_WHITESPACE", xpp.TYPES[xpp.IGNORABLE_WHITESPACE]);
        assertEquals("PROCESSING_INSTRUCTION", xpp.TYPES[xpp.PROCESSING_INSTRUCTION]);
        assertEquals("COMMENT", xpp.TYPES[xpp.COMMENT]);
        assertEquals("DOCDECL", xpp.TYPES[xpp.DOCDECL]);

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

        assertEquals(1, xpp.getLineNumber());
        assertEquals(0, xpp.getColumnNumber());



        // check the simplest possible XML document - just one root element
        for(int i = 1; i <= 2; ++i) {
            xpp.setInput(new StringReader(i == 1 ? "<foo/>" : "<foo></foo>"));
            assertEquals(1, xpp.getLineNumber());
            assertEquals(0, xpp.getColumnNumber());
            boolean empty = (i == 1);
            checkParserState(xpp, 0, xpp.START_DOCUMENT, null, null, false, -1);
            xpp.next();
            checkParserState(xpp, 1, xpp.START_TAG, "foo", null, empty, 0);
            xpp.next();
            checkParserState(xpp, 0, xpp.END_TAG, "foo", null, false, -1);
            xpp.next();
            checkParserState(xpp, 0, xpp.END_DOCUMENT, null, null, false, -1);
        }

        // one step further - it has content ...


        xpp.setInput(new StringReader("<foo attrName='attrVal'>bar<p:t>\r\n\t </p:t></foo>"));
        checkParserState(xpp, 0, xpp.START_DOCUMENT, null, null, false, -1);
        xpp.next();
        checkParserState(xpp, 1, xpp.START_TAG, "foo", null, false, 1);
        checkAttrib(xpp, 0, "attrName", "attrVal");
        xpp.next();
        checkParserState(xpp, 1, xpp.TEXT, null, "bar", false, -1);
        assertEquals(false, xpp.isWhitespace());
        xpp.next();
        checkParserState(xpp, 2, xpp.START_TAG, "p:t", null, false, 0);
        xpp.next();
        checkParserState(xpp, 2, xpp.TEXT, null, "\n\t ", false, -1);
        assertTrue(xpp.isWhitespace());
        xpp.next();
        checkParserState(xpp, 1, xpp.END_TAG, "p:t", null, false, -1);
        xpp.next();
        checkParserState(xpp, 0, xpp.END_TAG, "foo", null, false, -1);
        xpp.next();
        checkParserState(xpp, 0, xpp.END_DOCUMENT, null, null, false, -1);


    }

    public static void main (String[] args) {
        junit.textui.TestRunner.run (new TestSuite(TestSimple.class));
    }

}

