/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license please see accompanying LICENSE.txt file (available also at http://www.xmlpull.org/)

package org.xmlpull.v1.wrapper;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/**
 * Extensions to XmlSerialzier interface
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 * @author Naresh Bhatia
 */
public interface XmlSerializerWrapper extends XmlSerializer {
    public static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";

    public String getCurrentNamespace();
    public void setCurrentNamespace(String value);

    /** Write start tag in current namespace with name given as argument. */
    public XmlSerializer startTag (String name)
        throws IOException, IllegalArgumentException, IllegalStateException;

    /** Write end tag in current namespace with name given as argument. */
    public XmlSerializer endTag (String name)
        throws IOException, IllegalArgumentException, IllegalStateException;


    /**
     * Writes a simple element such as &lt;username>johndoe&lt;/username>. The namespace
     * and elementText are allowed to be null. If elementText is null, an xsi:nil="true"
     * will be added as an attribute.
     */
    public void element(String namespace, String elementName, String elementText)
        throws IOException, XmlPullParserException;

    /** Write simple text element in current namespace */
    public void element(String elementName, String elementText)
        throws IOException, XmlPullParserException;

}

