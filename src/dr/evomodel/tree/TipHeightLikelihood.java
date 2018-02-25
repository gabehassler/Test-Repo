package dr.evomodel.tree;
import dr.inference.distribution.ParametricDistributionModel;
import dr.inference.model.Likelihood;
import dr.inference.model.Parameter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
public class TipHeightLikelihood extends Likelihood.Abstract {
public TipHeightLikelihood(ParametricDistributionModel distribution, Parameter tipHeights) {
super(distribution);
this.distribution = distribution;
this.tipHeights = tipHeights;
offsets = new double[tipHeights.getDimension()];
for (int i = 0; i < tipHeights.getDimension(); i++) {
offsets[i] = tipHeights.getParameterValue(i);
}
}
// **************************************************************
// Likelihood IMPLEMENTATION
// **************************************************************
public double calculateLogLikelihood() {
double logL = 0.0;
for (int i = 0; i < tipHeights.getDimension(); i++) {
logL += distribution.logPdf(tipHeights.getParameterValue(i) - offsets[i]);
}
return logL;
}
protected boolean getLikelihoodKnown() {
return false;
}
// **************************************************************
// XMLElement IMPLEMENTATION
// **************************************************************
public Element createElement(Document d) {
throw new RuntimeException("Not implemented yet!");
}
ParametricDistributionModel distribution;
private final Parameter tipHeights;
private final double[] offsets;
}
