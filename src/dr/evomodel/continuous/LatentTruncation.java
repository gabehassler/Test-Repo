package dr.evomodel.continuous;
import dr.math.distributions.Distribution;
public interface LatentTruncation {
    boolean validTraitForTip(int tip);
    double getNormalizationConstant(Distribution working);
    double getLogLikelihood();
    public abstract class Delegate {
        public double getNormalizationConstant(Distribution working) {
            if (!normalizationKnown) {
                normalizationConstant = computeNormalizationConstant(working);
                normalizationKnown = true;
            }
            return normalizationConstant;
        }
        public void setNormalizationKnown(boolean value) {
            normalizationKnown = value;
        }
        protected abstract double computeNormalizationConstant(Distribution working);
        private boolean normalizationKnown = false;
        private double normalizationConstant;
    }
}
