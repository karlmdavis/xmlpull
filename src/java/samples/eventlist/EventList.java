package eventlist;

import java.io.*;

//import org.kxml2.io.*;
import org.xmlpull.v1.*;

public class EventList {

    public static void main (String [] args) throws IOException, XmlPullParserException{

        //XmlReader xr = new XmlReader ();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput (new FileReader (args [0]));
        int eventType;
        while ((eventType = xpp.next()) != xpp.END_DOCUMENT) {
            if(eventType == xpp.START_TAG) {
                System.out.println ("START_TAG "+xpp.getName());
            } else if(eventType == xpp.END_TAG) {
                System.out.println ("END_TAG  "+xpp.getName());
            } else if(eventType == xpp.TEXT) {
                System.out.println ("TEXT     "+xpp.getName());
            }
        }

    }

}





