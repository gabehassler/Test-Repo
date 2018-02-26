
package dr.evomodelxml.coalescent;

import dr.evomodel.coalescent.CoalescentIntervalProvider;
import dr.evomodel.coalescent.CoalescentIntervalStatistic;
import dr.xml.*;


public class CoalescentIntervalStatisticParser extends AbstractXMLObjectParser {

    public static final String COALESCENT_INTERVAL_STATISTIC = "coalescentIntervalStatistic";

    public String getParserDescription() {
        return "";
    }

    public Class getReturnType() {
        return CoalescentIntervalStatistic.class;
    }

    public XMLSyntaxRule[] getSyntaxRules() {
        return new XMLSyntaxRule[]{
                new ElementRule(CoalescentIntervalProvider.class),
        };
    }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        CoalescentIntervalProvider coalescent = (CoalescentIntervalProvider) xo.getChild(CoalescentIntervalProvider.class);
        return new CoalescentIntervalStatistic(coalescent);
    }

    public String getParserName() {
        return COALESCENT_INTERVAL_STATISTIC;
    }

}
