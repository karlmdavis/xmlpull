/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// see LICENSE.txt in distribution for copyright and license information

package org.xmlpull.v1;

import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * This class is used to create implementations of XML Pull Parser defined in XMPULL V1 API.
 * The name of actual facotry class will be determied based on several parameters.
 * It works similar to JAXP but tailored to work in J2ME environments
 * (no access to system properties or file system) so name of parser class factory to use
 * and its class used for loading (no classloader - on J2ME no access to context class loaders)
 * must be passed explicitly. If no name of parser factory was passed (or is null)
 * it will try to find name by searching in CLASSPATH for
 * META-INF/services/org.xmlpull.v1.XmlPullParserFactory resource that should contain
 * the name of parser facotry class. If none found it will try to create a default parser
 * factory (if available) or throw exception.
 *
 * <p><strong>NOTE:</strong>In J2SE or J2EE environments to get best results use
 * <code>newInstance(property, classLoaderCtx)</code>
 * where first argument is
 * <code>System.getProperty(XmlPullParserFactory.DEFAULT_PROPERTY_NAME)</code>
 * and second is <code>Thread.getContextClassLoader().getClas()</code> .
 *
 * @see XmlPullParser
 *
 * @author Aleksander Slominski [http://www.extreme.indiana.edu/~aslom/]
 */

public class XmlPullParserFactory
{

    /** name of parser factory property that should be used for system property
     * or in general to retrieve parser factory clas sname from configuration
     * (currently name of peroperty is org.xmlpull.v1.XmlPullParserFactory)
     */
    public static final String PROPERTY_NAME =
        "org.xmlpull.v1.XmlPullParserFactory";
    private static final String RESOURCE_NAME =
        "/META-INF/services/" + PROPERTY_NAME;

    private static final String DEFAULT_PROPERTY =
        "org.xmlpull.xpp3.XmlPullParser,org.kxml2.io.KXmlParser";


    // features are kept there
    protected Hashtable features = new Hashtable();

    private static final boolean DEBUG = false;

    private static final String DEBUG_PREFIX = "DEBUG XMLPULL factory: ";

    /** Private method for debugging */
    private static void debug(String msg) {
        if(!DEBUG)
            throw new RuntimeException(
                "only when DEBUG enabled can print messages");
        System.err.println(DEBUG_PREFIX+msg);
    }


    private Class foundParserClass;

    /**
     * Protected constructor to be called by factory implementations.
     */
    protected XmlPullParserFactory()
    {
    }

    /**
     * Protected constructor to be called by factory implementations.
     */
    protected XmlPullParserFactory(Class foundParserClass)
    {
        this.foundParserClass = foundParserClass;
    }

    /**
     * Set the features to be set when XML Pull Parser is created by this factory.
     *
     * @param name string with URI identifying feature
     * @param state if true feature will be set; if false will be ignored
     */
    public void setFeature(String name,
                           boolean state) throws XmlPullParserException
    {
        features.put(name, new Boolean(state));
    }

    /**
     * Return the current value of the feature with given name.
     *
     * @param name The name of feature to be retrieved.
     * @return The value of named feature.
     *     Unknown features are <string>always</strong> returned as false
     */
    public boolean getFeature (String name) {
        Boolean value = (Boolean) features.get(name);
        return value != null ? value.booleanValue() : false;
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
//        features.put(XmlPullParser.FEATURE_PROCESS_NAMESPACES, new Boolean(awareness));
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
//        Boolean value = (Boolean) features.get(XmlPullParser.FEATURE_PROCESS_NAMESPACES);
//        return value != null ? value.booleanValue() : false;
//    }
//
//    /**
//     * Specifies that the parser produced by this factory will be validating
//     *
//     * By default the value of this is set to false.
//     *
//     * @param validating - if true the parsers created by this factory  must be validating.
//     */
//    public void setValidating(boolean validating)
//        throws XmlPullParserException
//    {
//        features.put(XmlPullParser.FEATURE_VALIDATION, new Boolean(validating));
//    }
//
//    /**
//     * Indicates whether or not the factory is configured to produce parsers
//     * which validate the XML content during parse.
//     *
//     * @return   true if the factory is configured to produce parsers
//     * which validate the XML content during parse; false otherwise.
//     */
//    public boolean isValidating()
//    {
//        Boolean value = (Boolean) features.get(XmlPullParser.FEATURE_VALIDATION);
//        return value != null ? value.booleanValue() : false;
//    }

    /**
     *
     * Creates a new instance of a XML Pull Parser
     * using the currently configured factory parameters.
     *
     * @return A new instance of a XML Pull Parser.
     * @throws XmlPullParserException if a parser cannot be created which satisfies the
     * requested configuration.
     */
    public XmlPullParser newPullParser() throws XmlPullParserException
    {
        try {
            XmlPullParser pp =  (XmlPullParser) foundParserClass.newInstance();
            if( ! features.isEmpty() ) {
                Enumeration keys = features.keys();
                while(keys.hasMoreElements()) {
                    String key = (String) keys.nextElement();
                    Boolean value = (Boolean) features.get(key);
                    if(value != null && value.booleanValue() == true) {
                        pp.setFeature(key, true);
                    }
                }
            }
            return pp;
        } catch(Exception ex) {
            throw new XmlPullParserException(
                "could not create parser "+foundParserClass+" : "+ex, ex);
        }
    }

    /**
     * Create a new instance of a PullParserFactory used to create XML pull parser
     * (see description of class for more details).
     *
     * @return result of call to newInstance(null, null)
     */
    public static XmlPullParserFactory newInstance()
        throws XmlPullParserException
    {
        return newInstance(null, null);
    }

    /**
     * Get a new instance of a PullParserFactory from given class name.
     *
     * @param property use specified factory class if not null
     * @return result of call to newInstance(null, factoryClassName)
     */
    public static XmlPullParserFactory newInstance(String property)
        throws XmlPullParserException
    {
        return newInstance( null, property );
    }


    /**
     * Get instance of XML pull parser factiry.
     *
     * <p><b>NOTE:</b>  this allows to use -D system properties indirectly and still
     *    to support flexible configuration in J2ME environments..
     *
     * @param classLoaderCtx if null Class.forName will be used instead
     *    - simple way to use class loaders and still have ME compatibility!
     * @param hint with name of parser factory to use -
     *   it is a hint and is ignored if factory is not available.
     */
    public static XmlPullParserFactory newInstance(Class classLoaderCtx,
                                                   String property)
        throws XmlPullParserException
    {

        if(classLoaderCtx == null) classLoaderCtx = XmlPullParserFactory.class;
        if(property == null) {
            try {
                InputStream is = classLoaderCtx.getResourceAsStream( RESOURCE_NAME );

                if( is != null ) {

                    StringBuffer sb = new StringBuffer();

                    while (true) {
                        int ch = is.read();
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

                    property = sb.toString();
                }
            } catch(Exception ex) {}
        }
        if(property == null) property = DEFAULT_PROPERTY;

        String name = null;
        int start = 0;
        Class parserClass = null;
        for (int i = 0; i <= property.length(); i++)
        {
            // tricky: first check if passed last character and only check for comma at i!!!
            if((i == property.length() || property.charAt(i) == ',' ) && i > start )  {
                name = property.substring(start, i);
                start = i + 1;
                try {
                    Class klass = classLoaderCtx.forName(name);
                    Object o =klass.newInstance();
                    if( o instanceof XmlPullParserFactory ) {
                        return (XmlPullParserFactory) o;
                    } else if( o instanceof XmlPullParser ) {
                        parserClass = klass;
                        break;
                    }
                } catch  (Exception ex) {
                    if(DEBUG) {
                        debug("failed to load class "+name);
                        ex.printStackTrace();
                    }
                }
            }
        }
        if(parserClass != null) return new XmlPullParserFactory(parserClass);
        throw new XmlPullParserException(
            "could not create parser or factory from '"+property+"'");
    }

}

