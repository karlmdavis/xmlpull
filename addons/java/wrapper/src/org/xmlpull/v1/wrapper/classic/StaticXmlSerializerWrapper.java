/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license please see accompanying LICENSE.txt file (available also at http://www.xmlpull.org/)

package org.xmlpull.v1.wrapper.classic;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
import org.xmlpull.v1.util.XmlPullUtil;
import org.xmlpull.v1.wrapper.XmlSerializerWrapper;

/**
 * This class seemlesly extends exisiting serialzier implementation by adding new methods
 * (provided by XmlPullUtil) and delegating exisiting methods to parser implementation.
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 * @author Naresh Bhatia
 */
public class StaticXmlSerializerWrapper extends XmlSerializerDelegate
    implements XmlSerializerWrapper
{
    protected String currentNs;

    public StaticXmlSerializerWrapper(XmlSerializer xs) {
        super(xs);
    }

    public String getCurrentNamespace() { return currentNs; }
    public void setCurrentNamespace(String value) { currentNs = value; }


    public XmlSerializer startTag (String name)
        throws IOException, IllegalArgumentException, IllegalStateException
    {
        return xs.startTag(currentNs, name);
    }

    public XmlSerializer endTag (String name)
        throws IOException, IllegalArgumentException, IllegalStateException
    {
        return xs.endTag(currentNs, name);
    }

    /** Write simple text element in current namespace */
    public void element(String elementName, String elementText)
        throws IOException, XmlPullParserException
    {
        element(currentNs, elementName, elementText);
    }

    public void element(String namespace, String elementName, String elementText)
        throws IOException, XmlPullParserException
    {

        if (elementName == null) {
            throw new XmlPullParserException("name for element can not be null");
        }

        xs.startTag(namespace, elementName);
        if (elementText == null) {
            xs.attribute(XSI_NS, "nil", "true");
        }
        else {
            xs.text(elementText);
        }
        xs.endTag(namespace, elementName);
    }
}

