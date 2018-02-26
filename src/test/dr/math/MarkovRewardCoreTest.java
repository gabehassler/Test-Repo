
package test.dr.math;

import dr.inference.markovjumps.SericolaSeriesMarkovReward;

public class MarkovRewardCoreTest extends MathTestCase {

    private double[] Q3 = new double[]{
            -1, 1, 0,
            0.5, -1, 0.5,
            0, 1, -1
    };

    private double[] a3 = new double[]{0, 1, 2};

//    public void testMarkovCore3() {
//        SericolaSeriesMarkovReward core = new SericolaSeriesMarkovReward(Q3, a3, 3);
//        double[] W1 = core.computeCdf(1.0 - 1E-12, 1.0);
//        double[] P1 = core.computePdf(1.0 - 1E-12, 1.0);
//
//        double[] W2 = core.computeCdf(2.0 - 1E-12, 1.0);
//        double[] W3 = core.computeCdf(100 - 1E-12, 100);
//    }


    private double[] Q2 = new double[]{-3.1, 3.1, 1.1, -1.1};
    private double[] a2 = new double[]{0.0, 1.0};

    public void testMarkovCore2() {
        SericolaSeriesMarkovReward core = new SericolaSeriesMarkovReward(Q2, a2, 2);
//        double[] W1 = core.computeCdf(1.0 - 1E-12, 1.0);
//        double[] P1 = core.computePdf(1.0 - 1E-12, 1.0);

        for (double x = 0.1; x < 4.0; x += 0.1) {
            double[] P = core.computePdf(x, 4.0);
            System.err.println(x + ", " + P[3]);
        }

//        double[] W3 = core.computeCdf(10 - 1E-12, 10);
    }
}
