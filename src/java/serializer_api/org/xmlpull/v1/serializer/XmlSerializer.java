package org.xmlpull.v1.serializer;

import java.io.*;

/** PLEASE NOTE: This interface is not part of the XmlPull 1.0 API (yet). It
    is just included as basis for discussion. It may change in any way.

    The type constants match PER DEFINITION those defined in the
    XmlPullParser API.  They are repeated here for interface
    independency. */

public interface XmlSerializer {

    public static final int CDSECT = 5;
    public static final int ENTITY_REF = 6;
    public static final int IGNORABLE_WHITESPACE = 7;
    public static final int PROCESSING_INSTRUCTION = 8;
    public static final int COMMENT = 9;
    public static final int DOCDECL = 10;

   public void setFeature(String name,
                           boolean state) throws IOException;

    /**
     * Return the current value of the feature with given name.
     * <p><strong>NOTE:</strong> unknown features are <string>always</strong> returned as false
     *
     * @param name The name of feature to be retrieved.
     * @return The value of named feature.
     * @exception IllegalArgumentException if feature string is null
     */

    public boolean getFeature(String name);

    /** binds the given prefix to the given namespace.
        valid for the next element including child elements */

    void setPrefix (String prefix, String namespace);


    void setOutput (OutputStream os, String encoding);

    /** sets the output to the given writer;
        insert big warning here */

    void setOutput (Writer writer);

    /** writes a start tag with the given namespace and name.
        if the indent flag is set, a \r\n and getDepth () spaces
        are written. If there is no prefix defined for the given namespace,
        a prefix will be defined automatically. */

    void startTag (String namespace, String name) throws IOException;

    /** writes an attribute. calls to attribute must follow a call to
        startTag() immediately. if there is no prefix defined for the
        given namespace, a prefix will be defined automatically. */

    void attribute (String namespace, String name,
                    String value) throws IOException;

    /** writes all pending output to the stream */

    void flush () throws IOException;


    // indent must be set separately f. end: <!-- indented --> <tag>xxx</tag>
    // repetition of namespace and name is just for avoiding errors
    // background: in kXML I just had endTag, and non matching tags were
    // very difficult to find...

    void endTag (String namespace, String name,
                 boolean indent) throws IOException;

    /** Writes text, where special XML chars are escaped automatically */

    void text (String text) throws IOException;

    void text (char [] buf, int start, int len) throws IOException;

    /** Find a better name for this.... writes a "legacy event": Any
        of CDSECT, ENTITY_REF, IGNORABLE_WHITESPACE,
        PROCESSING_INSTRUCTION, COMMENT, and DOCDECL Some types may be
        silently ignored in WBXML (XXX should we make a distinction
        here, which may be ignored, and which events cause an
        exception???? XXX) */

    void legacy (int type, String text)  throws IOException;
    void legacy (int type, char [] text, int start, int len) throws IOException;
}

