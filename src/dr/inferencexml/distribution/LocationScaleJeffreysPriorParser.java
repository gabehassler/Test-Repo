package dr.inferencexml.distribution;
import dr.inference.distribution.LocationScaleJeffreysPrior;
import dr.inference.model.Parameter;
import dr.xml.*;
public class LocationScaleJeffreysPriorParser extends AbstractXMLObjectParser {
public static final String PARSER_NAME = "locationScaleJeffreysPrior";
public static final String LOCATION = TwoPieceLocationScaleDistributionModelParser.LOCATION;
public static final String SIGMA = TwoPieceLocationScaleDistributionModelParser.SIGMA;
public static final String GAMMA = TwoPieceLocationScaleDistributionModelParser.GAMMA;
public static final String PARAMETERIZATION = TwoPieceLocationScaleDistributionModelParser.PARAMETERIZATION;
public static final String ALPHA_0 = "alpha0";
public static final String BETA_0 = "beta0";
public String getParserName() {
return PARSER_NAME;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
Parameter location = (Parameter) xo.getChild(LOCATION).getChild(Parameter.class);
Parameter sigma = (Parameter) xo.getChild(SIGMA).getChild(Parameter.class);
Parameter gamma = (Parameter) xo.getChild(GAMMA).getChild(Parameter.class);
Parameter alpha0 = (Parameter) xo.getChild(ALPHA_0).getChild(Parameter.class);
Parameter beta0 = (Parameter) xo.getChild(BETA_0).getChild(Parameter.class);
String typeString = xo.getStringAttribute(PARAMETERIZATION);
LocationScaleJeffreysPrior.Type type = LocationScaleJeffreysPrior.Type.parseFromString(
typeString
);
if (type == null) {
throw new XMLParseException("Unable to parse location-scale Jeffrey priors type " + typeString);
}
return new LocationScaleJeffreysPrior(location, sigma, gamma, alpha0, beta0, type);
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private final XMLSyntaxRule[] rules = {
AttributeRule.newStringArrayRule(PARAMETERIZATION),
new ElementRule(LOCATION,
new XMLSyntaxRule[]{
new ElementRule(Parameter.class),
}),
new ElementRule(SIGMA,
new XMLSyntaxRule[]{
new ElementRule(Parameter.class),
}),
new ElementRule(GAMMA,
new XMLSyntaxRule[]{
new ElementRule(Parameter.class),
}),
new ElementRule(ALPHA_0,
new XMLSyntaxRule[]{
new ElementRule(Parameter.class),
}, true),
new ElementRule(BETA_0,
new XMLSyntaxRule[]{
new ElementRule(Parameter.class),
}, true),
};
public String getParserDescription() {
return "Calculates the likelihood of hyperparameters of the two-piece location-scale model.";
}
public Class getReturnType() {
return LocationScaleJeffreysPrior.class;
}
}
