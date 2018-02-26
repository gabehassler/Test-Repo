
package dr.evomodel.coalescent;

import dr.evolution.coalescent.Coalescent;
import dr.evolution.coalescent.DemographicFunction;
import dr.evolution.tree.Tree;
import dr.evolution.util.TaxonList;
import dr.evolution.util.Units;
import dr.evomodelxml.coalescent.CoalescentLikelihoodParser;

import java.util.List;
import java.util.logging.Logger;


public final class CoalescentLikelihood extends AbstractCoalescentLikelihood implements Units {

	// PUBLIC STUFF
	public CoalescentLikelihood(Tree tree,
	                            TaxonList includeSubtree,
	                            List<TaxonList> excludeSubtrees,
	                            DemographicModel demoModel) throws Tree.MissingTaxonException {

		super(CoalescentLikelihoodParser.COALESCENT_LIKELIHOOD, tree, includeSubtree, excludeSubtrees);

		this.demoModel = demoModel;

		addModel(demoModel);
	}

    // **************************************************************
	// Likelihood IMPLEMENTATION
	// **************************************************************

	public double calculateLogLikelihood() {

		DemographicFunction demoFunction = demoModel.getDemographicFunction();

		//double lnL =  Coalescent.calculateLogLikelihood(getIntervals(), demoFunction);
        double lnL =  Coalescent.calculateLogLikelihood(getIntervals(), demoFunction, demoFunction.getThreshold());

		if (Double.isNaN(lnL) || Double.isInfinite(lnL)) {
			Logger.getLogger("warning").severe("CoalescentLikelihood is " + Double.toString(lnL));
		}

		return lnL;
	}

	// **************************************************************
	// Units IMPLEMENTATION
	// **************************************************************

	public final void setUnits(Type u)
	{
		demoModel.setUnits(u);
	}

	public final Type getUnits()
	{
		return demoModel.getUnits();
	}

	// ****************************************************************
	// Private and protected stuff
	// ****************************************************************

	private DemographicModel demoModel = null;
}