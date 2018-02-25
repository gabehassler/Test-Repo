package dr.evomodel.antigenic;
import dr.inference.model.Likelihood;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.inference.operators.GibbsOperator;
import dr.inference.operators.MCMCOperator;
import dr.inference.operators.SimpleMCMCOperator;
import dr.math.MathUtils;
import dr.xml.*;
public class DirichletProcessGibbsOperator extends SimpleMCMCOperator implements GibbsOperator {
public final static String DIRICHLET_PROCESS_OPERATOR = "dirichletProcessOperator";
private final int N;
private final int K;
private final Parameter chiParameter;
private final Likelihood modelLikelihood;
public DirichletProcessGibbsOperator(Parameter clusteringParameter,
Parameter chiParameter,
Likelihood modelLikelihood,
double weight) {
this.clusteringParameter = clusteringParameter;
this.N = clusteringParameter.getDimension();
this.chiParameter = chiParameter;
this.modelLikelihood = modelLikelihood;
this.K = this.N; // TODO number of potential clusters should be much less than N
setWeight(weight);
}
public Parameter getParameter() {
return (Parameter) clusteringParameter;
}
public Variable getVariable() {
return clusteringParameter;
}
public final double doOperation() {
int index = MathUtils.nextInt(clusteringParameter.getDimension());
int[] occupancy = new int[N];
// construct cluster occupancy vector excluding the selected item and count
// the unoccupied clusters.
int X = K; // X = number of unoccupied clusters
for (int i = 0; i < clusteringParameter.getDimension(); i++) {
int j = (int) clusteringParameter.getParameterValue(i);
if (i != index) {
occupancy[j] += 1;
if (occupancy[j] == 1) { // first item in cluster
X -= 1; // one fewer unoccupied
}
}
}
double chi = chiParameter.getParameterValue(0);
double p1 = chi / ((N - 1 + chi) * X);
double[] P = new double[K];
for (int i = 0; i < K; i++) {
double p;
if (occupancy[i] == 0) {
p = p1;
} else {
p = occupancy[i] / (N - 1 + chi);
}
P[i] = Math.log(p); // Store in log-scale for addition with conditionalLogLikelihood
}
if (modelLikelihood != null) {
for (int k = 0; k < K; ++k) {
clusteringParameter.setParameterValue(index, k);
P[k] +=  modelLikelihood.getLogLikelihood();;
}
}
this.rescale(P); // Improve numerical stability
this.exp(P); // Transform back to probability-scale
int k = MathUtils.randomChoicePDF(P);
((Parameter) clusteringParameter).setParameterValue(index, k);
return 0.0;
}
private void exp(double[] logX) {
for (int i = 0; i < logX.length; ++i) {
logX[i] = Math.exp(logX[i]);
}
}
private void rescale(double[] logX) {
double max = this.max(logX);
for (int i = 0; i < logX.length; ++i) {
logX[i] -= max;
}
}
private double max(double[] x) {
double max = x[0];
for (double xi : x) {
if (xi > max) {
max = xi;
}
}
return max;
}
//MCMCOperator INTERFACE
public final String getOperatorName() {
return DIRICHLET_PROCESS_OPERATOR+"(" + clusteringParameter.getId() + "|" + chiParameter.getId() + ")";
}
public final void optimize(double targetProb) {
throw new RuntimeException("This operator cannot be optimized!");
}
public boolean isOptimizing() {
return false;
}
public void setOptimizing(boolean opt) {
throw new RuntimeException("This operator cannot be optimized!");
}
public double getMinimumAcceptanceLevel() {
return 0.1;
}
public double getMaximumAcceptanceLevel() {
return 0.4;
}
public double getMinimumGoodAcceptanceLevel() {
return 0.20;
}
public double getMaximumGoodAcceptanceLevel() {
return 0.30;
}
public String getPerformanceSuggestion() {
if (Utils.getAcceptanceProbability(this) < getMinimumAcceptanceLevel()) {
return "";
} else if (Utils.getAcceptanceProbability(this) > getMaximumAcceptanceLevel()) {
return "";
} else {
return "";
}
}
public String toString() {
return DIRICHLET_PROCESS_OPERATOR + "(" + clusteringParameter.getId() + ")";
}
//PRIVATE STUFF
private Parameter clusteringParameter = null;
public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {
public final static String CHI = "chi";
public final static String LIKELIHOOD = "likelihood";
public String getParserName() {
return DIRICHLET_PROCESS_OPERATOR;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
double weight = xo.getDoubleAttribute(MCMCOperator.WEIGHT);
Parameter clusteringParameter = (Parameter) xo.getChild(Parameter.class);
Parameter chiParameter = (Parameter)xo.getElementFirstChild(CHI);
Likelihood likelihood = null;
if (xo.hasChildNamed(LIKELIHOOD)) {
likelihood = (Likelihood)xo.getElementFirstChild(LIKELIHOOD);
}
return new DirichletProcessGibbsOperator(clusteringParameter, chiParameter, likelihood, weight);
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public String getParserDescription() {
return "An operator that picks a new allocation of an item to a cluster under the Dirichlet process.";
}
public Class getReturnType() {
return DirichletProcessGibbsOperator.class;
}
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private final XMLSyntaxRule[] rules = {
AttributeRule.newDoubleRule(MCMCOperator.WEIGHT),
new ElementRule(CHI, new XMLSyntaxRule[] {
new ElementRule(Parameter.class),
}),
new ElementRule(LIKELIHOOD, new XMLSyntaxRule[] {
new ElementRule(Likelihood.class),
}, true),
new ElementRule(Parameter.class)
};
};
public int getStepCount() {
return 1;
}
}