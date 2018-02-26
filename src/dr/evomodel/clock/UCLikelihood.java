package dr.evomodel.clock;
import dr.evomodel.tree.TreeModel;
import dr.inference.model.Parameter;
import dr.math.MathUtils;
import dr.math.distributions.NormalDistribution;
public class UCLikelihood extends RateEvolutionLikelihood {
    public UCLikelihood(TreeModel tree, Parameter ratesParameter, Parameter variance, Parameter rootRate, boolean isLogSpace) {
        super((isLogSpace) ? "LogNormally Distributed" : "Normally Distributed", tree, ratesParameter, rootRate, false);
        this.isLogSpace = isLogSpace;
        this.variance = variance;
        addVariable(variance);
    }
    double branchRateChangeLogLikelihood(double foo1, double rate, double foo2) {
        double var = variance.getParameterValue(0);
        double meanRate = rootRateParameter.getParameterValue(0);
        if (isLogSpace) {
            final double logmeanRate = Math.log(meanRate);
            final double logRate = Math.log(rate);
            return NormalDistribution.logPdf(logRate, logmeanRate - (var / 2.), Math.sqrt(var)) - logRate;
        } else {
            return NormalDistribution.logPdf(rate, meanRate, Math.sqrt(var));
        }
    }
    double branchRateSample(double foo1, double foo2) {
        double meanRate = rootRateParameter.getParameterValue(0);
        double var = variance.getParameterValue(0);
        if (isLogSpace) {
            final double logMeanRate = Math.log(meanRate);
            return Math.exp(MathUtils.nextGaussian() * Math.sqrt(var) + logMeanRate - (var / 2.));
        } else {
            return MathUtils.nextGaussian() * Math.sqrt(var) + meanRate;
        }
    }
    private final Parameter variance;
    boolean isLogSpace = false;
}
