package dr.inference.operators;
import dr.inference.model.Bounds;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.math.distributions.RandomGenerator;
import dr.xml.*;
public class GenericIndependentSampler extends SimpleMCMCOperator {
public static final String OPERATOR_NAME = "genericIndependentSampler";
private final Variable<Double> variable;
private final RandomGenerator randomGenerator;
private final boolean univariate;
private final int generatorLength;
public GenericIndependentSampler(Variable variable, RandomGenerator randomGenerator, double weight) {
this.variable = variable;
this.randomGenerator = randomGenerator;
Object draw = randomGenerator.nextRandom();
if (draw instanceof Double) {
generatorLength = 1;
univariate = true;
} else if (draw instanceof double[]) {
generatorLength = ((double[]) draw).length;
univariate = false;
} else {
throw new IllegalArgumentException("Unknown random generator in " + getOperatorName());
}
setWeight(weight);
}
public String getPerformanceSuggestion() {
return "";
}
public String getOperatorName() {
return "genericIndependentSampler(" + variable.getVariableName() + ")";
}
public double doOperation() throws OperatorFailedException {
double logq = 0;
final Bounds<Double> bounds = variable.getBounds();
final int dim = variable.getSize();
int i = 0;
while (i < dim) {
Object draw = randomGenerator.nextRandom();
if (univariate) {
double currentValue = variable.getValue(i);
double newValue = (Double) draw;
logq += randomGenerator.logPdf(currentValue) - randomGenerator.logPdf(newValue);
if (newValue < bounds.getLowerLimit(i) || newValue > bounds.getUpperLimit(i)) {
throw new OperatorFailedException("Proposed value outside boundaries");
}
} else {
double[] currentValue = new double[generatorLength];
double[] newValue = (double[]) draw;
for (int j = 0; j < generatorLength; ++j) {
final int index = i * generatorLength + j;
currentValue[j] = variable.getValue(index);
}
logq += randomGenerator.logPdf(currentValue) - randomGenerator.logPdf(newValue);
for (int j = 0; j < generatorLength; ++j) {
final int index = i * generatorLength + j;
if (newValue[j] < bounds.getLowerLimit(index) || newValue[j] > bounds.getUpperLimit(index)) {
throw new OperatorFailedException("Proposed value outside boundaries");
}
variable.setValue(index, newValue[j]);
}
}
i += generatorLength;
}
return logq;
}
public static dr.xml.XMLObjectParser PARSER = new dr.xml.AbstractXMLObjectParser() {
public String getParserName() {
return OPERATOR_NAME;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
double weight = xo.getDoubleAttribute(WEIGHT);
Parameter parameter = (Parameter) xo.getChild(Parameter.class);
RandomGenerator generator = (RandomGenerator) xo.getChild(RandomGenerator.class);
return new GenericIndependentSampler(parameter, generator, weight);
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private final XMLSyntaxRule[] rules = {
AttributeRule.newDoubleRule(WEIGHT),
new ElementRule(RandomGenerator.class),
new ElementRule(Parameter.class)
};
public String getParserDescription() {
return "This element returns an independence sampler from a provided generic distribution generator.";
}
public Class getReturnType() {
return MCMCOperator.class;
}
};
}
