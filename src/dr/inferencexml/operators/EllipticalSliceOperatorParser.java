
package dr.inferencexml.operators;

import dr.inference.distribution.MultivariateDistributionLikelihood;
import dr.inference.distribution.MultivariateNormalDistributionModel;
import dr.inference.model.CompoundParameter;
import dr.inference.model.Parameter;
import dr.inference.operators.EllipticalSliceOperator;
import dr.inference.operators.MCMCOperator;
import dr.math.distributions.GaussianProcessRandomGenerator;
import dr.math.distributions.MultivariateNormalDistribution;
import dr.xml.*;

public class EllipticalSliceOperatorParser extends AbstractXMLObjectParser {

    public static final String ELLIPTICAL_SLICE_SAMPLER = "ellipticalSliceSampler";
    public static final String SIGNAL_CONSTITUENT_PARAMETERS = "signalConstituentParameters";
    public static final String DRAW_BY_ROW = "drawByRow";  // TODO What is this?

    public String getParserName() {
        return ELLIPTICAL_SLICE_SAMPLER;
    }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        final double weight = xo.getDoubleAttribute(MCMCOperator.WEIGHT);
        final Parameter variable = (Parameter) xo.getChild(Parameter.class);
        boolean drawByRowTemp=false;
        if(xo.hasAttribute(DRAW_BY_ROW))
            drawByRowTemp=xo.getBooleanAttribute(DRAW_BY_ROW);
        final boolean drawByRow=drawByRowTemp;

        boolean signal = xo.getAttribute(SIGNAL_CONSTITUENT_PARAMETERS, true);
        if (!signal && !(variable instanceof CompoundParameter)) signal = true;

        GaussianProcessRandomGenerator gaussianProcess = (GaussianProcessRandomGenerator)
                xo.getChild(GaussianProcessRandomGenerator.class);

        if (gaussianProcess == null) {

            final MultivariateDistributionLikelihood likelihood =
                    (MultivariateDistributionLikelihood) xo.getChild(MultivariateDistributionLikelihood.class);

            if (!(likelihood.getDistribution() instanceof GaussianProcessRandomGenerator)) {
                throw new XMLParseException("Elliptical slice sampling only works for multivariate normally distributed random variables");
            }


            if(likelihood.getDistribution() instanceof MultivariateNormalDistribution)
                gaussianProcess = (MultivariateNormalDistribution) likelihood.getDistribution();

            if(likelihood.getDistribution() instanceof MultivariateNormalDistributionModel)
                gaussianProcess = (MultivariateNormalDistributionModel) likelihood.getDistribution();

        }
        EllipticalSliceOperator operator = new EllipticalSliceOperator(variable, gaussianProcess, drawByRow, signal);
        operator.setWeight(weight);
        return operator;
    }

    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************

    public String getParserDescription() {
        return "An elliptical slice sampler for parameters with Gaussian priors.";
    }

    public Class getReturnType() {
        return EllipticalSliceOperator.class;
    }

    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private final XMLSyntaxRule[] rules = {
            AttributeRule.newDoubleRule(MCMCOperator.WEIGHT),
            AttributeRule.newBooleanRule(SIGNAL_CONSTITUENT_PARAMETERS, true),
            new ElementRule(Parameter.class),
            new XORRule(
                    new ElementRule(GaussianProcessRandomGenerator.class),
                    new ElementRule(MultivariateDistributionLikelihood.class)
            ),
            AttributeRule.newBooleanRule(DRAW_BY_ROW, true),
//            new ElementRule(MultivariateNormalDistribution.class),
    };
}
