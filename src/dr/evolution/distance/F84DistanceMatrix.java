
package dr.evolution.distance;

import dr.evolution.alignment.PatternList;

public class F84DistanceMatrix extends DistanceMatrix
{
	//
	// Public stuff
	//
	
	public F84DistanceMatrix()
	{
		super();
	}

	public F84DistanceMatrix(PatternList patterns)
	{
		super(patterns);
	}

	public void setPatterns(PatternList patterns) {
		super.setPatterns(patterns);
		
		double[] freqs = patterns.getStateFrequencies();
		stateCount = patterns.getStateCount();
		if (stateCount != 4) {
			throw new IllegalArgumentException("F84DistanceMatrix must have nucleotide patterns");
		}

		double freqA = freqs[0];
		double freqC = freqs[1];
		double freqG = freqs[2];
		double freqT = freqs[3];
		
		double freqR = freqA + freqG;
		double freqY = freqC + freqT;
		
		constA =  ((freqA * freqG) / freqR) + ((freqC * freqT) / freqY);
		constB =  (freqA * freqG) + (freqC * freqT);
		constC =  (freqR * freqY);
	}
		
	protected double calculatePairwiseDistance(int taxon1, int taxon2) {
		int state1, state2;
		
		int n = patterns.getPatternCount();
		double weight, distance;
		double sumTs = 0.0;
		double sumTv = 0.0;
		double sumWeight = 0.0;
		
		int[] pattern;
		
		for (int i = 0; i < n; i++) {
			pattern = patterns.getPattern(i);
			
			state1 = pattern[taxon1];
			state2 = pattern[taxon2];
			
			weight = patterns.getPatternWeight(i);
			if (!dataType.isAmbiguousState(state1) && !dataType.isAmbiguousState(state2) && state1 != state2) {

				if ((state1 == 0 && state2 == 2) || (state1 == 2 && state2 == 0)) { 
					// it's a transition
					sumTs += weight;
				} else {
					// it's a transversion
					sumTv += weight;
				}
			}
			sumWeight += weight;
		}
		
		double P = sumTs / sumWeight;
		double Q = sumTv / sumWeight;
		
		double tmp1 = Math.log(1.0 - (P / (2.0 * constA)) - 
								(((constA - constB) * Q) / (2.0 * constA * constC)));
								
		double tmp2 = Math.log(1.0 - (Q / (2.0 * constC)));
		
		distance = -(2.0 * constA * tmp1) +
					(2.0 * (constA - constB - constC) * tmp2);
		
		if (distance < MAX_DISTANCE) {
			return distance;
		} else {
			return MAX_DISTANCE;
		}
	}
	
	//
	// Private stuff
	//
	
	private int stateCount;
	
	//used in correction formula
	private double constA, constB, constC;
	
}
