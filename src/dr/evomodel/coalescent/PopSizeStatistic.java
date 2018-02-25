package dr.evomodel.coalescent;
import dr.inference.model.Statistic;
import dr.inference.model.Variable;
import dr.xml.*;
public class PopSizeStatistic extends Statistic.Abstract {
public static final String POPSIZE_STATISTIC = "popSizeStatistic";
public DemographicModel model;
public double time;
public PopSizeStatistic(String name, DemographicModel model, double time) {
super(name);
this.model = model;
this.time = time;
}
public int getDimension() {
return 2 + model.getVariableCount();
}
public double getStatisticValue(int dim) {
if (dim == 0) return model.getDemographicFunction().getDemographic(time);
if (dim == 1) return model.getDemographicFunction().getIntensity(time);
return ((Variable<Double>)model.getVariable(dim - 2)).getValue(0);
}
public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {
public String getParserName() {
return POPSIZE_STATISTIC;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
String name = xo.getAttribute(NAME, xo.getId());
DemographicModel demo = (DemographicModel) xo.getChild(DemographicModel.class);
double time = xo.getDoubleAttribute("time");
return new PopSizeStatistic(name, demo, time);
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public String getParserDescription() {
return "A statistic that has as its value the height of the most recent common ancestor of a set of taxa in a given tree";
}
public Class getReturnType() {
return PopSizeStatistic.class;
}
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private XMLSyntaxRule[] rules = new XMLSyntaxRule[]{
new ElementRule(DemographicModel.class),
new StringAttributeRule("name", "A name for this statistic primarily for the purposes of logging", true),
AttributeRule.newDoubleRule("time", false),
};
};
}
