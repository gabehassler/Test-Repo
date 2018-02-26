package dr.evoxml;
import dr.xml.*;
import dr.evolution.util.Location;
public class LocationParser extends AbstractXMLObjectParser {
    public static final String DESCRIPTION = "description";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";
    public String getParserName() { return Location.LOCATION; }
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        if (xo.getChildCount() > 0) {
            throw new XMLParseException("No child elements allowed in location element.");
        }
        String description = xo.getAttribute(DESCRIPTION, "");
        double longitude = parseLongLat(xo.getAttribute(LONGITUDE, ""));
        double latitude = parseLongLat(xo.getAttribute(LATITUDE, ""));
        return Location.newLocation(xo.getId(), description, longitude, latitude);
    }
    private double parseLongLat(final String value) throws XMLParseException {
        double d = 0.0;
        if (value != null && value.length() > 0) {
            try {
                d = Double.parseDouble(value);
            } catch (NumberFormatException nfe) {
                // @todo - parse degrees minutes and seconds
            }
        }
        return d;
    }
    public String getParserDescription() {
        return "Specifies a location with an optional longitude and latitude";
    }
    public XMLSyntaxRule[] getSyntaxRules() { return rules; }
    private XMLSyntaxRule[] rules = new XMLSyntaxRule[] {
            AttributeRule.newStringRule(DESCRIPTION, true,
                    "A description of this location"),
            AttributeRule.newStringRule(LONGITUDE, true,
                    "The longitude in degrees, minutes, seconds or decimal degrees"),
            AttributeRule.newStringRule(LATITUDE, true,
                    "The latitude in degrees, minutes, seconds or decimal degrees"),
    };
    public Class getReturnType() { return Location.class; }
}