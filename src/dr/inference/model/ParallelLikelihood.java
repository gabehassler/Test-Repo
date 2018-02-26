
package dr.inference.model;


public interface ParallelLikelihood extends Likelihood {

	public boolean getLikelihoodKnown();

	public void setLikelihood(double likelihood);

}
