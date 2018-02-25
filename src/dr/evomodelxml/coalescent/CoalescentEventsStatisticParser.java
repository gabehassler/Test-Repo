package dr.evomodelxml.coalescent;
import dr.evomodel.coalescent.CoalescentEventsStatistic;
import dr.evomodel.coalescent.CoalescentIntervalProvider;
import dr.xml.*;
public class CoalescentEventsStatisticParser extends AbstractXMLObjectParser {
public static final String COALESCENT_EVENTS_STATISTIC = "coalescentEventsStatistic";
public static final boolean DEBUG = false;
public String getParserDescription() {
return "";
}
public Class getReturnType() {
return CoalescentEventsStatistic.class;
}
public XMLSyntaxRule[] getSyntaxRules() {
return new XMLSyntaxRule[]{
new ElementRule(CoalescentIntervalProvider.class)//,
//new ElementRule(TreeModel.class)
};
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
if (DEBUG) {
System.err.println("Parsing coalescentEventsStatistic");
}
CoalescentIntervalProvider coalescent = (CoalescentIntervalProvider) xo.getChild(CoalescentIntervalProvider.class);
//TreeModel treeModel = (TreeModel) xo.getChild(TreeModel.class);
//return new CoalescentEventsStatistic(coalescent, treeModel);
return new CoalescentEventsStatistic(coalescent);
}
public String getParserName() {
return COALESCENT_EVENTS_STATISTIC;
}
}
