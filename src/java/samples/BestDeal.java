import org.xmlpull.v1.*;
import java.io.*;
import java.util.*;
import java.text.MessageFormat;

public class BestDeal {

    protected static final String MESSAGE =
        "The best deal is proposed by {0}. "
            + "A(n) {1} delivered in {2,number,integer} days for "
            + "{3,number,currency}";

    protected static final String NAMESPACE_URI =
        "http://www.psol.com/xbe2/listing8.3";

    /**
     * properties we are collecting: best price, delivery time,
     * product and vendor names
     */

    public double price = Double.MAX_VALUE;
    public int delivery = Integer.MAX_VALUE;
    public String product = null;
    public String vendor = null;

    /**
     * target delivery value (refuse elements above this target)
     */

    protected int targetDelivery;

    /**
     * creates an "empty" BestDeal with the given target for delivery
     * @param td the target for delivery
     */

    public BestDeal(int td) {
        targetDelivery = td;
    }

    /** updates the best deal from the given list in the format
    */

    public void update(XmlPullParser parser)
        throws IOException, XmlPullParserException {

        parser.require(parser.START_DOCUMENT, null, null);

        parser.nextTag();
        parser.require(parser.START_TAG, NAMESPACE_URI, "name");
        product = parser.nextText();
        parser.require(parser.END_TAG, NAMESPACE_URI, "name");

        parser.nextTag();
        parser.require(parser.START_TAG, NAMESPACE_URI, "price-list");

        while (parser.nextTag() == parser.START_TAG) {
            checkVendor(parser);
        }

        parser.require(parser.END_TAG, NAMESPACE_URI, "price-list");

        parser.next();
        parser.require(parser.END_DOCUMENT, null, null);
    }

    /** subroutine handling a single vendor */

    public void checkVendor(XmlPullParser parser)
        throws IOException, XmlPullParserException {
        parser.require(parser.START_TAG, NAMESPACE_URI, "vendor");
        parser.nextTag();
        String currentVendor = null;

        while (parser.nextTag() == parser.START_TAG) {
            parser.require(parser.START_TAG, NAMESPACE_URI, null);
            String name = parser.getName();
            if (name.equals("name"))
                currentVendor = parser.nextText();
            else if (name.equals("price-quote")) {
                int currentDelivery =
                    Integer.parseInt(parser.getAttributeValue("", "delivery"));
                double currentPrice = Double.parseDouble(parser.nextText());

                if (currentDelivery < delivery && currentPrice < price) {
                    vendor = currentVendor;
                    price = currentPrice;
                    delivery = currentDelivery;
                }
            }
            else {
                System.err.println(
                    "unknown element: "
                        + name
                        + " value: "
                        + parser.nextText());
            }
            parser.require(parser.END_TAG, NAMESPACE_URI, name);
        }
        parser.require(parser.END_TAG, NAMESPACE_URI, "vendor");
    }

    /**
     * main() method
     * decodes command-line parameters and invoke the parser
     * @param args command-line argument
     * @throw Exception catch-all for underlying exceptions
     */

    public static void main(String[] args)
        throws IOException, XmlPullParserException {

        if (args.length < 2) {
            System.out.println("BestDeal <file> <delivery>");
            return;
        }

        BestDeal bestDeal = new BestDeal(Integer.parseInt(args[1]));

        XmlPullParser parser =
            XmlPullParserFactory.newInstance().newPullParser();

        InputStream is = new FileInputStream(args[0]);
        parser.setInput(is, null);
        bestDeal.update(parser);
        parser.setInput(null);
        is.close();

        Object[] objects =
            new Object[] {
                bestDeal.vendor,
                bestDeal.product,
                new Integer(bestDeal.delivery),
                new Double(bestDeal.price)};

        System.out.println(MessageFormat.format(MESSAGE, objects));
    }

}
