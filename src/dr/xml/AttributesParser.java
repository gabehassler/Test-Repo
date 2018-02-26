
package dr.xml;

import dr.util.Attribute;

public class AttributesParser extends AbstractXMLObjectParser {

	public final static String ATTRIBUTES = "attributes";
	public final static String NAMES = "names";
	public final static String VALUES = "values";

	public String getParserName() { return ATTRIBUTES; }
		
	public Object parseXMLObject(XMLObject xo) throws XMLParseException {
		
		String[] names = ((XMLObject)xo.getChild(NAMES)).getStringArrayChild(0);
		String[] values =((XMLObject)xo.getChild(VALUES)).getStringArrayChild(0);
		
		if (names.length != values.length) {
			throw new XMLParseException("The number of names and values must match.");
		}
		
		Attribute[] attributes = new Attribute[names.length];
		for (int i =0; i < attributes.length; i++) {
			attributes[i] = new Attribute.Default(names[i], values[i]);
		}
		
		return attributes;
	}

	//************************************************************************
	// AbstractXMLObjectParser implementation
	//************************************************************************

	public String getParserDescription() {
		return "This element represents an array of name/value pairs.";
	}
	
	public Class getReturnType() { return Attribute[].class; }

	public XMLSyntaxRule[] getSyntaxRules() { return rules; }

	private XMLSyntaxRule[] rules = new XMLSyntaxRule[] {
		AttributeRule.newStringArrayRule("names"),
		AttributeRule.newStringArrayRule("values" )
	};
	
}
