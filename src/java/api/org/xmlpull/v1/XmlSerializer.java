package org.xmlpull.v1;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

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
     * This call is valid for the next element including child elements.
     * The prefix and namespace MUST be always declared even if prefix
     * is not used in element (startTag() or attribute()) - for XML 1.0 infoset
     * it must result in declaring <code>xmlns:prefix='namespace'</code>
     * (or <code>xmlns:prefix="namespace"</code> depending what character is used
     * to quote attribute value).
     *
     * <p><b>NOTE:</b> this method MUST be called directly before startTag()
     *   and if anything but startTag() or setPrefix() is called next there will be exception.
     * <p><b>NOTE:</b> prefixes "xml" and "xmlns" are already bound
     *   and can not be redefined see:
     * <a href="http://www.w3.org/XML/xml-names-19990114-errata#NE05">Namespaces in XML Errata</a>.
     */
    public void setPrefix (String prefix, String namespace) throws IOException;

    /**
     * Return namespace that corresponds to given prefix
     * If there is no prefix bound to this namespace return null
     * but if generatePrefix is false then return generated prefix.
     *
     * <p><b>NOTE:</b> if the prefix is empty string "" and defualt namespace is bound
     * to this prefix then empty string ("") is returned.
     *
     * <p><b>NOTE:</b> prefixes "xml" and "xmlns" are already bound
     *   will have values as defined
     * <a href="http://www.w3.org/TR/REC-xml-names/">Namespaces in XML specification</a>
     */
    public String getPrefix (String namespace, boolean generatePrefix);

    /**
     * Writes a start tag with the given namespace and name.
     * If there is no prefix defined for the given namespace,
     * a prefix will be defined automatically.
     * The explicit prefixes for namespaces can be established by calling setPrefix()
     * immediately before this method.
     * If namespace is empty string no namespace prefix is printed but just name.
     */

    public XmlSerializer startTag (String namespace, String name) throws IOException;

    /**
     * Writes an attribute. calls to attribute must follow a call to
     * startTag() immediately. if there is no prefix defined for the
     * given namespace, a prefix will be defined automatically.
     * If namespace is nul no namespace prefix is printed but just name.
     */

    public XmlSerializer attribute (String namespace, String name,
                           String value) throws IOException;

    /**
     * This method is called explicitly after startTag() and attribute()
     * to close XML start tag. Can be called directly to enforce
     * serializer to write completely start tag. No more attributes
     * is allowed to be added after this call.
     */
    //public void closeStartTag () throws IOException;
    // use text("") instead


    /**
     * Write end tag. Repetition of namespace and name is just for avoiding errors
     * background: in kXML I just had endTag, and non matching tags were
     *  very difficult to find...
     * If namespace is nul no namespace prefix is printed but just name.
     */
    public XmlSerializer endTag (String namespace, String name) throws IOException;

    /** Writes text, where special XML chars are escaped automatically */
    public XmlSerializer text (String text) throws IOException;

    public XmlSerializer text (char [] buf, int start, int len) throws IOException;

    /**
     * write  CDSECT, ENTITY_REF, IGNORABLE_WHITESPACE,
     *  PROCESSING_INSTRUCTION, COMMENT, and DOCDECL Some types may be
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
     * and flush() is called on underlying output stream.
     */
    public void flush () throws IOException;


}

