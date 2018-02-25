package dr.inference.distribution;
import dr.inference.model.CompoundModel;
import dr.inference.model.Likelihood;
import dr.inference.model.Model;
import dr.inference.model.Statistic;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
public class MixedDistributionLikelihood extends Likelihood.Abstract {
public MixedDistributionLikelihood(ParametricDistributionModel[] distributions, Statistic data, Statistic indicators) {
// Mixed Distribution Likelihood contains two distribution models, not necessarily constant.
// To cater for that, they need to be returned as the "Model" of this likelyhood so that their state is correctly
// restored when an operation involving their parameters fails.
super(new CompoundModel("MixedDistributions"));
final CompoundModel cm = (CompoundModel) this.getModel();
for (ParametricDistributionModel m : distributions) {
cm.addModel(m);
}
this.distributions = distributions;
this.data = data;
this.indicators = indicators;
if (indicators.getDimension() == data.getDimension() - 1) {
impliedOne = true;
} else if (indicators.getDimension() != data.getDimension()) {
throw new IllegalArgumentException("Indicators length (" + indicators.getDimension() +
") != data length (" + data.getDimension() + ")");
}
}
// **************************************************************
// Likelihood IMPLEMENTATION
// **************************************************************
public final double calculateLogLikelihood() {
double logL = 0.0;
for (int j = 0; j < data.getDimension(); j++) {
int index;
if (impliedOne) {
if (j == 0) {
index = 1;
} else {
index = (int) indicators.getStatisticValue(j - 1);
}
} else {
index = (int) indicators.getStatisticValue(j);
}
logL += distributions[index].logPdf(data.getStatisticValue(j));
}
//System.err.println("mixed: " + logL);
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
private final ParametricDistributionModel[] distributions;
private final Statistic data;
private final Statistic indicators;
private boolean impliedOne = false;
public Model[] getUniqueModels() {
Model[] m = new Model[distributions[0] == distributions[1] ? 1 : 2];
m[0] = distributions[0];
if (m.length > 1) m[1] = distributions[1];
return m;
}
}
