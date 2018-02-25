package dr.evomodel.coalescent;
import dr.evolution.coalescent.ConstLogistic;
import dr.evolution.coalescent.DemographicFunction;
import dr.evomodelxml.coalescent.ConstantLogisticModelParser;
import dr.inference.model.Parameter;
public class ConstantLogisticModel extends DemographicModel {
//
// Public stuff
//
public ConstantLogisticModel(Parameter N0Parameter, Parameter N1Parameter, Parameter growthRateParameter, Parameter shapeParameter, double alpha, Type units) {
this(ConstantLogisticModelParser.CONSTANT_LOGISTIC_MODEL, N0Parameter, N1Parameter, growthRateParameter, shapeParameter, alpha, units);
}
private ConstantLogisticModel(String name, Parameter N0Parameter, Parameter N1Parameter, Parameter growthRateParameter, Parameter shapeParameter, double alpha, Type units) {
super(name);
constLogistic = new ConstLogistic(units);
this.N0Parameter = N0Parameter;
addVariable(N0Parameter);
N0Parameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.N1Parameter = N1Parameter;
addVariable(N1Parameter);
N1Parameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.growthRateParameter = growthRateParameter;
addVariable(growthRateParameter);
growthRateParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.shapeParameter = shapeParameter;
addVariable(shapeParameter);
shapeParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.alpha = alpha;
setUnits(units);
}
// general functions
public DemographicFunction getDemographicFunction() {
constLogistic.setN0(N0Parameter.getParameterValue(0));
constLogistic.setN1(N1Parameter.getParameterValue(0));
double r = growthRateParameter.getParameterValue(0);
constLogistic.setGrowthRate(r);
// AER 24/02/03
// logisticGrowth.setShape(Math.exp(shapeParameter.getParameterValue(0)));
// New parameterization of logistic shape to be the time at which the
// population reached some proportion alpha:
double C = ((1.0 - alpha) * Math.exp(-r * shapeParameter.getParameterValue(0))) / alpha;
constLogistic.setShape(C);
return constLogistic;
}
//
// protected stuff
//
private Parameter N0Parameter = null;
private Parameter N1Parameter = null;
private Parameter growthRateParameter = null;
private Parameter shapeParameter = null;
private double alpha = 0.5;
private ConstLogistic constLogistic = null;
}
