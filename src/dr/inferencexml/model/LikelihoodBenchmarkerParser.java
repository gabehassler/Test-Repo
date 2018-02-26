
package dr.inferencexml.model;

import dr.inference.model.Likelihood;
import dr.inference.model.LikelihoodBenchmarker;
import dr.xml.*;

import java.util.ArrayList;
import java.util.List;

public class LikelihoodBenchmarkerParser extends AbstractXMLObjectParser {

    public static final String BENCHMARKER = "benchmarker";

    public String getParserName() {
        return BENCHMARKER;
    }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        int iterationCount = 1000;

        if (xo.hasAttribute("iterationCount")) {
            iterationCount = xo.getIntegerAttribute("iterationCount");
        }

        List<Likelihood> likelihoods = new ArrayList<Likelihood>();

        for (int i = 0; i < xo.getChildCount(); i++) {
            Object xco = xo.getChild(i);
            if (xco instanceof Likelihood) {
                likelihoods.add((Likelihood) xco);
            }
        }

        if (likelihoods.size() == 0) {
            throw new XMLParseException("No likelihoods for benchmarking");
        }

        return new LikelihoodBenchmarker(likelihoods, iterationCount);
    }

    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************

    public String getParserDescription() {
        return "This element runs a benchmark on a series of likelihood calculators.";
    }

    public Class getReturnType() {
        return Likelihood.class;
    }

    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private final XMLSyntaxRule[] rules = {
            AttributeRule.newIntegerRule("iterationCount"),
            new ElementRule(Likelihood.class, 1, Integer.MAX_VALUE)
    };

}
