package dr.inferencexml.model;
import dr.inference.model.BooleanStatistic;
import dr.inference.model.NotBooleanStatistic;
import dr.xml.*;
public class NotBooleanStatisticParser extends AbstractXMLObjectParser {
    public static String NEGATE_STATISTIC = "notBooleanStatistic";
    public String getParserName() {
        return NEGATE_STATISTIC;
    }
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        BooleanStatistic booleanStatistic = (BooleanStatistic) xo.getChild(BooleanStatistic.class);
        return new NotBooleanStatistic(booleanStatistic);
    }
    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************
    public String getParserDescription() {
        return "This element returns a statistic that is the element-wise negation of the child boolean statistic.";
    }
    public Class getReturnType() {
        return BooleanStatistic.class;
    }
    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }
    private XMLSyntaxRule[] rules = new XMLSyntaxRule[]{
            new ElementRule(BooleanStatistic.class),
    };
}
