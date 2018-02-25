package dr.inference.operators;
import dr.inference.distribution.LogNormalDistributionModel;
import dr.inferencexml.operators.ScaleOperatorParser;
import dr.xml.*;
public class LNVarianceOperator extends ScaleOperator {
public static final String LN_VARIANCE_OPERATOR = "LNVarianceOperator";
private LogNormalDistributionModel lnd;
public LNVarianceOperator(LogNormalDistributionModel lnd, double scaleFactor, double weight, CoercionMode mode) {
//  super(lnd.getSParameter(), false, scaleFactor, weight, mode, null, 0.0);
super(lnd.getSParameter(), false, 0, scaleFactor, mode, null, 0, false);
this.lnd = lnd;
setWeight(weight);
}
final void cleanupOperation(double newS, double oldS) {
double newM = lnd.getM() + (oldS * oldS / 2.0) - (newS * newS / 2.0);
lnd.setM(newM);
}
public static dr.xml.XMLObjectParser PARSER = new dr.xml.AbstractXMLObjectParser() {
public String getParserName() {
return LN_VARIANCE_OPERATOR;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
CoercionMode mode = CoercionMode.parseMode(xo);
double weight = xo.getDoubleAttribute(WEIGHT);
double scaleFactor = xo.getDoubleAttribute(ScaleOperatorParser.SCALE_FACTOR);
if (scaleFactor <= 0.0 || scaleFactor >= 1.0) {
throw new XMLParseException("scaleFactor must be between 0.0 and 1.0");
}
LogNormalDistributionModel lnd = (LogNormalDistributionModel) xo.getChild(LogNormalDistributionModel.class);
return new LNVarianceOperator(lnd, scaleFactor, weight, mode);
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public String getParserDescription() {
return "This element returns a scale operator on a given parameter.";
}
public Class getReturnType() {
return MCMCOperator.class;
}
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private XMLSyntaxRule[] rules = new XMLSyntaxRule[]{
AttributeRule.newDoubleRule(ScaleOperatorParser.SCALE_FACTOR),
AttributeRule.newDoubleRule(WEIGHT),
AttributeRule.newBooleanRule(AUTO_OPTIMIZE, true),
new ElementRule(LogNormalDistributionModel.class)
};
};
public String toString() {
return "LNVarianceOperator(" + lnd.getSParameter().getParameterName() + " [" + getScaleFactor() + ", " + (1.0 / getScaleFactor()) + "]";
}
}
