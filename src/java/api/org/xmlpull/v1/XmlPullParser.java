/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// see LICENSE.txt in distribution for copyright and license information

package org.xmlpull.v1;

import java.io.IOException;
import java.io.Reader;

/**
 * XML Pull Parser is an interface that defines parsing functionlity provided
 * in <a href="http://www.xmlpull.org/">XMLPULL V1 API</a> (visit this website to
 * learn more about API and its implementations).
 *
 * <p>There are only two key methods: next() and nextToken() that provides
 * access to high level parsing events and to lower level tokens.
 *
 * <p>The parser is always in some state and current state can be determined by calling
 * getType() mehod.
 * Initially parser is in <a href="#START_DOCUMENT">START_DOCUMENT</a> state.
 *
 * <p>Method <a href="#next()">next()</a> return int that contains identifier of parsing event.
 * This method can return following events (and will change parser state to the returned event):<dl>
 * <dt><a href="#START_TAG">START_TAG</a><dd> XML start tag was read
 * <dt><a href="#TEXT">TEXT</a><dd> element contents was read and is available via getText()
 * <dt><a href="#END_TAG">END_TAG</a><dd> XML end tag was read
 * <dt><a href="#END_DOCUMENT">END_DOCUMENT</a><dd> no more events is available
 * </dl>
 *
 * @see XmlPullParserFactory
 *
 * @author Stefan Haustein
 * @author Aleksander Slominski [http://www.extreme.indiana.edu/~aslom/]
 */

public interface XmlPullParser {

    /** This constant represents lack of or default namespace (empty string "") */
    public static final String NO_NAMESPACE = "";

    // ----------------------------------------------------------------------------
    // EVENT TYPES as reported by next()

    /**
     * EVENT TYPE and TOKEN: signalize that parser is at the very beginning of the document
     * and nothing was read yet - the parser is before first call to next() or nextToken()
     * (available from <a href="#next()">next()</a> and <a href="#nextToken()">nextToken()</a>).
     *
     * @see #next
     * @see #nextToken
     */
    public final static int START_DOCUMENT = 0;

    /**
     * EVENT TYPE and TOKEN: logical end of xml document
     * (available from <a href="#next()">next()</a> and <a href="#nextToken()">nextToken()</a>).
     *
     * <p><strong>NOTE:</strong> calling again
     * <a href="#next()">next()</a> or <a href="#nextToken()">nextToken()</a>
     * will result in exception being thrown.
     *
     * @see #next
     * @see #nextToken
     */
    public final static int END_DOCUMENT = 1;

    /**
     * EVENT TYPE and TOKEN: start tag was just read
     * (available from <a href="#next()">next()</a> and <a href="#nextToken()">nextToken()</a>).
     * The name of start tag is available from getName(), its namespace and prefix are
     * available from getNamespace() and getPrefix()
     * if <a href='#PROCESS_NAMESPACES'>namespaces are enabled</a>.
     * See getAttribute* methods to retrieve element attributes.
     * See getNamespace* methods to retrieve newly declared namespaces.
     *
     * @see #next
     * @see #nextToken
     * @see #getName
     * @see #getPrefix
     * @see #getNamespace
     * @see #getAttributesCount
     * @see #getDepth
     * @see #getNamespacesCount
     * @see #getNamespace
     * @see #PROCESS_NAMESPACES
     */
    public final static int START_TAG = 2;

    /**
     * EVENT TYPE and TOKEN: end tag was just read
     * (available from <a href="#next()">next()</a> and <a href="#nextToken()">nextToken()</a>).
     * The name of start tag is available from getName(), its namespace and prefix are
     * available from getNamespace() and getPrefix()
     *
     * @see #next
     * @see #nextToken
     * @see #getName
     * @see #getPrefix
     * @see #getNamespace
     * @see #PROCESS_NAMESPACES
     */
    public final static int END_TAG = 3;


    /**
     * EVENT TYPE and TOKEN: character data was read and will be available by call to getText().
     * (available from <a href="#next()">next()</a> and <a href="#nextToken()">nextToken()</a>).
     * <p><strong>NOTE:</strong> this event is returned returned both by nextToken() and next(),
     * all following events are only returned by nextToken().
     * <p><strong>NOTE:</strong> next() will (in contrast to nextToken ()) accumulate multiple
     * TEXT events into one, skipping inbetween IGNORABLE_WHITESPACE,
     * PROCESSING_INSTRUCTION and COMMENT events.
     *
     * @see #next
     * @see #nextToken
     * @see #getText
     */
    public final static int TEXT = 4;

    // ----------------------------------------------------------------------------
    // additional events exposed by lower level nextToken()

    /**
     * TOKEN: CDATA sections was just read
     * (this token is available only from <a href="#nextToken()">nextToken()</a>).
     * The value of text inside CDATA section is available  by callling getText().
     *
     * @see #nextToken
     * @see #getText
     */
    public final static byte CDSECT = 5;

    /**
     * TOKEN: Entity reference was just read
     * (this token is available only from <a href="#nextToken()">nextToken()</a>).
     * The value of entity is available as geText() (null if unknown entity)
     * and the entity name is available by calling getName()
     *
     * @see #nextToken
     * @see #getName
     * @see #getText
     */
    public final static byte ENTITY_REF            = 6;

    /**
     * TOKEN: Ignorable whitespace was just read
     * (this token is available only from <a href="#nextToken()">nextToken()</a>).
     * For non-validating
     * parsers, this event is only reported by nextToken() when
     * outside the root elment.
     * Validating parsers may be able to detect ignorable whitespace at
     * other locations.
     * The value of ignorable whitespace is available by calling getText()
     *
     * <p><strong>NOTE:</strong> this is different than call isWhitespace()
     *    as element content may be whitespace but may not be ignorable whitespace.
     *
     * @see #nextToken
     * @see #getText
     */
    public static final byte IGNORABLE_WHITESPACE = 7;

    /**
     * TOKEN: XML processing instruction declaration was just read
     * and getText() will return text that is inside processing instruction
     * (this token is available only from <a href="#nextToken()">nextToken()</a>).
     *
     * @see #nextToken
     * @see #getText
     */
    public static final byte PROCESSING_INSTRUCTION = 8;

    /**
     * TOKEN: XML comment was just read and getText() will return value inside comment
     * (this token is available only from <a href="#nextToken()">nextToken()</a>).
     *
     * @see #nextToken
     * @see #getText
     */
    public static final int COMMENT = 9;

    /**
     * TOKEN: XML DOCTYPE declaration was just read
     * and getText() will return text that is inside DOCDECL
     * (this token is available only from <a href="#nextToken()">nextToken()</a>).
     *
     * @see #nextToken
     * @see #getText
     */
    public static final int DOCDECL = 10;



    /**
     * Use this array to convert evebt type number (such as START_TAG) to
     * to string giving event name, ex: "START_TAG" == TYPES[START_TAG]
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
     * FEATURE: Processing of namespaces is by default set to false.
     * <p><strong>NOTE:</strong> can not be changed during parsing!
     *
     * @see #getFeature
     * @see #setFeature
     */
    public static final String PROCESS_NAMESPACES =
        "http://xmlpull.org/v1/features/process-namespaces";

    /**
     * FEATURE: Report namespace attributes also - they can be distinguished
     * looking for prefix == "xmlns" or prefix == null and name == "xmlns
     * it is off by default and only meningful when PROCESS_NAMESPACES feature is on.
     * <p><strong>NOTE:</strong> can not be changed during parsing!
     *
     * @see #getFeature
     * @see #setFeature
     */
    public static final String REPORT_NAMESPACE_ATTRIBUTES =
        "http://xmlpull.org/v1/features/report-namespace-prefixes";

    /**
     * FEATURE: Processing of DOCDECL is by default set to false
     * and if DOCDECL is encountred it is reported by nextToken()
     * and ignored by next().
     *
     * If processing is set to true then DOCDECL must be processed by parser.
     *
     * <p><strong>NOTE:</strong> if the DOCDECL was ignored
     * further in parsing there may be fatal exception when undeclared
     * entity is encountered!
     * <p><strong>NOTE:</strong> can not be changed during parsing!
     *
     * @see #getFeature
     * @see #setFeature
     */
    public static final String PROCESS_DOCDECL =
        "http://xmlpull.org/v1/features/process-docdecl";

    /**
     * FEATURE: Report all validation errors as defined by XML 1.0 sepcification
     * (implies that PROCESS_DOCDECL is true and both internal and external DOCDECL
     * will be processed).
     * <p><strong>NOTE:</strong> can not be changed during parsing!
     *
     * @see #getFeature
     * @see #setFeature
     */
    public static final String VALIDATION =
        "http://xmlpull.org/v1/features/validation";

    /**
     * Use this call to change the general behaviour of the parser,
     * such as namespace processing or doctype declaration handling.
     * This method must be called before the first call to next or
     * nextToken. Otherwise, an exception is trown.
     * <p>Example: Use setFeature (PROCESS_NAMESPACES, true) in order
     * to switch on namespace processing. Default settings correspond
     * to properties requested from the XML Pull Parser factory
     * (if none were requested then all feautures are by default false).
     */
    public void setFeature(String name,
                           boolean state) throws XmlPullParserException;

    /**
     * Return the current value of the feature with given name.
     * <p><strong>NOTE:</strong> unknown features are <string>always</strong> returned as false
     *
     * @param name The name of feature to be retrieved.
     * @return The value of named feature.
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
     * <p><strong>NOTE:</strong> unknown features are <string>always</strong> returned as false
     *
     * @param name The name of property to be retrieved.
     * @return The value of named property.
     */
    public Object getProperty(String name);


    /**
     * Set the input for parser. Parser event state is set to START_DOCUMENT.
     * Using null parameter will stop parsing and reset parser state
     * as parser can free internal parsing buffers
     */
    public void setInput(Reader in) throws XmlPullParserException;

    /**
     * Set new value for entity.
     * <p><b>NOTE:</b> list of entites will be reset to standard XML
     * entities such as &amp; &lt; &gt;) after each call to setInput
     *
     * @see #setInput
     */
    public void defineCharacterEntity (String entity, String value) throws XmlPullParserException;

    /**
     * Return position in stack of first namespace slot for element at passed depth.
     * If namespaces are not enabled it returns always 0.
     * <p><b>NOTE:</b> default namespace is not included in namespace table but
     *  available by getNamespace() and not available form getNamespace(String)
     *
     * @see #getNamespacePrefix
     * @see #getNamespaceUri
     * @see #getNamespace()
     * @see #getNamespace(String)
     */
    public int getNamespacesCount(int depth) throws XmlPullParserException;

    /**
     * Return namespace prefixes for position pos in namespace stack
     */
    public String getNamespacePrefix(int pos) throws XmlPullParserException;

    /**
     * Return namespace URIs for position pos in namespace stack
     * If pos id out of range it throw exception.
     */
    public String getNamespaceUri(int pos) throws XmlPullParserException;

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
     *   if (getNamespacePrefix (i).equals (prefix)) {
     *     return getNamespaceUri (i);
     *   }
     *  }
     *  return null;
     * </pre>
     *
     * <p>However parser implementation can be more efficinet about.
     *
     * @see #getNamespacesCount
     * @see #getNamespacePrefix
     * @see #getNamespaceUri
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
     * Read text content of the current event as String.
     *
     * <p><strong>NOTE:</strong> parser must be on TEXT, COMMENT, PROCESSING_INSTRUCTION
     * or DOCDECL event; otherwise, null is returned.
     */

    public String getText ();


    /**
     * Get the buffer that contains text of the current event and
     * start offset of text is passed in first slot of input int array
     * and its length is in second slot.
     *
     * <p><strong>NOTE:</strong> this buffer must not
     * be modified and its content MAY change after call to next() or nextToken().
     *
     * <p><b>NOTE:</b> parser must be on
     * TEXT, COMMENT, PROCESSING_INSTRUCTION or DOCDECL event;
     * otherwise, null is returned.
     *
     * @see #getText
     * @see #getTextCharactersStart
     * @see #getTextCharactersLength
     *
     * @param holderForStartAndLength the 2-element int array into which
     *   values of start offset and length will be written into frist and second slot of array.
     * @return char buffer that contains text of current event
     *  or null if the current event has no text associated.
     */
    public char[] getTextCharacters(int [] holderForStartAndLength);

    // --------------------------------------------------------------------------
    // START_TAG / END_TAG shared methods

    /**
     * Returns the namespace URI of the current element.
     * If namespaces are NOT enabled, an empty String ("")
     * is returned.
     * The current event must be START_TAG or END_TAG, otherwise, null is returned.
     */
    public String getNamespace ();

    /**
     * Returns the (local) name of the current element
     * when namespaces are enabled
     * or raw name when namespaces are disabled.
     * The current event must be START_TAG, END_TAG or ENTITY_REF, otherwise null is returned.
     * <p><b>NOTE:</b> to reconstruct raw element name
     *  when namespaces are enabled you will need to
     *  add prefix and colon to localName if prefix is not null.
     *
     */
    public String getName();

    /**
     * Returns the prefix of the current element
     * or null if elemet has no prefix (is in defualt namespace).
     *  If namespaces are not enabled it always returns null.
     * If the current event is not  START_TAG or END_TAG the null value is returned.
     */
    public String getPrefix();


    /**
     * Returns true if the current event is START_TAG and the tag is degenerated
     * (e.g. &lt;foobar/&gt;).
     * <p><b>NOTE:</b> if parser is not on START_TAG then the exception will be thrown.
     */
    public boolean isEmptyElementTag() throws XmlPullParserException;

    // --------------------------------------------------------------------------
    // START_TAG Attributes retrieval methods

    /**
     * Returns the number of attributes on the current element;
     * -1 if the current event is not START_TAG
     *
     * @see #getAttributeNamespace
     * @see #getAttributeName
     * @see #getAttributePrefix
     * @see #getAttributeValue
     */
    public int getAttributesCount();

    /**
     * Returns the namespace URI of the specified attribute
     *  number index (starts from 0).
     * Returns "" if namespaces are not enabled.
     * Throws an IndexOutOfBoundsException if the index is out of range
     * or current event type is not START_TAG.
     *
     * @param zero based index of attribute
     * @return attribute namespace or null if namesapces processing is not enabled.
     */
    public String getAttributeNamespace (int index);

    /**
     * Returns the local name of the specified attribute
     * if namespaces are enabled or just attribute name if namespaces are disabled.
     * Throws an IndexOutOfBoundsException if the index is out of range
     * or current event type is not START_TAG.
     *
     * @param zero based index of attribute
     * @return attribute names
     */
    public String getAttributeName (int index);

    /**
     * Returns the prefix of the specified attribute
     * Returns null if the element has no prefix.
     * If namespaces are disabled it will always return null.
     * Throws an IndexOutOfBoundsException if the index is out of range
     * or current event type is not START_TAG.
     *
     * @param zero based index of attribute
     * @return attribute prefix or null if namesapces processing is not enabled.
     */
    public String getAttributePrefix(int index);

    /**
     * Returns the given attributes value
     * Throws an IndexOutOfBoundsException if the index is out of range
     * or current event type is not START_TAG.
     *
     * @param zero based index of attribute
     * @return value of attribute
     */
    public String getAttributeValue(int index);

    /**
     * Returns the attributes value identified by namespace URI and namespace localName.
     * If namespaces are disbaled namespace must be null.
     *
     * @param namespace Namespace of the attribute if namespaces are enabled otherwise must be null
     * @param name If namespaces enabled local name of attribute otherwise just attribute name
     * @return value of attribute
     */
    public String getAttributeValue(String namespace,
                                    String name);


    // --------------------------------------------------------------------------
    // actual parsing methods

    /**
     * Returns the type of the current event (START_TAG, END_TAG, CONTENT, etc.)
     *
     * @see #next()
     * @see #nextToken()
     */
    public int getType()
        throws XmlPullParserException;


    /**
     * Get next parsing event - element content wil be coalesced and only one
     * TEXT event must be returned for whole element content
     * (comments and processing instructions will be ignored and emtity references
     * must be expanded or exception mus be throw if entity reerence can not be exapnded).
     * If element is emoty then no TEXT will be reported.
     *
     * <p><b>NOTE:</b> empty element (such as &lt;tag/>) will be reported
     *  with  two separate events: START_TAG, END_TAG - it must be so to preserve
     *   parsing equivalency of empty element to &lt;tag>&lt;/tag>.
     *  (see isEmptyElementTag ())
     *
     * @see #isEmptyElementTag
     * @see #START_TAG
     * @see #TEXT
     * @see #END_TAG
     * @see #END_DOCUMENT
     */

    public int next()
        throws XmlPullParserException, IOException;


    /**
     * This method works similarly to next() but will expose
     * additional event types (COMMENT, DOCDECL, PROCESSING_INSTRUCTION, ENTITY_REF or
     * IGNORABLE_WHITESPACE) if they are available in input.
     *
     * <p><strong>NOTE:</strong> retirned text of token is not end-of-line normalized.
     *
     * @see #next
     * @see #START_TAG
     * @see #TEXT
     * @see #END_TAG
     * @see #END_DOCUMENT
     * @see #COMMENT
     * @see #DOCDECL
     * @see #PROCESSING_INSTRUCTION
     * @see #ENTITY_REF
     * @see #IGNORABLE_WHITESPACE
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
     *  || (name != null && !name.equals (getName ())
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


