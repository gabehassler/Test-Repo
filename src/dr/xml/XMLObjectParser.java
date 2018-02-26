
package dr.xml;

public interface XMLObjectParser {

    Class getReturnType();

    Object parseXMLObject(XMLObject xo, String id, ObjectStore store, boolean strictXML) throws XMLParseException;

    String getParserName();

    String[] getParserNames();

    String getParserDescription();

    boolean hasExample();

    String getExample();


    String toHTML(XMLDocumentationHandler handler);

    String toWiki(XMLDocumentationHandler handler);


    XMLSyntaxRule[] getSyntaxRules();

    boolean isAllowed(String elementName);

    boolean hasSyntaxRules();
}
