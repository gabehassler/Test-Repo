package dr.evomodel.coalescent;
import dr.evolution.coalescent.DemographicFunction;
import dr.evolution.coalescent.ExponentialGrowth;
import dr.evolution.coalescent.PowerLawGrowth;
import dr.evomodelxml.coalescent.ExponentialGrowthModelParser;
import dr.evomodelxml.coalescent.PowerLawGrowthModelParser;
import dr.inference.model.Parameter;
public class PowerLawGrowthModel extends DemographicModel {
    //
    // Public stuff
    //
    public PowerLawGrowthModel(Parameter N0Parameter, Parameter growthRateParameter,
                               Type units) {
        this(PowerLawGrowthModelParser.POWER_LAW_GROWTH_MODEL, N0Parameter, growthRateParameter, units);
    }
    public PowerLawGrowthModel(String name, Parameter N0Parameter, Parameter powerParameter,
                               Type units) {
        super(name);
        powerLawGrowth = new PowerLawGrowth(units);
        this.N0Parameter = N0Parameter;
        addVariable(N0Parameter);
        N0Parameter.addBounds(new Parameter.DefaultBounds(Double.MAX_VALUE, 0.0, 1));
        this.powerParameter = powerParameter;
        addVariable(powerParameter);
        powerParameter.addBounds(new Parameter.DefaultBounds(Double.MAX_VALUE, 1, 1));
        setUnits(units);
    }
    // general functions
    public DemographicFunction getDemographicFunction() {
        powerLawGrowth.setN0(N0Parameter.getParameterValue(0));
        double r = powerParameter.getParameterValue(0);
        powerLawGrowth.setR(r);
        return powerLawGrowth;
    }
    //
    // protected stuff
    //
    Parameter N0Parameter = null;
    Parameter powerParameter = null;
    PowerLawGrowth powerLawGrowth = null;
}
