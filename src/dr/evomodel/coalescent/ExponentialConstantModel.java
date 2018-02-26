package dr.evomodel.coalescent;
import dr.evolution.coalescent.DemographicFunction;
import dr.evolution.coalescent.ExpConstant;
import dr.evolution.coalescent.ExponentialLogistic;
import dr.evomodelxml.coalescent.ExponentialConstantModelParser;
import dr.evomodelxml.coalescent.ExponentialLogisticModelParser;
import dr.inference.model.Parameter;
public class ExponentialConstantModel extends DemographicModel {
    //
    // Public stuff
    //
    public ExponentialConstantModel(Parameter N0Parameter,
                                    Parameter growthRateParameter,
                                    Parameter transitionTimeParameter,
                                    Type units) {
        this(ExponentialConstantModelParser.EXPONENTIAL_CONSTANT_MODEL,
                N0Parameter,
                growthRateParameter,
                transitionTimeParameter,
                units);
    }
    public ExponentialConstantModel(String name, Parameter N0Parameter,
                                    Parameter growthRateParameter,
                                    Parameter transitionTimeParameter,
                                    Type units) {
        super(name);
        exponentialConstant = new ExpConstant(units);
        this.N0Parameter = N0Parameter;
        addVariable(N0Parameter);
        N0Parameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
        this.growthRateParameter = growthRateParameter;
        addVariable(growthRateParameter);
        growthRateParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
        this.transitionTimeParameter = transitionTimeParameter;
        addVariable(transitionTimeParameter);
        transitionTimeParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY, 1));
        setUnits(units);
    }
    // general functions
    public DemographicFunction getDemographicFunction() {
        exponentialConstant.setN0(N0Parameter.getParameterValue(0));
        exponentialConstant.setGrowthRate(growthRateParameter.getParameterValue(0));
        exponentialConstant.setTransitionTime(transitionTimeParameter.getParameterValue(0));
        return exponentialConstant;
    }
    //
    // protected stuff
    //
    Parameter N0Parameter = null;
    Parameter growthRateParameter = null;
    Parameter transitionTimeParameter = null;
    ExpConstant exponentialConstant = null;
}