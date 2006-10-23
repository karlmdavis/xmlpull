/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license see accompanying LICENSE_TESTS.txt file (available also at http://www.xmlpull.org)

package org.xmlpull.v1.dom2_builder;

//import junit.framework.Test;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.dom2_builder.DOM2XmlPullBuilder;

/**
 * Test some wrapper utility operations.
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 */
public class TestDOM2XmlPullBuilder extends TestCase {
    
    public static void main (String[] args) {
        junit.textui.TestRunner.run (new TestSuite(TestDOM2XmlPullBuilder.class));
    }
    
    
    public TestDOM2XmlPullBuilder(String name) {
        super(name);
    }
    
    protected void setUp() throws XmlPullParserException {
    }
    
    
    final String DEFAULT_NS_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
        "<ServerConfiguration xmlns=\" http://your-namespace-here\" \n"+
        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchem a-instance\" xsi:schemaLocation=\"http://your-namespace-here test.xsd\">\n"+
        "  <Server>savage-m60</Server>\n"+
        "</ServerConfiguration>\n"+
        "";

    public void testDomDefaultNs() throws IOException, XmlPullParserException {
        testDom(DEFAULT_NS_XML);
    }

    final String PREFIXED_NS_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
        "<jtc:ServerConfiguration xmlns:jtc=\" http://your-namespace-here\" \n"+
        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchem a-instance\" xsi:schemaLocation=\"http://your-namespace-here test.xsd\">\n"+
        "  <jtc:Server>savage-m60</jtc:Server>\n"+
        "</jtc:ServerConfiguration>\n"+
        "";

    public void testDomPrefixNs() throws IOException, XmlPullParserException {
        testDom(PREFIXED_NS_XML);
    }
    
    public void testDom(final String XML) throws IOException, XmlPullParserException {
        DOM2XmlPullBuilder builder = new DOM2XmlPullBuilder();
        
        StringReader reader = new StringReader(XML);
        
        // create document
        
        Element el1 = builder.parse(reader);
        // show it ...
    }
    
    public void testSimpleBuild() throws IOException, XmlPullParserException {
        DOM2XmlPullBuilder builder = new DOM2XmlPullBuilder();
        
        final String XML = "<n:foo xmlns:n='uri1'><bar n:attr='test' xmlns='uri2'>baz</bar></n:foo>";
        StringReader reader = new StringReader(XML);
        
        // create document
        
        Element el1 = builder.parse(reader);
        //System.out.println("doc="+doc);
        
        
        // serialize and deserialzie ...
        StringWriter sw = new StringWriter();
        
        // requires JAXP
        //        TransformerFactory xformFactory
        //            = TransformerFactory.newInstance();
        //        Transformer idTransform = xformFactory.newTransformer();
        //        Source input = new DOMSource(doc1);
        //        Result output = new StreamResult(sw);
        //        idTransform.transform(input, output);
        
        //OutputFormat fmt = new OutputFormat();
        //XMLSerializer serializer = new XMLSerializer(sw, null);
        //serializer.serialize(doc1);
        //sw.close();
        //String serialized = sw.toString();
        //System.out.println("serialized="+serialized);
        
        reader = new StringReader(XML);
        
        // reparse
        
        Element el2 = builder.parse(reader);
        
        // check that what was written is OK
        
        Element root = el2; //doc2.getDocumentElement();
        //System.out.println("root="+root);
        //System.out.println ("root ns=" + root.getNamespaceURI() + ", localName=" +root.getLocalName());
        assertEquals("uri1", root.getNamespaceURI());
        assertEquals("foo", root.getLocalName());
        
        NodeList children = root.getElementsByTagNameNS("*","bar");
        Element bar = (Element)children.item(0);
        //System.out.println ("bar ns=" + bar.getNamespaceURI() + ", localName=" +bar.getLocalName());
        assertEquals("uri2", bar.getNamespaceURI());
        assertEquals("bar", bar.getLocalName());
        
        //
        String attrValue = bar.getAttributeNS("uri1", "attr");
        assertEquals("test", attrValue);
        Attr attr = bar.getAttributeNodeNS("uri1", "attr");
        assertNotNull(attr);
        assertEquals("uri1", attr.getNamespaceURI());
        assertEquals("attr", attr.getLocalName());
        assertEquals("test", attr.getValue());
        
        
        Text text = (Text)bar.getFirstChild();
        //System.out.println("text="+text.getNodeValue());
        assertEquals("baz", text.getNodeValue());
    }
    
    
    
    private static String printable(char ch) {
        if(ch == '\n') {
            return "\\n";
        } else if(ch == '\r') {
            return "\\r";
        } else if(ch == '\t') {
            return "\\t";
        } if(ch > 127 || ch < 32) {
            StringBuffer buf = new StringBuffer("\\u");
            String hex = Integer.toHexString((int)ch);
            for (int i = 0; i < 4-hex.length(); i++)
            {
                buf.append('0');
            }
            buf.append(hex);
            return buf.toString();
        }
        return ""+ch;
    }
    
    private static String printable(String s) {
        if(s == null) return null;
        StringBuffer buf = new StringBuffer();
        for(int i = 0; i < s.length(); ++i) {
            buf.append(printable(s.charAt(i)));
        }
        s = buf.toString();
        return s;
    }
    
    private static String normalized(String s) {
        if(s == null) return null;
        StringBuffer buf = new StringBuffer();
        boolean seenCR = false;
        for(int i = 0; i < s.length(); ++i) {
            char ch = s.charAt(i);
            if(ch == '\r') {
                buf.append('\n');
                seenCR = true;
            } else if(ch == '\n') {
                if( !seenCR ) {
                    buf.append(ch);
                }
                seenCR = false;
            } else {
                buf.append(ch);
                seenCR = false;
            }
        }
        s = buf.toString();
        return s;
    }
    
}

