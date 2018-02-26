package dr.evolution.alignment;
import dr.evolution.datatype.*;
import dr.evolution.util.Taxon;
import dr.evolution.util.TaxonList;
import java.util.List;
import java.util.Iterator;
public class SimpleSiteList implements SiteList {
	private final TaxonList taxonList;
	private final DataType dataType;
    private int siteCount = 0;
    private int[][] sitePatterns = new int[0][];
    public SimpleSiteList(DataType dataType) {
        this.taxonList = null;
        this.dataType = dataType;
    }
	public SimpleSiteList(DataType dataType, TaxonList taxonList) {
		this.taxonList = taxonList;
        this.dataType = dataType;
	}
    public int addPattern(int[] pattern) {
        int capacity = sitePatterns.length;
        if (siteCount >= capacity) {
            capacity += 10000;
            int[][] newSitePatterns = new int[capacity][];
            for (int i = 0; i < siteCount; i++) {
                newSitePatterns[i] = sitePatterns[i];
            }
            sitePatterns = newSitePatterns;
        }
        sitePatterns[siteCount] = pattern;
        siteCount++;
        return siteCount - 1;
    }
	// **************************************************************
	// SiteList IMPLEMENTATION
	// **************************************************************
	public int getSiteCount() {
		return siteCount;
	}
	public int[] getSitePattern(int siteIndex) {
		return sitePatterns[siteIndex];
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
		if (taxonList == null) throw new RuntimeException("SimpleSiteList has no taxonList");
		return taxonList.getTaxonCount();
	}
	public Taxon getTaxon(int taxonIndex) {
		if (taxonList == null) throw new RuntimeException("SimpleSiteList has no taxonList");
		return taxonList.getTaxon(taxonIndex);
	}
	public String getTaxonId(int taxonIndex) {
		if (taxonList == null) throw new RuntimeException("SimpleSiteList has no taxonList");
		return taxonList.getTaxonId(taxonIndex);
	}
	public int getTaxonIndex(String id) {
		if (taxonList == null) throw new RuntimeException("SimpleSiteList has no taxonList");
		return taxonList.getTaxonIndex(id);
	}
	public int getTaxonIndex(Taxon taxon) {
		if (taxonList == null) throw new RuntimeException("SimpleSiteList has no taxonList");
		return taxonList.getTaxonIndex(taxon);
	}
    public List<Taxon> asList() {
        if (taxonList == null) throw new RuntimeException("SimpleSiteList has no taxonList");
        return taxonList.asList();
    }
    public Iterator<Taxon> iterator() {
        if (taxonList == null) throw new RuntimeException("SimpleSiteList has no taxonList");
        return taxonList.iterator();
    }
	public Object getTaxonAttribute(int taxonIndex, String name) {
		if (taxonList == null) throw new RuntimeException("SimpleSiteList has no taxonList");
		return taxonList.getTaxonAttribute(taxonIndex, name);
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
