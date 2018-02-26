
package dr.app.beagle.evomodel.substmodel;

import dr.evolution.datatype.DataType;
import dr.inference.loggers.LogColumn;
import dr.inference.loggers.NumberColumn;
import dr.inference.model.BayesianStochasticSearchVariableSelection;
import dr.inference.model.Likelihood;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.math.matrixAlgebra.Vector;

import java.util.Arrays;

public class ComplexSubstitutionModel extends GeneralSubstitutionModel implements Likelihood {

    public ComplexSubstitutionModel(String name, DataType dataType, FrequencyModel freqModel, Parameter parameter) {
        super(name, dataType, freqModel, parameter, -1);
        probability = new double[stateCount * stateCount];
    }

    protected EigenSystem getDefaultEigenSystem(int stateCount) {
        return new ComplexColtEigenSystem(stateCount);
    }

    public void getTransitionProbabilities(double distance, double[] matrix) {
        double temp;

        EigenDecomposition eigen = getEigenDecomposition();

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

    protected int getRateCount(int stateCount) {
        return (stateCount - 1) * stateCount;
    }

    protected void setupRelativeRates(double[] rates) {
        for (int i = 0; i < rates.length; i++)
            rates[i] = ratesParameter.getParameterValue(i);
    }

    protected void setupQMatrix(double[] rates, double[] pi, double[][] matrix) {
        int i, j, k = 0;
        for (i = 0; i < stateCount; i++) {
            for (j = i + 1; j < stateCount; j++) {
                double thisRate = rates[k++];
                if (thisRate < 0.0) thisRate = 0.0;
                matrix[i][j] = thisRate * pi[j];
            }
        }
        // Copy lower triangle in column-order form (transposed)
        for (j = 0; j < stateCount; j++) {
            for (i = j + 1; i < stateCount; i++) {
                double thisRate = rates[k++];
                if (thisRate < 0.0) thisRate = 0.0;
                matrix[i][j] = thisRate * pi[j];
            }
        }
    }

    public boolean canReturnComplexDiagonalization() {
        return true;
    }

    protected double getNormalizationValue(double[][] matrix, double[] pi) {
        double norm = 1.0;
        if (doNormalization) {
            norm = super.getNormalizationValue(matrix, pi);
        }
//            return super.getNormalizationValue(matrix, pi);
//        } else {
//            return 1.0;
//        }
//        System.err.println("norm = " + doNormalization + " " + norm);
//        System.err.println(new Matrix(matrix));
        return norm;
    }

    public double getLogLikelihood() {
        if (BayesianStochasticSearchVariableSelection.Utils.connectedAndWellConditioned(probability, this))
            return 0;
        return Double.NEGATIVE_INFINITY;
    }

    public boolean evaluateEarly() {
        return true;
    }

    public String prettyName() {
        return Abstract.getPrettyName(this);
    }

    public void setNormalization(boolean doNormalization) {
        this.doNormalization = doNormalization;
    }

    public void makeDirty() {

    }

    public void printLastProbabilityMatrix() {
        getLogLikelihood();
        System.err.println((probability == null) ? "Null probability vector" : "Not null probability vector");
        if (probability == null) {
            boolean test = BayesianStochasticSearchVariableSelection.Utils.connectedAndWellConditioned(probability, this);
            System.err.println("BSSVS valid = " + test);
        }
        System.err.println(new Vector(probability));
    }


    @Override
    public boolean isUsed() {
        return super.isUsed() && isUsed;
    }

    public void setUsed() {
        isUsed = true;
    }

    private boolean isUsed = false;
    private double[] probability;

    public Model getModel() {
        return this;
    }

    public LogColumn[] getColumns() {
        return new LogColumn[]{
                new LikelihoodColumn(getId())
        };
    }

    protected class LikelihoodColumn extends NumberColumn {
        public LikelihoodColumn(String label) {
            super(label);
        }

        public double getDoubleValue() {
            return getLogLikelihood();
        }
    }

    void setDoNormalization(boolean normalize) {
        this.doNormalization = normalize;
    }

    private boolean doNormalization = true;
}
