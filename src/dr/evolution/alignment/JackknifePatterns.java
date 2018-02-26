
package dr.evolution.alignment;

import dr.math.MathUtils;

public class JackknifePatterns extends ResamplePatterns
{

	public JackknifePatterns() {
	}

	public JackknifePatterns(SiteList patterns) {
		setPatterns(patterns);
	}

	public void resamplePatterns() {
	
		int siteCount = patterns.getSiteCount();
		int oldPatternCount = patterns.getPatternCount();
					
		int[] siteIndices = MathUtils.shuffled(siteCount);
		int n = siteCount / 2;
	
		patternIndices = new int[oldPatternCount];
		weights = new double[oldPatternCount];
				
		int pattern;
		
		patternCount = 0;
		
		for (int i = 0; i < n; i++) {

			pattern = patterns.getPatternIndex(siteIndices[i]);
			
			int j = 0;
			for (j = 0; j < patternCount; j++) {
				if (patternIndices[j] == pattern) {
					break;
				}
			} 
			
			if (j < patternCount) {
				weights[j] += 1.0;
			} else {			
				patternIndices[j] = pattern;
				weights[j] = 1.0;
				patternCount++;
			}
		}

	}

}
