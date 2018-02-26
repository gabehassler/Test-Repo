package dr.evolution.alignment;
import dr.evolution.datatype.AminoAcids;
import dr.evolution.datatype.Codons;
import dr.evolution.datatype.DataType;
import dr.evolution.datatype.Nucleotides;
import dr.evolution.util.Taxon;
import java.util.*;
public abstract class ResamplePatterns implements PatternList, dr.util.XHTMLable
{
	protected SiteList patterns = null;
	protected int patternCount = 0;
	protected double[] weights;
	protected int[] patternIndices;
	public void setPatterns(SiteList patterns) {
		this.patterns = patterns;
	}
	public abstract void resamplePatterns();
    // **************************************************************
    // PatternList IMPLEMENTATION
    // **************************************************************
	public int getPatternCount() {
		return patternCount;
	}
	public int getStateCount() {
		if (patterns == null) throw new RuntimeException("ResamplePatterns has no source patterns");
		return patterns.getStateCount();
	}
	public int getPatternLength() {
		if (patterns == null) throw new RuntimeException("ResamplePatterns has no source patterns");
		return patterns.getPatternLength();
	}
	public int[] getPattern(int patternIndex) {
		if (patterns == null) throw new RuntimeException("ResamplePatterns has no source patterns");
		return patterns.getPattern(patternIndices[patternIndex]);
	}
	public int getPatternState(int taxonIndex, int patternIndex) {
		return getPattern(patternIndex)[taxonIndex];
	}
	public double getPatternWeight(int patternIndex) {
		return weights[patternIndex];
	}
	public double[] getPatternWeights() {
		return weights;
	}
	public DataType getDataType() {
		if (patterns == null) throw new RuntimeException("ResamplePatterns has no source patterns");
		return patterns.getDataType();
	}
	public double[] getStateFrequencies() {
		return PatternList.Utils.empiricalStateFrequencies(this);
	}
   // **************************************************************
    // TaxonList IMPLEMENTATION
    // **************************************************************
	public int getTaxonCount() {
		if (patterns == null) throw new RuntimeException("ResamplePatterns has no source patterns");
		return patterns.getTaxonCount();
	}
	public Taxon getTaxon(int taxonIndex) {
		if (patterns == null) throw new RuntimeException("ResamplePatterns has no source patterns");
		return patterns.getTaxon(taxonIndex);
	}
	public String getTaxonId(int taxonIndex) {
		if (patterns == null) throw new RuntimeException("ResamplePatterns has no source patterns");
		return patterns.getTaxonId(taxonIndex);
	}
	public int getTaxonIndex(String id) {
		if (patterns == null) throw new RuntimeException("ResamplePatterns has no source patterns");
		return patterns.getTaxonIndex(id);
	}
	public int getTaxonIndex(Taxon taxon) {
		if (patterns == null) throw new RuntimeException("SitePatterns has no alignment");
		return patterns.getTaxonIndex(taxon);
	}
	public Object getTaxonAttribute(int taxonIndex, String name) {
		if (patterns == null) throw new RuntimeException("ResamplePatterns has no source patterns");
		return patterns.getTaxonAttribute(taxonIndex, name);
	}
    public List<Taxon> asList() {
        List<Taxon> taxa = new ArrayList<Taxon>();
        for (int i = 0, n = getTaxonCount(); i < n; i++) {
            taxa.add(getTaxon(i));
        }
        return taxa;
    }
    public Iterator<Taxon> iterator() {
        return new Iterator<Taxon>() {
            private int index = -1;
            public boolean hasNext() {
                return index < getTaxonCount() - 1;
            }
            public Taxon next() {
                index ++;
                return getTaxon(index);
            }
            public void remove() { /* do nothing */ }
        };
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
    // **************************************************************
    // XHTMLable IMPLEMENTATION
    // **************************************************************
	public String toXHTML() {
		String xhtml = "<p><em>Jackknife Pattern List</em>  pattern count = ";
		xhtml += getPatternCount();
		xhtml += "</p>";
		xhtml += "<pre>";
		int count, state;
		int type = getDataType().getType();
		count = getPatternCount();
		int length, maxLength = 0;
		for (int i = 0; i < count; i++) {
			length = Integer.toString((int)getPatternWeight(i)).length();
			if (length > maxLength)
				maxLength = length;
		}
		for (int i = 0; i < count; i++) {
			length = Integer.toString(i+1).length();
			for (int j = length; j < maxLength; j++)
				xhtml += " ";
			xhtml += Integer.toString(i+1) + ": ";
			length = Integer.toString((int)getPatternWeight(i)).length();
			xhtml += Integer.toString((int)getPatternWeight(i));
			for (int j = length; j <= maxLength; j++)
				xhtml += " ";
			for (int j = 0; j < getTaxonCount(); j++) {
				state = getPatternState(j, i);
				if (type == DataType.NUCLEOTIDES) {
					xhtml += Nucleotides.INSTANCE.getChar(state) + " ";
				} else if (type == DataType.CODONS) {
					xhtml += Codons.UNIVERSAL.getTriplet(state) + " ";
				} else {
					xhtml += AminoAcids.INSTANCE.getChar(state) + " ";
				}
			}
			xhtml += "\n";
		}
		xhtml += "</pre>";
		return xhtml;
	}
}
