package dr.inferencexml.distribution;
import dr.inference.distribution.MultivariateDistributionLikelihood;
import dr.inference.distribution.MultivariateNormalDistributionModel;
import dr.inference.model.MatrixParameter;
import dr.inference.model.Parameter;
import dr.xml.*;
public class MultivariateNormalDistributionModelParser extends AbstractXMLObjectParser {
    public static final String NORMAL_DISTRIBUTION_MODEL = "multivariateNormalDistributionModel";
    public String getParserName() {
        return NORMAL_DISTRIBUTION_MODEL;
    }
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        XMLObject cxo = xo.getChild(MultivariateDistributionLikelihood.MVN_MEAN);
        Parameter mean = (Parameter) cxo.getChild(Parameter.class);
        cxo = xo.getChild(MultivariateDistributionLikelihood.MVN_PRECISION);
        MatrixParameter precision = (MatrixParameter) cxo.getChild(MatrixParameter.class);
        if (mean.getDimension() != precision.getRowDimension() ||
                mean.getDimension() != precision.getColumnDimension())
            throw new XMLParseException("Mean and precision have wrong dimensions in " + xo.getName() + " element");
        return new MultivariateNormalDistributionModel(mean, precision);
    }
    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************
    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }
    private final XMLSyntaxRule[] rules = {
            new ElementRule(MultivariateDistributionLikelihood.MVN_MEAN,
                    new XMLSyntaxRule[]{new ElementRule(Parameter.class)}),
            new ElementRule(MultivariateDistributionLikelihood.MVN_PRECISION,
                    new XMLSyntaxRule[]{new ElementRule(MatrixParameter.class)}),
    };
    public String getParserDescription() {
        return "Describes a normal distribution with a given mean and precision " +
                "that can be used in a distributionLikelihood element";
    }
    public Class getReturnType() {
        return MultivariateNormalDistributionModel.class;
    }
}
