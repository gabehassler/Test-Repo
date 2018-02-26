
package dr.app.beagle.evomodel.parsers;

import dr.app.beagle.evomodel.treelikelihood.MarkovJumpsTraitProvider;
import dr.app.beagle.evomodel.utilities.CompleteHistoryLogger;
import dr.app.beagle.evomodel.utilities.HistoryFilter;
import dr.inference.loggers.Logger;
import dr.xml.*;

public class CompleteHistoryLoggerParser extends AbstractXMLObjectParser {

    public static final String NAME = "completeHistoryLogger";

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        MarkovJumpsTraitProvider treeLikelihood =
                (MarkovJumpsTraitProvider) xo.getChild(MarkovJumpsTraitProvider.class);

        HistoryFilter filter = (HistoryFilter) xo.getChild(HistoryFilter.class);

        return new CompleteHistoryLogger(treeLikelihood, filter);
    }

    public String getParserName() {
        return NAME;
    }

    public String getParserDescription() {
        return "A logger to record all transitions in the complete history.";
    }

    public Class getReturnType() {
        return Logger.class;
    }

    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private final XMLSyntaxRule[] rules = {
            new ElementRule(MarkovJumpsTraitProvider.class),
            new ElementRule(HistoryFilter.class, true),
    };
}
