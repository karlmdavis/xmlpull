/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// see LICENSE.txt in distribution for copyright and license information

package org.xmlpull.v1;

import java.io.IOException;
import java.io.Reader;

/**
 * Interface defining XML Pull Parser.
 *
 * @author Stefan Haustein
 * @author Aleksander Slominski [http://www.extreme.indiana.edu/~aslom/]
 */

public interface XmlPullParser {

    /** this constant represent empty default namespace (empty string "") */
    public static final String NO_NAMESPACE = "";

    // ----------------------------------------------------------------------------
    // EVENT TYPES as reported by next()

    /**
     * Return value of getType before first call to next() or nextToken()
     */
    public final static int START_DOCUMENT = 0;

    /**
     * Signal logical end of xml document
     * Calling next() or nextToken() any more will cause exception to be thrown.
     */
    public final static int END_DOCUMENT = 1;

    /**
     * Start tag was just read
     */
    public final static int START_TAG = 2;

    /**
     * End tag was just read
     */
    public final static int END_TAG = 3;


    /**
     * NOTE: This is and following constants are only returned by nextToken(),
     * element content was just read by next() or nextToken().
     * <p>NOTE: next() will (in contrast to nextToken ()) accumulate multiple
     * TEXT events into one, skipping inbetween IGNORABLE_WHITESPACE,
     * PROCESSING_INSTRUCTION and COMMENT events.
     */
    public final static int TEXT = 4;

    // ----------------------------------------------------------------------------
    // additional events exposed by lower level nextToken()

    /**
     * CDATA sections was just read and value of text inside is available  by callling getText()
     */
    public final static byte CDSECT = 5;

    /**
     * Entity reference was just read.
     * The value of entity is available as geText() (null if unknown entity)
     * and the entity naem is available by calling getName()
     */
    public final static byte ENTITY_REF            = 6;

    /**
     * Ignorable whitespace was just read. For non-validating
     * parsers, this event is only reported by nextToken() when
     * outside the root elment.
     * Validating parsers may be able to detect ignorable whitespace at
     * other locations. Please note the differenct to isWhitespace().
     * The value of whotespace is available by calling getText()
     */
    public static final byte IGNORABLE_WHITESPACE = 7;




    /**
     * XML processing instruction declaration was just read
     * and getText() will return text that is inside processing instruction.
     */
    public static final byte PROCESSING_INSTRUCTION = 8;

    /**
     * XML comment was just read and getText() will return value inside comment.
     */
    public static final int COMMENT = 9;

    /**
     * XML DOCTYPE declaration was just read
     * and getText() will return text that is inside DOCDECL
     */
    public static final int DOCDECL = 10;



    /**
     * Use this array to convert evebt type number (such as START_TAG) to
     * to string giving event name "START_TAG" == TYPES[START_TAG]
     */
    public static final String [] TYPES = {
        "START_DOCUMENT",
            "END_DOCUMENT",
            "START_TAG",
            "END_TAG",
            "TEXT",
            "CDSECT",
            "ENTITY_REF",
            "IGNORABLE_WHITESPACE",
            "PROCESSING_INSTRUCTION",
            "COMMENT",
            "DOCDECL"
    };


    // ----------------------------------------------------------------------------
    // namespace related features

    /**
     * Processing of namespaces is by default false.
     * Can not be changed during parsing
     */
    public static final String PROCESS_NAMESPACES =
        "http://xmlpull.org/v1/features/process-namespaces";

    /**
     * Report namespace attributes also - they can be distinguished
     * looking for prefix == "xmlns" or prefix == null and name == "xmlns
     * it is off by default and only meningful when PROCESS_NAMESPACES feature is on.
     */
    public static final String REPORT_NAMESPACE_ATTRIBUTES =
        "http://xmlpull.org/v1/features/report-namespace-prefixes";

    // docdecl related properties

    /**
     * processing of docdecl is by default false
     * and if DOCDECL is encountred an exception will be thrown if
     * parser can not process it
     * Can not be changed during parsing.
     */
    public static final String PROCESS_DOCDECL =
        "http://xmlpull.org/v1/features/process-docdecl";

    /**
     * Reports the DOCDECL instead of throwing
     * an exception if PROCESS_DOCDECL is not
     * set, by defualt this property is off
     * Can not be changed during parsing.
     */
    //public static final int REPORT_DOCDECL = 8;
    public static final String REPORT_DOCDECL =
        "http://xmlpull.org/v1/features/report-docdecl";


    /**
     * Report all validation errors.
     * Can not be changed during parsing.
     */
    public static final String VALIDATION =
        "http://xmlpull.org/v1/features/validation";

    /**
     * Use this call to change the general behaviour of the parser,
     * such as namespace processing or doctype declaration handling.
     * This method must be called before the first call to next or
     * nextToken. Otherwise, an exception is trown. Allowed constants
     * are: PROCESS_NAMESPACES, PROCESS_DOCDECL, REPORT_DOCDECL.
     * <p>Example: Use setFeature (PROCESS_NAMESPACES, true) in order
     * to switch on namespace processing. Default settings correspond
     * to properties requested from the factory.
     *
     */
    public void setFeature(String name,
                           boolean state) throws XmlPullParserException;

    /**
     * Return the current value of the feature with given name.
     *
     * @param name The name of feature to be retrieved.
     * @return The value of named feature.
     *     Unknown features are <string>always</strong> returned as false
     */

    public boolean getFeature(String name);

    /**
     * Set the value of a property.
     *
     * The property name is any fully-qualified URI.
     */
    public void setProperty(String name,
                            Object value) throws XmlPullParserException;

    /**
     * Look up the value of a property.
     *
     * The property name is any fully-qualified URI. I
     *
     * @param name The name of property to be retrieved.
     * @return The value of named property.
     *     Unknown features are <string>always</strong> returned as null.
     */
    public Object getProperty(String name);


    /**
     * Set the input for parser.
     * Using null parameter will reset parser state.
     */
    public void setInput(Reader in) throws XmlPullParserException;

    /**
     * Set new value for enyity.
     * <p><b>NOTE:</b> list of entites will be reset to to standard XML
     * entities such as &amp; &lt; &gt;) after each call to setInput
     * @see #setInput
     */
    public void defineCharacterEntity (String entity, String value) throws XmlPullParserException;

    /**
     * Return position in stack of first namespace slot for element at passed depth.
     * If namespaces are not enabled it returns always 0.
     */
    public int getNamespacesCount(int depth) throws XmlPullParserException;

    /**
     * Return namespace prefixes for position pos in namespace stack
     */
    public String getNamespacesPrefix(int pos) throws XmlPullParserException;

    /**
     * Return namespace URIs for position pos in namespace stack
     * If pos id out of range it throw exception.
     */
    public String getNamespacesUri(int pos) throws XmlPullParserException;

    /**
     * Return uri for the given prefix.
     * It is depending on current state of parser to find
     * what namespace uri is mapped from namespace prefix.
     * For example for 'xsi' if xsi namespace prefix
     * was declared to 'urn:foo' it will return 'urn:foo'.
     *
     * <p>It will return null if namespace could not be found.
     * (should we throw an exception in that case?)</p>
     *
     * Convenience method for
     *
     * <pre>
     *  for (int i = getNamespacesCount (getDepth ())-1; i >= 0; i--) {
     *   if (getNamespacesPrefix (i).equals (prefix)) {
     *     return getNamespacesUri (i);
     *   }
     *  }
     *  return null;
     * </pre>
     *
     * <p>However parser implementation can be more efficinet about.
     */


    public String getNamespace (String prefix)
        throws XmlPullParserException;


    // --------------------------------------------------------------------------
    // miscellaneous reporting methods

    /**
     * Returns the current depth of the element.
     * Outside the root element, the depth is 0. The
     * depth is incremented by 1 when a start tag is reached.
     * The depth is decremented AFTER the end tag
     * event was observed.
     *
     * <pre>
     * &lt;!-- outside --&gt;     0
     * &lt;root>               1
     *   sometext           1
     *     &lt;foobar&gt;         2
     *     &lt;/foobar&gt;        2
     * &lt;/root&gt;              1
     * &lt;!-- outside --&gt;     0
     * &lt;/pre&gt;
     */
    public int getDepth();

    /**
     * Short text describing parser position, including a
     * description of the current event and data source if known
     * and if possible what parser was seeing lastly in input.
     * This method is especially useful to give more meaningful error messages.
     */

    public String getPositionDescription ();


    /**
     * Current line
     */
    public int getLineNumber();

    /**
     * Current columnt
     */
    public int getColumnNumber();


    // --------------------------------------------------------------------------
    // TEXT related methods

    /**
     * Check if current TEXT event contains only whitespace characters.
     * For IGNORABLE_WHITESPACE, this is always true. If the current event
     * is neither a event TEXT nor a IGNORABLE_WHITESPACE event, false is
     * returned. Please note that non-validating parsers are not
     * able to distinguish whitespace and ignorable whitespace
     * except from whitespace outside the root element. ignorable
     * whitespace is reported as separate event which is exposed
     * via nextToken only.
     *
     * <p><b>NOTE:</b> it can be only called for element content related events
     * such as TEXT, CDSECT, IGNORABLE_WHITESPACE and ENTITY_REF otherwise
     * exception will be thrown.
     */

    public boolean isWhitespace() throws XmlPullParserException;

    /**
     * Read current content as String.
     *
     * <p><b>NOTE:</b> parser must be on TEXT, COMMENT, PROCESSING_INSTRUCTION
     * or DOCDECL event;
     * otherwise, null is returned. The parser will never generate a sequence of
     * two TEXT events.
     */

    public String getText () throws XmlPullParserException;


    // --------------------------------------------------------------------------
    // START_TAG / END_TAG shared methods

    /**
     * Returns the namespace URI of the current element.
     * If namespaces are NOT enabled, an empty String ("")
     * is returned.
     * The current event must be START_TAG or END_TAG, otherwise,
     * null is returned.
     */
    public String getNamespace ();

    /**
     * Returns the (local) name of the current element
     * when namespaces are enabled
     * or raw name when namespaces are disabled.
     * (current event must be START_TAG or END_TAG, otherwise, null is returned)
     * <p><b>NOTE:</b> to reconstruct raw element name
     *  when namespaces are enabled you will need to
     *  add prefix and colon to localName if prefix is not null.
     *
     */
    public String getName();

    /**
     * Returns the prefix of the current element
     * or null if elemet has no prefix (is in defualt namespace).
     *  If namespaces are not enabled it is always null.
     * (current event must be START_TAG or END_TAG)
     */
    public String getPrefix();


    /**
     * Returns true if the current event is START_TAG or END_TAG and the
     * tag is degenerated (e.g. &lt;foobar/&gt;).
     * <p><b>NOTE:</b> if parser is not on START_TAG or END_TAG the exception will be thrown.
     */
    public boolean isEmptyElementTag() throws XmlPullParserException;

    // --------------------------------------------------------------------------
    // START_TAG Attributes retrieval methods

    /**
     * Returns the number of attributes on the current element;
     * -1 if the current event is not START_TAG
     */
    public int getAttributeCount();

    /**
     * Returns the namespace URI of the specified attribute
     *  number index (starts from 0).
     * Returns "" if namespaces are not enabled. Throws an
     * IndexOutOfBoundsException if the index is out of range
     */
    public String getAttributeNamespace (int index);

    /**
     * Returns the localname of the specified attribute
     * if namespaces enabled or just attribute name if namespaces disabled.
     * Throws an IndexOutOfBoundsException if the index is invalid.
     */
    public String getAttributeName(int index);

    /**
     * Returns the prefix of the specified attribute
     * Returns null if the element has no prefix.
     * If namespaces are disbaled it will always return null.
     * Throws an IndexOutOfBoundsException if the index is
     * out of range.
     */
    public String getAttributePrefix(int index);

     /**
     * Returns the given attributes value
     * Throws an IndexOutOfBoundsException if the index is
     * out of range.
     */
    public String getAttributeValue(int index);

    /**
     * Returns the attributes value identified by namespace URI and namespace localName.
     * If namespaces are disbaled use null namespace.
     */
    public String getAttributeValue(String namespace,
                                    String name);


    // --------------------------------------------------------------------------
    // actual parsing methods

    /**
     * Returns the type of the current event (START_TAG, END_TAG, CONTENT, etc)
     */
    public int getType()
        throws XmlPullParserException;


    /**
     * Get next parsing event.
     * <p><b>NOTE:</b> empty element (such as &lt;tag/>) will be reported
     *  with  two separate events: START_TAG, END_TAG - it must be so to preserve
     *   parsing equivalency of empty element to &lt;tag>&lt;/tag>.
     *  (see isEmptyElement ())
     */

    public int next()
        throws XmlPullParserException, IOException;


    /**
     * This methids works similarly to next() but will expose
     * additional event types (COMMENT, DOCTYPE, PROCESSING_INSTRUCTION or
     * IGNORABLE_WHITESPACE)
     * if they are available in input.
     */

    public int nextToken()
        throws XmlPullParserException, IOException;

    //-----------------------------------------------------------------------------
    // utility methods to mak XML parsing easier ...

    /**
     * test if the current event is of the given type and if the
     * namespace and name do match. null will match any namespace
     * and any name. If the current event is TEXT with isWhitespace()=
     * true, and the required type is not TEXT, next () is called prior
     * to the test. If the test is not passed, an exception is
     * thrown. The exception text indicates the parser position,
     * the expected event and the current event (not meeting the
     * requirement.
     *
     * <p>essentially it does this
     * <pre>
     *  if (getType() == TEXT && type != TEXT && isWhitespace ())
     *    next ();
     *
     *  if (type != getType
     *  || (namespace != null && !namespace.equals (getNamespace ()))
     *  || (names != null && !name.equals (getName ())
     *     throw new XmlPullParserException ( "....");
     * </pre>
     */
    public void require (int type, String namespace, String name)
        throws XmlPullParserException, IOException;


    /**
     * If the current event is text, the value of getText is
     * returned and next() is called. Otherwise, an empty
     * String ("") is returned. Useful for reading element
     * content without needing to performing an additional
     * check if the element is empty.
     *
     * <p>essentially it does this
     * <pre>
     *   if (getType != TEXT) return ""
     *    String result = getText ();
     *    next ();
     *    return result;
     *  </pre>
     */
    public String readText () throws XmlPullParserException, IOException;

}


