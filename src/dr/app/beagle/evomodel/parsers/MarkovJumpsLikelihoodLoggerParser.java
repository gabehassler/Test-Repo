package dr.app.beagle.evomodel.parsers;
import dr.app.beagle.evomodel.treelikelihood.MarkovJumpsTraitProvider;
import dr.inference.loggers.LogColumn;
import dr.inference.loggers.Loggable;
import dr.inference.loggers.NumberColumn;
import dr.xml.*;
public class MarkovJumpsLikelihoodLoggerParser extends AbstractXMLObjectParser {
    public static final String PARSER_NAME = "dataLikelihood";
    @Override
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        final MarkovJumpsTraitProvider dataLikelihood =
                (MarkovJumpsTraitProvider) xo.getChild(MarkovJumpsTraitProvider.class);
        return new Loggable() {
            public LogColumn[] getColumns() {
                return new LogColumn[]{
                        new LikelihoodColumn(dataLikelihood, "dataLike")
                };
            }
        };
    }
    protected class LikelihoodColumn extends NumberColumn {
        final private MarkovJumpsTraitProvider tree;
        public LikelihoodColumn(MarkovJumpsTraitProvider tree, String label) {
            super(label);
            this.tree = tree;
        }
        public double getDoubleValue() {
            return tree.getLogLikelihood();
        }
    }
    @Override
    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }
    private static XMLSyntaxRule[] rules = {
            new ElementRule(MarkovJumpsTraitProvider.class),
    };
    @Override
    public String getParserDescription() {
        return null;
    }
    @Override
    public Class getReturnType() {
        return Loggable.class;
    }
    public String getParserName() {
        return PARSER_NAME;
    }
}
