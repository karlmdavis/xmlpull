import java.io.FileInputStream;

import org.xmlpull.v1.*;
import org.xmlpull.v1.xmlrpc.XmlRpcParser;


public class XmlRpcSample {

	public static void main(String[] argv) throws Exception {

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
 		XmlPullParser pp = factory.newPullParser();

		pp.setInput(new FileInputStream("sample.xml"), null);

		XmlRpcParser xrp = new XmlRpcParser(pp);
		
		System.out.println("parsed: "+ xrp.parseResponse());
		
	}


}
