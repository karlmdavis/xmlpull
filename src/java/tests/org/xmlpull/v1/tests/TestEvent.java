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
 * More complete test to verify paring.
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 */
public class TestEvent extends UtilTestCase {
    private XmlPullParserFactory factory;

    public TestEvent(String name) {
        super(name);
    }

    protected void setUp() throws XmlPullParserException {
        factory = XmlPullParserFactory.newInstance(
            System.getProperty(XmlPullParserFactory.DEFAULT_PROPERTY_NAME)
        );
        factory.setNamespaceAware(true);
        assertEquals(true, factory.getFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES));
        assertEquals(false, factory.getFeature(XmlPullParser.FEATURE_VALIDATION));
    }

    protected void tearDown() {
    }

    public void testEvent() throws Exception {
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new StringReader(TEST_XML));

        checkParserStateNs(xpp, 0, xpp.START_DOCUMENT, null, 0, null, null, null, false, -1);

        xpp.next();
        checkParserStateNs(xpp, 1, xpp.START_TAG, null, 0, "", "root", null, false, 0);
        xpp.next();
        checkParserStateNs(xpp, 1, xpp.TEXT, null, 0, null, null, "\n", false, -1);

        xpp.next();
        checkParserStateNs(xpp, 2, xpp.START_TAG, null, 0, "", "foo", null, false, 0);
        xpp.next();
        checkParserStateNs(xpp, 2, xpp.TEXT, null, 0, null, null, "bar", false, -1);
        xpp.next();
        checkParserStateNs(xpp, 1, xpp.END_TAG, null, 0, "", "foo", null, false, -1);
        xpp.next();
        checkParserStateNs(xpp, 1, xpp.TEXT, null, 0, null, null, "\n", false, -1);

        xpp.next();
        checkParserStateNs(xpp, 2, xpp.START_TAG,
                           null, 0, "http://www.xmlpull.org/temp", "hugo", null, false, 0);
        xpp.next();
        checkParserStateNs(xpp, 2, xpp.TEXT, null, 0, null, null, " \n\n \n  ", false, -1);

        xpp.next();
        checkParserStateNs(xpp, 3, xpp.START_TAG,
                           null, 0, "http://www.xmlpull.org/temp", "hugochild", null, false, 0);
        xpp.next();
        checkParserStateNs(xpp, 3, xpp.TEXT, null, 0, null, null,
                           "This is in a new namespace", false, -1);
        xpp.next();
        checkParserStateNs(xpp, 2, xpp.END_TAG,
                           null, 0, "http://www.xmlpull.org/temp", "hugochild", null, false, -1);

        xpp.next();
        checkParserStateNs(xpp, 1, xpp.END_TAG,
                           null, 0, "http://www.xmlpull.org/temp", "hugo", null, false, -1);
        xpp.next();
        checkParserStateNs(xpp, 1, xpp.TEXT, null, 0, null, null, "\t\n", false, -1);

        xpp.next();
        checkParserStateNs(xpp, 2, xpp.START_TAG, null, 0, "", "bar", null, true, 1);
        checkAttribNs(xpp, 0, null, "", "testattr", "123abc");
        xpp.next();
        checkParserStateNs(xpp, 1, xpp.END_TAG, null, 0, "", "bar", null, false, -1);
        xpp.next();
        checkParserStateNs(xpp, 0, xpp.END_TAG, null, 0, "", "root", null, false, -1);
        xpp.next();
        checkParserStateNs(xpp, 0, xpp.END_DOCUMENT, null, 0, null, null, null, false, -1);
    }

    public static void main (String[] args) {
        junit.textui.TestRunner.run (new TestSuite(TestEvent.class));
    }

}

