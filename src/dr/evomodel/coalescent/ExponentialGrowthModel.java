package dr.evomodel.coalescent;
import dr.evolution.coalescent.DemographicFunction;
import dr.evolution.coalescent.ExponentialGrowth;
import dr.evomodelxml.coalescent.ExponentialGrowthModelParser;
import dr.inference.model.Parameter;
public class ExponentialGrowthModel extends DemographicModel {
//
// Public stuff
//
public ExponentialGrowthModel(Parameter N0Parameter, Parameter growthRateParameter,
Type units, boolean usingGrowthRate) {
this(ExponentialGrowthModelParser.EXPONENTIAL_GROWTH_MODEL, N0Parameter, growthRateParameter, units, usingGrowthRate);
}
public ExponentialGrowthModel(String name, Parameter N0Parameter, Parameter growthRateParameter,
Type units, boolean usingGrowthRate) {
super(name);
exponentialGrowth = new ExponentialGrowth(units);
this.N0Parameter = N0Parameter;
addVariable(N0Parameter);
N0Parameter.addBounds(new Parameter.DefaultBounds(Double.MAX_VALUE, 0.0, 1));
this.growthRateParameter = growthRateParameter;
addVariable(growthRateParameter);
growthRateParameter.addBounds(new Parameter.DefaultBounds(Double.MAX_VALUE, -Double.MAX_VALUE, 1));
this.usingGrowthRate = usingGrowthRate;
setUnits(units);
}
// general functions
public DemographicFunction getDemographicFunction() {
exponentialGrowth.setN0(N0Parameter.getParameterValue(0));
if (usingGrowthRate) {
double r = growthRateParameter.getParameterValue(0);
exponentialGrowth.setGrowthRate(r);
} else {
double doublingTime = growthRateParameter.getParameterValue(0);
exponentialGrowth.setDoublingTime(doublingTime);
}
return exponentialGrowth;
}
//
// protected stuff
//
Parameter N0Parameter = null;
Parameter growthRateParameter = null;
ExponentialGrowth exponentialGrowth = null;
boolean usingGrowthRate = true;
}
