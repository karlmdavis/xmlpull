/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license please see accompanying LICENSE.txt file (available also at http://www.xmlpull.org/)

package org.xmlpull.v1.wrapper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;
import org.xmlpull.v1.wrapper.classic.StaticXmlPullParserWrapper;
import org.xmlpull.v1.wrapper.classic.StaticXmlSerializerWrapper;

/**
 * Handy functions that combines XmlPull API into higher level functionality.
 * <p>NOTE: returned wrapper object is <strong>not</strong> multi-thread safe
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 */

public class XmlPullWrapperFactory {
    private final static boolean DEBUG = false;
    protected ClassLoader classLoader;
    protected XmlPullParserFactory factory;
    protected boolean useDynamic;

    public static XmlPullWrapperFactory newInstance() throws XmlPullParserException
    {
        //TODO: make into real pluggable factory service (later ...)?
        return new XmlPullWrapperFactory(null);
    }

    public static XmlPullWrapperFactory newInstance(XmlPullParserFactory factory)
        throws XmlPullParserException
    {
        return new XmlPullWrapperFactory(factory);
    }

    // ------------ IMPLEMENTATION

    protected XmlPullWrapperFactory(XmlPullParserFactory factory) throws XmlPullParserException {
        if(factory != null) {
            this.factory = factory;
        } else {
            this.factory = XmlPullParserFactory.newInstance();
        }
    }


    public void setFeature(String name,
                           boolean state) throws XmlPullParserException
    {
        factory.setFeature(name, state);
    }


    public boolean getFeature (String name) {
        return factory.getFeature(name);
    }

    public void setNamespaceAware(boolean awareness) {
        factory.setNamespaceAware(awareness);
    }

    public boolean isNamespaceAware() {
        return factory.isNamespaceAware();
    }

    public void setValidating(boolean validating) {
        factory.setValidating(validating);
    }

    public boolean isValidating() {
        return factory.isValidating();
    }
    //public void setUseDynamic(boolean enable) { useDynamic = enable; };
    //public boolean getUseDynamic() { return useDynamic; };

    public XmlPullParserWrapper newPullWrapper() throws XmlPullParserException {
        XmlPullParser pp = factory.newPullParser();
        //        if(useDynamic) {
        //            return (XmlPullParserWrapper) DynamicXmlPullParserWrapper.newProxy(pp, classLoader);
        //        } else {
        return new StaticXmlPullParserWrapper(pp);
    }

    public XmlPullParserWrapper newPullWrapper(XmlPullParser pp) throws XmlPullParserException {
        return new StaticXmlPullParserWrapper(pp);
    }

    public XmlSerializerWrapper newSerializerWrapper() throws XmlPullParserException {
        XmlSerializer xs = factory.newSerializer();
        return new StaticXmlSerializerWrapper(xs);
    }

    public XmlSerializerWrapper newSerializerWrapper(XmlSerializer xs) throws XmlPullParserException {
        return new StaticXmlSerializerWrapper(xs);
    }

}

