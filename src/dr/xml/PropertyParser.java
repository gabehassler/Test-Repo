package dr.xml;
import dr.util.Property;
public class PropertyParser extends AbstractXMLObjectParser {
    public String getParserName() {
        return "property";
    }
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        Object object = xo.getChild(0);
        String name = xo.getStringAttribute("name");
        Property property;
        if (xo.hasAttribute("index")) {
            int index = xo.getIntegerAttribute("index");
            property = new Property(object, name, index);
        } else if (xo.hasAttribute("label")) {
            String label = xo.getStringAttribute("label");
            property = new Property(object, name, label);
        } else {
            property = new Property(object, name);
        }
        if (property.getGetter() == null)
            throw new XMLParseException("unknown property, " + name + ", for object, " + object + ", in property element");
        return property;
    }
    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************
    public String getParserDescription() {
        return "This element returns an object representing the named property of the given child object.";
    }
    public Class getReturnType() {
        return Object.class;
    }
    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }
    private XMLSyntaxRule[] rules = new XMLSyntaxRule[]{
            new StringAttributeRule("name", "name of the property", "length"),
            new ElementRule(Object.class)
    };
}
