/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// see LICENSE.txt in distribution for copyright and license information

package org.xmlpull.v1.tests;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Simple test ot verify pull parser factory
 *
 * @author Aleksander Slominski [http://www.extreme.indiana.edu/~aslom/]
 */
public class TestFactory extends TestCase {

    public TestFactory(String name) {
        super(name);
    }

    public void testFactory() throws Exception {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance(
            System.getProperty(XmlPullParserFactory.DEFAULT_PROPERTY_NAME));
        System.out.println("factory = "+factory);
        XmlPullParser xpp = factory.newPullParser();
        assertEquals(false, xpp.getFeature(XmlPullParser.PROCESS_NAMESPACES));
        factory.setFeature(XmlPullParser.PROCESS_NAMESPACES, true);
        xpp = factory.newPullParser();
        assertEquals(true, xpp.getFeature(XmlPullParser.PROCESS_NAMESPACES));

        factory.setNamespaceAware(false);
        assertEquals(false, factory.isNamespaceAware());
        xpp = factory.newPullParser();
        assertEquals(false, xpp.getFeature(XmlPullParser.PROCESS_NAMESPACES));

        factory.setNamespaceAware(true);
        assertEquals(true, factory.isNamespaceAware());
        xpp = factory.newPullParser();
        assertEquals(true, xpp.getFeature(XmlPullParser.PROCESS_NAMESPACES));
    }

    public static void main (String[] args) {
        junit.textui.TestRunner.run (new TestSuite(TestFactory.class));
    }
}

