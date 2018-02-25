package dr.app.beagle.evomodel.branchmodel.lineagespecific;
import dr.inference.model.CompoundParameter;
import dr.inference.model.Parameter;
import dr.xml.AbstractXMLObjectParser;
import dr.xml.ElementRule;
import dr.xml.XMLObject;
import dr.xml.XMLParseException;
import dr.xml.XMLSyntaxRule;
public class CountableRealizationsParameterParser extends
AbstractXMLObjectParser {
public static final String COUNTABLE_REALIZATIONS_PARAMETER = "countableRealizationsParameter";
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
Parameter categoriesParameter = (Parameter) xo .getElementFirstChild(DirichletProcessPriorParser.CATEGORIES);
CompoundParameter realizedParameters = (CompoundParameter) xo .getChild(CompoundParameter.class);
return new CountableRealizationsParameter(categoriesParameter,
realizedParameters);
}
public XMLSyntaxRule[] getSyntaxRules() {
return new XMLSyntaxRule[] {
new ElementRule(DirichletProcessPriorParser.CATEGORIES,
new XMLSyntaxRule[] { new ElementRule(Parameter.class,
false) }), // categories assignments
new ElementRule(CompoundParameter.class, false) // realized parameters
};
}
public String getParserDescription() {
return COUNTABLE_REALIZATIONS_PARAMETER;
}
public Class getReturnType() {
return Parameter.class;
}
public String getParserName() {
return COUNTABLE_REALIZATIONS_PARAMETER;
}
}
