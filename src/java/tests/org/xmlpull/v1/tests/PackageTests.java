/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license see accompanying LICENSE_TESTS.txt file (available also at http://www.xmlpull.org)

package org.xmlpull.v1.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.xmlpull.v1.XmlPullParserFactory;

/**
 * TODO: add tests for
 * <pre>
 * test mixed next() with nextToken()
 * </pre>
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 */
public class PackageTests extends TestRunner
{
    public PackageTests()
    {
        super();
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite("XMLPULL V1 API TESTS");

        suite.addTestSuite(TestFactory.class);
        suite.addTestSuite(TestSimple.class);
        suite.addTestSuite(TestSimpleWithNs.class);
        suite.addTestSuite(TestSerialize.class);
        suite.addTestSuite(TestSerializeWithNs.class);
        suite.addTestSuite(TestSimpleToken.class);
        suite.addTestSuite(TestAttributes.class);
        suite.addTestSuite(TestEolNormalization.class);
        suite.addTestSuite(TestEntityReplacement.class);
        suite.addTestSuite(TestEvent.class);
        suite.addTestSuite(TestToken.class);
        suite.addTestSuite(TestMisc.class);
        suite.addTestSuite(TestSetInput.class);

        return suite;
    }

    public synchronized void startTest(Test test) {
        writer().print(".");
        //if (fColumn++ >= 40) {
        //      writer().println();
        //      fColumn= 0;
        //}
    }

    private static StringBuffer notes = new StringBuffer();
    public static void addNote(String note) {
        notes.append(note);
    }


    public void runPackageTests(String testFactoryName) {
        writer().println("Executing XMLPULL tests"
                        +(testFactoryName != null ? " for '"+testFactoryName+"'" : ""));
        notes.setLength(0);
        XmlPullParserFactory f = null;
        try {
            f = UtilTestCase.factoryNewInstance();
            addNote("* factory "+f.getClass()+"\n");//+" created from property "
            //+System.getProperty(XmlPullParserFactory.PROPERTY_NAME)+"\n");
        } catch (Exception ex) {
            System.err.println(
                "ERROR: tests aborted - could not create instance of XmlPullParserFactory:");
            ex.printStackTrace();
            System.exit(1);
        }
        try {
            f.newPullParser();
        } catch (Exception ex) {
            System.err.println(
                "ERROR: tests aborted - could not create instance of XmlPullParser from factory "
                    +f.getClass());
            ex.printStackTrace();
            System.exit(2);
        }

        try {
            f.newSerializer();
        } catch (Exception ex) {
            System.err.println(
                "ERROR: tests aborted - could not create instance of XmlSerializer from factory "
                    +f.getClass());
            ex.printStackTrace();
            System.exit(3);
        }

        // now run all tests ...
        //junit.textui.TestRunner.run(suite());
        TestRunner aTestRunner= new TestRunner();
        try {
            TestResult r = doRun(suite(), false);
            if (!r.wasSuccessful())
                System.exit(-1);
            //System.exit(0);
        } catch(Exception e) {
            System.err.println(e.getMessage());
            System.exit(-2);
        }

        if(notes.length() > 0) {
            writer().println("Test results "
                                 +(testFactoryName != null ? "for '"+testFactoryName+"'" : "")
                                 +"\n"+notes+"\n");
        }
    }

    public void printFinalReport() {
        writer().println("\nAll tests were passed.");
    }

    public static void main (String[] args) {
        final PackageTests driver = new PackageTests();
        final String listOfTests = System.getProperty("org.xmlpull.v1.tests");
        final String name = XmlPullParserFactory.PROPERTY_NAME;
        final String oldValue = System.getProperty(name);
        if(listOfTests != null) {
            int pos = 0;
            while (pos < listOfTests.length()) {
                int cut = listOfTests.indexOf(':', pos);
                if (cut == -1) cut = listOfTests.length();
                String testFactoryName = listOfTests.substring(pos, cut);
                if("DEFAULT".equals(testFactoryName)) {
                    if(oldValue != null) {
                        System.setProperty(name, oldValue);
                    } else {
                        // overcoming limitation of System.setProperty not allowing
                        //  null or empty values (who knows how to unset property ?!)
                        System.setProperty(name, "DEFAULT");
                    }
                } else {
                    System.setProperty(name, testFactoryName);
                }
                driver.runPackageTests(testFactoryName);
                pos = cut + 1;
            }
            driver.printFinalReport();

        } else {
            driver.runPackageTests(null);
        }
        System.exit(0);
    }

}

