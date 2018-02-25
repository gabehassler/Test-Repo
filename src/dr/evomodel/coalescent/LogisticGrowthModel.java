package dr.evomodel.coalescent;
import dr.evolution.coalescent.DemographicFunction;
import dr.evolution.coalescent.LogisticGrowth;
import dr.evomodelxml.coalescent.LogisticGrowthModelParser;
import dr.inference.model.Parameter;
public class LogisticGrowthModel extends DemographicModel {
//
// Public stuff
//
public LogisticGrowthModel(Parameter N0Parameter, Parameter growthRateParameter,
Parameter shapeParameter, double alpha, Type units,
boolean usingGrowthRate) {
this(LogisticGrowthModelParser.LOGISTIC_GROWTH_MODEL, N0Parameter, growthRateParameter, shapeParameter, alpha, units, usingGrowthRate);
}
public LogisticGrowthModel(String name, Parameter N0Parameter, Parameter growthRateParameter, Parameter shapeParameter, double alpha, Type units, boolean usingGrowthRate) {
super(name);
logisticGrowth = new LogisticGrowth(units);
this.N0Parameter = N0Parameter;
addVariable(N0Parameter);
N0Parameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.growthRateParameter = growthRateParameter;
addVariable(growthRateParameter);
growthRateParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.shapeParameter = shapeParameter;
addVariable(shapeParameter);
shapeParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.alpha = alpha;
this.usingGrowthRate = usingGrowthRate;
setUnits(units);
}
// general functions
public DemographicFunction getDemographicFunction() {
logisticGrowth.setN0(N0Parameter.getParameterValue(0));
if (usingGrowthRate) {
double r = growthRateParameter.getParameterValue(0);
logisticGrowth.setGrowthRate(r);
} else {
double doublingTime = growthRateParameter.getParameterValue(0);
logisticGrowth.setDoublingTime(doublingTime);
}
logisticGrowth.setTime50(shapeParameter.getParameterValue(0));
return logisticGrowth;
}
//
// protected stuff
//
Parameter N0Parameter = null;
Parameter growthRateParameter = null;
Parameter shapeParameter = null;
double alpha = 0.5;
LogisticGrowth logisticGrowth = null;
boolean usingGrowthRate = true;
}
