/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license please see accompanying LICENSE.txt file (available also at http://www.xmlpull.org/)

package org.xmlpull.v1.wrapper.classic;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.wrapper.XmlPullParserWrapper;
import org.xmlpull.v1.util.XmlPullUtil;

/**
 * This class seemlesly extends exisiting parser implementation by adding new methods
 * (provided by XmlPullUtil) and delegating exisiting methods to parser implementation.
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 */
public class StaticXmlPullParserWrapper extends XmlPullParserDelegate
    implements XmlPullParserWrapper
{
    public StaticXmlPullParserWrapper(XmlPullParser pp) {
        super(pp);
    }

    public String getAttributeValue(String name)
    {
        return XmlPullUtil.getAttributeValue(pp, name);
    }


  /**
     * Read the text of a required element and return it or throw exception if
     * required element is not found. Useful for getting the text of simple
     * elements such as <username>johndoe</username>. Assumes that parser is
     * just before the start tag and leaves the parser at the end tag. If the
     * text is nil (e.g. <username xsi:nil="true"/>), then a null will be returned.
     */

    public String getRequiredElementText(String namespace, String name)
        throws IOException, XmlPullParserException {
            if (name == null) {
                throw new XmlPullParserException("name for element can not be null");
            }

            String text = null;
            nextStartTag(namespace, name);
            if (isNil()) {
                nextEndTag(namespace, name);
            }
            else {
                text = pp.nextText();
            }
            pp.require(XmlPullParser.END_TAG, namespace, name);
            return text;
    }

    /**
     * Is the current tag nil? Checks for xsi:nil="true".
     */
    public boolean isNil()
        throws IOException, XmlPullParserException {

        boolean result = false;
        String value = pp.getAttributeValue(XSI_NS, "nil");
        if ("true".equals(value)) {
            result = true;
        }

        return result;
    }

    public String getPITarget() throws IllegalStateException {
        return XmlPullUtil.getPITarget(pp);
    }

    public String getPIData() throws IllegalStateException {
        return XmlPullUtil.getPIData(pp);
    }

    public boolean matches(int type, String namespace, String name)
        throws XmlPullParserException
    {
        return XmlPullUtil.matches(pp, type, namespace, name);
    }

    /**
     * call parser nextTag() and check that it is START_TAG, throw exception if not.
     */
    public void nextStartTag()
        throws XmlPullParserException, IOException
    {
        XmlPullUtil.nextStartTag(pp);
    }

    /**
     * combine nextTag(); pp.require(pp.START_TAG, namespace, name);
     */
    public void nextStartTag(String namespace, String name)
        throws XmlPullParserException, IOException
    {
        XmlPullUtil.nextStartTag(pp, namespace, name);
    }

    /**
     * combine nextTag(); pp.require(pp.END_TAG, namespace, name);
     */
    public void nextEndTag(String namespace, String name)
        throws XmlPullParserException, IOException
    {
        XmlPullUtil.nextEndTag(pp, namespace, name);
    }


    /**
     * Read text content of element with given namespace and name
     * (use null namespace do indicate that nemspace should not be checked)
     */

    public String nextText(String namespace, String name)
        throws IOException, XmlPullParserException
    {
        return XmlPullUtil.nextText(pp, namespace, name);
    }

    /**
     * Read attribute value and return it or throw exception if
     * current element does not have such attribute.
     */

    public String getRequiredAttributeValue(String namespace, String name)
        throws IOException, XmlPullParserException
    {
        return XmlPullUtil.getRequiredAttributeValue(pp, namespace, name);
    }

    public void nextEndTag() throws XmlPullParserException, IOException {
        XmlPullUtil.nextEndTag(pp);
    }


    public void skipSubTree() throws XmlPullParserException, IOException {
        XmlPullUtil.skipSubTree(pp);
    }

}

