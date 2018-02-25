package dr.inferencexml.model;
import dr.inference.model.Parameter;
import dr.inference.model.ProductParameter;
import dr.xml.*;
import java.util.ArrayList;
import java.util.List;
public class ProductParameterParser extends AbstractXMLObjectParser {
public static final String PRODUCT_PARAMETER = "productParameter";
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
List<Parameter> paramList = new ArrayList<Parameter>();
int dim = -1;
for (int i = 0; i < xo.getChildCount(); ++i) {
Parameter parameter = (Parameter) xo.getChild(i);
if (dim == -1) {
dim = parameter.getDimension();
} else {
if (parameter.getDimension() != dim) {
throw new XMLParseException("All parameters in product '" + xo.getId() + "' must be the same length");
}
}
paramList.add(parameter);
}
return new ProductParameter(paramList);
}
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private final XMLSyntaxRule[] rules = {
new ElementRule(Parameter.class,1,Integer.MAX_VALUE),
};
public String getParserDescription() {
return "A element-wise product of parameters.";
}
public Class getReturnType() {
return Parameter.class;
}
public String getParserName() {
return PRODUCT_PARAMETER;
}
}
