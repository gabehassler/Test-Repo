
package dr.app.beagle.evomodel.parsers;

import dr.app.beagle.evomodel.utilities.HistoryFilter;
import dr.util.Identifiable;
import dr.xml.*;

public class HistoryFilterParser extends AbstractXMLObjectParser {

    public static final String NAME = "historyFilter";
    public static final String MAX_TIME = "maxTime";
    public static final String MIN_TIME = "minTime";
    public static final String SOURCES = "sources";
    public static final String DESTINATIONS = "destinations";
    public static final String INCLUDE_ALL = "includeAll";

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        double maxTime = xo.getAttribute(MAX_TIME, Double.POSITIVE_INFINITY);
        double minTime = xo.getAttribute(MIN_TIME, 0.0);

        return new HistoryFilter.SetFilter(null, null, maxTime, minTime);
    }

    public String getParserName() {
        return NAME;
    }

    public String getParserDescription() {
        return "A logger to filter transitions in the complete history.";
    }

    public Class getReturnType() {
        return HistoryFilter.class;
    }

    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private final XMLSyntaxRule[] rules = {
            AttributeRule.newDoubleRule(MAX_TIME, true),
            AttributeRule.newDoubleRule(MIN_TIME, true),
            new ElementRule(SOURCES,
                    new XMLSyntaxRule[]{
                            new XORRule(
                                    AttributeRule.newBooleanRule(INCLUDE_ALL, true),
                                    new ElementRule(Identifiable.class, 1, Integer.MAX_VALUE) // TODO Fix type
                            ),
                    }
                    , true),
            new ElementRule(DESTINATIONS,
                    new XMLSyntaxRule[]{
                            new XORRule(
                                    AttributeRule.newBooleanRule(INCLUDE_ALL, true),
                                    new ElementRule(Identifiable.class, 1, Integer.MAX_VALUE) // TODO Fix type
                            ),
                    }
                    , true),
    };
}
