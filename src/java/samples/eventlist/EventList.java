package eventlist;

import java.io.*;

//import org.kxml2.io.*;
import org.xmlpull.v1.*;

public class EventList {

    public static void main (String [] args) throws IOException, XmlPullParserException{

	XmlReader xr = new XmlReader ();
	xr.setInput (new FileReader (args [0]));

	while (xr.nextToken () != xr.END_DOCUMENT) {
	    System.out.println (xr.getPositionDescription ());
	}

    }

}





