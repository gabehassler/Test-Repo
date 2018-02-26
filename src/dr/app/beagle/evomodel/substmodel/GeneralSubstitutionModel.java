
package dr.app.beagle.evomodel.substmodel;

import dr.evolution.datatype.DataType;
import dr.inference.model.Parameter;
import dr.inference.model.DuplicatedParameter;

public class GeneralSubstitutionModel extends BaseSubstitutionModel {

    protected int ratesRelativeTo;

    public GeneralSubstitutionModel(String name, DataType dataType, FrequencyModel freqModel,
                                    Parameter parameter, int relativeTo) {
        this(name, dataType, freqModel, parameter, relativeTo, null);

    }

    public GeneralSubstitutionModel(String name, DataType dataType, FrequencyModel freqModel,
                                    Parameter parameter, int relativeTo, EigenSystem eigenSystem) {

        super(name, dataType, freqModel, eigenSystem);

        ratesParameter = parameter;
        if (ratesParameter != null) {
            addVariable(ratesParameter);
            if (!(ratesParameter instanceof DuplicatedParameter))
                ratesParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0,
                        ratesParameter.getDimension()));
        }
        setRatesRelativeTo(relativeTo);
    }

    protected GeneralSubstitutionModel(String name, DataType dataType, FrequencyModel freqModel, int relativeTo) {

        super(name, dataType, freqModel,
                null);

        setRatesRelativeTo(relativeTo);
    }

    protected void frequenciesChanged() {
        // Nothing to precalculate
    }

    protected void ratesChanged() {
        // Nothing to precalculate
    }

    protected void setupRelativeRates(double[] rates) {
        for (int i = 0; i < rates.length; i++) {
            if (i == ratesRelativeTo) {
                rates[i] = 1.0;
            } else if (i < ratesRelativeTo) {
                rates[i] = ratesParameter.getParameterValue(i);
            } else {
                rates[i] = ratesParameter.getParameterValue(i - 1);
            }
        }
    }

    public void setRatesRelativeTo(int ratesRelativeTo) {
        this.ratesRelativeTo = ratesRelativeTo;
    }

    // *****************************************************************
    // Interface Model
    // *****************************************************************


    protected void storeState() {
    } // nothing to do

    protected void restoreState() {
        updateMatrix = true;
    }

    protected void acceptState() {
    } // nothing to do


    protected Parameter ratesParameter = null;
}