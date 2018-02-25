package dr.inference.model;
import dr.xml.*;
public class StatisticParser extends dr.xml.AbstractXMLObjectParser {
public final static String STATISTIC = "statistic";
public String getParserName() { return STATISTIC; }
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
final StatisticList statList = (StatisticList)xo.getChild(StatisticList.class);
final String name = xo.getStringAttribute("name");
final Statistic stat = statList.getStatistic(name);
if (stat == null) {
StringBuffer buffer = new StringBuffer("Unknown statistic name, " + name + "\n");
buffer.append("Valid statistics are:");
for (int i = 0; i < statList.getStatisticCount(); i++) {
buffer.append("\n  ").append(statList.getStatistic(i).getStatisticName());
}
throw new XMLParseException(buffer.toString());
}
return stat;	
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public String getParserDescription() {
return "A statistic of a given name from the specified object.  ";
}
public Class getReturnType() { return Statistic.class; }
public XMLSyntaxRule[] getSyntaxRules() { return rules; }
private final XMLSyntaxRule[] rules = {
new StringAttributeRule("name", "The name of the statistic you wish to extract from the given object"),
new ElementRule(StatisticList.class)
};
}
