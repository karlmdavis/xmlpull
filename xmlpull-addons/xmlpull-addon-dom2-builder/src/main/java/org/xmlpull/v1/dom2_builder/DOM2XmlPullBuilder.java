/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license please see accompanying LICENSE.txt file (available also at http://www.xmlpull.org/)

package org.xmlpull.v1.dom2_builder;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
//import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * <strong>Simplistic</strong> DOM2 builder that should be enough to do support most cases.
 * Requires JAXP DOMBuilder to provide DOM2 implementation.
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 */

public class DOM2XmlPullBuilder {

    /**
     * Minimal inline test
     */
    public static void main(String[] args) throws Exception {
        DOM2XmlPullBuilder builder = new DOM2XmlPullBuilder();

        final String XML = "<n:foo xmlns:n='uri1'><bar n:attr='test' xmlns='uri2'>baz</bar></n:foo>";
        StringReader reader = new StringReader(XML);

        // create document

        Document doc1 = builder.parse(reader);
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

        Document doc2 = builder.parse(reader);

        // check that what was written is OK

        Element root = doc2.getDocumentElement();
        //System.out.println("root="+root);
        System.out.println ("root ns=" + root.getNamespaceURI() + ", localName=" +root.getLocalName());
        assertEquals("uri1", root.getNamespaceURI());
        assertEquals("foo", root.getLocalName());

        NodeList children = root.getElementsByTagNameNS("*","bar");
        Element bar = (Element)children.item(0);
        System.out.println ("bar ns=" + bar.getNamespaceURI() + ", localName=" +bar.getLocalName());
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
        System.out.println("text="+text.getNodeValue());
        assertEquals("baz", text.getNodeValue());

    }

    private static void assertEquals(String expected, String s) {
        if((expected != null && !expected.equals(s)) || (expected == null && s == null)) {
            throw new RuntimeException("expected '"+expected+"' but got '"+s+"'");
        }
    }
    private static void assertNotNull(Object o) {
        if(o == null) {
            throw new RuntimeException("expected no null value");
        }
    }

    protected XmlPullParser pp;
    protected Document doc;

    public DOM2XmlPullBuilder() throws XmlPullParserException {
        pp = XmlPullParserFactory.newInstance().newPullParser();
        pp.setFeature(pp.FEATURE_PROCESS_NAMESPACES, true);
    }
    public DOM2XmlPullBuilder(XmlPullParser pp) throws XmlPullParserException {
        this.pp = pp;
    }

    public Document parse(Reader reader) throws XmlPullParserException, IOException {
        try {
            DocumentBuilderFactory factory
                = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            doc = builder.newDocument();
        } catch (FactoryConfigurationError ex) {
            new XmlPullParserException(
                "could not configure factory JAXP DocumentBuilderFactory: "+ex, null, ex);
        } catch (ParserConfigurationException ex) {
            new XmlPullParserException(
                "could not configure parser JAXP DocumentBuilderFactory: "+ex, null, ex);
        }
        pp.setInput(reader);
        pp.next();
        pp.require( pp.START_TAG, null, null);
        Element root = parseNode();
        doc.appendChild(root);
        return doc;
    }

    protected Element parseNode() throws XmlPullParserException, IOException {
        pp.require( pp.START_TAG, null, null);
        String name = pp.getName();
        String ns = pp.getNamespace(    );
        String prefix = pp.getPrefix();
        String qname = prefix != null ? prefix+":"+name : name;
        Element parent = doc.createElementNS(ns, qname);

        //declare namespaces - quite painful and easy to fail process in DOM2
        for (int i = pp.getNamespaceCount(pp.getDepth()-1); i < pp.getNamespaceCount(pp.getDepth()); ++i)
        {
            String xmlnsPrefix = pp.getNamespacePrefix(i);
            String xmlnsUri = pp.getNamespaceUri(i);
            String xmlnsDecl = (prefix != null) ? "xmlns:"+xmlnsPrefix : "xmlns";
            parent.setAttributeNS("http://www.w3.org/2000/xmlns/", xmlnsDecl, xmlnsUri);
        }

        // process attributes
        for (int i = 0; i < pp.getAttributeCount(); i++)
        {
            String attrNs = pp.getAttributeNamespace(i);
            String attrName = pp.getAttributeName(i);
            String attrValue = pp.getAttributeValue(i);
            if(attrNs == null || attrNs.length() == 0) {
                parent.setAttribute(attrName, attrValue);
            } else {
                String attrPrefix = pp.getAttributePrefix(i);
                String attrQname = attrPrefix != null ? attrPrefix+":"+attrName : attrName;
                parent.setAttributeNS(attrNs, attrQname, attrValue);
            }
        }

        // process children
        while( pp.next() != pp.END_TAG ) {
            if (pp.getEventType() == pp.START_TAG) {
                Element el = parseNode();
                parent.appendChild(el);
            } else if (pp.getEventType() == pp.TEXT) {
                String text = pp.getText();
                Text textEl = doc.createTextNode(text);
                parent.appendChild(textEl);
            } else {
                throw new XmlPullParserException(
                    "unexpected event "+pp.TYPES[ pp.getEventType() ], pp, null);
            }
        }
        pp.require( pp.END_TAG, ns, name);
        return parent;
    }

}

