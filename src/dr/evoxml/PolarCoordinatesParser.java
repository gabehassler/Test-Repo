package dr.evoxml;
import dr.geo.math.SphericalPolarCoordinates;
import dr.xml.*;
public class PolarCoordinatesParser extends AbstractXMLObjectParser {
public String getParserName() { return "latLong"; }
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
double latitude = xo.getDoubleAttribute("latitude");
double longitude = xo.getDoubleAttribute("longitude");
return new SphericalPolarCoordinates(latitude, longitude);
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public String getParserDescription() {
return "A latitude/longitude pair representing a point on the surface of the Earth.";
}
public Class getReturnType() { return SphericalPolarCoordinates.class; }
public XMLSyntaxRule[] getSyntaxRules() { return rules; }
private XMLSyntaxRule[] rules = new XMLSyntaxRule[] {
AttributeRule.newDoubleRule("longitude"),
AttributeRule.newDoubleRule("latitude")
};
}
