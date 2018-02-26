
package dr.evomodel.coalescent;

import dr.evolution.coalescent.ConstantPopulation;
import dr.evolution.coalescent.DemographicFunction;
import dr.evolution.coalescent.LinearGrowth;
import dr.evomodelxml.coalescent.ConstantPopulationModelParser;
import dr.evomodelxml.coalescent.LinearGrowthModelParser;
import dr.inference.model.Parameter;

public class LinearGrowthModel extends DemographicModel {
    //
    // Public stuff
    //
    public LinearGrowthModel(Parameter slopeParameter, Type units) {

        this(LinearGrowthModelParser.LINEAR_GROWTH_MODEL, slopeParameter, units);
    }

    public LinearGrowthModel(String name, Parameter slopeParameter, Type units) {

        super(name);

        linearGrowth = new LinearGrowth(units);

        this.slopeParameter = slopeParameter;
        addVariable(slopeParameter);
        slopeParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
        setUnits(units);
    }

    // general functions

    public DemographicFunction getDemographicFunction() {
        linearGrowth.setN0(slopeParameter.getParameterValue(0));
        return linearGrowth;
    }

    //
    // protected stuff
    //

    private Parameter slopeParameter;
    private LinearGrowth linearGrowth = null;
}
