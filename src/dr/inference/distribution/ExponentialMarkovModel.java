package dr.inference.distribution;
import dr.inference.model.AbstractModelLikelihood;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.math.distributions.GammaDistribution;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
public class ExponentialMarkovModel extends AbstractModelLikelihood {
public static final String EXPONENTIAL_MARKOV_MODEL = "exponentialMarkovLikelihood";
public ExponentialMarkovModel(Parameter chainParameter, boolean jeffreys, boolean reverse, double shape) {
super(EXPONENTIAL_MARKOV_MODEL);
this.chainParameter = chainParameter;
this.jeffreys = jeffreys;
this.reverse = reverse;
this.shape = shape;
addVariable(chainParameter);
chainParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, chainParameter.getDimension()));
}
public Parameter getChainParameter() {
return (Parameter)getVariable(0);
}
// *****************************************************************
// Interface Model
// *****************************************************************
public void handleModelChangedEvent(Model model, Object object, int index) {
// no intermediates need to be recalculated...
}
protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
// no intermediates need to be recalculated...
}
protected void storeState() {
} // no additional state needs storing
protected void restoreState() {
} // no additional state needs restoring
protected void acceptState() {
} // no additional state needs accepting
// **************************************************************
// XMLElement IMPLEMENTATION
// **************************************************************
public Element createElement(Document document) {
throw new RuntimeException("Not implemented!");
}
// **************************************************************
// Likelihood
// **************************************************************
public Model getModel() {
return this;
}
private int index(int i) {
if (reverse)
return chainParameter.getDimension() - i - 1;
else
return i;
}
public double getLogLikelihood() {
double logL = 0.0;
// jeffreys Prior!
if (jeffreys) {
logL += -Math.log(chainParameter.getParameterValue(index(0)));
}
for (int i = 1; i < chainParameter.getDimension(); i++) {
final double mean = chainParameter.getParameterValue(index(i - 1));
final double x = chainParameter.getParameterValue(index(i));
//logL += dr.math.distributions.ExponentialDistribution.logPdf(x, 1.0/mean);
final double scale = mean / shape;
logL += GammaDistribution.logPdf(x, shape, scale);
}
return logL;
}
public void makeDirty() {
}
// **************************************************************
// Identifiable IMPLEMENTATION
// **************************************************************
private String id = null;
public void setId(String id) {
this.id = id;
}
public String getId() {
return id;
}
// **************************************************************
// Private instance variables
// **************************************************************
private Parameter chainParameter = null;
private boolean jeffreys = false;
private boolean reverse = false;
private double shape = 1.0;
}
