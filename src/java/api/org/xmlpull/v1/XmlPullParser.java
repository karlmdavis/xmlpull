/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// see LICENSE.txt in distribution for copyright and license information

package org.xmlpull.v1;

import java.io.IOException;
import java.io.Reader;

// - START_DOCUMENT added (see below)
// - reset removed because of redundancy with setInput(?)
//   ALEK restored :-) reset() is good if you want to maintain
//   pool of parsers and recalim resources
// * removed again ;-) setInput (null) can do the job!
// # OK
// - renamed methods from "read" to "get" if they do not
//   change the state of the parser
// - removed "report namespace attributes" related methods:
//   if we include "report namespace attributes", let
//   us use a property constant for this.
//   ALEK added property
// * Stefan added property
// # i added last final property too: DISABLE_MIXED_CONTENT
// # this is very useful for parsing SOAP as it has no mixed content
// # so parser can remove ignorable whitespaces when called by next()
// # http://www.w3.org/TR/REC-xml#sec-mixed-content
// - qname split methods removed (see mail)
// - some method names changed (see mail)
// - renamed CONTENT to TEXT:
//   IMHO, tags and attributes are some kind of content, too,
//   and "character data" seemed too long.
//   ALEK: TEXT now has sematic of CONTENT as well fro next() to do gathered TEXT events
// - renamed readContent to getText
//   ALEK: readTextContent takes care to gather content form multiple TEXT events
// * changed back to getText because "multiple or not" is determined by using
//   next () or nextToken ()
// # OK so we have just now overloaded TEXT depending on next or nextToken
// - getContentLength removed because
//   you have changed readContent to return a string,
//   (which I prefer anyway), so it is no
//   longer required for creating a sufficient
//   buffer
//   ALEK but string can be lazy created - ie not created until user asks for
//    and i may want to see if there is any non-whitespace content befor ei do anything
//    hence need for getTextContentLength
// * isWhitespace can be queried independently of getTextContentLength,
//   so I do not see the point, even if the string is lazy created
// # OK for simplicity
// - renamed DOCTYPE to DOCDECL for consisteny
//   with properties
// - renamed getAttributeValueFromName to
//   getAttributeValue.
// - renamed getEventType to getType for
//   consistency with getName etc...
// - renumbered some constants in order
//   to be able to simplify detection of
//   "illegal" constants.
// - renamed getPosDesc to getStateDescription.
//   Should we consider using toString for
//   the corresponding functionality instead?
// * what do you think? toString is simpler to rember, it is there anyway
//   and a "senseful" toString method would be nice and match the functionality!
// # state deswcription is meant to produce more emaningflu error messgaes
// # to string may include some parser depenednt variables ...
// # to remove ambiguity i have renamed it to getPositionDescription
// - should we define public static final string NO_NAMESPACE = ""?
//   ALEK added it too
//
// * removed content related methods because next() will accumulate
//   text already -> no need for additional methods
// # OK
// * Can we skip whitespace-only automatically when next() is called?
//   I would prefer that very much. If whitespace-only text is relevant
//   somewhere (which I have nevers seen ;-), there is still the
//   nextToken option
// # we can not skip whitespace as i may need to extract value of string "   \n" !
// * Renamed isEmptyElemTag to isEmptyElementTag
// # OK
// * Renamed isWhitespaceText to isWhitespace because
// # OK
//   "whitespace" already suggests "text". However,
//   if whitespace would be exposed using nextToken() only,
//   we could creat another event type instead
// # this will not work as you must expose all text content otherwise it is not XML parser
// # - however you can ignnore it if it isWhitespace
// # if you know that element ha sno mixed content use DISBALE_MIXED_CONTENT property
// * Would you agree to have the PullParserException
//   extend IOException (or RuntimeException) (saves 2 bytes
//   per call)
// # i have removed require as it is really simple method and user can implemented it
// # or we can have utility cloass available for this API with such things

/**
 * Interface defining simple XML Pull Parser.
 *
 * <p>Properties
 * <p>
 * All propertires are false by default.
 * Properties are encoded as flags passed to setProperty() method to
 * enable extra features. Conforming parsers MUST provide a
 * version with all features switched off and SHOULD provide
 * support for PROCESS_NAMESPACES. All other features are
 * optional.
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
    public final static byte CDSECT                = 5;
        
    /**
     * Ignorable whitespace was just read. For non-validating
     * parsers, this event is only reported by nextToken() when
     * outside the root elment.
     * Validating parsers may be able to detect ignorable whitespace at
     * other locations. Please note the differenct to isWhitespace().
     * The value of whotespace is available by calling getText()
     */
    public static final byte IGNORABLE_WHITESPACE = 6;
    
    /**
     * XML DOCTYPE declaration was just read
     * and getText() will return text that is inside DOCDECL
     */
    public static final int DOCDECL = 7;
    
    /**
     * XML processing instruction declaration was just read
     * and getText() will return text that is inside processing instruction.
     */
    public static final byte PROCESSING_INSTRUCTION = 8;
        
    /**
     * XML comment was just read and getText() will return value inside comment.
     */
    public static final int COMMENT                 = 9;
    
    /**
     * Entity reference was just read.
     * The value of entity is available as geText() (null if unknown entity)
     * and the entity naem is available by calling getName()
     */
    public final static byte ENTITY_REF            = 10;
    
    
    
    // ----------------------------------------------------------------------------
    // namespace related properties
    
    /**
     * Processing of namespaces is by default false.
     * Can not be changed during parsing
     */
    public static final int PROCESS_NAMESPACES = 0x101;
    
    /**
     * Report namespace attributes also - they can be distinguished
     * looking for prefix == "xmlns" or prefix == null and name == "xmlns
     * it is off by default and only meningful when PROCESS_NAMESPACES property is on.
     */
    public static final int REPORT_NAMESPACE_ATTRIBUTES = 0x102;
    
    // docdecl related properties
    
    /**
     * processing of docdecl is by default false
     * and if DOCDECL is encountred an exception will be thrown if
     * parser can not process it
     * Can not be changed during parsing.
     */
    public static final int PROCESS_DOCDECL = 0x111;
    
    /**
     * Reports the DOCDECL instead of throwing
     * an exception if PROCESS_DOCDECL is not
     * set, by defualt this property is off
     * Can not be changed during parsing.
     */
    public static final int REPORT_DOCDECL = 0x112;
    
    // additional (optional) features of parser
    
    /**
     * This is very useful property for parsing XML (such as SOAP) that has no mixed content
     * so parser can remove ignorable whitespaces when called by next()
     * See http://www.w3.org/TR/REC-xml#sec-mixed-content for more description
     */
    
    //public static final int NO_MIXED_CONTENT = 0x111;
    
    /**
     * Relaxed parsing rules
     *   are applied, allowing "abuse" of the parser for
     *   HTML parsing. Cannot be combined with namespace
     * processing. Can not be changed during parsing.
     */
    
    //public static final int RELAXED_PARSING = 0x0110;
    
    /** Use this call to change the general behaviour of the parser,
     such as namespace processing or doctype declaration handling.
     This method must be called before the first call to next or
     nextToken. Otherwise, an exception is trown. Allowed constants
     are: PROCESS_NAMESPACES, PROCESS_DOCDECL, REPORT_DOCDECL.
     Example: Use setProperty (PROCESS_NAMESPACES, true) in order
     to switch on namespace processing. Default settings correspond
     to properties requested from the factory. */
    
    public void setProperty (int propertyConstant,
                             boolean state) throws XmlPullParserException;
    
    public boolean getProperty (int propertyConstant);
    
    
    //    public void setFeature(String featureUri,
    //                           Object featureValue) throws XmlPullParserException;
    //
    //    public Object getFeature(String featureUri);
    
    
    /**
     * Set the input for parser.
     * Using null parameter will reset parser state.
     */
    public void setInput(Reader in) throws XmlPullParserException;
    
    /*
     * Reset the parser state.  Usefult to reclaim buffer spaces when
     * for exmaple when parser is returned to pool.
     
     what about setInput(null)
     
     public void reset() throws XmlPullParserException;
     
     */
    
    /**
     * Set the entity table to use by parser (in addition to standard XML
     * entities such as &amp; &lt; &gt;).
     * Must be called before parsing is started.
     */
    
    //public void setCharacterEntityTable (Hashtable h) throws XmlPullParserException;
    
    /**
     * Set new value for enyity.
     * <p><b>NOTE:</b> list of entites will be reset to to standard XML
     * entities such as &amp; &lt; &gt;) after each call to setInput
     * @see #setInput
     */
    public void defineCharacterEntity (String entity, String value) throws XmlPullParserException;
    
    /**
     * Return position in stack of first namespace slat for element at passed depth.
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
    
    /*
     * Return local part of qname.
     * For example for 'xsi:type' it returns 'type'.
     
     public String getQNameLocal(String qName)
     throws XmlPullParserException;
     */
    
    
    /** Return uri for the given prefix.
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
    
    /* Check if current TEXT event contains only whitespace characters.
     For IGNORABLE_WHITESPACE, this is always true. If the current event
     is neither a event TEXT nor a IGNORABLE_WHITESPACE event, false is
     returned. Please note that non-validating parsers are not
     able to distinguish whitespace and ignorable whitespace
     except from whitespace outside the root element. ignorable
     whitespace is reported as separate event which is exposed
     via nextToken only. */
    
    public boolean isWhitespace() throws XmlPullParserException;
    
    /**
     * Read current content as Stirng.
     *
     * <p><b>NOTE:</b> parser must be on TEXT, COMMENT, PROCESSING_INSTRUCTION
     * or DOCDECL event;
     * otherwise, null is returned. The parser will never generate a sequence of
     * two TEXT events.
     */
    
    public String getText () throws XmlPullParserException;
    
    
    // --------------------------------------------------------------------------
    // CONTENT related methods
    
    /*
     * Check if current CONTENT contains only whitespace characters.
     *
     public boolean isWhitespaceTextContent() throws XmlPullParserException;
     
     *
     * Read current content as Stirng
     * this method return gathered content for element
     * (aggregating multiple TEXT, entity references and CDATA sections).
     *
     * <p><b>NOTE:</b> parser must be on CONTENT event.
     *
     public String readTextContent()
     throws XmlPullParserException;
     
     *
     * Return how big is content.
     *
     * <p><b>NOTE:</b> parser must be on CONTENT event.
     *
     public int getTextContentLength()
     throws XmlPullParserException;
     
     */
    
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
     * Returns the raw name (prefix + ':' + localName) of the current element
     * (current event must be START_TAG or END_TAG)
     */
    //public String getRawName();
    
    
    /** Returns true if the current event is START_TAG and the
     tag is degenerated (e.g. &lt;foobar/&gt;).*/
    
    public boolean isEmptyElementTag();
    //public boolean isDegenerated ();
    
    
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
    
    /*
     * Returns the raw name of the specified attribute
     * Returns null if invalid index.
     
     public String getAttributeRawName(int index);
     
     
     /**
     * Returns the given attributes value
     * Returns null if invalid index.
     */
    public String getAttributeValue(int index);
    
    
    /**
     * Returns the given attributes value
     * Returns null if no attribute with rawName.
     */
    //public String getAttributeValueFromRawName(String rawName);
    
    /**
     * Returns the attributes value by attribute name.
     * If namespaces are disbaled use null namespace and
     */
    public String getAttributeValue(String namespace,
                                    String name);
    
    /*
     * Return true if attribute at index is namespace declaration
     * such as xmlns='...' or xmlns:prefix='...'
     
     this is really simple to implement if really needed...
     remvoing saves ~50bytes in the .class file
     
     
     public boolean isAttributeNamespaceDeclaration(int index);
     */
    
    
    // --------------------------------------------------------------------------
    // actual parsing methods
    
    /**
     * Returns the type of the current element (START_TAG, END_TAG, CONTENT, etc)
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

