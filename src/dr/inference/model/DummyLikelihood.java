
package dr.inference.model;

public class DummyLikelihood extends Likelihood.Abstract {

	public DummyLikelihood(Model model) {
		super(model);
	}

	// **************************************************************
    // Likelihood IMPLEMENTATION
    // **************************************************************

	protected boolean getLikelihoodKnown() {
		return false;
	}

	public double calculateLogLikelihood() {
		return 0.0;
	}

}

