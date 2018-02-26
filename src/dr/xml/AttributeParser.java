
package dr.xml;

import dr.util.Attribute;

public class AttributeParser extends AbstractXMLObjectParser {

	public final static String ATTRIBUTE = "attr";
	public final static String NAME = "name";
	public final static String VALUE = "value";

	public String getParserName() { return ATTRIBUTE; }
		
	public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        final String name = xo.getStringAttribute(NAME);
        if( xo.hasAttribute(VALUE) ) {
            return new Attribute.Default<Object>(name, xo.getAttribute(VALUE));
        }
        final Object value = xo.getChild(0);

        return new Attribute.Default<Object>(name, value);
	}
	
	//************************************************************************
	// AbstractXMLObjectParser implementation
	//************************************************************************

	public String getParserDescription() {
		return "This element represents a name/value pair.";
	}
	
	public Class getReturnType() { return Attribute.class; }

	public XMLSyntaxRule[] getSyntaxRules() { return rules; }

	private final XMLSyntaxRule[] rules = {
		new StringAttributeRule("name", "The name to give to this attribute"),
		new ElementRule(Object.class )
	};
}
