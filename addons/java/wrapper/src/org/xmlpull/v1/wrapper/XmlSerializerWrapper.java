/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license please see accompanying LICENSE.txt file (available also at http://www.xmlpull.org/)

package org.xmlpull.v1.wrapper;

import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;

/**
 * Extensions to XmlSerialzier interface
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 */
public interface XmlSerializerWrapper extends XmlSerializer {
    public static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";

    public String getDefaultNamespace();
    public void setDefaultNamespace(String value);

    public XmlSerializer startTag (String name)
        throws IOException, IllegalArgumentException, IllegalStateException;

    public XmlSerializer endTag (String name)
        throws IOException, IllegalArgumentException, IllegalStateException;
}

