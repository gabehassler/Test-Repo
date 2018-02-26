package dr.inferencexml.distribution;
import dr.inference.distribution.TwoPieceLocationScaleDistributionModel;
import dr.inference.model.Parameter;
import dr.math.distributions.Distribution;
import dr.xml.*;
public class TwoPieceLocationScaleDistributionModelParser extends AbstractXMLObjectParser {
    public static final String DISTRIBUTION_MODEL = "twoPieceLocationScaleDistributionModel";
    public static final String LOCATION = "location";
    public static final String SIGMA = "sigma";
    public static final String GAMMA = "gamma";
    public static final String PARAMETERIZATION = "parameterization";
    public String getParserName() {
        return DISTRIBUTION_MODEL;
    }
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        Parameter locationParam;
        Parameter sigmaParam;
        Parameter gammaParam;
        XMLObject cxo = xo.getChild(LOCATION);
        if (cxo.getChild(0) instanceof Parameter) {
            locationParam = (Parameter) cxo.getChild(Parameter.class);
        } else {
            locationParam = new Parameter.Default(cxo.getDoubleChild(0));
        }
        String parameterizationLabel = (String) xo.getAttribute(PARAMETERIZATION);
        TwoPieceLocationScaleDistributionModel.Parameterization parameterization =
                TwoPieceLocationScaleDistributionModel.Parameterization.parseFromString(parameterizationLabel);
        if (parameterization == null) {
            throw new XMLParseException("Unrecognized parameterization '" + parameterizationLabel + "'");
        }
        cxo = xo.getChild(SIGMA);
        if (cxo.getChild(0) instanceof Parameter) {
            sigmaParam = (Parameter) cxo.getChild(Parameter.class);
        } else {
            sigmaParam = new Parameter.Default(cxo.getDoubleChild(0));
        }
        cxo = xo.getChild(GAMMA);
        if (cxo.getChild(0) instanceof Parameter) {
            gammaParam = (Parameter) cxo.getChild(Parameter.class);
        } else {
            gammaParam = new Parameter.Default(cxo.getDoubleChild(0));
        }
        Distribution distribution = (Distribution) xo.getChild(Distribution.class);
        return new TwoPieceLocationScaleDistributionModel(locationParam, distribution,
                sigmaParam, gammaParam, parameterization);
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
                            new XORRule(
                                    new ElementRule(Parameter.class),
                                    new ElementRule(Double.class)
                            )}
            ),
            new ElementRule(SIGMA,
                    new XMLSyntaxRule[]{
                            new XORRule(
                                    new ElementRule(Parameter.class),
                                    new ElementRule(Double.class)
                            )}, true
            ),
            new ElementRule(GAMMA,
                    new XMLSyntaxRule[]{
                            new XORRule(
                                    new ElementRule(Parameter.class),
                                    new ElementRule(Double.class)
                            )}, true
            ),
            new ElementRule(Distribution.class),
    };
    public String getParserDescription() {
        return "Describes a two-piece location-scale distribution with a given location and two scales " +
                "that can be used in a distributionLikelihood element";
    }
    public Class getReturnType() {
        return TwoPieceLocationScaleDistributionModelParser.class;
    }
}
