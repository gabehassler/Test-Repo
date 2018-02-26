
package dr.inference.distribution;

import dr.inference.model.Model;
import dr.math.distributions.MultivariateDistribution;


public interface ParametricMultivariateDistributionModel extends MultivariateDistribution, Model {
	
public double[] nextRandom();
	
}