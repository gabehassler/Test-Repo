package dr.evomodel.substmodel;
import dr.evolution.datatype.DataType;
import dr.evomodelxml.substmodel.GeneralSubstitutionModelParser;
import dr.inference.model.DuplicatedParameter;
import dr.inference.model.Parameter;
public class GeneralSubstitutionModel extends AbstractSubstitutionModel implements dr.util.XHTMLable {
    protected int ratesRelativeTo;
    public GeneralSubstitutionModel(
            DataType dataType,
            FrequencyModel freqModel,
            Parameter parameter,
            int relativeTo) {
        super(GeneralSubstitutionModelParser.GENERAL_SUBSTITUTION_MODEL, dataType, freqModel);
        ratesParameter = parameter;
        if (ratesParameter != null) {
            addVariable(ratesParameter);
            if (!(ratesParameter instanceof DuplicatedParameter))
                ratesParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, ratesParameter.getDimension()));
        }
        setRatesRelativeTo(relativeTo);
    }
    protected GeneralSubstitutionModel(
            String name,
            DataType dataType,
            FrequencyModel freqModel,
            int relativeTo) {
        super(name, dataType, freqModel);
        setRatesRelativeTo(relativeTo);
    }
    protected void frequenciesChanged() {
        // Nothing to precalculate
    }
    protected void ratesChanged() {
        // Nothing to precalculate
    }
    protected void setupRelativeRates() {
        for (int i = 0; i < relativeRates.length; i++) {
            if (i == ratesRelativeTo) {
                relativeRates[i] = 1.0;
            } else if (i < ratesRelativeTo) {
                relativeRates[i] = ratesParameter.getParameterValue(i);
            } else {
                relativeRates[i] = ratesParameter.getParameterValue(i - 1);
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
    // **************************************************************
    // XHTMLable IMPLEMENTATION
    // **************************************************************
    public String toXHTML() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<em>General Model</em>");
        return buffer.toString();
    }
    protected Parameter ratesParameter = null;
}
