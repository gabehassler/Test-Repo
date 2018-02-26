
package dr.app.beagle.evomodel.substmodel;

import dr.app.beagle.evomodel.parsers.FrequencyModelParser;
import dr.evolution.datatype.DataType;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FrequencyModel extends AbstractModel {

    public FrequencyModel(DataType dataType, double[] frequencyParameter) {
        this(dataType, new Parameter.Default(frequencyParameter));
    }

    public FrequencyModel(String name) {
        super(name);
    }

    public FrequencyModel(DataType dataType, Parameter frequencyParameter) {

        super(FrequencyModelParser.FREQUENCY_MODEL);

        double sum = getSumOfFrequencies(frequencyParameter);

        if (Math.abs(sum - 1.0) > 1e-8) {
            throw new IllegalArgumentException("Frequencies do not sum to 1, they sum to " + sum);
        }

        this.frequencyParameter = frequencyParameter;
        addVariable(frequencyParameter);
        frequencyParameter.addBounds(new Parameter.DefaultBounds(1.0, 0.0, frequencyParameter.getDimension()));
        this.dataType = dataType;
    }

    private double getSumOfFrequencies(Parameter frequencies) {
        double total = 0.0;
        for (int i = 0; i < frequencies.getDimension(); i++) {
            total += frequencies.getParameterValue(i);
        }
        return total;
    }

    public void setFrequency(int i, double value) {
        frequencyParameter.setParameterValue(i, value);
    }

    public double getFrequency(int i) {
        return frequencyParameter.getParameterValue(i);
    }

    public int getFrequencyCount() {
        return frequencyParameter.getDimension();
    }

    public Parameter getFrequencyParameter() {
        return frequencyParameter;
    }

    public double[] getFrequencies() {
        double[] frequencies = new double[getFrequencyCount()];
        for (int i = 0; i < frequencies.length; i++) {
            frequencies[i] = getFrequency(i);
        }
        return frequencies;
    }

    public double[] getCumulativeFrequencies() {
        double[] frequencies = getFrequencies();
        for (int i = 1; i < frequencies.length; i++) {
            frequencies[i] += frequencies[i - 1];
        }
        return frequencies;
    }

    public DataType getDataType() {
        return dataType;
    }

    // *****************************************************************
    // Interface Model
    // *****************************************************************

    protected void handleModelChangedEvent(Model model, Object object, int index) {
        // no intermediates need recalculating....
    }

    protected void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        // no intermediates need recalculating....
    }

    protected void storeState() {
    } // no state apart from parameters to store

    protected void restoreState() {
    } // no state apart from parameters to restore

    protected void acceptState() {
    } // no state apart from parameters to accept

    public Element createElement(Document doc) {
        throw new RuntimeException("Not implemented!");
    }

    private DataType dataType = null;
    Parameter frequencyParameter = null;

}