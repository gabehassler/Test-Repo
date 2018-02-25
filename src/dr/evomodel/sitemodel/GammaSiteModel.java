package dr.evomodel.sitemodel;
import dr.evomodel.substmodel.FrequencyModel;
import dr.evomodel.substmodel.SubstitutionModel;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.math.distributions.GammaDistribution;
public class GammaSiteModel extends AbstractModel implements SiteModel {
public GammaSiteModel(SubstitutionModel substitutionModel) {
this(substitutionModel,
null,
null,
0,
null);
}
public GammaSiteModel(SubstitutionModel substitutionModel, double alpha, int categoryCount) {
this(substitutionModel,
null,
new Parameter.Default(alpha),
categoryCount,
null);
}
public GammaSiteModel(SubstitutionModel substitutionModel, double pInvar) {
this(substitutionModel,
null,
null,
0,
new Parameter.Default(pInvar));
}
public GammaSiteModel(SubstitutionModel substitutionModel, double alpha, int categoryCount, double pInvar) {
this(substitutionModel,
null,
new Parameter.Default(alpha),
categoryCount,
new Parameter.Default(pInvar));
}
public GammaSiteModel(SubstitutionModel substitutionModel,
Parameter muParameter,
Parameter shapeParameter, int gammaCategoryCount,
Parameter invarParameter) {
super(SITE_MODEL);
this.substitutionModel = substitutionModel;
addModel(substitutionModel);
this.muParameter = muParameter;
if (muParameter != null) {
addVariable(muParameter);
muParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
}
this.shapeParameter = shapeParameter;
if (shapeParameter != null) {
this.categoryCount = gammaCategoryCount;
addVariable(shapeParameter);
// The quantile calculator fails when the shape parameter goes much below
// 1E-3 so we have put a hard lower bound on it. If this is not there then
// the category rates can go to 0 and cause a -Inf likelihood (whilst this
// is not a problem as the state will be rejected, it could mask other issues
// and this seems the better approach.
shapeParameter.addBounds(new Parameter.DefaultBounds(1.0E3, 1.0E-3, 1));
} else {
this.categoryCount = 1;
}
this.invarParameter = invarParameter;
if (invarParameter != null) {
this.categoryCount += 1;
addVariable(invarParameter);
invarParameter.addBounds(new Parameter.DefaultBounds(1.0, 0.0, 1));
}
categoryRates = new double[this.categoryCount];
categoryProportions = new double[this.categoryCount];
ratesKnown = false;
}
public void setMu(double mu) {
muParameter.setParameterValue(0, mu);
}
public final double getMu() {
return muParameter.getParameterValue(0);
}
public void setAlpha(double alpha) {
shapeParameter.setParameterValue(0, alpha);
ratesKnown = false;
}
public final double getAlpha() {
return shapeParameter.getParameterValue(0);
}
public Parameter getMutationRateParameter() {
return muParameter;
}
public Parameter getAlphaParameter() {
return shapeParameter;
}
public Parameter getPInvParameter() {
return invarParameter;
}
public void setMutationRateParameter(Parameter parameter) {
if (muParameter != null) removeVariable(muParameter);
muParameter = parameter;
if (muParameter != null) addVariable(muParameter);
}
public void setAlphaParameter(Parameter parameter) {
if (shapeParameter != null) removeVariable(shapeParameter);
shapeParameter = parameter;
if (shapeParameter != null) addVariable(shapeParameter);
}
public void setPInvParameter(Parameter parameter) {
if (invarParameter != null) removeVariable(invarParameter);
invarParameter = parameter;
if (invarParameter != null) addVariable(invarParameter);
}
// *****************************************************************
// Interface SiteModel
// *****************************************************************
public boolean integrateAcrossCategories() {
return true;
}
public int getCategoryCount() {
return categoryCount;
}
public int getCategoryOfSite(int site) {
throw new IllegalArgumentException("Integrating across categories");
}
public double getRateForCategory(int category) {
synchronized (this) {
if (!ratesKnown) {
calculateCategoryRates();
}
}
final double mu = (muParameter != null) ? muParameter.getParameterValue(0) : 1.0;
return categoryRates[category] * mu;
}
public double[] getCategoryRates() {
synchronized (this) {
if (!ratesKnown) {
calculateCategoryRates();
}
}
final double mu = (muParameter != null) ? muParameter.getParameterValue(0) : 1.0;
final double[] rates = new double[categoryRates.length];
for (int i = 0; i < rates.length; i++) {
rates[i] = categoryRates[i] * mu;
}
return rates;
}
public void getTransitionProbabilities(double substitutions, double[] matrix) {
substitutionModel.getTransitionProbabilities(substitutions, matrix);
}
public double getProportionForCategory(int category) {
synchronized (this) {
if (!ratesKnown) {
calculateCategoryRates();
}
}
return categoryProportions[category];
}
public double[] getCategoryProportions() {
synchronized (this) {
if (!ratesKnown) {
calculateCategoryRates();
}
}
return categoryProportions;
}
private void calculateCategoryRates() {
double propVariable = 1.0;
int cat = 0;
if (invarParameter != null) {
categoryRates[0] = 0.0;
categoryProportions[0] = invarParameter.getParameterValue(0);
propVariable = 1.0 - categoryProportions[0];
cat = 1;
}
if (shapeParameter != null) {
final double a = shapeParameter.getParameterValue(0);
double mean = 0.0;
final int gammaCatCount = categoryCount - cat;
for (int i = 0; i < gammaCatCount; i++) {
categoryRates[i + cat] = GammaDistribution.quantile((2.0 * i + 1.0) / (2.0 * gammaCatCount), a, 1.0 / a);
mean += categoryRates[i + cat];
categoryProportions[i + cat] = propVariable / gammaCatCount;
}
mean = (propVariable * mean) / gammaCatCount;
for (int i = 0; i < gammaCatCount; i++) {
categoryRates[i + cat] /= mean;
}
} else {
categoryRates[cat] = 1.0 / propVariable;
categoryProportions[cat] = propVariable;
}
ratesKnown = true;
}
public FrequencyModel getFrequencyModel() {
return substitutionModel.getFrequencyModel();
}
public SubstitutionModel getSubstitutionModel() {
return substitutionModel;
}
// *****************************************************************
// Interface ModelComponent
// *****************************************************************
protected void handleModelChangedEvent(Model model, Object object, int index) {
// Substitution model has changed so fire model changed event
listenerHelper.fireModelChanged(this, object, index);
}
protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
if (variable == shapeParameter) {
ratesKnown = false;
} else if (variable == invarParameter) {
ratesKnown = false;
} else {
// is the muParameter and nothing needs to be done
}
listenerHelper.fireModelChanged(this, variable, index);
}
protected void storeState() {
} // no additional state needs storing
protected void restoreState() {
ratesKnown = false;
}
protected void acceptState() {
} // no additional state needs accepting
private SubstitutionModel substitutionModel = null;
private Parameter muParameter;
private Parameter shapeParameter;
private Parameter invarParameter;
private boolean ratesKnown;
private int categoryCount;
private double[] categoryRates;
private double[] categoryProportions;
}