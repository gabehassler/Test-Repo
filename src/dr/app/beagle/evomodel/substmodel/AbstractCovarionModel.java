package dr.app.beagle.evomodel.substmodel;
import dr.evolution.datatype.DataType;
import dr.inference.model.Parameter;
public abstract class AbstractCovarionModel extends BaseSubstitutionModel {
    AbstractCovarionModel(String name, DataType dataType, Parameter frequencies, Parameter hiddenFrequencies) {
        super(name, dataType, new CovarionFrequencyModel(dataType, frequencies, hiddenFrequencies));
    }
    @Override
    protected void setupRelativeRates(double[] rates) {
    }
    protected abstract double getNormalizationValue(double[][] matrix, double[] pi);
}
