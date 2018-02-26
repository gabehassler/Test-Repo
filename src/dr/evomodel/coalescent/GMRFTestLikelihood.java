
package dr.evomodel.coalescent;

import dr.evolution.tree.Tree;
import dr.inference.model.DesignMatrix;
import dr.inference.model.Parameter;
import no.uib.cipr.matrix.SymmTridiagMatrix;

public class GMRFTestLikelihood extends GMRFSkyrideLikelihood {

	public GMRFTestLikelihood(Tree tree, Parameter popParameter, Parameter precParameter, Parameter lambda, Parameter beta, DesignMatrix design) {
		super(tree, popParameter, null, precParameter, lambda, beta, design, false, true);
	}


	private Parameter intervalsParameter;
	private Parameter statsParameter;
	private Parameter betaParameter;

	public GMRFTestLikelihood(Parameter inPop, Parameter inPrec, Parameter inLambda, Parameter inBeta, DesignMatrix design, Parameter intervals, Parameter statParameter) {
		super();
		popSizeParameter = inPop;
		precisionParameter = inPrec;
		lambdaParameter = inLambda;
		intervalsParameter = intervals;
		statsParameter = statParameter;
		betaParameter = inBeta;
		fieldLength = popSizeParameter.getDimension();
		addVariable(popSizeParameter);
		addVariable(precisionParameter);
		addVariable(lambdaParameter);
		addVariable(intervalsParameter);
		addVariable(statsParameter);
		addVariable(betaParameter);

		setupGMRFWeights();
	}


	protected void storeState() {
		super.storeState();
		System.arraycopy(coalescentIntervals, 0, storedCoalescentIntervals, 0, coalescentIntervals.length);
		System.arraycopy(sufficientStatistics, 0, storedSufficientStatistics, 0, sufficientStatistics.length);
		storedWeightMatrix = weightMatrix.copy();
	}


	protected void restoreState() {
		super.restoreState();
		System.arraycopy(storedCoalescentIntervals, 0, coalescentIntervals, 0, storedCoalescentIntervals.length);
		System.arraycopy(storedSufficientStatistics, 0, sufficientStatistics, 0, storedSufficientStatistics.length);
		weightMatrix = storedWeightMatrix;

	}

	public double calculateLogLikelihood() {
		return 0;
	}

	protected void setupGMRFWeights() {

//        int index = 0;
//
//        double length = 0;
//        double weight = 0;
//        for (int i = 0; i < getIntervalCount(); i++) {
//            length += getInterval(i);
//            weight += getInterval(i) * getLineageCount(i) * (getLineageCount(i) - 1);
//            if (getIntervalType(i) == CoalescentEventType.COALESCENT) {
//                coalescentIntervals[index] = length;
//                sufficientStatistics[index] = weight / 2.0;
//                index++;
//                length = 0;
//                weight = 0;
//            }
//        }

		coalescentIntervals = intervalsParameter.getParameterValues();
		sufficientStatistics = statsParameter.getParameterValues();
		storedCoalescentIntervals = new double[coalescentIntervals.length];
		storedSufficientStatistics = new double[sufficientStatistics.length];

		//Set up the weight Matrix
		double[] offdiag = new double[fieldLength - 1];
		double[] diag = new double[fieldLength];

//        double precision = precisionParameter.getParameterValue(0);

		//First set up the offdiagonal entries;
		for (int i = 0; i < fieldLength - 1; i++) {
			offdiag[i] = -2.0 / (coalescentIntervals[i] + coalescentIntervals[i + 1]);
		}

		//Then set up the diagonal entries;
		for (int i = 1; i < fieldLength - 1; i++)
			diag[i] = -(offdiag[i] + offdiag[i - 1]);

		//Take care of the endpoints
		diag[0] = -offdiag[0];
		diag[fieldLength - 1] = -offdiag[fieldLength - 2];


		weightMatrix = new SymmTridiagMatrix(diag, offdiag);

	}

}
