package org.xmlpull.v1.serializer;

import java.io.*;

/** PLEASE NOTE: This interface is not part of the XmlPull 1.0 API (yet). It
    is just included as basis for discussion. It may change in any way */

public interface XmlSerializer {

    /** binds the given prefix to the given namespace. 
	valid for the next element including child elements */

    void setPrefix (String prefix, String namespace);

    /** sets the output to the given writer */

    void setOutput (Writer writer); 
    
    /** writes a start tag with the given namespace and name. 
	if the indent flag is set, a \r\n and getDepth () spaces 
	are written. If there is no prefix defined for the given namespace,
        a prefix will be defined automatically. */

    void startTag (String namespace, String name,
		   boolean indent) throws IOException;

    /** writes an attribute. calls to attribute must follow a call to
        startTag() immediately. if there is no prefix defined for the
        given namespace, a prefix will be defined automatically. */

    void attribute (String namespace, String name, 
		    String value) throws IOException;

    /** writes all pending output to the stream */

    void flush () throws IOException;

    void close () throws IOException;

    // indent must be set separately f. end: <!-- indented --> <tag>xxx</tag>
    // repetition of namespace and name is just for avoiding errors
    // background: in kXML I just had endTag, and non matching tags were
    // very difficult to find...

    void endTag (String namespace, String name, 
		 boolean indent) throws IOException;

    /** Writes text, where special XML chars are escaped automatically */

    void text (String text) throws IOException;

    /** writes an XML comment. Will be silently ignored in WBXML */

    void comment (String comment) throws IOException ;

    /** writes a processing instructuib */

    void processingInstruction (String pi);
}
