
package dr.evomodel.substmodel;

import dr.evomodelxml.substmodel.TN93Parser;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;

public class TN93 extends AbstractNucleotideModel {

    private Variable<Double> kappa1Variable = null;
    private Variable<Double> kappa2Variable = null;

    private boolean updateIntermediates = true;


    private double p1a;
    private double p0a;
    private double p3b;
    private double p2b;
    private double a;
    private double b;
    private double p1aa;
    private double p0aa;
    private double p3bb;
    private double p2bb;
    private double p1aIsa;
    private double p0aIsa;
    private double p3bIsb;
    private double p2bIsb;
    private double k1g;
    private double k1a;
    private double k2t;
    private double k2c;
    private double subrateScale;

    public TN93(Variable kappa1Variable, Variable kappa2Variable, FrequencyModel freqModel) {

        super(TN93Parser.TN93_MODEL, freqModel);
        this.kappa1Variable = kappa1Variable;
        this.kappa2Variable = kappa2Variable;
        addVariable(kappa1Variable);
        addVariable(kappa2Variable);
        kappa1Variable.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
        kappa2Variable.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
        updateIntermediates = true;
    }

    public final double getKappa1() {
        return kappa1Variable.getValue(0);
    }

    public final double getKappa2() {
        return kappa2Variable.getValue(0);
    }


    protected void frequenciesChanged() {
        // frequencyModel changed
        updateIntermediates = true;
    }

    // I am not sure how HKY works without this
    // Comment this function out to get bug 138
    protected void ratesChanged() {
        // frequencyModel changed
        updateIntermediates = true;
    }

    private void calculateIntermediates() {

        calculateFreqRY();

        double k1 = getKappa1();
        double k2 = getKappa2();

//        System.out.println(getModelName() + " Using " + k1 + " " + k2);
        // A hack until I get right this boundary case. gives results accurate to 1e-8 in the P matrix
        // so should be OK even like this.
        if (k1 == 1) {
            k1 += 1E-10;
        }
        if (k2 == 1) {
            k2 += 1e-10;
        }

        double l1 = k1 * k1 * freqR + k1 * (2 * freqY - 1) - freqY;
        double l2 = k2 * k2 * freqY + k2 * (2 * freqR - 1) - freqR;

        p1a = freqG * l1;
        p0a = freqA * l1;
        p3b = freqT * l2;
        p2b = freqC * l2;

        a = -(k1 * freqR + freqY);
        b = -(k2 * freqY + freqR);

        p1aa = p1a / a;
        p0aa = p0a / a;
        p3bb = p3b / b;
        p2bb = p2b / b;

        p1aIsa = p1a / (1 + a);
        p0aIsa = p0a / (1 + a);
        p3bIsb = p3b / (1 + b);
        p2bIsb = p2b / (1 + b);

        k1g = k1 * freqG;
        k1a = k1 * freqA;
        k2t = k2 * freqT;
        k2c = k2 * freqC;

        subrateScale = 2 * (k1 * freqA * freqG + k2 * freqC * freqT + freqR * freqY);
        // updateMatrix = true;
        updateIntermediates = false;
    }

    public void getTransitionProbabilities(double distance, double[] matrix) {
        synchronized (this) {
            if (updateIntermediates) {
                calculateIntermediates();
            }
        }

        distance /= subrateScale;

        double[] q = {
                0, k1g, freqC, freqT,
                k1a, 0, freqC, freqT,
                freqA, freqG, 0, k2t,
                freqA, freqG, k2c, 0
        };

        q[0] = -(q[1] + q[2] + q[3]);
        q[5] = -(q[4] + q[6] + q[7]);
        q[10] = -(q[8] + q[9] + q[11]);
        q[15] = -(q[12] + q[13] + q[14]);

        double[] fa0 = {
                1 + q[0] - p1aa, q[1] + p1aa, q[2], q[3],
                q[4] + p0aa, 1 + q[5] - p0aa, q[6], q[7],
                q[8], q[9], 1 + q[10] - p3bb, q[11] + p3bb,
                q[12], q[13], q[14] + p2bb, 1 + q[15] - p2bb
        };


        double[] fa1 = {
                -q[0] + p1aIsa, -q[1] - p1aIsa, -q[2], -q[3],
                -q[4] - p0aIsa, -q[5] + p0aIsa, -q[6], -q[7],
                -q[8], -q[9], -q[10] + p3bIsb, -q[11] - p3bIsb,
                -q[12], -q[13], -q[14] - p2bIsb, -q[15] + p2bIsb};

        double et = Math.exp(-distance);

        for (int k = 0; k < 16; ++k) {
            fa1[k] = fa1[k] * et + fa0[k];
        }

        final double eta = Math.exp(distance * a);
        final double etb = Math.exp(distance * b);

        double za = eta / (a * (1 + a));
        double zb = etb / (b * (1 + b));
        double u0 = p1a * za;
        double u1 = p0a * za;
        double u2 = p3b * zb;
        double u3 = p2b * zb;

        fa1[0] += u0;
        fa1[1] -= u0;
        fa1[4] -= u1;
        fa1[5] += u1;

        fa1[10] += u2;
        fa1[11] -= u2;
        fa1[14] -= u3;
        fa1[15] += u3;

        // transpose 2 middle rows and columns
        matrix[0] = fa1[0];
        matrix[1] = fa1[2];
        matrix[2] = fa1[1];
        matrix[3] = fa1[3];
        matrix[4] = fa1[8];
        matrix[5] = fa1[10];
        matrix[6] = fa1[9];
        matrix[7] = fa1[11];
        matrix[8] = fa1[4];
        matrix[9] = fa1[6];
        matrix[10] = fa1[5];
        matrix[11] = fa1[7];
        matrix[12] = fa1[12];
        matrix[13] = fa1[14];
        matrix[14] = fa1[13];
        matrix[15] = fa1[15];

        //System.arraycopy(fa1, 0, matrix, 0, 16);
    }

    public void setupMatrix() {
    }

    protected void setupRelativeRates() {
    }

    // untested
//     public double[] getEigenValues() {
//        final double k1 = getKappa1();
//        final double k2 = getKappa2();
//        calculateFreqRY();
//
//        return new double[]{0, -1, -(k1*freqR + freqY), -(k2*freqY + freqR)};
//     }
//
//     public double[][] getEigenVectors() {
//        calculateFreqRY();
//
//         final double[][] emat = {
//                 {1., 1, 1., 1},
//                 {1, 1, -freqR / freqY, -freqR / freqY},
//                 {1, -freqA / freqC, 0, 0},
//                 {0, 0, 1, -freqG / freqT}};
//
//         // make them norm 1
//         for(int k = 0; k < 4; ++k) {
//             double s = 0;
//             for(int i = 0; i < 4; ++i) {
//                s += emat[k][i];
//             }
//             s = 1.0/Math.sqrt(s);
//             for(int i = 0; i < 4; ++i) {
//                emat[k][i] *= s;
//             }
//         }
//         return emat;
//     }

    // *****************************************************************
    // Interface Model
    // *****************************************************************

    public void restoreState() {
        super.restoreState();
        updateIntermediates = true;
    }

    // **************************************************************
    // XHTMLable IMPLEMENTATION
    // **************************************************************

    public String toXHTML() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("<em>TN93 Model</em>");
        buffer.append(" (kappa = ");
        buffer.append(getKappa1()).append(",").append(getKappa2());
        buffer.append(")");

        return buffer.toString();
    }
}