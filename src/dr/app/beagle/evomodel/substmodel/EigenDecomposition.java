
package dr.app.beagle.evomodel.substmodel;

import java.io.Serializable;

public class EigenDecomposition implements Serializable {

    public EigenDecomposition(double[] evec, double[] ievc, double[] eval) {
        Evec = evec;
        Ievc = ievc;
        Eval = eval;
    }

    public EigenDecomposition copy() {
        double[] evec = Evec.clone();
        double[] ievc = Ievc.clone();
        double[] eval = Eval.clone();

        return new EigenDecomposition(evec, ievc, eval);
    }

    public final double[] getEigenVectors() {
        return Evec;
    }

    public final double[] getInverseEigenVectors() {
        return Ievc;
    }

    public final double[] getEigenValues() {
        return Eval;
    }

    public void normalizeEigenValues(double scale) {
        int dim = Eval.length;
        for (int i = 0; i < dim; i++)

            Eval[i] /= scale;
    }

    // Eigenvalues, eigenvectors, and inverse eigenvectors
    private final double[] Evec;
    private final double[] Ievc;
    private final double[] Eval;

}
