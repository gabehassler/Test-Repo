package dr.evomodel.coalescent;
import dr.evolution.coalescent.DemographicFunction;
import dr.evolution.coalescent.ExpConstant;
import dr.evolution.coalescent.ExponentialExponential;
import dr.evolution.coalescent.PiecewiseExponentialPopulation;
import dr.evomodelxml.coalescent.ExponentialConstantModelParser;
import dr.evomodelxml.coalescent.ExponentialExponentialModelParser;
import dr.inference.model.Parameter;
public class ExponentialExponentialModel extends DemographicModel {
    //
    // Public stuff
    //
    public ExponentialExponentialModel(Parameter N0Parameter,
                                       Parameter growthRateParameter,
                                       Parameter ancestralGrowthRateParameter,
                                       Parameter transitionTimeParameter,
                                       Type units) {
        this(ExponentialExponentialModelParser.EXPONENTIAL_EXPONENTIAL_MODEL,
                N0Parameter,
                growthRateParameter,
                ancestralGrowthRateParameter,
                transitionTimeParameter,
                units);
    }
    public ExponentialExponentialModel(String name, Parameter N0Parameter,
                                       Parameter growthRateParameter,
                                       Parameter ancestralGrowthRateParameter,
                                       Parameter transitionTimeParameter,
                                       Type units) {
        super(name);
        exponentialExponential = new ExponentialExponential(units);
        this.N0Parameter = N0Parameter;
        addVariable(N0Parameter);
        N0Parameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
        this.growthRateParameter = growthRateParameter;
        addVariable(growthRateParameter);
        growthRateParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
        this.ancestralGrowthRateParameter = ancestralGrowthRateParameter;
        addVariable(ancestralGrowthRateParameter);
        ancestralGrowthRateParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
        this.transitionTimeParameter = transitionTimeParameter;
        addVariable(transitionTimeParameter);
        transitionTimeParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY, 1));
        setUnits(units);
    }
    // general functions
    public DemographicFunction getDemographicFunction() {
        exponentialExponential.setN0(N0Parameter.getParameterValue(0));
        exponentialExponential.setGrowthRate(growthRateParameter.getParameterValue(0));
        exponentialExponential.setAncestralGrowthRate(ancestralGrowthRateParameter.getParameterValue(0));
        exponentialExponential.setTransitionTime(transitionTimeParameter.getParameterValue(0));
        return exponentialExponential;
    }
    //
    // protected stuff
    //
    Parameter N0Parameter = null;
    Parameter growthRateParameter = null;
    Parameter ancestralGrowthRateParameter = null;
    Parameter transitionTimeParameter = null;
    ExponentialExponential exponentialExponential = null;
}