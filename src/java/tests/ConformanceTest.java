import java.io.*;

import org.kxml2.io.*;
import org.xmlpull.v1.*;

public class ConformanceTest {

    public static void test (int i, int j,  
			     boolean generate)  throws IOException, XmlPullParserException {
	
	File xmlFile = new File ("test-"+i+".xml");
	File eventFile = new File ("test-"+i+"-" + j + ".events");

	System.out.println ((generate ? "generating " : "reading ") + eventFile);

	FileReader r = new FileReader (xmlFile);
	XmlPullParser xp = new XmlReader ();
	
	xp.setInput (r);

	boolean token = (j & 1) != 0;
	xp.setProperty (XmlPullParser.PROCESS_NAMESPACES, (j & 2) != 0);
	xp.setProperty (XmlPullParser.REPORT_NAMESPACE_ATTRIBUTES, (j & 4) != 0);
	xp.setProperty (XmlPullParser.REPORT_DOCDECL, (j & 8) != 0);
	
	BufferedReader reader = null;
	Writer writer = null;

	if (generate) 
	    writer = new FileWriter (eventFile);
	else 
	    reader = new BufferedReader (new FileReader (eventFile));
	
	
	do {
	    if (token) xp.nextToken ();
	    else xp.next ();

	    String event = describe (xp);

	    if (generate) {
		writer.write (event);
		writer.write ("\r\n");
	    }
	    else {
		String expected = reader.readLine ();
		if (!event.equals (expected)) 
		    throw new RuntimeException ("expected: "+expected+ " actual: "+event);
	    }
	}
	while (xp.getType () != XmlPullParser.END_DOCUMENT);

	r.close ();
	if (generate) 
	    writer.close ();
	else 
	    reader.close ();


	System.out.println (" - OK");
    }


    public static String escape (String s) {
	StringBuffer buf = new StringBuffer ();
	for (int i = 0; i < s.length (); i++) {
	    char c = s.charAt (i);
	    if (c < ' ') buf.append ("^" + ((char) (c + 64)));
	    else if (c == '^' || c=='\'') buf.append ("^"+c);
	    else buf.append (c);
	}
	return buf.toString ();
    }


    public static String describe (XmlPullParser xp)  throws IOException, XmlPullParserException {
	int type = xp.getType ();

	StringBuffer buf = new StringBuffer 
	    (""+xp.getLineNumber () + " " +XmlPullParser.TYPES [type]);
	
	if (type == XmlPullParser.START_TAG 
	    || type == XmlPullParser.END_TAG) {	    

	    buf.append (xp.isEmptyElementTag () ? " (empty): " : ": ");

	    buf.append ('<');
	    if (type == XmlPullParser.END_TAG) 
		buf.append ('/');
	    
	    buf.append ("{"+xp.getNamespace()+"}");
	    buf.append (xp.getPrefix()+":");
	    buf.append (xp.getName ());
	    
	    int cnt = xp.getAttributeCount ();
	    for (int i = 0; i < cnt; i++) {
		buf.append (' ');
		buf.append ("{" +xp.getAttributeNamespace (i)+"}");
		buf.append (xp.getAttributePrefix (i)+":");
		buf.append (xp.getAttributeName (i)+"='");
		buf.append (escape (xp.getAttributeValue (i))+"'");
	    }


	    buf.append ('>');
	}
	else {
	    buf.append (xp.isWhitespace () ? " (whitespace): " : ": ");
	    buf.append ("'"+escape (xp.getText ())+"'");
	}

	return buf.toString ();
    } 


    public static void main (String [] args) throws IOException, XmlPullParserException{

	boolean generate = args.length > 0 && args [0].equals ("-generate");
	
	for (int i = 0; i < 1; i++) {

	    for (int j = 0; j < 16; j++) {
		try {
		    test (i, j, generate);
		}
		catch (Exception e) {
		    e.printStackTrace ();
		}
	    }
	}	
    }
}











