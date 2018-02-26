
package dr.inference.mcmc;

import dr.math.MathUtils;
import dr.inference.markovchain.Acceptor;

public class MCMCCriterion implements Acceptor {

    // this parameter is actually 1/T, when the temperature parameter is 0.0, then the distribution
    // is flat and will always accept (symmetric) proposals, i.e. hastings ratio of 0 in log space.
    // As this temperature parameter increases, the posterior gets more peaked.
    protected double temperature = 1.0;

    public MCMCCriterion() {

        temperature = 1.0;
    }

    public MCMCCriterion(double t) {
        temperature = t;
    }

    public double getAcceptanceValue(double oldScore, double hastingsRatio) {

        final double acceptanceValue =
                (MathUtils.randomLogDouble() + (oldScore * temperature) - hastingsRatio) / temperature;

        return acceptanceValue;
    }

    public boolean accept(double oldScore, double newScore, double hastingsRatio, double[] logr) {

        logr[0] = (newScore - oldScore) * temperature + hastingsRatio;

        // for coercedAcceptanceProbability
        if (logr[0] > 0) logr[0] = 0.0;

        final double v = MathUtils.randomLogDouble();
        final boolean accept = v < logr[0];

        return accept;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

}
