package dr.evomodel.coalescent;
import dr.evolution.coalescent.DemographicFunction;
import dr.evolution.coalescent.FlexibleGrowth;
import dr.evolution.coalescent.LogisticGrowth;
import dr.evomodelxml.coalescent.LogisticGrowthModelParser;
import dr.evomodelxml.coalescent.PeakAndDeclineModelParser;
import dr.inference.model.Parameter;
public class PeakAndDeclineModel extends DemographicModel {
//
// Public stuff
//
public PeakAndDeclineModel(Parameter peakValueParameter, Parameter shapeParameter, Parameter peakTimeParameter,
Type units) {
this(PeakAndDeclineModelParser.PEAK_AND_DECLINE_MODEL, peakValueParameter, shapeParameter, peakTimeParameter,
units);
}
public PeakAndDeclineModel(String name, Parameter peakValueParameter, Parameter shapeParameter,
Parameter peakTimeParameter, Type units) {
super(name);
flexibleGrowth = new FlexibleGrowth(units);
this.peakValueParameter = peakValueParameter;
addVariable(peakValueParameter);
peakValueParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.peakTimeParameter = peakTimeParameter;
addVariable(peakTimeParameter);
peakTimeParameter.addBounds(new Parameter.DefaultBounds(0, Double.NEGATIVE_INFINITY, 1));
this.shapeParameter = shapeParameter;
addVariable(shapeParameter);
shapeParameter.addBounds(new Parameter.DefaultBounds(0, Double.NEGATIVE_INFINITY, 1));
setUnits(units);
}
// general functions
public DemographicFunction getDemographicFunction() {
double peakTimeValue = peakTimeParameter.getParameterValue(0);
double peakValueValue = peakValueParameter.getParameterValue(0);
double shapeValue = shapeParameter.getParameterValue(0);
double flexibleN0 = peakValueValue*(1-shapeValue)/(shapeValue*peakTimeValue);
double flexibleK = (-shapeValue/Math.pow(-peakTimeValue, shapeValue-1));
flexibleGrowth.setN0(flexibleN0);
flexibleGrowth.setK(flexibleK);
flexibleGrowth.setR(shapeValue);
return flexibleGrowth;
}
//
// protected stuff
//
Parameter peakValueParameter = null;
Parameter shapeParameter = null;
Parameter peakTimeParameter = null;
FlexibleGrowth flexibleGrowth = null;
}
