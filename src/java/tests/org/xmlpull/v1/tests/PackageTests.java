/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// see LICENSE.txt in distribution for copyright and license information

package org.xmlpull.v1.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
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

        return suite;
    }

    public static void main (String[] args) {
        junit.textui.TestRunner.run (suite());
        System.exit(0);
    }

}

