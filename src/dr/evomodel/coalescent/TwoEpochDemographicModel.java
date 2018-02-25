package dr.evomodel.coalescent;
import dr.evolution.coalescent.DemographicFunction;
import dr.evolution.coalescent.TwoEpochDemographic;
import dr.evomodelxml.coalescent.TwoEpochDemographicModelParser;
import dr.inference.model.Parameter;
public class TwoEpochDemographicModel extends DemographicModel {
//
// Public stuff
//
public TwoEpochDemographicModel(DemographicModel demo1, DemographicModel demo2, Parameter transitionTimeParameter, Type units) {
this(TwoEpochDemographicModelParser.TWO_EPOCH_MODEL, demo1, demo2, transitionTimeParameter, units);
}
public TwoEpochDemographicModel(String name, DemographicModel demo1, DemographicModel demo2, Parameter transitionTimeParameter, Type units) {
super(name);
this.demo1 = demo1;
addModel(demo1);
for (int i = 0; i < demo1.getVariableCount(); i++) {
addVariable((Parameter)demo1.getVariable(i));
}
this.demo2 = demo2;
addModel(demo2);
for (int i = 0; i < demo2.getVariableCount(); i++) {
addVariable((Parameter)demo2.getVariable(i));
}
this.transitionTimeParameter = transitionTimeParameter;
addVariable(transitionTimeParameter);
setUnits(units);
}
// general functions
public DemographicFunction getDemographicFunction() {
TwoEpochDemographic twoEpoch = new TwoEpochDemographic(demo1.getDemographicFunction(), demo2.getDemographicFunction(), getUnits());
twoEpoch.setTransitionTime(transitionTimeParameter.getParameterValue(0));
return twoEpoch;
}
private Parameter transitionTimeParameter = null;
private DemographicModel demo1 = null;
private DemographicModel demo2 = null;
}
