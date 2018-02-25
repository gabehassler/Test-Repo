package dr.evomodel.coalescent;
import dr.evolution.coalescent.CataclysmicDemographic;
import dr.evolution.coalescent.DemographicFunction;
import dr.evomodelxml.coalescent.CataclysmicDemographicModelParser;
import dr.inference.model.Parameter;
public class CataclysmicDemographicModel extends DemographicModel {
public CataclysmicDemographicModel(Parameter N0Parameter, Parameter N1Parameter, Parameter growthRateParameter, Parameter timeParameter, Type units) {
this(CataclysmicDemographicModelParser.CATACLYSM_MODEL, N0Parameter, N1Parameter, growthRateParameter, timeParameter, units);
}
public CataclysmicDemographicModel(String name, Parameter N0Parameter, Parameter N1Parameter, Parameter growthRateParameter, Parameter timeParameter, Type units) {
super(name);
cataclysm = new CataclysmicDemographic(units);
this.N0Parameter = N0Parameter;
addVariable(N0Parameter);
N0Parameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.N1Parameter = N1Parameter;
addVariable(N1Parameter);
N1Parameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.growthRateParameter = growthRateParameter;
addVariable(growthRateParameter);
growthRateParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.MIN_VALUE, 1));
this.timeParameter = timeParameter;
addVariable(timeParameter);
timeParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.MIN_VALUE, 1));
setUnits(units);
}
// general functions
public DemographicFunction getDemographicFunction() {
cataclysm.setN0(N0Parameter.getParameterValue(0));
cataclysm.setGrowthRate(growthRateParameter.getParameterValue(0));
cataclysm.setCataclysmTime(timeParameter.getParameterValue(0));
// Doesn't this...
double N0 = N0Parameter.getParameterValue(0);
double N1 = N1Parameter.getParameterValue(0) * N0;
double t = timeParameter.getParameterValue(0);
double declineRate = Math.log(N1/N0)/t;
double t = timeParameter.getParameterValue(0);
double declineRate = Math.log(N1Parameter.getParameterValue(0)) / t;
cataclysm.setDeclineRate(declineRate);
return cataclysm;
}
//
// protected stuff
//
Parameter N0Parameter = null;
Parameter N1Parameter = null;
Parameter growthRateParameter = null;
Parameter timeParameter = null;
CataclysmicDemographic cataclysm = null;
}
