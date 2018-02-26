package dr.evomodel.substmodel;
import dr.evolution.datatype.DataType;
import dr.inference.model.Model;
public interface SubstitutionModel extends Model {
    void getTransitionProbabilities(double distance, double[] matrix);
    double[][] getEigenVectors();
    double[][] getInverseEigenVectors();
    double[] getEigenValues();
    FrequencyModel getFrequencyModel();
    DataType getDataType();
}
