/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// see LICENSE_TESTS.txt in distribution for copyright and license information

package org.xmlpull.v1.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * TODO: add tests for
 * <pre>
 * test mixed next() with nextToken()
 * </pre>
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
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
        suite.addTestSuite(TestAttributes.class);
        suite.addTestSuite(TestEolNormalization.class);
        suite.addTestSuite(TestEntityReplacement.class);
        suite.addTestSuite(TestEvent.class);
        suite.addTestSuite(TestToken.class);
        suite.addTestSuite(TestMisc.class);

        return suite;
    }

    public static void main (String[] args) {
        junit.textui.TestRunner.run (suite());
        System.exit(0);
    }

}

