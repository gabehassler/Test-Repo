package dr.app.beagle.evomodel.substmodel;
import java.io.Serializable;
public interface EigenSystem extends Serializable {
EigenDecomposition decomposeMatrix(double[][] matrix);
void computeExponential(EigenDecomposition ed, double time, double[] matrix);
double computeExponential(EigenDecomposition ed, double time, int i, int j);
}
