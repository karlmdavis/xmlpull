/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// see LICENSE.txt in distribution for copyright and license information

package org.xmlpull.v1;

/**
 * This exception is thrown to signal XML Pull Parser related faults.
 *
 * @author Aleksander Slominski [http://www.extreme.indiana.edu/~aslom/]
 */
public class XmlPullParserException extends Exception {
    protected Throwable detail;
    protected int row = -1;
    protected int column = -1;

    public XmlPullParserException() {
    }

    public XmlPullParserException(String s) {
        super(s);
    }

    public XmlPullParserException(String msg, XmlPullParser parser, Throwable thrwble) {
        super(msg + ((parser != null) ? parser.getPositionDescription() : ""));
        if(parser != null) this.row = parser.getLineNumber();
        if(parser != null) this.column = parser.getColumnNumber();
        this.detail = thrwble;
    }

    public Throwable getDetail() { return detail; }
    //public void setDetail(Throwable cause) { this.detail = cause; }
    public int getLineNumber() { return row; }
    public int getColumnNumber() { return column; }

    public String getMessage() {
        if(detail == null)
            return super.getMessage();
        else
            return super.getMessage() + "; nested exception is: \n\t"
                + detail.getMessage();
    }

    //NOTE: code that prints this and detail is difficult in J2ME
    public void printStackTrace() {
        if (detail == null) {
            super.printStackTrace();
        } else {
            synchronized(System.err) {
                System.err.println(super.getMessage() + "; nested exception is:");
                detail.printStackTrace();
            }
        }
    }

}

