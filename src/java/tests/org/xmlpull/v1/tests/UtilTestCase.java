/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// see LICENSE_TESTS.txt in distribution for copyright and license information

package org.xmlpull.v1.tests;

import java.io.IOException;

import junit.framework.TestCase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Some common utilities to help with XMLPULL tests.
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 */
public class UtilTestCase extends TestCase {
    private XmlPullParserFactory factory;

    protected String TEST_XML =
        "<root>\n"+
        "<foo>bar</foo>\r\n"+
        "<hugo xmlns=\"http://www.xmlpull.org/temp\"> \n\r \n"+
        "  <hugochild>This is in a <!-- comment -->new namespace</hugochild>"+
        "</hugo>\t\n"+
        "<bar testattr='123abc' />"+
        "</root>\n"+
        "\n"+
        "<!-- an xml sample document without meaningful content -->\n";


    public UtilTestCase(String name) {
        super(name);
    }



    public void checkParserState(
        XmlPullParser xpp,
        int depth,
        int type,
        String name,
        String text,
        boolean isEmpty,
        int attribCount
    ) throws XmlPullParserException, IOException
    {
        assertEquals("PROCESS_NAMESPACES", false, xpp.getFeature(xpp.FEATURE_PROCESS_NAMESPACES));
        assertEquals("TYPES[getType()]", xpp.TYPES[type], xpp.TYPES[xpp.getEventType()]);
        assertEquals("getType()", type, xpp.getEventType());
        assertEquals("getDepth()", depth, xpp.getDepth());
        assertEquals("getPrefix()", null, xpp.getPrefix());
        assertEquals("getNamespacesCount(getDepth())", 0, xpp.getNamespaceCount(depth));
        if(xpp.getEventType() == xpp.START_TAG || xpp.getEventType() == xpp.END_TAG) {
            assertEquals("getNamespace()", "", xpp.getNamespace());
        } else {
            assertEquals("getNamespace()", null, xpp.getNamespace());
        }
        assertEquals("getName()", name, xpp.getName());

        if(xpp.getEventType() != xpp.START_TAG && xpp.getEventType() != xpp.END_TAG) {
            assertEquals("getText()", printable(text), printable(xpp.getText()));

            int [] holderForStartAndLength = new int[2];
            char[] buf = xpp.getTextCharacters(holderForStartAndLength);
            if(buf != null) {
                String s = new String(buf, holderForStartAndLength[0], holderForStartAndLength[1]);
                assertEquals("getText(holder)", printable(text), printable(s));
            } else {
                assertEquals("getTextCharacters()", null, text);
            }
        }
        if(type == xpp.START_TAG) {
            assertEquals("isEmptyElementTag()", isEmpty, xpp.isEmptyElementTag());
        } else {
            try {
                xpp.isEmptyElementTag();
                fail("isEmptyElementTag() must throw exception if parser not on START_TAG");
            } catch(XmlPullParserException ex) {
            }
        }
        assertEquals("getAttributeCount()", attribCount, xpp.getAttributeCount());
    }

    public void checkParserStateNs(
        XmlPullParser xpp,
        int depth,
        int type,
        String prefix,
        int nsCount,
        String namespace,
        String name,
        String text,
        boolean isEmpty,
        int attribCount
    ) throws XmlPullParserException, IOException
    {
        // this methid can be used with enabled and not enabled namespaces
        //assertEquals("PROCESS_NAMESPACES", true, xpp.getFeature(xpp.FEATURE_PROCESS_NAMESPACES));
        assertEquals("TYPES[getType()]", xpp.TYPES[type], xpp.TYPES[xpp.getEventType()]);
        assertEquals("getType()", type, xpp.getEventType());
        assertEquals("getName()", name, xpp.getName());

        assertEquals("getDepth()", depth, xpp.getDepth());
        assertEquals("getPrefix()", prefix, xpp.getPrefix());
        assertEquals("getNamespacesCount(getDepth())", nsCount, xpp.getNamespaceCount(depth));
        assertEquals("getNamespace()", namespace, xpp.getNamespace());

        if(xpp.getEventType() != xpp.START_TAG && xpp.getEventType() != xpp.END_TAG) {
            assertEquals("getText()", printable(text), printable(xpp.getText()));

            int [] holderForStartAndLength = new int[2];
            char[] buf = xpp.getTextCharacters(holderForStartAndLength);
            if(buf != null) {
                String s = new String(buf, holderForStartAndLength[0], holderForStartAndLength[1]);
                assertEquals("getText(holder)", printable(text), printable(s));
            } else {
                assertEquals("getTextCharacters()", null, text);
            }

        }

        if(type == xpp.START_TAG) {
            assertEquals("isEmptyElementTag()", isEmpty, xpp.isEmptyElementTag());
        } else {
            try {
                xpp.isEmptyElementTag();
                fail("isEmptyElementTag() must throw exception if parser not on START_TAG");
            } catch(XmlPullParserException ex) {
            }
        }
        assertEquals("getAttributeCount()", attribCount, xpp.getAttributeCount());
    }

    public void checkAttrib(
        XmlPullParser xpp,
        int pos,
        String name,
        String value
    ) throws XmlPullParserException, IOException
    {
        assertEquals("must be on START_TAG", xpp.START_TAG, xpp.getEventType());
        assertEquals("getAttributePrefix()",null, xpp.getAttributePrefix(pos));
        assertEquals("getAttributeNamespace()","", xpp.getAttributeNamespace(pos));
        assertEquals("getAttributeName()",name, xpp.getAttributeName(pos));
        assertEquals("getAttributeValue()",value, xpp.getAttributeValue(pos));
        assertEquals("getAttributeValue(name)",value, xpp.getAttributeValue(null, name));
    }


    public void checkAttribNs(
        XmlPullParser xpp,
        int pos,
        String prefix,
        String namespace,
        String name,
        String value
    ) throws XmlPullParserException, IOException
    {
        assertEquals("must be on START_TAG", xpp.START_TAG, xpp.getEventType());
        assertEquals("getAttributePrefix()",prefix, xpp.getAttributePrefix(pos));
        assertEquals("getAttributeNamespace()",namespace, xpp.getAttributeNamespace(pos));
        assertEquals("getAttributeName()",name, xpp.getAttributeName(pos));
        assertEquals("getAttributeValue()",printable(value), printable(xpp.getAttributeValue(pos)));
        assertEquals("getAttributeValue(ns,name)",
                     printable(value), printable(xpp.getAttributeValue(namespace, name)));
    }

    public void checkNamespace(
        XmlPullParser xpp,
        int pos,
        String prefix,
        String uri,
        boolean checkMapping
    ) throws XmlPullParserException, IOException
    {
        assertEquals("getNamespacePrefix()",prefix, xpp.getNamespacePrefix(pos));
        assertEquals("getNamespaceUri()",uri, xpp.getNamespaceUri(pos));
        if(checkMapping) {
            assertEquals("getNamespace(prefix)", uri, xpp.getNamespace (prefix));
        }
    }

    protected String printable(char ch) {
        if(ch == '\n') {
            return "\\n";
        } else if(ch == '\r') {
            return "\\r";
        } else if(ch == '\t') {
            return "\\t";
        } if(ch > 127 || ch < 32) {
            return "\\u"+Integer.toHexString((int)ch);
        }
        return ""+ch;
    }

    protected String printable(String s) {
        if(s == null) return null;
        StringBuffer buf = new StringBuffer();
        for(int i = 0; i < s.length(); ++i) {
            buf.append(printable(s.charAt(i)));
        }
        s = buf.toString();
        return s;
    }

}

