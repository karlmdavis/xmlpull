package org.xmlpull.v1.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

//ISSUE: how to write epilog?

//ISSUE: how to avoid possibility that user will call setPrefx and may override automatic prefix ...
//ISSUE: if namespace set in setPrefix was not used shoul dit still result in xmlns:ns='' declaration?

//ISSUE: is flush doing anything but call closeStartTag()?
//ISSUE: maybe replace flush() with closeStartTag() (if not used it is still called implicitly ...)
//      this will allow to write unesscaped strings safely (like for example BASE64 or hexbin)
//ISSUE: how to write efficiently unescaped XML --> use flush() and write into stream ...
//ISSUE: implicit closing of startTag() requires more description and exmaples!!!

// RESOLVED: left flush() with exact description what it does but also added closeTag()

//ISSUE: where setPrefix() should or not be called (before first startTag(), between attribute() ...
// RESOLVED: setPrefix() is affecting only

//ISSUE: add close() that validates if XML document was writent correctly (finsihed depth == 0, etc.)
//RESOLVED: instead provides endDocument() (and startDoument())

//ISSUE: have one method tagWithContent() that combines startTag, text and endTag  ...
//RESOLVED: later ...

//ISSUE: additional properties to specify indent character, indent size, attrib quotation character
//RESOLVED: added get/setProperty to support it as options

//ISSUE: should xml:space and xml:lang scope be maintained ?
//RESOLVED: not in this version

//ISSUE: document that element and attribute names and namespaces are not escpaed!
//ISSUE: document that comment, CDSECT etc. are not escaped!!!
//ISSUE: docuemnt that duplicate attribute declarations are not detected !


/**
 * Define an interface to serialziation of XML Infoset.
 * This interface abstracts away if serialized XML is XML 1.0 comaptible text or
 * other formats of XML 1.0 serializations (such as binary XML for example with WBXML).
 *
 * <p><b>PLEASE NOTE:</b> This interface is not part of the XmlPull 1.0 API (yet). It
 * is just included as basis for discussion. It may change in any way.
 *
 */

public interface XmlSerializer {
    /**
     * FEATURE: disable or enable serializer output indentation
     * for elements on that on or below current depth
     * (as defined by matching endTag() to current in-scope startTag()).
     * If supported in the implementation then this featue MUST be scoped
     * depending on startTag() and endTag() scope.
     *
     * <p><strong>NOTE:</strong> can be changed during parsing!
     * <p><strong>NOTE:</strong> may be ignored by serializers
     *  (for example when serializer produces binary XML output like WBXML)
     *
     * @see #getFeature
     * @see #setFeature
     */
    public static final String FEATURE_INDENT_OUTPUT =
        "http://xmlpull.org/v1/doc/features.html#indent-output";

    /**
     * Set feature identified by name (recommended to be URI for uniqueness).
     * If feature is not recocgnized then IllegalArgumentException MUST be thrown.
     */
    public void setFeature(String name,
                           boolean state) throws IllegalArgumentException;

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
                            Object value) throws IllegalArgumentException;

    /**
     * Look up the value of a property.
     *
     * The property name is any fully-qualified URI. I
     * <p><strong>NOTE:</strong> unknown properties are <string>always</strong> returned as null
     *
     * @param name The name of property to be retrieved.
     * @return The value of named property.
     */
    public Object getProperty(String name);


    /**
     * Set to use binary output stream with given encoding.
     */
    public void setOutput (OutputStream os, String encoding) throws IOException;

    /**
     * sets the output to the given writer;
     * insert big warning here -- no information about encoding is available
     */
    public void setOutput (Writer writer) throws IOException;


    public void startDocument (String encoding, Boolean standalone) throws IOException;

    public void endDocument () throws IOException;

    /**
     * Binds the given prefix to the given namespace.
     * valid for the next element including child elements.
     *
     * <p><b>NOTE:</b> prefixes "xml" and "xmlns" msut be already bound
     */
    public void setPrefix (String prefix, String namespace);

    /** writes a start tag with the given namespace and name.
     if the indent flag is set, a \r\n and getDepth () spaces
     are written. If there is no prefix defined for the given namespace,
     a prefix will be defined automatically.
     If namespace is nul no namespace prefix is printed but just name.
     */

    public void startTag (String namespace, String name) throws IOException;

    /** writes an attribute. calls to attribute must follow a call to
     startTag() immediately. if there is no prefix defined for the
     given namespace, a prefix will be defined automatically.
     If namespace is nul no namespace prefix is printed but just name.
     */

    public void attribute (String namespace, String name,
                           String value) throws IOException;

    /**
     * This method is called explicitly after startTag() and attribute()
     * to close XML start tag. Can be called directly to enforce
     * serializer to write completely start tag. No more attributes
     * is allowed to be added after this call.
     */
    public void closeStartTag () throws IOException;


    /**
     * repetition of namespace and name is just for avoiding errors
     * background: in kXML I just had endTag, and non matching tags were
     *  very difficult to find...
     * If namespace is nul no namespace prefix is printed but just name.
     */
    public void endTag (String namespace, String name) throws IOException;

    /** Writes text, where special XML chars are escaped automatically */
    public void text (String text) throws IOException;

    public void text (char [] buf, int start, int len) throws IOException;

    /**
     * write  CDSECT, ENTITY_REF, IGNORABLE_WHITESPACE,
     * PROCESSING_INSTRUCTION, COMMENT, and DOCDECL Some types may be
     * silently ignored in WBXML (XXX should we make a distinction
     * here, which may be ignored, and which events cause an
     * exception???? XXX)
     */

    public void cdsect (String text)  throws IOException;
    public void entityRef (String text)  throws IOException;
    public void processingInstruction (String text)  throws IOException;
    public void comment (String text)  throws IOException;
    public void docdecl (String text)  throws IOException;
    public void ignorableWhitespace (String text)  throws IOException;

    /**
     * writes all pending output to the stream,
     * if  startTag() or attribute() was caled then start tag is closed
     * by calling closeStartTag() and then flush() is called on
     * underlying output stream.
     */
    public void flush () throws IOException;


}

