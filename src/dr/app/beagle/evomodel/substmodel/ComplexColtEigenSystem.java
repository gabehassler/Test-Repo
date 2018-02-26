package dr.app.beagle.evomodel.substmodel;
import cern.colt.matrix.DoubleMatrix2D;
import dr.math.matrixAlgebra.RobustEigenDecomposition;
import java.util.Arrays;
public class ComplexColtEigenSystem extends ColtEigenSystem {
    public ComplexColtEigenSystem(int stateCount) {
        super(stateCount);
    }
    public ComplexColtEigenSystem(int stateCount, boolean checkConditioning, int maxConditionNumber, int maxIterations) {
        super(stateCount, checkConditioning, maxConditionNumber, maxIterations);
    }
    protected double[] getAllEigenValues(RobustEigenDecomposition decomposition) {
        double[] realEval = decomposition.getRealEigenvalues().toArray();
        double[] imagEval = decomposition.getImagEigenvalues().toArray();
        final int dim = realEval.length;
        double[] merge = new double[2 * dim];
        System.arraycopy(realEval, 0, merge, 0, dim);
        System.arraycopy(imagEval, 0, merge, dim, dim);
        return merge;
    }
    protected double[] getEmptyAllEigenValues(int dim) {
        return new double[2 * dim];
    }
    protected boolean validDecomposition(DoubleMatrix2D eigenV) {
        return true;
    }
    public double computeExponential(EigenDecomposition eigen, double distance, int i, int j) {
        throw new RuntimeException("Not yet implemented");
    }
    public void computeExponential(EigenDecomposition eigen, double distance, double[] matrix) {
        double temp;
        if (eigen == null) {
            Arrays.fill(matrix, 0.0);
            return;
        }
        double[] Evec = eigen.getEigenVectors();
        double[] Eval = eigen.getEigenValues();
        double[] EvalImag = new double[stateCount];
        System.arraycopy(Eval, stateCount, EvalImag, 0, stateCount);
        double[] Ievc = eigen.getInverseEigenVectors();
        double[][] iexp = new double[stateCount][stateCount];
// Eigenvalues and eigenvectors of a real matrix A.
//
// If A is symmetric, then A = V*D*V' where the eigenvalue matrix D is diagonal
// and the eigenvector matrix V is orthogonal. I.e. A = V D V^t and V V^t equals
// the identity matrix.
//
// If A is not symmetric, then the eigenvalue matrix D is block diagonal with
// the real eigenvalues in 1-by-1 blocks and any complex eigenvalues,
// lambda + i*mu, in 2-by-2 blocks, [lambda, mu; -mu, lambda]. The columns
// of V represent the eigenvectors in the sense that A*V = V*D. The matrix
// V may be badly conditioned, or even singular, so the validity of the
// equation A = V D V^{-1} depends on the conditioning of V.
        for (int i = 0; i < stateCount; i++) {
            if (EvalImag[i] == 0) {
                // 1x1 block
                temp = Math.exp(distance * Eval[i]);
                for (int j = 0; j < stateCount; j++) {
                    iexp[i][j] = Ievc[i * stateCount + j] * temp;
                }
            } else {
                // 2x2 conjugate block
                // If A is 2x2 with complex conjugate pair eigenvalues a +/- bi, then
                // exp(At) = exp(at)*( cos(bt)I + \frac{sin(bt)}{b}(A - aI)).
                int i2 = i + 1;
                double b = EvalImag[i];
                double expat = Math.exp(distance * Eval[i]);
                double expatcosbt = expat * Math.cos(distance * b);
                double expatsinbt = expat * Math.sin(distance * b);
                for (int j = 0; j < stateCount; j++) {
                    iexp[i][j] = expatcosbt * Ievc[i * stateCount + j] +
                            expatsinbt * Ievc[i2 * stateCount + j];
                    iexp[i2][j] = expatcosbt * Ievc[i2 * stateCount + j] -
                            expatsinbt * Ievc[i * stateCount + j];
                }
                i++; // processed two conjugate rows
            }
        }
        int u = 0;
        for (int i = 0; i < stateCount; i++) {
            for (int j = 0; j < stateCount; j++) {
                temp = 0.0;
                for (int k = 0; k < stateCount; k++) {
                    temp += Evec[i * stateCount + k] * iexp[k][j];
                }
                matrix[u] = Math.abs(temp);
                u++;
            }
        }
    }
}
