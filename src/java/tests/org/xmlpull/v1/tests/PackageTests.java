/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// see LICENSE.txt in distribution for copyright and license information

package org.xmlpull.v1.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * TODO: add tests for
 * <pre>
 *
 * attrib uniq
 * EOL normalization
 * TODO test mixed next() with nextToken()
 *
 *
 * TEST XML ROUNDTRIP capability
 *
 *     public static final String REPORT_NAMESPACE_ATTRIBUTES =
 *        "http://xmlpull.org/v1/features/report-namespace-prefixes";
 *
 *  public static final String PROCESS_DOCDECL =
 *      "http://xmlpull.org/v1/features/process-docdecl";
 *
 * TEST http://xmlpull.org/v1/features/xml-roundtrip
 *
 *  public void defineCharacterEntity (String entity, String value) throws XmlPullParserException;
 *
 *    public boolean isWhitespace() throws XmlPullParserException;
 *
 *  public int nextToken()
 *      throws XmlPullParserException, IOException;
 *
 *  public void require (int type, String namespace, String name)
 *      throws XmlPullParserException, IOException;
 *
 *    public String readText () throws XmlPullParserException, IOException;
 *
 */
public class PackageTests extends TestCase
{
    public PackageTests(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite("XMLPULL V1 API TESTS");

        suite.addTestSuite(TestFactory.class);
        suite.addTestSuite(TestSimple.class);
        suite.addTestSuite(TestSimpleWithNs.class);
        suite.addTestSuite(TestSimpleToken.class);
        suite.addTestSuite(TestEvent.class);
        suite.addTestSuite(TestToken.class);

        return suite;
    }

    public static void main (String[] args) {
        junit.textui.TestRunner.run (suite());
        System.exit(0);
    }

}

