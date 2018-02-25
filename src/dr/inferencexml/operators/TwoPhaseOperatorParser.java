package dr.inferencexml.operators;
import java.util.ArrayList;
import java.util.List;
import dr.inference.model.Parameter;
import dr.inference.operators.AbstractCoercableOperator;
import dr.inference.operators.AdaptableVarianceMultivariateNormalOperator;
import dr.inference.operators.CoercableMCMCOperator;
import dr.inference.operators.CoercionMode;
import dr.inference.operators.TwoPhaseOperator;
import dr.xml.AbstractXMLObjectParser;
import dr.xml.AttributeRule;
import dr.xml.ElementRule;
import dr.xml.XMLObject;
import dr.xml.XMLParseException;
import dr.xml.XMLSyntaxRule;
public class TwoPhaseOperatorParser extends AbstractXMLObjectParser {
public static final boolean DEBUG = false;
public static final String TWO_PHASE_OPERATOR = "twoPhaseOperator";
public static final String WEIGHT = "weight";
public static final String INITIAL = "initial";
public static final String BURNIN = "burnin";
public static final String PHASE_ONE = "phaseOne";
public static final String PHASE_TWO = "phaseTwo";
public String getParserName() {
return TWO_PHASE_OPERATOR;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
if (DEBUG) {
System.err.println("\nParsing TwoPhaseOperator");
}
final CoercionMode mode = CoercionMode.parseMode(xo);
final double weight = xo.getDoubleAttribute(WEIGHT);
final int initial = xo.getIntegerAttribute(INITIAL);
final int burnin = xo.getIntegerAttribute(BURNIN);
if (scaleFactor <= 0.0 || scaleFactor >= 1.0) {
throw new XMLParseException("scaleFactor must be between 0.0 and 1.0");
}*/
//final Parameter parameter = (Parameter) xo.getChild(Parameter.class);
if (DEBUG) {
System.err.println("child count: " + xo.getChildCount());
System.err.println(xo.getChild(PHASE_ONE));
System.err.println(xo.getChild(PHASE_ONE).getChildCount());
System.err.println(xo.getChild(PHASE_TWO));
System.err.println(xo.getChild(PHASE_TWO).getChildCount());
}
List<AbstractCoercableOperator> phaseOneOperators = new ArrayList<AbstractCoercableOperator>();
int phaseOneCount = xo.getChild(PHASE_ONE).getChildCount();
for (int i = 0; i < phaseOneCount; i++) {
phaseOneOperators.add((AbstractCoercableOperator)xo.getChild(PHASE_ONE).getChild(i));
}
if (DEBUG) {
System.err.println("arrayList one size: " + phaseOneOperators.size());
for (int i = 0; i < phaseOneOperators.size(); i++) {
System.err.println("  " + phaseOneOperators.get(i));
}
}
int phaseTwoCount = xo.getChild(PHASE_TWO).getChildCount();
for (int i = 0; i < phaseTwoCount; i++) {
phaseTwoOperators.add((AbstractCoercableOperator)xo.getChild(PHASE_TWO).getChild(i));
}*/
List<AdaptableVarianceMultivariateNormalOperator> phaseTwoOperators = new ArrayList<AdaptableVarianceMultivariateNormalOperator>();
int phaseTwoCount = xo.getChild(PHASE_TWO).getChildCount();
for (int i = 0; i < phaseTwoCount; i++) {
phaseTwoOperators.add((AdaptableVarianceMultivariateNormalOperator)xo.getChild(PHASE_TWO).getChild(i));
}
if (DEBUG) {
System.err.println("arrayList two size: " + phaseTwoOperators.size());
for (int i = 0; i < phaseTwoOperators.size(); i++) {
System.err.println("  " + phaseTwoOperators.get(i));
}
}
//keep track of the parameters of phase one here, as apparently we can't get to them afterwards
//let's just get them from phase two, as there I can implement whatever I want
List<Parameter> parameters = new ArrayList<Parameter>();
for (int i = 0; i < phaseTwoCount; i++) {
parameters.add(phaseTwoOperators.get(i).getParameter());
}
if (DEBUG) {
System.err.println("parameter list size: " + parameters.size());
for (int i = 0; i < parameters.size(); i++) {
System.err.println("  " + parameters.get(i));
}
}
return new TwoPhaseOperator(phaseOneOperators, phaseTwoOperators, parameters, initial, burnin, weight, mode);
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public String getParserDescription() {
return "This element returns a two phase operator.";
}
public Class getReturnType() {
return TwoPhaseOperator.class;
}
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private final XMLSyntaxRule[] rules = {
AttributeRule.newDoubleRule(WEIGHT),
AttributeRule.newIntegerRule(INITIAL),
AttributeRule.newIntegerRule(BURNIN),
AttributeRule.newBooleanRule(CoercableMCMCOperator.AUTO_OPTIMIZE, true),
new ElementRule(PHASE_ONE,
new XMLSyntaxRule[]{new ElementRule(AbstractCoercableOperator.class, 1, Integer.MAX_VALUE)}),
new ElementRule(PHASE_TWO,
new XMLSyntaxRule[]{new ElementRule(AdaptableVarianceMultivariateNormalOperator.class, 1, Integer.MAX_VALUE)})
new XMLSyntaxRule[]{new ElementRule(AbstractCoercableOperator.class, 1, Integer.MAX_VALUE)})*/
};
}
