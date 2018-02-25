package dr.evomodel.epidemiology;
import dr.evolution.coalescent.DemographicFunction;
import dr.evomodel.coalescent.DemographicModel;
import dr.inference.loggers.LogColumn;
import dr.inference.loggers.NumberColumn;
import dr.inference.model.*;
import dr.math.distributions.NormalDistribution;
public class SIRModel extends DemographicModel implements Likelihood {
//
// Public stuff
//
public SIRModel(Parameter reproductiveNumberParameter,
Parameter recoveryRateParameter,
Parameter hostPopulationSizeParameter,
Parameter proportionsParameter,
Type units) {
this(SIRModelParser.SIR_MODEL, reproductiveNumberParameter, recoveryRateParameter,
hostPopulationSizeParameter, proportionsParameter, units);
}
public SIRModel(String name,
Parameter reproductiveNumberParameter,
Parameter recoveryRateParameter,
Parameter hostPopulationSizeParameter,
Parameter proportionsParameter,
Type units) {
super(name);
this.reproductiveNumberParameter = reproductiveNumberParameter;
addVariable(reproductiveNumberParameter);
reproductiveNumberParameter.addBounds(new Parameter.DefaultBounds(Double.MAX_VALUE, 1.0, 1));
this.recoveryRateParameter = recoveryRateParameter;
addVariable(recoveryRateParameter);
recoveryRateParameter.addBounds(new Parameter.DefaultBounds(Double.MAX_VALUE, 0.0, 1));
this.hostPopulationSizeParameter = hostPopulationSizeParameter;
addVariable(hostPopulationSizeParameter);
hostPopulationSizeParameter.addBounds(new Parameter.DefaultBounds(Double.MAX_VALUE, 0.0, 1));
this.proportionsParameter = proportionsParameter;
addVariable(proportionsParameter);
demographicFunction = new SIRDemographicFunction(units);
setUnits(units);
addStatistic(new TimeseriesStatistic("susceptibles"));
addStatistic(new TimeseriesStatistic("infecteds"));
addStatistic(new TimeseriesStatistic("recovereds"));
addStatistic(new TimeseriesStatistic("effectivePopulationSize"));
}
@Override
protected void handleVariableChangedEvent(final Variable variable, final int index, final Variable.ChangeType type) {
demographicFunction.reset();
fireModelChanged();
likelihoodKnown = false;
}
@Override
protected void storeState() {
storedLogLikelihood = logLikelihood;
demographicFunction.store();
}
@Override
protected void restoreState() {
logLikelihood = storedLogLikelihood;
likelihoodKnown = true;
demographicFunction.restore();
}
// return S(t)
public double getSusceptibles(final double t) {
return demographicFunction.getSusceptibles(t);
}
// return I(t)
public double getInfecteds(final double t) {
return demographicFunction.getInfecteds(t);
}
// return R(t)
public double getRecovereds(final double t) {
return demographicFunction.getRecovereds(t);
}
// return R(t)
public double getEffectivePopulationSize(final double t) {
return demographicFunction.getDemographic(t);
}
public String prettyName() {
return Likelihood.Abstract.getPrettyName(this);
}
public final double getLogLikelihood() {
if (!getLikelihoodKnown()) {
logLikelihood = calculateLogLikelihood();
likelihoodKnown = true;
}
return logLikelihood;
}
protected boolean getLikelihoodKnown() {
return likelihoodKnown;
}
public boolean evaluateEarly() {
return false;
}
public boolean isUsed() {
return isUsed;
}
public void setUsed() {
isUsed = true;
}
public Model getModel() {
return this;
}
public void makeDirty() {
likelihoodKnown = false;
}
public LogColumn[] getColumns() {
return new LogColumn[]{
new LikelihoodColumn(getId())
};
}
protected class LikelihoodColumn extends NumberColumn {
public LikelihoodColumn(String label) {
super(label);
}
public double getDoubleValue() {
return getLogLikelihood();
}
}
public double calculateLogLikelihood() {
double r = demographicFunction.getRecovereds(endTime);
return NormalDistribution.logPdf(r, 0, 100);
}
private boolean isUsed = false;
private boolean likelihoodKnown = false;
private double logLikelihood;
private double storedLogLikelihood;
private double stepSize = 0.01;
private double endTime = 5;
public DemographicFunction getDemographicFunction() {
return demographicFunction;
}
Parameter reproductiveNumberParameter = null;
Parameter recoveryRateParameter = null;
Parameter hostPopulationSizeParameter = null;
Parameter proportionsParameter = null;
SIRDemographicFunction demographicFunction = null;
class SIRDemographicFunction extends DemographicFunction.Abstract {
DynamicalSystem syst = new DynamicalSystem(0, 0.01);
public SIRDemographicFunction(Type units) {
super(units);
double hostPop = hostPopulationSizeParameter.getParameterValue(0);
double initialS = hostPop * proportionsParameter.getParameterValue(0);
double initialI = hostPop * proportionsParameter.getParameterValue(1);
double initialR = hostPop * proportionsParameter.getParameterValue(2);
syst.addVariable("susceptibles", initialS);
syst.addVariable("infecteds", initialI);
syst.addVariable("recovereds", initialR);
syst.addVariable("total", hostPop);
syst.addForce("contact", reproductiveNumberParameter.getParameterValue(0) * recoveryRateParameter.getParameterValue(0),
new String[]{"infecteds","susceptibles"}, new String[]{"total"}, "susceptibles", "infecteds");
syst.addForce("recovery", recoveryRateParameter.getParameterValue(0), new String[]{"infecteds"},
new String[]{}, "infecteds", "recovereds");
}
public void reset() {
double hostPop = hostPopulationSizeParameter.getParameterValue(0);
double initialS = hostPop * proportionsParameter.getParameterValue(0);
double initialI = hostPop * proportionsParameter.getParameterValue(1);
double initialR = hostPop * proportionsParameter.getParameterValue(2);
syst.resetVar("susceptibles", initialS);
syst.resetVar("infecteds", initialI);
syst.resetVar("recovereds", initialR);
syst.resetVar("total", hostPop);
syst.resetForce("contact", reproductiveNumberParameter.getParameterValue(0) * recoveryRateParameter.getParameterValue(0));
syst.resetForce("recovery", recoveryRateParameter.getParameterValue(0));
syst.resetTime();
}
public void store () {
syst.store();
}
public void restore () {
syst.restore();
}
// return N(t)
public double getDemographic(final double t) {
double beta = reproductiveNumberParameter.getParameterValue(0) * recoveryRateParameter.getParameterValue(0);
double numer = getInfecteds(t) * hostPopulationSizeParameter.getParameterValue(0);
double denom = 2.0 * beta * getSusceptibles(t);
double ne = numer / denom;
if (ne < 0.001) {
ne = 0.001;
}
return ne;
}
// return log N(t)
public double getLogDemographic(final double t) {
return Math.log(getDemographic(t));
}
// return S(t)
public double getSusceptibles(final double t) {
return syst.getValue("susceptibles", t);
}
// return I(t)
public double getInfecteds(final double t) {
return syst.getValue("infecteds", t);
}
// return R(t)
public double getRecovereds(final double t) {
return syst.getValue("recovereds", t);
}
// return t/N(t)
public double getIntensity(final double t) {
return 1.0;
}
// return x*N(t)
public double getInverseIntensity(final double x) {
return 1.0;
}
// return integral of 1/N(t)
public double getIntegral(final double start, final double finish) {
double neAvg = 0.5 * (getDemographic(start) + getDemographic(finish));
double integral = (finish-start)*(1.0 / neAvg);
return integral ;
}
// ignore the rest:
public int getNumArguments() {
return 0;
}
public String getArgumentName(final int n) {
return null;
}
public double getArgument(final int n) {
return 0;
}
public void setArgument(final int n, final double value) {
}
public double getLowerBound(final int n) {
return 0;
}
public double getUpperBound(final int n) {
return 0;
}
public DemographicFunction getCopy() {
return null;
}
public double getThreshold() {
return 0;
}
}
public class TimeseriesStatistic extends Statistic.Abstract {
public TimeseriesStatistic(String name) {
super(name);
}
@Override
public String getDimensionName(final int i) {
double t = (double) i * stepSize;
return Double.toString(t);
}
public int getDimension() {
return (int) (endTime / stepSize);
}
public double getStatisticValue(final int i) {
double t = (double) i * stepSize;
if (getStatisticName().equals("susceptibles")) {
return getSusceptibles(t);
}
else if (getStatisticName().equals("infecteds")) {
return getInfecteds(t);
}
else if (getStatisticName().equals("recovereds")) {
return getRecovereds(t);
}
else if (getStatisticName().equals("effectivePopulationSize")) {
return getEffectivePopulationSize(t);
}
else {
return 0.0;
}
}
}
}