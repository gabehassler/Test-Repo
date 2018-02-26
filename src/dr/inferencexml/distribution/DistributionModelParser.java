package dr.inferencexml.distribution;
import dr.inference.distribution.ParametricDistributionModel;
import dr.inference.model.Parameter;
import dr.xml.*;
public abstract class DistributionModelParser extends AbstractXMLObjectParser {
    public static final String OFFSET = "offset";
    public static final String MEAN = "mean";
    public static final String SHAPE = "shape";
    public static final String SCALE = "scale";
    abstract ParametricDistributionModel parseDistributionModel(Parameter[] parameters, double offset);
    abstract String[] getParameterNames();
    abstract boolean allowOffset();
    public final Object parseXMLObject(XMLObject xo) throws XMLParseException {
        double offset = xo.getAttribute(OFFSET, 0.0);
        String[] names = getParameterNames();
        Parameter[] parameters = new Parameter[names.length];
        for (int i = 0; i < names.length; i++) {
            parameters[i] = getParameter(xo, names[i]);
        }
        return parseDistributionModel(parameters, offset);
    }
    private Parameter getParameter(XMLObject xo, String parameterName) throws XMLParseException {
        final XMLObject cxo = xo.getChild(parameterName);
        return cxo.getChild(0) instanceof Parameter ?
                (Parameter) cxo.getChild(Parameter.class) : new Parameter.Default(cxo.getDoubleChild(0));
    }
    public XMLSyntaxRule[] getSyntaxRules() {
        String[] names = getParameterNames();
        XMLSyntaxRule[] rules = new XMLSyntaxRule[names.length + (allowOffset() ? 1 : 0)];
        for (int i = 0; i < names.length; i++) {
            rules[i] = new XORRule(
                    new ElementRule(names[i], Double.class),
                    new ElementRule(names[i], Parameter.class)
            );
        }
        if (allowOffset()) {
            rules[rules.length - 1] = AttributeRule.newDoubleRule(OFFSET, true);
        }
        return rules;
    }
}
