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
public class TestToken extends UtilTestCase {
    private XmlPullParserFactory factory;

    public TestToken(String name) {
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

    public void testToken() throws Exception {
        XmlPullParser xpp = factory.newPullParser();


    }

    public static void main (String[] args) {
        junit.textui.TestRunner.run (new TestSuite(TestToken.class));
    }

}

