
package dr.evolution.alignment;

import dr.math.MathUtils;

public class ShuffledSiteList extends ResamplePatterns implements SiteList
{

	public ShuffledSiteList() {
	}

	public ShuffledSiteList(SiteList patterns) {
		setPatterns(patterns);
	}

    public void setPatterns(SiteList patterns) {
        this.patterns = patterns;
        resamplePatterns();
    }
	public void resamplePatterns() {
	
		int siteCount = patterns.getSiteCount();
        siteIndices = MathUtils.shuffled(siteCount);
	}

    public int getSiteCount() {
        return siteIndices.length;
    }

    public int[] getSitePattern(int siteIndex) {
        return patterns.getSitePattern(siteIndices[siteIndex]);
    }

    public int getPatternIndex(int siteIndex) {
        return patterns.getPatternIndex(siteIndices[siteIndex]);
    }

    public int getState(int taxonIndex, int siteIndex) {
        return patterns.getState(taxonIndex, siteIndices[siteIndex]);
    }

    int siteIndices[] = null;
}
