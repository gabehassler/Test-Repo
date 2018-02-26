
package dr.evolution.alignment;

import dr.evolution.datatype.*;
import dr.evolution.util.Taxon;

import java.util.*;

public class PairedSitePatterns implements SiteList {

	private final SiteList siteList;

	private final PairedDataType dataType;

	public PairedSitePatterns(SiteList siteList) {
		this.siteList = siteList;
		dataType = new PairedDataType(siteList.getDataType());
	}

	// **************************************************************
	// SiteList IMPLEMENTATION
	// **************************************************************

	public int getSiteCount() {
		int n = siteList.getSiteCount();
		return ((n - 1) * n) / 2;
	}

	public int[] getSitePattern(int siteIndex) {
		int n = siteList.getSiteCount();
		int index1 = 0;
		int index2 = 0;
		int site = 0;
		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				if (site == siteIndex) {
					index1 = i;
					index2 = j;
					break;
				}
				site++;
			}
			if (site == siteIndex) {
				break;
			}
		}

		int[] pattern = new int[siteList.getTaxonCount()];

		int[] pattern1 = siteList.getSitePattern(index1);
		int[] pattern2 = siteList.getSitePattern(index2);

		for (int k = 0; k < pattern.length; k ++) {
			pattern[k] = dataType.getState(pattern1[k], pattern2[k]);
		}

		return pattern;
	}

	public int getPatternIndex(int siteIndex) {
		return siteIndex;
	}

	public int getState(int taxonIndex, int siteIndex) {
		return getSitePattern(siteIndex)[taxonIndex];
	}

	// **************************************************************
	// PatternList IMPLEMENTATION
	// **************************************************************

	public int getPatternCount() {
		return getSiteCount();
	}

	public int getStateCount() {
		return dataType.getStateCount();
	}

	public int getPatternLength() {
		return getTaxonCount();
	}

	public int[] getPattern(int patternIndex) {
		return getSitePattern(patternIndex);
	}

	public int getPatternState(int taxonIndex, int patternIndex) {
		return getSitePattern(patternIndex)[taxonIndex];
	}

	public double getPatternWeight(int patternIndex) {
		return 1.0;
	}

	public double[] getPatternWeights() {
		double[] weights = new double[getSiteCount()];
		for (int i = 0; i < getSiteCount(); i++) {
			weights[i] = 1.0;
		}
		return weights;
	}

	public DataType getDataType() {
		return dataType;
	}

	public double[] getStateFrequencies() {
		return Utils.empiricalStateFrequencies(this);
	}

	// **************************************************************
	// TaxonList IMPLEMENTATION
	// **************************************************************

	public int getTaxonCount() {
		if (siteList == null) throw new RuntimeException("SitePatterns has no alignment");
		return siteList.getTaxonCount();
	}

	public Taxon getTaxon(int taxonIndex) {
		if (siteList == null) throw new RuntimeException("SitePatterns has no alignment");
		return siteList.getTaxon(taxonIndex);
	}

	public String getTaxonId(int taxonIndex) {
		if (siteList == null) throw new RuntimeException("SitePatterns has no alignment");
		return siteList.getTaxonId(taxonIndex);
	}

	public int getTaxonIndex(String id) {
		if (siteList == null) throw new RuntimeException("SitePatterns has no alignment");
		return siteList.getTaxonIndex(id);
	}

	public int getTaxonIndex(Taxon taxon) {
		if (siteList == null) throw new RuntimeException("SitePatterns has no alignment");
		return siteList.getTaxonIndex(taxon);
	}

    public List<Taxon> asList() {
        if (siteList == null) throw new RuntimeException("SitePatterns has no alignment");
        return siteList.asList();
    }

    public Iterator<Taxon> iterator() {
        if (siteList == null) throw new RuntimeException("SitePatterns has no alignment");
        return siteList.iterator();
    }

	public Object getTaxonAttribute(int taxonIndex, String name) {
		if (siteList == null) throw new RuntimeException("SitePatterns has no alignment");
		return siteList.getTaxonAttribute(taxonIndex, name);
	}

	// **************************************************************
	// Identifiable IMPLEMENTATION
	// **************************************************************

	protected String id = null;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
