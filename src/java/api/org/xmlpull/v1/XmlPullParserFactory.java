/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// see LICENSE.txt in distribution for copyright and license information

package org.xmlpull.v1;

import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * This class is used to create implementations of  XML Pull Parser.
 * Based on JAXP ideas but tailored to work in J2ME environments
 * (no access to system properties or file system) so it creates
 * only one kind of parser but souce code can be moved without any
 * changes to J2SE or J2EE environment.
 *
 * @see XmlPullParser
 * @author Aleksander Slominski [aslom@extreme.indiana.edu]
 */

public class XmlPullParserFactory
{
    private static final boolean DEBUG = false;

    public static final String DEFAULT_PROPERTY_NAME =
        "org.xmlpull.v1.XmlPullParserFactory";

    //private static Class MY_CLASS;
    private static Object MY_REF = new XmlPullParserFactory();
    private static final String DEFAULT_KXML_IMPL_FACTORY_CLASS_NAME =
        "org.xmlpull.v1.kxml.KXmlPullParserFactoryImpl";
    private static final String DEFAULT_XPP_IMPL_FACTORY_CLASS_NAME =
        "org.xmlpull.v1.xpp.XppPullParserFactorySmallImpl";
    private static final String DEFAULT_RESOURCE_NAME =
        "/META-INF/services/" + DEFAULT_PROPERTY_NAME;
    private static String foundFactoryClassName = null;
    protected boolean namespaceAware;

    /**
     * Proteted constructor to be called by factory implementations.
     */
    protected XmlPullParserFactory()
    {
    }

    /**
     * Get a new instance of a PullParserFactory used to create XML pull parser.
     */
    public static XmlPullParserFactory newInstance()
        throws XmlPullParserException
    {
        return newInstance(null, null);
    }

    /**
     * Get a new instance of a PullParserFactory from given class name.
     *
     * @param factoryClassName use specified factory class if not null
     */
    public static XmlPullParserFactory newInstance(String factoryClassName)
        throws XmlPullParserException
    {
        return newInstance( null, factoryClassName );
    }

    /**
     * Get a new instance of a PullParserFactory used to create XML Pull Parser.
     * <p><b>NOTE:</b> passing classLoaderCtx is not very useful in ME
     *    but can be useful in container environment where
     *    multiple class loaders are used
     *    (it is using Class as ClassLoader is not in ME profile).
     *
     * @param classLoaderCtx if not null it is used to find
     *    default factory and to create instance
     */
    public static XmlPullParserFactory newInstance(Class classLoaderCtx)
        throws XmlPullParserException
    {
        return newInstance( classLoaderCtx, null );
    }

    private final static String PREFIX = "DEBUG XMLPULL factory: ";

    private static void debug(String msg) {
        if(!DEBUG)
            throw new RuntimeException(
                "only when DEBUG enabled can print messages");
        System.err.println(PREFIX+msg);
    }

    /**
     * Get instance of XML pull parser factiry.
     *
     * <p><b>NOTE:</b>  this allows to use elegantly -D system properties or
     *    similar configuratin in ME environments..
     *
     * @param classLoaderCtx if null Class.forName will be used instead
     *    - simple way to use class loaders and still have ME compatibility!
     * @param hint with name of parser factory to use -
     *   it is a hint and is ignored if factory is not available.
     */
    private static XmlPullParserFactory newInstance(Class classLoaderCtx,
                                                    String factoryClassName)
        throws XmlPullParserException
    {

        // if user hinted factory then try to use it ...


        XmlPullParserFactory factoryImpl = null;
        if(factoryClassName != null) {
            try {
                //*
                Class clazz = null;
                if(classLoaderCtx != null) {
                    clazz = classLoaderCtx.forName(factoryClassName);
                } else {
                    clazz = Class.forName(factoryClassName);
                }
                factoryImpl = (XmlPullParserFactory) clazz.newInstance();
                //foundFactoryClassName = factoryClassName;
                //*/
                if(DEBUG) debug("loaded "+clazz);
            } catch  (Exception ex) {
                if(DEBUG) debug("failed to load "+factoryClassName);
                if(DEBUG) ex.printStackTrace();
            }
        }

        // if could not load then proceed with pre-configured
        if(factoryImpl == null) {

            // default factory is unknown - try to find it!
            if(foundFactoryClassName == null) {
                findFactoryClassName( classLoaderCtx );
            }

            if(foundFactoryClassName != null) {
                try {

                    Class clazz = null;
                    if(classLoaderCtx != null) {
                        clazz = classLoaderCtx.forName(foundFactoryClassName);
                    } else {
                        clazz = Class.forName(foundFactoryClassName);
                    }
                    factoryImpl = (XmlPullParserFactory) clazz.newInstance();
                    if(DEBUG) debug("loaded pre-configured "+clazz);
                } catch  (Exception ex) {
                    if(DEBUG) debug("failed to use pre-configured "
                                        +foundFactoryClassName);
                    if(DEBUG) ex.printStackTrace();
                }
            }
        }

        // still could not load then proceed with default
        if(factoryImpl == null) {
            try {
                Class clazz = null;
                factoryClassName = DEFAULT_XPP_IMPL_FACTORY_CLASS_NAME;
                // give one more chance for small implementation
                if(classLoaderCtx != null) {
                    clazz = classLoaderCtx.forName(factoryClassName);
                } else {
                    clazz = Class.forName(factoryClassName);
                }
                factoryImpl = (XmlPullParserFactory) clazz.newInstance();

                if(DEBUG) debug("using default full implementation "
                                    +factoryImpl);

                // make it as pre-configured default
                foundFactoryClassName = factoryClassName;

            } catch( Exception ex2) {
                try {
                    Class clazz = null;
                    factoryClassName = DEFAULT_KXML_IMPL_FACTORY_CLASS_NAME;
                    // give one more chance for small implementation
                    if(classLoaderCtx != null) {
                        clazz = classLoaderCtx.forName(factoryClassName);
                    } else {
                        clazz = Class.forName(factoryClassName);
                    }
                    factoryImpl = (XmlPullParserFactory) clazz.newInstance();

                    if(DEBUG) debug("no factory was found instead "+
                                        "using small default impl "+factoryImpl);

                    // now it is pre-configured default
                    foundFactoryClassName = factoryClassName;

                } catch( Exception ex3) {
                    throw new XmlPullParserException(
                        "could not load any factory class "+
                            "(even small or full default implementation)", ex3);
                }
            }
        }

        // return what was found..
        if(factoryImpl == null) throw new RuntimeException(
                "XMLPULL: internal parser factory error");
        return factoryImpl;
    }

    // --- private utility methods

    private static void findFactoryClassName( Class classLoaderCtx )
    {
        if(foundFactoryClassName != null) //return; // foundFactoryClassName;
            throw new RuntimeException("internal XMLPULL initialization error");

        InputStream is = null;
        try {

            if(classLoaderCtx != null) {
                if(DEBUG) debug(
                        "trying to load "+DEFAULT_RESOURCE_NAME+
                            " from "+classLoaderCtx);
                is = classLoaderCtx.getResourceAsStream( DEFAULT_RESOURCE_NAME );
            }

            if(is == null) {
                Class klass = MY_REF.getClass(); //XmlPullParserFactory.getClass();
                if(DEBUG) debug(
                        "opening "+DEFAULT_RESOURCE_NAME+
                            " (class context "+klass+")");
                is = klass.getResourceAsStream( DEFAULT_RESOURCE_NAME );
            }

            if( is != null ) {

                foundFactoryClassName = readLine( is );

                if( DEBUG ) debug(
                        "foundFactoryClassName=" + foundFactoryClassName );

                //if( foundFactoryClassName != null
                //   && !  "".equals( foundFactoryClassName ) ) {
                //  return foundFactoryClassName;
                //}
            }
        } catch( Exception ex ) {
            if( DEBUG ) ex.printStackTrace();
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch(Exception ex) {
                }
            }
        }
        //return foundFactoryClassName;
    }

    private static String readLine(InputStream input) throws IOException
    {
        StringBuffer sb = new StringBuffer();

        while (true) {
            int ch = input.read();
            if (ch < 0) {
                break;
            } else if (ch == '\n') {
                break;
            }
            sb.append((char) ch);
        }

        // strip end-of-line \r\n if necessary
        int n = sb.length();
        if ((n > 0) && (sb.charAt(n - 1) == '\r')) {
            sb.setLength(n - 1);
        }

        return (sb.toString());
    }

//    /**
//     * Specifies that the parser produced by this factory will provide
//     * support for XML namespaces.
//     * By default the value of this is set to false.
//     *
//     * @param awareness true if the parser produced by this code
//     *    will provide support for XML namespaces;  false otherwise.
//     */
//    public void setNamespaceAware(boolean awareness)
//        throws XmlPullParserException
//    {
//        namespaceAware = awareness;
//    }
//
//    /**
//     * Indicates whether or not the factory is configured to produce
//     * parsers which are namespace aware.
//     *
//     * @return  true if the factory is configured to produce parsers
//     *    which are namespace aware; false otherwise.
//     */
//    public boolean isNamespaceAware()
//    {
//        return namespaceAware;
//    }

    /**
     * Create new XML pull parser with default setting.
     */
    public XmlPullParser newPullParser() throws XmlPullParserException {
//        XmlPullParser pp = new org.xmlpull.v1.kxml.KXmlPullParserImpl();
//        pp.setNamespaceAware(namespaceAware);
//        return pp;
        //throw new XmlPullParserException("not implemented");
        throw new XmlPullParserException(
            "newPullParser() must be implemented by other class");

    }

    /**
     * Create new XML pull parser with passed propertiesin binary flasgs
     * @see XmlPullParser for list of supported flags
     */
    public XmlPullParser newPullParser(int properties) throws XmlPullParserException {
        throw new XmlPullParserException(
            "newPullParser() must be implemented by other class");

    }

}

