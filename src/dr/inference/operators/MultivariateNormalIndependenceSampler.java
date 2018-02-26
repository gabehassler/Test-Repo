
package dr.inference.operators;

import dr.inference.model.Parameter;
import dr.inference.regression.SelfControlledCaseSeries;
import dr.math.MathUtils;
import dr.math.Poisson;
import dr.math.distributions.NormalDistribution;
import dr.xml.*;

import java.util.HashSet;
import java.util.Set;

public class MultivariateNormalIndependenceSampler extends AbstractCoercableOperator {

    public static final String OPERATOR_NAME = "multivariateNormalIndependenceSampler";
    public static final String SCALE_FACTOR = "scaleFactor";
    public static final String SET_SIZE_MEAN = "setSizeMean";

    private double scaleFactor;
    private final Parameter parameter;
    private final int dim;
    private double setSizeMean;
    private final SelfControlledCaseSeries sccs;

    public MultivariateNormalIndependenceSampler(Parameter parameter,
                                                 SelfControlledCaseSeries sccs,
                                                 double setSizeMean,
                                                 double weight, double scaleFactor, CoercionMode mode) {
        super(mode);
        this.scaleFactor = scaleFactor;
        this.parameter = parameter;
        setWeight(weight);
        dim = parameter.getDimension();
        setWeight(weight);
        this.sccs = sccs;
        this.setSizeMean = setSizeMean;
    }

    public String getPerformanceSuggestion() {
        return "";
    }

    public String getOperatorName() {
        return "independentNormalDistribution(" + parameter.getVariableName() + ")";
    }

    public double doOperation() throws OperatorFailedException {

        double[] mean = sccs.getMode();
        double[] currentValue = parameter.getParameterValues();
        double[] newValue = new double[dim];

        Set<Integer> updateSet = new HashSet<Integer>();

        if (setSizeMean != -1.0) {
            final int listLength = Poisson.nextPoisson(setSizeMean);
            while (updateSet.size() <  listLength) {
                int newInt = MathUtils.nextInt(parameter.getDimension());
                if (!updateSet.contains(newInt)) {
                    updateSet.add(newInt);
                }
            }
        } else {
            for (int i = 0; i < dim; ++i) {
                updateSet.add(i);
            }
        }

        double logq = 0;
        for (Integer i : updateSet) {
            newValue[i] = mean[i] + scaleFactor * MathUtils.nextGaussian();
            if (UPDATE_ALL) {
                parameter.setParameterValueQuietly(i, newValue[i]);
            } else {
                parameter.setParameterValue(i, newValue[i]);
            }

            logq += (NormalDistribution.logPdf(currentValue[i], mean[i], scaleFactor) -
                    NormalDistribution.logPdf(newValue[i], mean[i], scaleFactor));
        }

//        for (Integer i : updateSet) {
//            parameter.setParameterValueQuietly(i, newValue[i]);
//        }

        if (UPDATE_ALL) {
            parameter.setParameterValueNotifyChangedAll(0, parameter.getParameterValue(0));
        }

        return logq;
    }

    private static final boolean UPDATE_ALL = false;

    public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {

        public String getParserName() {
            return OPERATOR_NAME;
        }

        public Object parseXMLObject(XMLObject xo) throws XMLParseException {

            CoercionMode mode = CoercionMode.parseMode(xo);

            double weight = xo.getDoubleAttribute(WEIGHT);
            double scaleFactor = xo.getDoubleAttribute(SCALE_FACTOR);

            if (scaleFactor <= 0.0) {
                throw new XMLParseException("scaleFactor must be greater than 0.0");
            }

            Parameter parameter = (Parameter) xo.getChild(Parameter.class);

            SelfControlledCaseSeries sccs = (SelfControlledCaseSeries) xo.getChild(SelfControlledCaseSeries.class);

            double setSizeMean = xo.getAttribute(SET_SIZE_MEAN, -1.0);

            return new MultivariateNormalIndependenceSampler(parameter, sccs, setSizeMean, weight, scaleFactor, mode);
        }

        //************************************************************************
        // AbstractXMLObjectParser implementation
        //************************************************************************

        public XMLSyntaxRule[] getSyntaxRules() {
            return rules;
        }

        private final XMLSyntaxRule[] rules = {
                AttributeRule.newDoubleRule(SCALE_FACTOR),
                AttributeRule.newDoubleRule(WEIGHT),
                AttributeRule.newBooleanRule(AUTO_OPTIMIZE, true),
                AttributeRule.newDoubleRule(SET_SIZE_MEAN, true),
                new ElementRule(SelfControlledCaseSeries.class),
                new ElementRule(Parameter.class),
        };

        public String getParserDescription() {
            return "This element returns an independence sampler from a provided normal distribution model.";
        }

        public Class getReturnType() {
            return MultivariateNormalIndependenceSampler.class;
        }

    };

    public double getCoercableParameter() {
        return Math.log(scaleFactor);
    }

    public void setCoercableParameter(double value) {
        scaleFactor = Math.exp(value);
    }

    public double getRawParameter() {
        return scaleFactor;
    }

}
