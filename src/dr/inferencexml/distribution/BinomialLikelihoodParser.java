
package dr.inferencexml.distribution;

import dr.inference.distribution.BinomialLikelihood;
import dr.inference.model.Likelihood;
import dr.inference.model.Parameter;
import dr.xml.*;

public class BinomialLikelihoodParser extends AbstractXMLObjectParser {

    public static final String TRIALS = "trials";
    public static final String COUNTS = "counts";
    public static final String PROPORTION = "proportion";
    public static final String VALUES = "values";

    public String getParserName() {
        return BinomialLikelihood.BINOMIAL_LIKELIHOOD;
    }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        XMLObject cxo = xo.getChild(TRIALS);
        Parameter trialsParam = (Parameter) cxo.getChild(Parameter.class);

        cxo = xo.getChild(PROPORTION);
        Parameter proportionParam = (Parameter) cxo.getChild(Parameter.class);

        cxo = xo.getChild(COUNTS);
        Parameter counts = null;
        if (cxo.hasAttribute(VALUES)) {
            int[] tmp = cxo.getIntegerArrayAttribute(VALUES);
            double[] v = new double[tmp.length];
            for (int i = 0; i < tmp.length; ++i) {
                v[i] = tmp[i];
            }
            counts = new Parameter.Default(v);
        } else {
            counts = (Parameter) cxo.getChild(Parameter.class);
        }

        if (trialsParam.getDimension() != counts.getDimension()) {
            throw new XMLParseException("Trials dimension (" + trialsParam.getDimension()
                    + ") must equal counts dimension (" + counts.getDimension() + ")");
        }

        return new BinomialLikelihood(trialsParam, proportionParam, counts);

    }

    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************

    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private final XMLSyntaxRule[] rules = {
            new ElementRule(TRIALS,
                    new XMLSyntaxRule[]{new ElementRule(Parameter.class)}),
            new ElementRule(PROPORTION,
                    new XMLSyntaxRule[]{new ElementRule(Parameter.class)}),
            new XORRule(
                    new ElementRule(COUNTS,
                            new XMLSyntaxRule[]{AttributeRule.newIntegerArrayRule(VALUES, false),})
                    ,
                    new ElementRule(COUNTS,
                            new XMLSyntaxRule[]{new ElementRule(Parameter.class)}
                    )),
    };

    public String getParserDescription() {
        return "Calculates the likelihood of some data given some parametric or empirical distribution.";
    }

    public Class getReturnType() {
        return Likelihood.class;
    }
}
