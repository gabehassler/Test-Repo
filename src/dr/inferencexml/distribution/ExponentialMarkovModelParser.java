package dr.inferencexml.distribution;
import dr.inference.distribution.ExponentialMarkovModel;
import dr.inference.model.Parameter;
import dr.xml.*;
public class ExponentialMarkovModelParser extends AbstractXMLObjectParser {
    public static final String CHAIN_PARAMETER = "chainParameter";
    public static final String JEFFREYS = "jeffreys";
    public static final String REVERSE = "reverse";
    public static final String SHAPE = "shape";
    public String getParserName() {
        return ExponentialMarkovModel.EXPONENTIAL_MARKOV_MODEL;
    }
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        XMLObject object = xo.getChild(CHAIN_PARAMETER);
        Parameter chainParameter = (Parameter) object.getChild(0);
        boolean jeffreys = xo.getAttribute(JEFFREYS, false);
        boolean reverse = xo.getAttribute(REVERSE, false);
        double shape = xo.getAttribute(SHAPE, 1.0);
        if (shape < 1.0) {
            throw new XMLParseException("ExponentialMarkovModel: shape parameter must be >= 1.0");
        }
        if (shape == 1.0) {
            System.out.println("Exponential markov model on parameter " +
                    chainParameter.getParameterName() + " (jeffreys=" + jeffreys + ", reverse=" +
                    reverse + ")");
        } else {
            System.out.println("Gamma markov model on parameter " +
                    chainParameter.getParameterName() + " (jeffreys=" + jeffreys + ", reverse=" +
                    reverse + " shape=" + shape + ")");
        }
        return new ExponentialMarkovModel(chainParameter, jeffreys, reverse, shape);
    }
    public String getParserDescription() {
        return "A continuous state, discrete time markov chain in which each new state is an " +
                "exponentially distributed variable with a mean of the previous state.";
    }
    public Class getReturnType() {
        return ExponentialMarkovModel.class;
    }
    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }
    private final XMLSyntaxRule[] rules = {
            AttributeRule.newBooleanRule(JEFFREYS, true),
            AttributeRule.newBooleanRule(REVERSE, true),
            new ElementRule(CHAIN_PARAMETER, Parameter.class)
    };
}