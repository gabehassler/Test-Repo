package dr.evoxml;
import dr.evolution.util.Date;
import dr.evolution.util.Location;
import dr.evolution.util.Taxon;
import dr.util.Attribute;
import dr.xml.*;
public class TaxonParser extends AbstractXMLObjectParser {
    public final static String TAXON = "taxon";
    public String getParserName() {
        return TAXON;
    }
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        if (dr.xml.XMLParser.ID.contains("\'") && dr.xml.XMLParser.ID.contains("\"")) {
            // unable to handle taxon names that contain both single and double quotes
            // as it won't be possible to wrap it in either.
            throw new XMLParseException("Illegal taxon name, " + dr.xml.XMLParser.ID + ", - contains both single and double quotes");
        }
        Taxon taxon = new Taxon(xo.getStringAttribute(dr.xml.XMLParser.ID));
        for (int i = 0; i < xo.getChildCount(); i++) {
            Object child = xo.getChild(i);
            if (child instanceof Date) {
                taxon.setDate((Date) child);
            } else if (child instanceof Location) {
                taxon.setLocation((Location) child);
            } else if (child instanceof Attribute) {
                final Attribute attr = (Attribute) child;
                taxon.setAttribute(attr.getAttributeName(), attr.getAttributeValue());
            } else if (child instanceof Attribute[]) {
                Attribute[] attrs = (Attribute[]) child;
                for (Attribute attr : attrs) {
                    taxon.setAttribute(attr.getAttributeName(), attr.getAttributeValue());
                }
            } else {
                throw new XMLParseException("Unrecognized element found in taxon element");
            }
        }
        return taxon;
    }
    public String getParserDescription() {
        return "";
    }
    public Class getReturnType() {
        return Taxon.class;
    }
    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }
    private final XMLSyntaxRule[] rules = {
            new StringAttributeRule(dr.xml.XMLParser.ID, "A unique identifier for this taxon"),
            new ElementRule(Attribute.Default.class, true),
            new ElementRule(Date.class, true),
            new ElementRule(Location.class, true)
    };
}
