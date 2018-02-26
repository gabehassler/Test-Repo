
package dr.inferencexml.distribution;

import dr.inference.distribution.WishartGammalDistributionModel;
import dr.inference.model.Parameter;
import dr.xml.*;

public class WishartGammaDistributionModelParser extends AbstractXMLObjectParser {

    public static final String WISHART_GAMMA_DISTRIBUTION_MODEL = "wishartGammaDistributionModel";
    public static final String DF_PARAMETER = "df";
    public static final String MIXING_PARAMETER = "mixing";
    public static final String SCALE_PARAMETER = "scale";
    public static final String FIX_MIXING = "fixMixing";

    public String getParserName() {
        return WISHART_GAMMA_DISTRIBUTION_MODEL;
    }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        XMLObject cxo = xo.getChild(DF_PARAMETER);
        Parameter df = (Parameter) cxo.getChild(Parameter.class);

        cxo = xo.getChild(MIXING_PARAMETER);
        Parameter mixing = (Parameter) cxo.getChild(Parameter.class);

        cxo = xo.getChild(SCALE_PARAMETER);
        Parameter scale = (Parameter) cxo.getChild(Parameter.class);

        if ((mixing.getDimension() != scale.getDimension())
                && df.getDimension() != 1) {
            throw new XMLParseException("DF, mixing and scale parameters have wrong dimensions in " + xo.getName()
                    + " element");
        }

        boolean randomMixing = !xo.getAttribute(FIX_MIXING, false);

        return new WishartGammalDistributionModel(df, mixing, scale, randomMixing);
    }

    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************

    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private final XMLSyntaxRule[] rules = {
            new ElementRule(DF_PARAMETER,
                    new XMLSyntaxRule[]{new ElementRule(Parameter.class)}),
            new ElementRule(MIXING_PARAMETER,
                    new XMLSyntaxRule[]{new ElementRule(Parameter.class)}),
            new ElementRule(SCALE_PARAMETER,
                    new XMLSyntaxRule[]{new ElementRule(Parameter.class)}),
            AttributeRule.newBooleanRule(FIX_MIXING, true),
    };

    public String getParserDescription() {
        return "more magic";
    }

    public Class getReturnType() {
        return WishartGammalDistributionModel.class;
    }

}
