package dr.evomodel.coalescent;
import dr.evolution.coalescent.DemographicFunction;
import dr.evolution.coalescent.ExponentialLogistic;
import dr.evomodelxml.coalescent.ExponentialLogisticModelParser;
import dr.inference.model.Parameter;
public class ExponentialLogisticModel extends DemographicModel {
    //
    // Public stuff
    //
    public ExponentialLogisticModel(Parameter N0Parameter,
                                    Parameter logisticGrowthParameter,
                                    Parameter logisticShapeParameter,
                                    Parameter exponentialGrowthParameter,
                                    Parameter transitionTimeParameter,
                                    double alpha, Type units) {
        this(ExponentialLogisticModelParser.EXPONENTIAL_LOGISTIC_MODEL,
                N0Parameter,
                logisticGrowthParameter,
                logisticShapeParameter,
                exponentialGrowthParameter,
                transitionTimeParameter,
                alpha, units);
    }
    public ExponentialLogisticModel(String name, Parameter N0Parameter,
                                    Parameter logisticGrowthParameter,
                                    Parameter logisticShapeParameter,
                                    Parameter exponentialGrowthParameter,
                                    Parameter transistionTimeParameter,
                                    double alpha, Type units) {
        super(name);
        exponentialLogistic = new ExponentialLogistic(units);
        this.N0Parameter = N0Parameter;
        addVariable(N0Parameter);
        N0Parameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
        this.logisticGrowthParameter = logisticGrowthParameter;
        addVariable(logisticGrowthParameter);
        logisticGrowthParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
        this.logisticShapeParameter = logisticShapeParameter;
        addVariable(logisticShapeParameter);
        logisticShapeParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
        this.exponentialGrowthParameter = exponentialGrowthParameter;
        addVariable(exponentialGrowthParameter);
        exponentialGrowthParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
        this.transistionTimeParameter = transistionTimeParameter;
        addVariable(transistionTimeParameter);
        transistionTimeParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
        this.alpha = alpha;
        setUnits(units);
    }
    // general functions
    public DemographicFunction getDemographicFunction() {
        exponentialLogistic.setN0(N0Parameter.getParameterValue(0));
        double r = logisticGrowthParameter.getParameterValue(0);
        exponentialLogistic.setGrowthRate(r);
        double r1 = exponentialGrowthParameter.getParameterValue(0);
        exponentialLogistic.setR1(r1);
        double t = transistionTimeParameter.getParameterValue(0);
        exponentialLogistic.setTime(t);
        // logisticGrowth.setShape(Math.exp(shapeParameter.getParameterValue(0)));
        exponentialLogistic.setTime50(logisticShapeParameter.getParameterValue(0));
        //exponentialLogistic.setShapeFromTimeAtAlpha(logisticShapeParameter.getParameterValue(0), alpha);
        return exponentialLogistic;
    }
    //
    // protected stuff
    //
    Parameter N0Parameter = null;
    Parameter logisticGrowthParameter = null;
    Parameter logisticShapeParameter = null;
    Parameter exponentialGrowthParameter = null;
    Parameter transistionTimeParameter = null;
    double alpha = 0.5;
    ExponentialLogistic exponentialLogistic = null;
}