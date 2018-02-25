package dr.inference.model;
import dr.xml.*;
public class ConstantLikelihood extends Likelihood.Abstract {
public static final String CONSTANT_LIKELIHOOD = "constantLikelihood";
public static final String LOG_VALUE = "logValue";
public ConstantLikelihood(double logValue) {
super(null);
this.logValue = logValue;
}
protected boolean getLikelihoodKnown() {
return false;
}
public double calculateLogLikelihood() {
return logValue;
}
public boolean evaluateEarly() {
return true;
}
public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {
public static final String DATA = "data";
public String getParserName() {
return CONSTANT_LIKELIHOOD;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
double logValue = xo.getAttribute(LOG_VALUE, 0.0);
return new ConstantLikelihood(logValue);
}
public String getParserDescription() {
return "A function that returns a constant value as a likelihood.";
}
public Class getReturnType() {
return ConstantLikelihood.class;
}
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private final XMLSyntaxRule[] rules = {
AttributeRule.newDoubleRule(LOG_VALUE),
};
};
private final double logValue;
}
