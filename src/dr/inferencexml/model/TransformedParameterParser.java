package dr.inferencexml.model;
import dr.inference.model.Parameter;
import dr.inference.model.TransformedParameter;
import dr.util.Transform;
import dr.xml.*;
public class TransformedParameterParser extends AbstractXMLObjectParser {
    public static final String TRANSFORMED_PARAMETER = "transformedParameter";
    public static final String INVERSE = "inverse";
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        final Parameter parameter = (Parameter) xo.getChild(Parameter.class);
        final Transform.ParsedTransform parsedTransform = (Transform.ParsedTransform) xo.getChild(Transform.ParsedTransform.class);
        final boolean inverse = xo.getAttribute(INVERSE, false);
        TransformedParameter transformedParameter = new TransformedParameter(parameter, parsedTransform.transform, inverse);
        return transformedParameter;
    }
    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }
    private final XMLSyntaxRule[] rules = {
            new ElementRule(Parameter.class),
            new ElementRule(Transform.ParsedTransform.class),
            AttributeRule.newBooleanRule(INVERSE, true),
    };
    public String getParserDescription() {
        return "A transformed parameter.";
    }
    public Class getReturnType() {
        return TransformedParameter.class;
    }
    public String getParserName() {
        return TRANSFORMED_PARAMETER;
    }
}
