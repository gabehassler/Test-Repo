
package test.dr.evomodel.substmodel;

import dr.evomodel.branchratemodel.LatentStateBranchRateModel;
import dr.inference.markovjumps.MarkovReward;
import dr.inference.markovjumps.SericolaSeriesMarkovReward;
import dr.inference.markovjumps.TwoStateOccupancyMarkovReward;
import dr.inference.model.Parameter;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.integration.TrapezoidIntegrator;
import test.dr.math.MathTestCase;


public class TwoStateOccupancyMarkovRewardsTest extends MathTestCase {

    private static final double tolerance = 10E-3;

    public void testTwoStateSericolaRewards1() {
        final double rate = 0.0015;
//        final double prop = 0.5;
        final double prop = 0.66666;

        final double branchLength = 2000.0;
        final boolean print = false;

//        MarkovReward markovReward = createMarkovReward(rate, prop);
        MarkovReward markovReward = createSericolaMarkovReward(rate, prop);

        run(markovReward, rate, prop, branchLength, print, 1000);
    }

    public void testTwoStateSericolaRewards2() {
        final double rate = 0.0015;
        final double prop = 0.5;
//        final double prop = 0.66666;
        final double branchLength = 1000.0;
        final boolean print = false;

        MarkovReward markovReward = createMarkovReward(rate, prop);
//        MarkovReward markovReward = createSericolaMarkovReward(rate, prop);

        run(markovReward, rate, prop, branchLength, print, 1000);
    }

    public void testLatentStateBranchRateModel() throws FunctionEvaluationException, MaxIterationsExceededException {

        LatentStateBranchRateModel model = new LatentStateBranchRateModel(
                new Parameter.Default(0.001), new Parameter.Default(0.5));

        TrapezoidIntegrator integator = new TrapezoidIntegrator();

        final double branchLength = 2000;
        double integral = integator.integrate(new LatentStateDensityFunction(model, branchLength), 0.0, 1.0);

        System.out.println("testLatentStateBeanchRateModel");
        System.out.println("Integral = " + integral);

        assertEquals(integral, 1.0, tolerance);
    }

    private void run(MarkovReward markovReward, double rate, double prop, double branchLength,
                     boolean print, int length) {
        DensityFunction densityFunction = new DensityFunction(markovReward, branchLength, rate, prop);

        final double step = branchLength / length;
        int i = 0;
        double sum = 0.0;
        double modeY = 0.0;
        double modeX = 0.0;

        for (double x = 0.0; x <= branchLength; x += step, ++i) {

            double density = 0;
            density = densityFunction.value(x);

            if (x == 0.0) {
                modeY = density;
            } else {
                if (density > modeY) {
                    modeY = density;
                    modeX = x;
                }
            }

            if (x == 0.0 || x == branchLength) {
                sum += density;
            } else {
                sum += 2.0 * density;
            }

            if (print) {
                System.out.println(i + "\t" + String.format("%3.2f", x) + "\t" + String.format("%5.3e", density));
            }
        }
        sum *= (branchLength / 2.0 / length);

        // TODO Normalization is missing in LatentBranchRateModel
        System.out.println("branchLength = " + branchLength);
        System.out.println("rate = " + rate);
        System.out.println("prop = " + prop);
        System.out.println("Integral = " + sum);
        System.out.println("Mode = " + String.format("%3.2e", modeY) + " at " + modeX);

        assertEquals(sum, 1.0, tolerance);

        TrapezoidIntegrator integrator = new TrapezoidIntegrator();
        double integral = 0.0;
        try {
            integral = integrator.integrate(new UnitDensityFunction(markovReward, branchLength, rate, prop), 0.0, 1.0);
        } catch (MaxIterationsExceededException e) {
            e.printStackTrace();
        } catch (FunctionEvaluationException e) {
            e.printStackTrace();
        }
        System.out.println("unt int = " + integral);
        assertEquals(integral, 1.0, tolerance);

        System.out.println("\n");
    }

    private class LatentStateDensityFunction implements UnivariateRealFunction {

        private final LatentStateBranchRateModel model;
        private final double branchLength;

        LatentStateDensityFunction(LatentStateBranchRateModel model, double branchLength) {
            this.model = model;
            this.branchLength = branchLength;
        }

        public double value(double prop) {
            return model.getBranchRewardDensity(prop, branchLength);
        }
    }

    private class DensityFunction implements UnivariateRealFunction {

        private final MarkovReward markovReward;
        private final double branchLength;
        private final double rate;
        private final double prop;

        DensityFunction(MarkovReward markovReward, double branchLength, double rate, double prop) {
            this.markovReward = markovReward;
            this.branchLength = branchLength;
            this.rate = rate;
            this.prop = prop;
        }

        @Override
        public double value(double v) { //throws FunctionEvaluationException {
            return markovReward.computePdf(v, branchLength, 0, 0) /
                    (markovReward.computeConditionalProbability(branchLength, 0, 0)
                            - Math.exp(-rate * prop * branchLength));
        }
    }

    private class UnitDensityFunction implements UnivariateRealFunction {

        private final MarkovReward markovReward;
        private final double branchLength;
        private final double rate;
        private final double prop;

        UnitDensityFunction(MarkovReward markovReward, double branchLength, double rate, double prop) {
            this.markovReward = markovReward;
            this.branchLength = branchLength;
            this.rate = rate;
            this.prop = prop;
        }

        @Override
        public double value(double v) { //throws FunctionEvaluationException {
            double density = markovReward.computePdf(v * branchLength, branchLength, 0, 0) /
                    (markovReward.computeConditionalProbability(branchLength, 0, 0)
                            - Math.exp(-rate * prop * branchLength));
            return density * branchLength;
        }
    }

    private double[] createLatentInfinitesimalMatrix(final double rate, final double prop) {
        double[] mat = new double[]{
                -rate * prop, rate * prop,
                rate * (1.0 - prop), -rate * (1.0 - prop)
        };
        return mat;
    }

    private SericolaSeriesMarkovReward createSericolaMarkovReward(final double rate, final double prop) {
        double[] r = new double[]{0.0, 1.0};
        return new SericolaSeriesMarkovReward(createLatentInfinitesimalMatrix(rate, prop), r, 2);
    }

    private TwoStateOccupancyMarkovReward createMarkovReward(final double rate, final double prop) {
        TwoStateOccupancyMarkovReward markovReward = new
                TwoStateOccupancyMarkovReward(
                createLatentInfinitesimalMatrix(rate, prop)
        );
        return markovReward;
    }
}
