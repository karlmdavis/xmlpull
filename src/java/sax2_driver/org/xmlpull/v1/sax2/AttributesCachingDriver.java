/* -*- mode: Java; c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/

package org.xmlpull.v1.sax2;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Extensions of SAX2 Driver that provides a separate Attributes object
 * for each startElement call so it is safe to keep the reference to Attributes.
 *
 * @author <a href="mailto:hkrug@rationalizer.com">Holger Krug</a>
 */
public class AttributesCachingDriver extends Driver
{
    public AttributesCachingDriver() throws XmlPullParserException {
        super();
    }

    public AttributesCachingDriver(XmlPullParser pp) throws XmlPullParserException {
        super(pp);
    }

    /**
     * Calls {@link ContentHandler.startElement(String, String,
     * String, Attributes) startElement} on the
     * <code>ContentHandler</code> with copied attribute values. The
     * {@link Attributes} object is may be stored.
     */
    protected void startElement(String namespace, String localName, String qName) throws SAXException {
        contentHandler.startElement(namespace, localName, qName, new AttributesImpl(this));
    }

}



