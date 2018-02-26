
package dr.evomodel.coalescent;

import dr.evolution.coalescent.ConstantPopulation;
import dr.evolution.tree.Tree;
import dr.evolution.util.Units;
import dr.evomodel.tree.TreeModel;
import dr.evomodelxml.coalescent.CoalescentLikelihoodParser;
import dr.inference.model.Likelihood;
import dr.inference.model.Parameter;
import dr.xml.*;

public class SkylineLikelihood extends OldAbstractCoalescentLikelihood {

	// PUBLIC STUFF

	public static final String SKYLINE_LIKELIHOOD = "skyLineLikelihood";
	public static final String POPULATION_SIZES = "populationSizes";

	public SkylineLikelihood(Tree tree, Parameter popSizeParameter) {
		super(SKYLINE_LIKELIHOOD);

		this.popSizeParameter = popSizeParameter;
		int tips = tree.getExternalNodeCount();
		int params = popSizeParameter.getDimension();
		if (tips - params != 1) {
			throw new IllegalArgumentException("Number of tips (" + tips + ") must be one greater than number of pop sizes (" + params + ")");
		}

		this.tree = tree;
		if (tree instanceof TreeModel) {
			addModel((TreeModel)tree);
		}
		addVariable(popSizeParameter);
		setupIntervals();

		addStatistic(new DeltaStatistic());
	}

	// **************************************************************
    // Likelihood IMPLEMENTATION
    // **************************************************************

	public double calculateLogLikelihood() {

		if (!intervalsKnown) setupIntervals();

		double logL = 0.0;

		double currentTime = 0.0;

		int popIndex=0;

		ConstantPopulation cp = new ConstantPopulation(Units.Type.YEARS);

		for (int j = 0; j < intervalCount; j++) {

			cp.setN0(popSizeParameter.getParameterValue(popIndex));
			if (getIntervalType(j) == CoalescentEventType.COALESCENT) {
				popIndex += 1;
			}

			logL += calculateIntervalLikelihood(cp, intervals[j], currentTime, lineageCounts[j], getIntervalType(j));

			// insert zero-length coalescent intervals
			int diff = getCoalescentEvents(j)-1;
			for (int k = 0; k < diff; k++) {
				cp.setN0(popSizeParameter.getParameterValue(popIndex));
				logL += calculateIntervalLikelihood(cp, 0.0, currentTime, lineageCounts[j]-k-1,
                        CoalescentEventType.COALESCENT);
				popIndex += 1;
			}

			currentTime += intervals[j];


		}

		return logL;
	}

	// ****************************************************************
	// Private and protected stuff
	// ****************************************************************

	public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {

		public String getParserName() { return SKYLINE_LIKELIHOOD; }

		public Object parseXMLObject(XMLObject xo) throws XMLParseException {

			XMLObject cxo = (XMLObject)xo.getChild(POPULATION_SIZES);
			Parameter param = (Parameter)cxo.getChild(Parameter.class);

			cxo = (XMLObject)xo.getChild(CoalescentLikelihoodParser.POPULATION_TREE);
			TreeModel treeModel = (TreeModel)cxo.getChild(TreeModel.class);

			return new SkylineLikelihood(treeModel, param);
		}

		//************************************************************************
		// AbstractXMLObjectParser implementation
		//************************************************************************

		public String getParserDescription() {
			return "This element represents the likelihood of the tree given the population size vector.";
		}

		public Class getReturnType() { return Likelihood.class; }

		public XMLSyntaxRule[] getSyntaxRules() { return rules; }

		private XMLSyntaxRule[] rules = new XMLSyntaxRule[] {
			new ElementRule(POPULATION_SIZES, new XMLSyntaxRule[] {
				new ElementRule(Parameter.class)
			}),
			new ElementRule(CoalescentLikelihoodParser.POPULATION_TREE, new XMLSyntaxRule[] {
				new ElementRule(TreeModel.class)
			}),
		};
	};

	Parameter popSizeParameter = null;
}