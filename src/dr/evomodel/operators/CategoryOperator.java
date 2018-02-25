package dr.evomodel.operators;
import dr.evolution.alignment.Alignment;
import dr.evomodel.sitemodel.CategorySampleModel;
import dr.inference.model.Parameter;
import dr.inference.operators.OperatorFailedException;
import dr.inference.operators.SimpleMCMCOperator;
import dr.xml.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
public class CategoryOperator extends SimpleMCMCOperator {
public static final String CATEGORY_OPERATOR = "categoryOperator";
// dimension of categoryParameter should be set beforehand
public CategoryOperator(CategorySampleModel siteModel, int siteCount,
Parameter categoryParameter, double weight) {
this.categoryParameter = categoryParameter;
setWeight(weight);
this.siteModel = siteModel;
this.categoryCount = siteModel.getCategoryCount();
this.siteCount = siteCount;
}
public final double doOperation() throws OperatorFailedException {
int randomSite = (int) (Math.random() * siteCount);
int currentCategory = (int) categoryParameter.getParameterValue(randomSite);
siteModel.subtractSitesInCategoryCount(currentCategory);
int[] temp = new int[categoryCount - 1];
int count = 0;
for (int i = 0; i < categoryCount; i++) {
if (i != currentCategory) {
temp[count] = i;
count++;
}
}
int newCategory = temp[(int) (Math.random() * temp.length)];
categoryParameter.setParameterValue(randomSite, newCategory);
siteModel.addSitesInCategoryCount(newCategory);
return 0.0;
}
// Interface MCMCOperator
public final String getOperatorName() {
return CATEGORY_OPERATOR;
}
public Element createOperatorElement(Document d) {
throw new RuntimeException("Not implemented!");
}
public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {
public String getParserName() {
return CATEGORY_OPERATOR;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
Parameter catParam = (Parameter) xo.getChild(Parameter.class);
CategorySampleModel siteModel = (CategorySampleModel) xo.getChild(CategorySampleModel.class);
Alignment alignment = (Alignment) xo.getChild(Alignment.class);
double weight = xo.getDoubleAttribute(WEIGHT);
return new CategoryOperator(siteModel, alignment.getSiteCount(),
catParam, weight);
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public String getParserDescription() {
return "An operator on categories of sites.";
}
public Class getReturnType() {
return CategoryOperator.class;
}
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private XMLSyntaxRule[] rules = new XMLSyntaxRule[]{
AttributeRule.newDoubleRule("weight"),
new ElementRule(Parameter.class),
new ElementRule(CategorySampleModel.class),
new ElementRule(Alignment.class)
};
};
public String toString() {
return getOperatorName();
}
public String getPerformanceSuggestion() {
return "";
}
// Private instance variables
private Parameter categoryParameter;
private CategorySampleModel siteModel;
private int categoryCount;
private int siteCount;
}
