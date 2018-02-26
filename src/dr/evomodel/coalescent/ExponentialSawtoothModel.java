
package dr.evomodel.coalescent;

import dr.evolution.coalescent.DemographicFunction;
import dr.evolution.coalescent.ExponentialSawtooth;
import dr.evomodelxml.coalescent.ExponentialSawtoothModelParser;
import dr.inference.model.Parameter;

public class ExponentialSawtoothModel extends DemographicModel {

    //
    // Public stuff
    //
    public ExponentialSawtoothModel(Parameter N0Parameter, Parameter growthRateParameter, Parameter wavelengthParameter, Parameter offsetParameter, Type units) {

        this(ExponentialSawtoothModelParser.EXPONENTIAL_SAWTOOTH, N0Parameter, growthRateParameter, wavelengthParameter, offsetParameter, units);
    }

    public ExponentialSawtoothModel(String name, Parameter N0Parameter, Parameter growthRateParameter, Parameter wavelengthParameter, Parameter offsetParameter, Type units) {

        super(name);

        expSaw = new ExponentialSawtooth(units);

        this.N0Parameter = N0Parameter;
        addVariable(N0Parameter);
        N0Parameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.MIN_VALUE, 1));

        this.growthRateParameter = growthRateParameter;
        addVariable(growthRateParameter);
        growthRateParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, -Double.MAX_VALUE, 1));

        this.wavelengthParameter = wavelengthParameter;
        addVariable(wavelengthParameter);
        wavelengthParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.MIN_VALUE, 1));

        this.offsetParameter = offsetParameter;
        addVariable(offsetParameter);
        offsetParameter.addBounds(new Parameter.DefaultBounds(1.0, -1.0, 1));

        setUnits(units);
    }


    // general functions

    public DemographicFunction getDemographicFunction() {
        expSaw.setN0(N0Parameter.getParameterValue(0));
        expSaw.setGrowthRate(growthRateParameter.getParameterValue(0));
        expSaw.setWavelength(wavelengthParameter.getParameterValue(0));

        double offset = offsetParameter.getParameterValue(0);
        if (offset < 0.0) {
            offset += 1.0;
        }
        expSaw.setOffset(offset);

        return expSaw;
    }
    //
    // protected stuff
    //

    Parameter N0Parameter = null;
    Parameter growthRateParameter = null;
    Parameter wavelengthParameter = null;
    Parameter offsetParameter = null;
    ExponentialSawtooth expSaw = null;
}
