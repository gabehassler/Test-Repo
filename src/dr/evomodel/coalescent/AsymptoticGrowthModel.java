package dr.evomodel.coalescent;
import dr.evolution.coalescent.DemographicFunction;
import dr.evolution.coalescent.FlexibleGrowth;
import dr.evomodelxml.coalescent.AsymptoticGrowthModelParser;
import dr.evomodelxml.coalescent.LogisticGrowthModelParser;
import dr.inference.model.Parameter;
public class AsymptoticGrowthModel extends DemographicModel {
//
// Public stuff
//
public AsymptoticGrowthModel(Parameter asymptoteValueParameter, Parameter shapeParameter, Type units) {
this(AsymptoticGrowthModelParser.ASYMPTOTIC_GROWTH_MODEL, asymptoteValueParameter, shapeParameter, units);
}
public AsymptoticGrowthModel(String name, Parameter asymptoteValueParameter, Parameter shapeParameter,
Type units) {
super(name);
flexibleGrowth = new FlexibleGrowth(units);
this.asyptoteValue = asymptoteValueParameter;
addVariable(asymptoteValueParameter);
asymptoteValueParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.shapeParameter = shapeParameter;
addVariable(shapeParameter);
shapeParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0, 1));
setUnits(units);
}
// general functions
public DemographicFunction getDemographicFunction() {
double asymptoteValue = asyptoteValue.getParameterValue(0);
double shapeValue = shapeParameter.getParameterValue(0);
double flexibleN0 = asymptoteValue/shapeValue;
flexibleGrowth.setN0(flexibleN0);
flexibleGrowth.setK(shapeValue);
flexibleGrowth.setR(0);
return flexibleGrowth;
}
//
// protected stuff
//
Parameter asyptoteValue = null;
Parameter shapeParameter = null;
FlexibleGrowth flexibleGrowth = null;
}
