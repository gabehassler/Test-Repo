
package dr.evolution.alignment;

public interface SiteList extends PatternList {
	int getSiteCount();

	int[] getSitePattern(int siteIndex);

	int getPatternIndex(int siteIndex);
	
	int getState(int taxonIndex, int siteIndex);
}
