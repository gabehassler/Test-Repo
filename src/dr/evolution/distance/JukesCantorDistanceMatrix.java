
package dr.evolution.distance;

import dr.evolution.alignment.PatternList;

public class JukesCantorDistanceMatrix extends DistanceMatrix
{
	//
	// Public stuff
	//
	
	public JukesCantorDistanceMatrix()
	{
		super();
	}

	public JukesCantorDistanceMatrix(PatternList patterns)
	{
		super(patterns);
	}

	public void setPatterns(PatternList patterns) {
		super.setPatterns(patterns);

        final int stateCount = patterns.getStateCount();

		const1 = ((double) stateCount - 1) / stateCount;
		const2 = ((double) stateCount) / (stateCount - 1) ;
	}
		
	protected double calculatePairwiseDistance(int i, int j) {
		final double obsDist = super.calculatePairwiseDistance(i, j);
		
		if (obsDist == 0.0) return 0.0;
	
		if (obsDist >= const1) {
			return MAX_DISTANCE;
		} 
        
		final double expDist = -const1 * Math.log(1.0 - (const2 * obsDist));

		if (expDist < MAX_DISTANCE) {
			return expDist;
		} else {
			return MAX_DISTANCE;
		}
	}
	
	//
	// Private stuff
	//

    //used in correction formula
	private double const1, const2;
}
