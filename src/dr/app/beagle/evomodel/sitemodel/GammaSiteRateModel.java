package dr.app.beagle.evomodel.sitemodel;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.math.distributions.GammaDistribution;
import dr.app.beagle.evomodel.substmodel.SubstitutionModel;
public class GammaSiteRateModel extends AbstractModel implements SiteRateModel {
public GammaSiteRateModel(String name) {
this(   name,
null,
null,
0,
null);
}
public GammaSiteRateModel(String name, double alpha, int categoryCount) {
this(   name,
null,
new Parameter.Default(alpha),
categoryCount,
null);
}
public GammaSiteRateModel(String name, double pInvar) {
this(   name,
null,
null,
0,
new Parameter.Default(pInvar));
}
public GammaSiteRateModel(String name, double alpha, int categoryCount, double pInvar) {
this(   name,
null,
new Parameter.Default(alpha),
categoryCount,
new Parameter.Default(pInvar));
}
public GammaSiteRateModel(
String name,
Parameter muParameter,
Parameter shapeParameter, int gammaCategoryCount,
Parameter invarParameter) {
super(name);
this.muParameter = muParameter;
if (muParameter != null) {
addVariable(muParameter);
muParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
}
this.shapeParameter = shapeParameter;
if (shapeParameter != null) {
this.categoryCount = gammaCategoryCount;
addVariable(shapeParameter);
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
// Interface SiteRateModel
// *****************************************************************
public int getCategoryCount() {
return categoryCount;
}
public double[] getCategoryRates() {
synchronized (this) {
if (!ratesKnown) {
calculateCategoryRates();
}
}
return categoryRates;
}
public double[] getCategoryProportions() {
synchronized (this) {
if (!ratesKnown) {
calculateCategoryRates();
}
}
return categoryProportions;
}
public double getRateForCategory(int category) {
synchronized (this) {
if (!ratesKnown) {
calculateCategoryRates();
}
}
return categoryRates[category];
}
public double getProportionForCategory(int category) {
synchronized (this) {
if (!ratesKnown) {
calculateCategoryRates();
}
}
return categoryProportions[category];
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
if (muParameter != null) { // Moved multiplication by mu to here; it also
// needed by double[] getCategoryRates() -- previously ignored
double mu = muParameter.getParameterValue(0);
for (int i=0; i < categoryCount; i++)
categoryRates[i] *= mu;
}
ratesKnown = true;
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
} else if (variable == muParameter) {
ratesKnown = false; // MAS: I changed this because the rate parameter can affect the categories if the parameter is in siteModel and not clockModel
} else {
throw new RuntimeException("Unknown variable in GammaSiteRateModel.handleVariableChangedEvent");
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
private Parameter muParameter;
private Parameter shapeParameter;
private Parameter invarParameter;
private boolean ratesKnown;
private int categoryCount;
private double[] categoryRates;
private double[] categoryProportions;
// This is here solely to allow the GammaSiteModelParser to pass on the substitution model to the
// HomogenousBranchSubstitutionModel so that the XML will be compatible with older BEAST versions. To be removed
// at some point.
public SubstitutionModel getSubstitutionModel() {
return substitutionModel;
}
public void setSubstitutionModel(SubstitutionModel substitutionModel) {
this.substitutionModel = substitutionModel;
}
private SubstitutionModel substitutionModel;
}