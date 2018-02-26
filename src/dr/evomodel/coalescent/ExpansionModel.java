package dr.evomodel.coalescent;
import dr.evolution.coalescent.DemographicFunction;
import dr.evolution.coalescent.Expansion;
import dr.evomodelxml.coalescent.ExpansionModelParser;
import dr.inference.model.Parameter;
public class ExpansionModel extends DemographicModel {
    //
    // Public stuff
    //
    public ExpansionModel(Parameter N0Parameter, Parameter N1Parameter,
                          Parameter growthRateParameter, Type units, boolean usingGrowthRate) {
        this(ExpansionModelParser.EXPANSION_MODEL, N0Parameter, N1Parameter, growthRateParameter, units, usingGrowthRate);
    }
    public ExpansionModel(String name, Parameter N0Parameter, Parameter N1Parameter,
                          Parameter growthRateParameter, Type units, boolean usingGrowthRate) {
        super(name);
        expansion = new Expansion(units);
        this.N0Parameter = N0Parameter;
        addVariable(N0Parameter);
        N0Parameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
        this.N1Parameter = N1Parameter;
        addVariable(N1Parameter);
        N1Parameter.addBounds(new Parameter.DefaultBounds(1.0, 0.0, 1));
        this.growthRateParameter = growthRateParameter;
        addVariable(growthRateParameter);
        growthRateParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
        this.usingGrowthRate = usingGrowthRate;
        setUnits(units);
    }
    // general functions
    public DemographicFunction getDemographicFunction() {
        double N0 = N0Parameter.getParameterValue(0);
        double N1 = N1Parameter.getParameterValue(0);
        double growthRate = growthRateParameter.getParameterValue(0);
        if (usingGrowthRate) {
            expansion.setGrowthRate(growthRate);
        } else {
            double doublingTime = growthRate;
            growthRate = Math.log(2) / doublingTime;
            expansion.setDoublingTime(doublingTime);
        }
        expansion.setN0(N0);
        expansion.setProportion(N1);
        return expansion;
    }
    //
    // protected stuff
    //
    Parameter N0Parameter = null;
    Parameter N1Parameter = null;
    Parameter growthRateParameter = null;
    Expansion expansion = null;
    boolean usingGrowthRate = true;
}
