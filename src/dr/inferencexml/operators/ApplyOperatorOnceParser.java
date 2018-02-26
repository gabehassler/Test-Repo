
package dr.inferencexml.operators;

import dr.inference.operators.OperatorFailedException;
import dr.inference.operators.SimpleMCMCOperator;
import dr.xml.*;

public class ApplyOperatorOnceParser extends AbstractXMLObjectParser {

    public static final String APPLY = "applyOperatorOnce";

    @Override
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        for (int i = 0; i < xo.getChildCount(); ++i) {
            SimpleMCMCOperator operator = (SimpleMCMCOperator) xo.getChild(i);
            try {
                operator.doOperation();
            } catch (OperatorFailedException e) {
                // Do nothing
            }
        }
        return null;
    }

    @Override
    public XMLSyntaxRule[] getSyntaxRules() {
        return new XMLSyntaxRule[]{
                new ElementRule(SimpleMCMCOperator.class, 1, Integer.MAX_VALUE),
        };
    }

    @Override
    public String getParserDescription() {
        return "Applies a series of operators once before continuing";
    }

    @Override
    public Class getReturnType() {
        return ApplyOperatorOnceParser.class;
    }

    public String getParserName() {
        return APPLY;
    }
}
