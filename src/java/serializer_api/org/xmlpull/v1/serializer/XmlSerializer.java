package org.xmlpull.v1.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

//ISSUE: is flush doing anything but call closeStartTag()?
//ISSUE: maybe replace flush() with closeStartTag() (if not used it is still called implicitly ...)
//      this will allow to write unesscaped strings safely (like for example BASE64 or hexbin)
//ISSUE: how to write efficiently unescaped XML --> use flush() and write into stream ...

//ISSUE: where setPrefix() should or not be called (before first startTag(), between attribute() ...

//ISSUE: add close() that validates if XML document was writent correctly (finsihed depth == 0, etc.)

//ISSUE: have one method tagWithContent() that combines startTag, text and endTag  ...

//ISSUE: additional properties to specify indent character, indent size, attrib quotation character

//ISSUE: should xml:space and xml:lang scope be maintained ?

//ISSUE: document that element and attribute names and namespaces are not escpaed!
//ISSUE: document that comment, CDSECT etc. are not escaped!!!
//ISSUE: docuemnt that duplicate attribute declarations are not detected !


//ISSUE: implicit closing of startTag() requires more description and exmaples!!!


/** PLEASE NOTE: This interface is not part of the XmlPull 1.0 API (yet). It
 is just included as basis for discussion. It may change in any way.

 The type constants match PER DEFINITION those defined in the
 XmlPullParser API.  They are repeated here for interface
 independency. */

public interface XmlSerializer {
    /**
     * FEATURE: disable or enable serializer output indentation
     * for elements on that on or below current depth
     * (as defined by matching endTag() to current in-scope startTag()).
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


    public void setOutput (OutputStream os, String encoding) throws IOException;

    /** sets the output to the given writer;
     insert big warning here */

    public void setOutput (Writer writer) throws IOException;

    /** binds the given prefix to the given namespace.
     valid for the next element including child elements */

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

    /*
     // repetition of namespace and name is just for avoiding errors
     // background: in kXML I just had endTag, and non matching tags were
     // very difficult to find...
     If namespace is nul no namespace prefix is printed but just name.
     */
    public void endTag (String namespace, String name) throws IOException;

    /** Writes text, where special XML chars are escaped automatically */
    public void text (String text) throws IOException;

    public void text (char [] buf, int start, int len) throws IOException;

    /** Find a better name for this.... writes a "legacy event": Any
     of CDSECT, ENTITY_REF, IGNORABLE_WHITESPACE,
     PROCESSING_INSTRUCTION, COMMENT, and DOCDECL Some types may be
     silently ignored in WBXML (XXX should we make a distinction
     here, which may be ignored, and which events cause an
     exception???? XXX) */

    public void cdsect (String text)  throws IOException;
    public void entityRef (String text)  throws IOException;
    public void processingInstruction (String text)  throws IOException;
    public void comment (String text)  throws IOException;
    public void docdecl (String text)  throws IOException;
    public void ignorableWhitespace (String text)  throws IOException;

    /**
     * writes all pending output to the stream,
     * if  startTag() or attribute() was caled then start tag is closed
     * no more attributes an be added by calling attribute()
     */
    public void flush () throws IOException;


}

