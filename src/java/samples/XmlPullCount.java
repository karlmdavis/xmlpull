/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license please see accompanying LICENSE.txt file (available also at http://www.xmlpull.org/)

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Simple example that counts XML elements, characters and attributes.
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 */
public class XmlPullCount
{
    public final static String SAMPLE_XML =
        "<?xml version=\"1.0\"?>\n"+
        "\n"+
        "<poem xmlns=\"http://www.megginson.com/ns/exp/poetry\">\n"+
        "<title>Roses are Red</title>\n"+
        "<l>Roses are red,</l>\n"+
        "<l>Violets are blue;</l>\n"+
        "<l>Sugar is sweet,</l>\n"+
        "<l>And I love you.</l>\n"+
        "</poem>";
    int countChars;
    int countAttribs;
    int countSTags;

    public static void main (String args[])
        throws XmlPullParserException, IOException
    {


        XmlPullParserFactory factory = XmlPullParserFactory.newInstance(
            System.getProperty(XmlPullParserFactory.PROPERTY_NAME), null);
        factory.setNamespaceAware(true);
        System.out.println("using factory "+factory.getClass());

        XmlPullParser xpp = factory.newPullParser();
        System.out.println("using parser "+xpp.getClass());

        XmlPullCount app = new XmlPullCount();

        for(int c = 0; c < 2; ++c) {
            System.out.println("run#"+c);
            app.resetCounters();
            if(args.length == 0) {
                System.out.println("Parsing simple sample XML length="+SAMPLE_XML.length());
                xpp.setInput( new StringReader( SAMPLE_XML ) );
                app.countXml(xpp);
            } else {
                //r (int i = 0; i < args.length; i++) {

                File f = new File(args[0]);
                System.out.println("Parsing file: "+args[0]+" length="+f.length());
                xpp.setInput ( new FileReader ( args [0] ) );
                app.countXml(xpp);
                //
            }
            app.printReport();
        }
        System.out.println("finished");
    }

    public void resetCounters() {
        countChars = countSTags = countAttribs = 0;
    }

    public void printReport() {
        System.out.println("characters="+countChars
                               +" elements="+countSTags
                               +" attributes="+countAttribs);

    }

    public void countXml(XmlPullParser xpp) throws XmlPullParserException, IOException {

        int holderForStartAndLength[] = new int[2];


        int eventType = xpp.getEventType();
        if(eventType == XmlPullParser.START_DOCUMENT) {
            eventType = xpp.next();
        }
        while (eventType != XmlPullParser.END_DOCUMENT) {
            //System.out.println("pos="+xpp.getPositionDescription());
            if(eventType == XmlPullParser.START_TAG) {
                ++countSTags;
                countAttribs += xpp.getAttributeCount();
            } else if(eventType == XmlPullParser.TEXT) {
                //char ch[] = xpp.getTextCharacters(holderForStartAndLength);
                xpp.getTextCharacters(holderForStartAndLength);
                //int start = holderForStartAndLength[0];
                int length = holderForStartAndLength[1];
                countChars += length;
            }
            eventType = xpp.next();
        }
    }
}
