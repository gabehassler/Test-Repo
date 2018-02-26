
package dr.evomodel.coalescent;

import dr.evolution.coalescent.ConstExpConst;
import dr.evolution.coalescent.DemographicFunction;
import dr.evomodelxml.coalescent.ConstExpConstModelParser;
import dr.inference.model.Parameter;

public class ConstExpConstModel extends DemographicModel {

    //
    // Public stuff
    //

    public ConstExpConstModel(Parameter N0Parameter, Parameter N1Parameter, Parameter growthRateParameter, Parameter timeParameter,
                              Parameter epochParameter, boolean useNumericalIntegrator, Type units) {

        this(ConstExpConstModelParser.CONST_EXP_CONST_MODEL, N0Parameter, N1Parameter, growthRateParameter, timeParameter, epochParameter, useNumericalIntegrator, units);
    }

    public ConstExpConstModel(String name, Parameter N0Parameter, Parameter N1Parameter, Parameter growthRateParameter, Parameter timeParameter,
                              Parameter epochParameter, boolean useNumericalIntegrator, Type units) {

        super(name);

        if (N1Parameter != null && growthRateParameter != null) {
            throw new RuntimeException("Only one of N1 and growthRate can be specified");
        }

        constExpConst = new ConstExpConst(
                (N1Parameter != null ? ConstExpConst.Parameterization.ANCESTRAL_POPULATION_SIZE : ConstExpConst.Parameterization.GROWTH_RATE),
                useNumericalIntegrator, units);

        this.N0Parameter = N0Parameter;
        addVariable(N0Parameter);
        N0Parameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));

        this.N1Parameter = N1Parameter;
        this.growthRateParameter = growthRateParameter;

        if (N1Parameter != null) {
            addVariable(N1Parameter);
            N1Parameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
        } else {
            addVariable(growthRateParameter);
            growthRateParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
        }

        this.timeParameter = timeParameter;
        addVariable(timeParameter);
        timeParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));

        this.epochParameter = epochParameter;
        addVariable(epochParameter);
        epochParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));

        setUnits(units);
    }


    // general functions

    public DemographicFunction getDemographicFunction() {

        constExpConst.setEpochTime(epochParameter.getParameterValue(0));
        constExpConst.setN0(N0Parameter.getParameterValue(0));
        constExpConst.setTime1(timeParameter.getParameterValue(0));

        if (N1Parameter != null) {
            constExpConst.setN1(N1Parameter.getParameterValue(0));
        } else {
            constExpConst.setGrowthRate(growthRateParameter.getParameterValue(0));
        }

        return constExpConst;
    }

    //
    // protected stuff
    //

    private final Parameter N0Parameter;
    private final Parameter N1Parameter;
    private final Parameter growthRateParameter;
    private final Parameter timeParameter;
    private final Parameter epochParameter;
    private final ConstExpConst constExpConst;
}