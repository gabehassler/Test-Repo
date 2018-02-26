package dr.app.beagle.evomodel.substmodel;
import dr.evolution.datatype.DataType;
public interface SubstitutionProcess {
    void getTransitionProbabilities(double distance, double[] matrix);
    EigenDecomposition getEigenDecomposition();
    FrequencyModel getFrequencyModel();
    public void getInfinitesimalMatrix(double[] matrix);
    DataType getDataType();
    boolean canReturnComplexDiagonalization();
}