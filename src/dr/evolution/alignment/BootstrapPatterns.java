package dr.evolution.alignment;
import dr.math.MathUtils;
public class BootstrapPatterns extends ResamplePatterns {
	public BootstrapPatterns() {
	}
	public BootstrapPatterns(SiteList patterns) {
		setPatterns(patterns);
	}
	public void resamplePatterns() {
		int siteCount = patterns.getSiteCount();
		int oldPatternCount = patterns.getPatternCount();
		patternIndices = new int[oldPatternCount];
		weights = new double[oldPatternCount];
		int site, pattern;
		patternCount = 0;
		for (int i = 0; i < siteCount; i++) {
			site = MathUtils.nextInt(siteCount);
			pattern = patterns.getPatternIndex(site);
			int j = 0;
			for (j = 0; j < patternCount; j++) {
				if (patternIndices[j] == pattern) {
					break;
				}
			}
			if (j < patternCount) {
				weights[j] += 1.0;
			} else {
				patternIndices[patternCount] = pattern;
				weights[patternCount] = 1.0;
				patternCount++;
			}
		}
	}
}
