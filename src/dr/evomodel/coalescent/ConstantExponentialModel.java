package dr.evomodel.coalescent;
import dr.evolution.coalescent.ConstExponential;
import dr.evolution.coalescent.DemographicFunction;
import dr.evomodelxml.coalescent.ConstantExponentialModelParser;
import dr.inference.model.Parameter;
public class ConstantExponentialModel extends DemographicModel {
//
// Public stuff
//
public ConstantExponentialModel(Parameter N0Parameter, Parameter timeParameter,
Parameter growthRateParameter, Type units, boolean usingGrowthRate) {
this(ConstantExponentialModelParser.CONSTANT_EXPONENTIAL_MODEL, N0Parameter, timeParameter, growthRateParameter, units, usingGrowthRate);
}
public ConstantExponentialModel(String name, Parameter N0Parameter, Parameter timeParameter,
Parameter growthRateParameter, Type units, boolean usingGrowthRate) {
super(name);
constExponential = new ConstExponential(units);
this.N0Parameter = N0Parameter;
addVariable(N0Parameter);
N0Parameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.timeParameter = timeParameter;
addVariable(timeParameter);
timeParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.growthRateParameter = growthRateParameter;
addVariable(growthRateParameter);
growthRateParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.usingGrowthRate = usingGrowthRate;
setUnits(units);
}
// general functions
public DemographicFunction getDemographicFunction() {
double time = timeParameter.getParameterValue(0);
double N0 = N0Parameter.getParameterValue(0);
double growthRate = growthRateParameter.getParameterValue(0);
if (!usingGrowthRate) {
double doublingTime = growthRate;
growthRate = Math.log(2) / doublingTime;
}
constExponential.setGrowthRate(growthRate);
constExponential.setN0(N0);
constExponential.setN1(N0 * Math.exp(-time * growthRate));
return constExponential;
}
//
// protected stuff
//
Parameter N0Parameter = null;
Parameter timeParameter = null;
Parameter growthRateParameter = null;
ConstExponential constExponential = null;
boolean usingGrowthRate = true;
}
