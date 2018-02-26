package dr.evomodel.speciation;


import dr.evolution.util.Units;
import dr.inference.model.Likelihood;








public class AlloppMSCoalescent extends Likelihood.Abstract implements Units {
    private final AlloppSpeciesNetworkModel asnetwork;
    private final AlloppSpeciesBindings apsp;
	
    
    
    public AlloppMSCoalescent(AlloppSpeciesBindings apspecies, AlloppSpeciesNetworkModel apspnetwork) {
        super(apspnetwork);
        apsp = apspecies;
        asnetwork = apspnetwork;
        
        asnetwork.addModelListener(this);
        apsp.addModelListeners(this);
    }

    
    
    @Override
	protected double calculateLogLikelihood() {
    	for (int i = 0; i < apsp.numberOfGeneTrees(); i++) {
    		if (!apsp.geneTreeFitsInNetwork(i, asnetwork)) {
    			return Double.NEGATIVE_INFINITY;
    		}
    	}
	    // grjtodo-oneday JH has compatible flags for efficiency. I'm checking
	    // every time.
    	
        double logl = 0;
        for(int i = 0; i < apsp.numberOfGeneTrees(); i++) {
            final double v = apsp.geneTreeLogLikelihood(i, asnetwork);
            assert ! Double.isNaN(v);
            logl += v;
        }
        return logl;
    }


	@Override
	protected boolean getLikelihoodKnown() {
		return false;
	}

    
	public Type getUnits() {
		return asnetwork.getUnits();
	}

	public void setUnits(Type units) {
		// TODO Auto-generated method stub
        // one day may allow units other than substitutions

	}


	
}
