/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// see LICENSE.txt in distribution for copyright and license information

package org.xmlpull.v1;

import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This class is used to create implementations of XML Pull Parser defined in XMPULL V1 API.
 * The name of actual facotry class will be determied based on several parameters.
 * It works similar to JAXP but tailored to work in J2ME environments
 * (no access to system properties or file system) so name of parser class factory to use
 * and its class used for loading (no classloader - on J2ME no access to context class loaders)
 * must be passed explicitly. If no name of parser factory was passed (or is null)
 * it will try to find name by searching in CLASSPATH for
 * META-INF/services/org.xmlpull.v1.XmlPullParserFactory resource that should contain
 * a comma separated list of class names of factories or parsers to try (in order from
 * left to the right). If none found, it will throw an exception.
 *
 * <p><strong>NOTE:</strong>In J2SE or J2EE environments, you may want to use
 * <code>newInstance(property, classLoaderCtx)</code>
 * where first argument is
 * <code>System.getProperty(XmlPullParserFactory.PROPERTY_NAME)</code>
 * and second is <code>Thread.getContextClassLoader().getClass()</code> .
 *
 * @see XmlPullParser
 *
 * @author Aleksander Slominski [http://www.extreme.indiana.edu/~aslom/], Stefan Haustein
 */

public class XmlPullParserFactory {

    /** Name of the system or midlet property that should be used for
        a system property containing a comma separated list of factory
        or parser class names (value:
        org.xmlpull.v1.XmlPullParserFactory). */


    public static final String PROPERTY_NAME = 
        "org.xmlpull.v1.XmlPullParserFactory";

    private static final String RESOURCE_NAME = 
        "/META-INF/services/" + PROPERTY_NAME;


    // public static final String DEFAULT_PROPERTY =
    //    "org.xmlpull.xpp3.XmlPullParser,org.kxml2.io.KXmlParser";


    protected Vector parserClasses;

    // features are kept there
    protected Hashtable features = new Hashtable();


    /**
     * Protected constructor to be called by factory implementations.
     */

    protected XmlPullParserFactory() {
    }



    /**
     * Set the features to be set when XML Pull Parser is created by this factory.
     *
     * @param name string with URI identifying feature
     * @param state if true feature will be set; if false will be ignored
     */

    public void setFeature(String name,
                           boolean state) throws XmlPullParserException {
        
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

    /**
     * Specifies that the parser produced by this factory will provide
     * support for XML namespaces.
     * By default the value of this is set to false.
     *
     * @param awareness true if the parser produced by this code
     *    will provide support for XML namespaces;  false otherwise.
     */

    public void setNamespaceAware(boolean awareness) {
        features.put (XmlPullParser.FEATURE_PROCESS_NAMESPACES, new Boolean (awareness));
    }

    /**
     * Indicates whether or not the factory is configured to produce
     * parsers which are namespace aware.
     *
     * @return  true if the factory is configured to produce parsers
     *    which are namespace aware; false otherwise.
     */

    public boolean isNamespaceAware() {
        return getFeature (XmlPullParser.FEATURE_PROCESS_NAMESPACES);
    }


    /**
     * Specifies that the parser produced by this factory will be validating
     *
     * By default the value of this is set to false.
     *
     * @param validating - if true the parsers created by this factory  must be validating.
     */

    public void setValidating(boolean validating) {
        features.put (XmlPullParser.FEATURE_VALIDATION, new Boolean (validating));
    }

    /**
     * Indicates whether or not the factory is configured to produce parsers
     * which validate the XML content during parse.
     *
     * @return   true if the factory is configured to produce parsers
     * which validate the XML content during parse; false otherwise.
     */

    public boolean isValidating() {
        return getFeature (XmlPullParser.FEATURE_VALIDATION);
    }

    /**
     * Creates a new instance of a XML Pull Parser
     * using the currently configured factory parameters.
     *
     * @return A new instance of a XML Pull Parser.
     * @throws XmlPullParserException if a parser cannot be created which satisfies the
     * requested configuration.
     */

    public XmlPullParser newPullParser() throws XmlPullParserException {
        
        if (parserClasses.size () == 0) throw new XmlPullParserException 
            ("No valid parser classes found in "+RESOURCE_NAME);

        StringBuffer issues = new StringBuffer ();

        for (int i = 0; i < parserClasses.size (); i++) {
            Class ppClass = (Class) parserClasses.elementAt (i);
            try {
                XmlPullParser pp = (XmlPullParser) ppClass.newInstance();
                //            if( ! features.isEmpty() ) {
                //Enumeration keys = features.keys();
                // while(keys.hasMoreElements()) {

                for (Enumeration e = features.keys (); e.hasMoreElements ();) {
                    String key = (String) e.nextElement();
                    Boolean value = (Boolean) features.get(key);
                    if(value != null && value.booleanValue()) {
                        pp.setFeature(key, true);
                    }
                }
                return pp;

            } catch(Exception ex) {
                issues.append (ppClass.getName () + ": "+ ex.toString ()+"; ");
            }
        }

        throw new XmlPullParserException ("could not create parser: "+issues);
    }
    
    /**
     * Create a new instance of a PullParserFactory that can be used
     * to create XML pull parsers (see class description for more
     * details).
     *
     * @return a new instance of a PullParserFactory, as returned by newInstance (null, null); */
    
    public static XmlPullParserFactory newInstance () throws XmlPullParserException {
        return newInstance(null, null);
    }



    public static XmlPullParserFactory newInstance (String classNames, Class context)
        throws XmlPullParserException {

        if (context == null) context = "".getClass ();

        if (classNames == null) {
            try {
                InputStream is = context.getResourceAsStream (RESOURCE_NAME);

                if (is == null) throw new XmlPullParserException 
                    ("Ressource not found: "+RESOURCE_NAME);
                
                StringBuffer sb = new StringBuffer();
                
                while (true) {
                    int ch = is.read();
                    if (ch < 0) break;  
                    else if (ch > ' ') 
                        sb.append((char) ch);
                }
                is.close ();

                classNames = sb.toString ();
            }
            catch (Exception e) {
                throw new XmlPullParserException (null, null, e);
            }        
        }            

        XmlPullParserFactory factory = null;
        Vector parserClasses = new Vector ();
        int pos = 0;

        while (pos < classNames.length ()) {
            int cut = classNames.indexOf (',', pos);

            if (cut == -1) cut = classNames.length ();
            String name = classNames.substring (pos, cut);

            Class candidate = null;
            Object instance = null;
                        
            try {
                candidate = Class.forName (name);
                // neccessary because of J2ME .class issue
                instance = candidate.newInstance (); 
            }
            catch (Exception e) {}
                        
            if (candidate != null) { 
                if (instance instanceof XmlPullParser)
                    parserClasses.addElement (candidate);
                else if (instance instanceof XmlPullParserFactory) {
                    if (factory == null) 
                        factory = (XmlPullParserFactory) instance;
                }
                else throw new XmlPullParserException ("incompatible class: "+name);
            }

            pos = cut + 1;
        }                        
        
        if (factory == null) factory = new XmlPullParserFactory ();
        factory.parserClasses = parserClasses; 

        return factory;
    }
}




