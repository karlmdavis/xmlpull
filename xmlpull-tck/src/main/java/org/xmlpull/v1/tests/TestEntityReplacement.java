/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license see accompanying LICENSE_TESTS.txt file (available also at http://www.xmlpull.org)

package org.xmlpull.v1.tests;

//import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.StringReader;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Test if entity replacement works ok.
 * This test is designe to work bboth for validating and non validating parsers!
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 */
public class TestEntityReplacement extends UtilTestCase {
    private XmlPullParserFactory factory;

    public TestEntityReplacement(String name) {
        super(name);
    }

    protected void setUp() throws XmlPullParserException {
        factory = factoryNewInstance();
        factory.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        assertEquals(true, factory.getFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES));
        //assertEquals(false, factory.getFeature(XmlPullParser.FEATURE_VALIDATION));
    }

    protected void tearDown() {
    }

    public void testEntityReplacement() throws IOException, XmlPullParserException
    {
        // taken from http://www.w3.org/TR/REC-xml#sec-entexpand
        final String XML_ENTITY_EXPANSION =
            "<?xml version='1.0'?>\n"+
            "<!DOCTYPE test [\n"+
            "<!ELEMENT test (#PCDATA) >\n"+
            "<!ENTITY % xx '&#37;zz;'>\n"+
            "<!ENTITY % zz '&#60;!ENTITY tricky \"error-prone\" >' >\n"+
            "%xx;\n"+
            "]>"+
            "<test>This sample shows a &tricky; method.</test>";

        XmlPullParser pp = factory.newPullParser();
        // default parser must work!!!!
        pp.setInput(new StringReader( XML_ENTITY_EXPANSION ) );
        if(pp.getFeature( XmlPullParser.FEATURE_PROCESS_DOCDECL ) == false) {
            pp.defineEntityReplacementText("tricky", "error-prone");
        }
        testEntityReplacement(pp);

        // now we try for no FEATURE_PROCESS_DOCDECL
        pp.setInput(new StringReader( XML_ENTITY_EXPANSION ) );
        try {
            pp.setFeature( XmlPullParser.FEATURE_PROCESS_DOCDECL, false );
        } catch( Exception ex ){
        }
        if( pp.getFeature( XmlPullParser.FEATURE_PROCESS_DOCDECL ) == false ) {
            pp.defineEntityReplacementText("tricky", "error-prone");
            testEntityReplacement(pp);
        }

        // try to use FEATURE_PROCESS_DOCDECL if supported
        pp.setInput(new StringReader( XML_ENTITY_EXPANSION ) );
        try {
            pp.setFeature( XmlPullParser.FEATURE_PROCESS_DOCDECL, true );
            //PackageTests.addNote("* feature "+pp.FEATURE_PROCESS_DOCDECL+" is supported\n");
        } catch( Exception ex ){
        }
        if( pp.getFeature( XmlPullParser.FEATURE_PROCESS_DOCDECL ) ) {
            testEntityReplacement(pp);
        }

        // try to use FEATURE_VALIDATION if supported
        pp.setInput(new StringReader( XML_ENTITY_EXPANSION ) );
        try {
            pp.setFeature( XmlPullParser.FEATURE_VALIDATION, true );
            //PackageTests.addNote("* feature "+pp.FEATURE_VALIDATION+" is supported\n");
        } catch( Exception ex ){
        }
        if( pp.getFeature( XmlPullParser.FEATURE_VALIDATION ) ) {
            testEntityReplacement(pp);
        }

    }

    public void testEntityReplacement(XmlPullParser pp) throws IOException, XmlPullParserException
    {
        pp.next();
        checkParserStateNs(pp, 1, XmlPullParser.START_TAG,
                           null, 0, "", "test", null, false, 0);
        pp.next();
        checkParserStateNs(pp, 1, XmlPullParser.TEXT, null, 0, null, null,
                           "This sample shows a error-prone method.", false, -1);
        pp.next();
        checkParserStateNs(pp, 1, XmlPullParser.END_TAG,
                           null, 0, "", "test", null, false, -1);
        pp.nextToken();
        checkParserStateNs(pp, 0, XmlPullParser.END_DOCUMENT, null, 0, null, null, null, false, -1);

    }


    public static void main (String[] args) {
        junit.textui.TestRunner.run (new TestSuite(TestEntityReplacement.class));
    }

}
