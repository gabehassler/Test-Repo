package dr.math.distributions;
import dr.inference.model.Likelihood;
public interface GaussianProcessRandomGenerator extends RandomGenerator {
    // Only implemented by Gaussian processes
    Likelihood getLikelihood();
}