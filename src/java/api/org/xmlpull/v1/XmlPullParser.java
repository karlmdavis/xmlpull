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
 * <p>There are following different
 * kinds of parser depending on which features are set:<ul>
 * <li>behaves like XML 1.0 comliant non-validating parser
 *  <em>if no DOCDECL is present</em> in XML documents when
 *   FEATURE_PROCESS_DOCDECL is false (this is <b>default parser</b>
 *   and internal enetites can still be defiend with defineEntityReplacementText())
 * <li>non-validating parser as defined in XML 1.0 spec when
 *   FEATURE_PROCESS_DOCDECL is true
 * <li>validating parser as defined in XML 1.0 spec when
 *   FEATURE_VALIDATION is true (and that implies that FEATURE_PROCESS_DOCDECL is true)
 * </ul>
 *
 *
 * <p>There are only two key methods: next() and nextToken() that provides
 * access to high level parsing events and to lower level tokens.
 *
 * <p>The parser is always in some event state and type of the current event
 * can be determined by calling
 * <a href="#next()">getEventType()</a> mehod.
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
 * The minimal working example of use of API would be looking like this:
 * <pre>
 * import java.io.IOException;
 * import java.io.StringReader;
 *
 * import org.xmlpull.v1.XmlPullParser;
 * import org.xmlpull.v1.XmlPullParserException;
 * import org.xmlpull.v1.XmlPullParserFactory;
 *
 * public class SimpleXmlPullApp
 * {
 *
 *     public static void main (String args[])
 *         throws XmlPullParserException, IOException
 *     {
 *         XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
 *         factory.setNamespaceAware(true);
 *         XmlPullParser xpp = factory.newPullParser();
 *
 *         xpp.setInput ( new StringReader ( "&lt;foo>Hello World!&lt;/foo>" ) );
 *         int eventType = xpp.getEventType();
 *         while (eventType != xpp.END_DOCUMENT) {
 *          if(eventType == xpp.START_DOCUMENT) {
 *              System.out.println("Start document");
 *          } else if(eventType == xpp.END_DOCUMENT) {
 *              System.out.println("End document");
 *          } else if(eventType == xpp.START_TAG) {
 *              System.out.println("Start tag "+xpp.getName());
 *          } else if(eventType == xpp.END_TAG) {
 *              System.out.println("End tag "+xpp.getName());
 *          } else if(eventType == xpp.TEXT) {
 *              System.out.println("Text "+xpp.getText());
 *          }
 *          eventType = xpp.next();
 *         }
 *     }
 * }
 * </pre>
 *
 * <p>When run it will produce following output:
 * <pre>
 * Start document
 * Start tag foo
 * Text Hello World!
 * End tag foo
 * </pre>
 *
 * <p>For more details on use of API please read
 * Quick Introduction available at <a href="http://www.xmlpull.org">http://www.xmlpull.org</a>
 *
 * @see XmlPullParserFactory
 * @see #defineEntityReplacementText
 * @see #next
 * @see #nextToken
 * @see #FEATURE_PROCESS_DOCDECL
 * @see #FEATURE_VALIDATION
 * @see #START_DOCUMENT
 * @see #START_TAG
 * @see #TEXT
 * @see #END_TAG
 * @see #END_DOCUMENT
 *
 * @author Stefan Haustein
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
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
     * if <a href='#FEATURE_PROCESS_NAMESPACES'>namespaces are enabled</a>.
     * See getAttribute* methods to retrieve element attributes.
     * See getNamespace* methods to retrieve newly declared namespaces.
     *
     * @see #next
     * @see #nextToken
     * @see #getName
     * @see #getPrefix
     * @see #getNamespace
     * @see #getAttributeCount
     * @see #getDepth
     * @see #getNamespaceCount
     * @see #getNamespace
     * @see #FEATURE_PROCESS_NAMESPACES
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
     * @see #FEATURE_PROCESS_NAMESPACES
     */
    public final static int END_TAG = 3;


    /**
     * EVENT TYPE and TOKEN: character data was read and will be available by call to getText()
     * (available from <a href="#next()">next()</a> and <a href="#nextToken()">nextToken()</a>).
     * <p><strong>NOTE:</strong> next() will (in contrast to nextToken ()) accumulate multiple
     * events into one TEXT event, skipping IGNORABLE_WHITESPACE,
     * PROCESSING_INSTRUCTION and COMMENT events.
     * <p><strong>NOTE:</strong> if state was reached by calling next() the text value will
     * be normalized and if the token was returned by nextToken() then getText() will
     * return unnormalized content (no end-of-line normalization - it is content exactly as in
     * input XML)
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
     * The entity name is available by calling getText() and it is user responsibility
     * to resolve entity reference.
     *
     * @see #nextToken
     * @see #getText
     */
    public final static byte ENTITY_REF = 6;

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
     * <p><strong>NOTE:</strong> this is different than callinf isWhitespace() method
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
    public static final String FEATURE_PROCESS_NAMESPACES =
        "http://xmlpull.org/v1/doc/features.html#process-namespaces";

    /**
     * FEATURE: Report namespace attributes also - they can be distinguished
     * looking for prefix == "xmlns" or prefix == null and name == "xmlns
     * it is off by default and only meaningful when FEATURE_PROCESS_NAMESPACES feature is on.
     * <p><strong>NOTE:</strong> can not be changed during parsing!
     *
     * @see #getFeature
     * @see #setFeature
     */
    public static final String FEATURE_REPORT_NAMESPACE_ATTRIBUTES =
        "http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes";

    /**
     * FEATURE: Processing of DOCDECL is by default set to false
     * and if DOCDECL is encountered it is reported by nextToken()
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
    public static final String FEATURE_PROCESS_DOCDECL =
        "http://xmlpull.org/v1/doc/features.html#process-docdecl";

    /**
     * FEATURE: Report all validation errors as defined by XML 1.0 sepcification
     * (implies that FEATURE_PROCESS_DOCDECL is true and both internal and external DOCDECL
     * will be processed).
     * <p><strong>NOTE:</strong> can not be changed during parsing!
     *
     * @see #getFeature
     * @see #setFeature
     */
    public static final String FEATURE_VALIDATION =
        "http://xmlpull.org/v1/doc/features.html#validation";

    /**
     * Use this call to change the general behaviour of the parser,
     * such as namespace processing or doctype declaration handling.
     * This method must be called before the first call to next or
     * nextToken. Otherwise, an exception is trown.
     * <p>Example: call setFeature(FEATURE_PROCESS_NAMESPACES, true) in order
     * to switch on namespace processing. Default settings correspond
     * to properties requested from the XML Pull Parser factory
     * (if none were requested then all feautures are by default false).
     *
     * @exception XmlPullParserException if feature is not supported or can not be set
     * @exception IllegalArgumentException if feature string is null
     */
    public void setFeature(String name,
                           boolean state) throws XmlPullParserException;

    /**
     * Return the current value of the feature with given name.
     * <p><strong>NOTE:</strong> unknown features are <string>always</strong> returned as false
     *
     * @param name The name of feature to be retrieved.
     * @return The value of named feature.
     * @exception IllegalArgumentException if feature string is null
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
     * <p><strong>NOTE:</strong> unknown features are <string>always</strong> returned as null
     *
     * @param name The name of property to be retrieved.
     * @return The value of named property.
     */
    public Object getProperty(String name);


    /**
     * Set the input for parser. Parser event state is set to START_DOCUMENT.
     * Using null parameter will stop parsing and reset parser state
     * allowing parser to free internal resources (such as parsing buffers).
     */
    public void setInput(Reader in) throws XmlPullParserException;

    /**
     * Set new value for entity replacement text as defined in
     * <a href="http://www.w3.org/TR/REC-xml#intern-replacement">XML 1.0 Section 4.5
     * Construction of Internal Entity Replacement Text</a>.
     * If FEATURE_PROCESS_DOCDECL or FEATURE_VALIDATION are set then calling this
     * function will reulst in exception because when processing of DOCDECL is enabled
     * there is no need to set manually entity replacement text.
     *
     * <p>The motivation for this function is to allow very small implementations of XMLPULL
     * that will work in J2ME environments and though may not be able to process DOCDECL
     * but still can be made to work with predefined DTDs by using this function to
     * define well known in advance entities.
     * Additionally as XML Schemas are replacing DTDs by allowing parsers not to process DTDs
     * it is possible to create more efficient parser implementations
     * that can be used as underlying layer to do XML schemas validation.
     *
     *
     * <p><b>NOTE:</b> this is replacement text and it is not allowed
     *  to contain any other entity reference
     * <p><b>NOTE:</b> list of pre-defined entites will always contain standard XML
     * entities (such as &amp;amp; &amp;lt; &amp;gt; &amp;quot; &amp;apos;)
     * and they cannot be replaced!
     *
     * @see #setInput
     * @see #FEATURE_PROCESS_DOCDECL
     * @see #FEATURE_VALIDATION
     */
    public void defineEntityReplacementText( String entityName,
                                            String replacementText ) throws XmlPullParserException;

    /**
     * Return position in stack of first namespace slot for element at passed depth.
     * If namespaces are not enabled it returns always 0.
     * <p><b>NOTE:</b> default namespace is not included in namespace table but
     *  available by getNamespace() and not available from getNamespace(String)
     *
     * @see #getNamespacePrefix
     * @see #getNamespaceUri
     * @see #getNamespace()
     * @see #getNamespace(String)
     */
    public int getNamespaceCount(int depth) throws XmlPullParserException;

    /**
     * Return namespace prefixes for position pos in namespace stack
     */
    public String getNamespacePrefix(int pos) throws XmlPullParserException;

    /**
     * Return namespace URIs for position pos in namespace stack
     * If pos is out of range it throw exception.
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
     *
     * <p>Convenience method for
     *
     * <pre>
     *  for (int i = getNamespaceCount (getDepth ())-1; i >= 0; i--) {
     *   if (getNamespacePrefix (i).equals (prefix)) {
     *     return getNamespaceUri (i);
     *   }
     *  }
     *  return null;
     * </pre>
     *
     * <p>However parser implementation can be more efficient about.
     *
     * @see #getNamespaceCount
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
     * </pre>
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
     * Current line number: numebering starts from 1.
     */
    public int getLineNumber();

    /**
     * Current column: numbering starts from 0 (returned when parser is in START_DOCUMENT state!)
     */
    public int getColumnNumber();


    // --------------------------------------------------------------------------
    // TEXT related methods

    /**
     * Check if current TEXT event contains only whitespace characters.
     * For IGNORABLE_WHITESPACE, this is always true.
     * For TEXT and CDSECT if the current event text contains at lease one non white space
     * character then false is returned. For any other event type exception is thrown.
     *
     * <p><b>NOTE:</b>  non-validating parsers are not
     * able to distinguish whitespace and ignorable whitespace
     * except from whitespace outside the root element. ignorable
     * whitespace is reported as separate event which is exposed
     * via nextToken only.
     *
     * <p><b>NOTE:</b> this function can be only called for element content related events
     * such as TEXT, CDSECT or IGNORABLE_WHITESPACE otherwise
     * exception will be thrown!
     */

    public boolean isWhitespace() throws XmlPullParserException;

    /**
     * Read text content of the current event as String.
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
     * <p><b>NOTE:</b> this methid must return always the same value as getText()
     * and if getText() returns null then this methid returns null as well and
     * values returned in holder MUST be -1 (both start and length).
     *
     * @see #getText
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
     * If namespaces are NOT enabled, an empty String ("") always is returned.
     * The current event must be START_TAG or END_TAG, otherwise, null is returned.
     */
    public String getNamespace ();

    /**
     * Returns the (local) name of the current element
     * when namespaces are enabled
     * or raw name when namespaces are disabled.
     * The current event must be START_TAG or END_TAG, otherwise null is returned.
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
    public int getAttributeCount();

    /**
     * Returns the namespace URI of the specified attribute
     *  number index (starts from 0).
     * Returns empty string ("") if namespaces are not enabled or attribute has no namespace.
     * Throws an IndexOutOfBoundsException if the index is out of range
     * or current event type is not START_TAG.
     *
     * <p><strong>NOTE:</p> if FEATURE_REPORT_NAMESPACE_ATTRIBUTES is set
     * then namespace attributes (xmlns:ns='...') amust be reported
     * with namespace
     * <a href="http://www.w3.org/2000/xmlns/">http://www.w3.org/2000/xmlns/</a>
     * (visit this URL for description!).
     * The default namespace attribute (xmlns="...") will be reported with empty namespace.
     * Then xml prefix is bound as defined in
     * <a href="http://www.w3.org/TR/REC-xml-names/#ns-using">Namespaces in XML</a>
     * specification to "http://www.w3.org/XML/1998/namespace".
     *
     * @param zero based index of attribute
     * @return attribute namespace or "" if namesapces processing is not enabled.
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
     * If current event type is not START_TAG then IndexOutOfBoundsException will be thrown.
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
     * Returns the type of the current event (START_TAG, END_TAG, TEXT, etc.)
     *
     * @see #next()
     * @see #nextToken()
     */
    public int getEventType()
        throws XmlPullParserException;


    /**
     * Get next parsing event - element content wil be coalesced and only one
     * TEXT event must be returned for whole element content
     * (comments and processing instructions will be ignored and emtity references
     * must be expanded or exception mus be thrown if entity reerence can not be exapnded).
     * If element content is empty (content is "") then no TEXT event will be reported.
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
     * additional event types (COMMENT, CDSECT, DOCDECL, ENTITY_REF, PROCESSING_INSTRUCTION, or
     * IGNORABLE_WHITESPACE) if they are available in input.
     *
     * <p>If special feature FEATURE_XML_ROUNDTRIP
     * (identified by URI: http://xmlpull.org/v1/doc/features.html#xml-roundtrip)
     * is true then it is possible to do XML document round trip ie. reproduce
     * exectly on output the XML input using getText().
     *
     * <p>Here is the list of tokens that can be  returned from nextToken()
     * and what getText() and getTextCharacters() returns:<dl>
     * <dt>START_DOCUMENT<dd>null
     * <dt>END_DOCUMENT<dd>null
     * <dt>START_TAG<dd>null
     *   unless FEATURE_XML_ROUNDTRIP enabled and then returns XML tag, ex: &lt;tag attr='val'>
     * <dt>END_TAG<dd>null
     * unless FEATURE_XML_ROUNDTRIP enabled and then returns XML tag, ex: &lt;/tag>
     * <dt>TEXT<dd>return unnormalized element content
     * <dt>IGNORABLE_WHITESPACE<dd>return unnormalized characters
     * <dt>CDSECT<dd>return unnormalized text <em>inside</em> CDATA
     *  ex. 'fo&lt;o' from &lt;!CDATA[fo&lt;o]]>
     * <dt>PROCESSING_INSTRUCTION<dd>return unnormalized PI content ex: 'pi foo' from &lt;?pi foo?>
     * <dt>COMMENT<dd>return comment content ex. 'foo bar' from &lt;!--foo bar-->
     * <dt>ENTITY_REF<dd>return unnormalized text of entity_name (&entity_name;)
     * <br><b>NOTE:</b> it is user responsibility to resolve entity reference
     * <br><b>NOTE:</b> character entities and standard entities such as
     *  &amp;amp; &amp;lt; &amp;gt; &amp;quot; &amp;apos; are reported as well
     * and are not resolved and not reported as TEXT tokens!
     * This requirement is added to allow to do roundtrip of XML documents!
     * <dt>DOCDECL<dd>return inside part of DOCDECL ex. returns:<pre>
     * &quot; titlepage SYSTEM "http://www.foo.bar/dtds/typo.dtd"
     * [&lt;!ENTITY % active.links "INCLUDE">]&quot;</pre>
     * <p>for input document that contained:<pre>
     * &lt;!DOCTYPE titlepage SYSTEM "http://www.foo.bar/dtds/typo.dtd"
     * [&lt;!ENTITY % active.links "INCLUDE">]></pre>
     * </dd>
     * </dl>
     *
     * <p><strong>NOTE:</strong> returned text of token is not end-of-line normalized.
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
     *  if (getEventType() == TEXT && type != TEXT && isWhitespace ())
     *    next ();
     *
     *  if (type != getEventType()
     *  || (namespace != null && !namespace.equals (getNamespace ()))
     *  || (name != null && !name.equals (getName ()))
     *     throw new XmlPullParserException ( "expected "+ TYPES[ type ]+getPositionDesctiption());
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
     *   if (getEventType != TEXT) return ""
     *   String result = getText ();
     *   next ();
     *   return result;
     * </pre>
     */
    public String readText () throws XmlPullParserException, IOException;

}

