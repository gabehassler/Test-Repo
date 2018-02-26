
package dr.app.beagle.evomodel.parsers;

import dr.app.beagle.evomodel.operators.PatternWeightIncrementOperator;
import dr.app.beagle.evomodel.treelikelihood.BeagleTreeLikelihood;
import dr.xml.*;

public class PatternWeightIncrementOperatorParser extends AbstractXMLObjectParser {

    public String getParserName() {
        return PatternWeightIncrementOperator.PATTERN_WEIGHT_INCREMENT_OPERATOR;
    }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

       BeagleTreeLikelihood treeLikelihood = (BeagleTreeLikelihood) xo.getChild(BeagleTreeLikelihood.class);
        final double weight = xo.getDoubleAttribute("weight");
        return new PatternWeightIncrementOperator(treeLikelihood, weight);
    }

    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************

    public String getParserDescription() {
        return "This element progressively adds sites into the pattern list.";
    }

    public Class getReturnType() {
        return PatternWeightIncrementOperator.class;
    }

    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private final XMLSyntaxRule[] rules = {
            AttributeRule.newDoubleRule("weight"),
            new ElementRule(BeagleTreeLikelihood.class),
    };
}
