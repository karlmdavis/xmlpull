/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license see accompanying LICENSE_TESTS.txt file (available also at http://www.xmlpull.org)

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

    private static StringBuffer notes = new StringBuffer();
    public static void addNote(String note) {
        notes.append(note);
    }

    public static void main (String[] args) {
        try {
            Object o = UtilTestCase.factoryNewInstance();
            addNote("* factory "+o.getClass()+"\n");
        } catch (Exception ex) {
            System.err.println(
                "ERROR: tests aborted - could not create instance of XmlPullParserFactory:");
            ex.printStackTrace();
            System.exit(1);
        }
        junit.textui.TestRunner.run (suite());
        if(notes.length() > 0) {
          System.out.println("Notes:\n"+notes);
        }
        System.exit(0);
    }

}

