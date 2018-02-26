package dr.evomodelxml.coalescent;
import dr.evolution.tree.Tree;
import dr.evomodel.coalescent.CoalescentTreeIntervalStatistic;
import dr.xml.AbstractXMLObjectParser;
import dr.xml.ElementRule;
import dr.xml.XMLObject;
import dr.xml.XMLParseException;
import dr.xml.XMLSyntaxRule;
public class CoalescentTreeIntervalStatisticParser extends AbstractXMLObjectParser {
    public static final String COALESCENT_TREE_INTERVAL_STATISTIC = "coalescentTreeIntervalStatistic";
    public String getParserDescription() {
        return "";
    }
    public Class getReturnType() {
        return CoalescentTreeIntervalStatistic.class;
    }
    public XMLSyntaxRule[] getSyntaxRules() {
        return new XMLSyntaxRule[]{
                new ElementRule(Tree.class),
        };
    }
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
    	Tree tree = (Tree) xo.getChild(Tree.class);
        return new CoalescentTreeIntervalStatistic(tree);
    }
    public String getParserName() {
        return COALESCENT_TREE_INTERVAL_STATISTIC;
    }
}
