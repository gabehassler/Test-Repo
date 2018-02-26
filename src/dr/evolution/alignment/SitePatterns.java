package dr.evolution.alignment;
import dr.evolution.datatype.AminoAcids;
import dr.evolution.datatype.Codons;
import dr.evolution.datatype.DataType;
import dr.evolution.datatype.Nucleotides;
import dr.evolution.util.Taxon;
import dr.evolution.util.TaxonList;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import java.util.*;
public class SitePatterns implements SiteList, dr.util.XHTMLable {
    protected SiteList siteList = null;
    protected int siteCount = 0;
    protected int patternCount = 0;
    protected int patternLength = 0;
    protected int[] sitePatternIndices;
    protected int invariantCount;
    protected double[] weights;
    protected int[][] patterns;
    protected int from, to, every;
    protected boolean strip = true;  // Strip out completely ambiguous sites
    protected boolean unique = true; // Compress into weighted list of unique patterns
    public SitePatterns(Alignment alignment) {
        this(alignment, null, 0, 0, 1);
    }
    public SitePatterns(Alignment alignment, TaxonList taxa) {
        this(alignment, taxa, 0, 0, 1);
    }
    public SitePatterns(Alignment alignment, int from, int to, int every) {
        this(alignment, null, from, to, every);
    }
//    /**
//     * Constructor for dnds
//     */
//    public SitePatterns(Alignment alignment, int from, int to, int every, boolean unique) {
//        this(alignment, null, from, to, every, unique);
//    }
    public SitePatterns(Alignment alignment, TaxonList taxa, int from, int to, int every) {
        this(alignment,taxa,from,to,every,true);
    }
    public SitePatterns(Alignment alignment, TaxonList taxa, int from, int to, int every, boolean strip) {
        this(alignment, taxa, from, to, every, strip, true);
    }
    public SitePatterns(Alignment alignment, TaxonList taxa, int from, int to, int every, boolean strip, boolean unique) {
        this(alignment, taxa, from, to, every, strip, unique, null);
    }
    public SitePatterns(Alignment alignment, TaxonList taxa, int from, int to, int every, boolean strip, boolean unique, int[] constantSiteCounts) {
        if (taxa != null) {
            SimpleAlignment a = new SimpleAlignment();
            for (int i = 0; i < alignment.getSequenceCount(); i++) {
                if (taxa.getTaxonIndex(alignment.getTaxonId(i)) != -1) {
                    a.addSequence(alignment.getSequence(i));
                }
            }
            alignment = a;
        }
        this.strip = strip;
        this.unique = unique;
        setPatterns(alignment, from, to, every, constantSiteCounts);
    }
    public SitePatterns(SiteList siteList) {
        this(siteList, -1, -1, 1, true, true);
    }
    public SitePatterns(SiteList siteList, int from, int to, int every) {
        this(siteList, from, to, every, true, true);
    }
    public SitePatterns(SiteList siteList, int from, int to, int every, boolean strip) {
        this(siteList, from, to, every, strip, true);
    }
    public SitePatterns(SiteList siteList, int from, int to, int every, boolean strip, boolean unique) {
        this.strip = strip;
        this.unique = unique;
        setPatterns(siteList, from, to, every, null);
    }
    public SitePatterns(SiteList siteList, boolean[] mask) {
        this(siteList, mask, true, true);
    }
    public SitePatterns(SiteList siteList, boolean[] mask, boolean strip) {
        this(siteList, mask, strip, true);
    }
    public SitePatterns(SiteList siteList, boolean[] mask, boolean strip, boolean unique) {
        this.strip = strip;
        this.unique = unique;
        setPatterns(siteList, mask);
    }
    public SiteList getSiteList() {
        return siteList;
    }
    public int getFrom() {
        return from;
    }
    public int getTo() {
        return to;
    }
    public int getEvery() {
        return every;
    }
    public void setPatterns(SiteList siteList, int from, int to, int every, int[] constantSiteCounts) {
        this.siteList = siteList;
        this.from = from;
        this.to = to;
        this.every = every;
        if (siteList == null) {
            return;
        }
        if (from <= -1)
            from = 0;
        if (to <= -1)
            to = siteList.getSiteCount() - 1;
        if (every <= 0)
            every = 1;
        siteCount = ((to - from) / every) + 1;
        patternCount = 0;
        patterns = new int[siteCount][];
        sitePatternIndices = new int[siteCount];
        weights = new double[siteCount];
        invariantCount = 0;
        if (constantSiteCounts != null) {
            if (constantSiteCounts.length != siteList.getStateCount()) {
                throw new IllegalArgumentException("Constant site count array length doesn't equal the number of states");
            }
            for (int i = 0; i < siteList.getStateCount(); i++) {
                int[] pattern = new int[siteList.getPatternLength()];
                for (int j = 0; j < siteList.getPatternLength(); j++) {
                    pattern[j] = i;
                }
                addPattern(pattern, constantSiteCounts[i]);
            }
        }
        int site = 0;
        for (int i = from; i <= to; i += every) {
            int[] pattern = siteList.getSitePattern(i);
            if (!strip || !isInvariant(pattern) ||
                    (!isGapped(pattern) &&
                            !isAmbiguous(pattern) &&
                            !isUnknown(pattern))) {
                sitePatternIndices[site] = addPattern(pattern);
            }  else {
                sitePatternIndices[site] = -1;
            }
            site++;
        }
    }
    public void setPatterns(SiteList siteList, boolean[] mask) {
        this.siteList = siteList;
        if (siteList == null) {
            return;
        }
        from = 0;
        to = siteList.getSiteCount() - 1;
        every = 1;
        siteCount = siteList.getSiteCount();
        patternCount = 0;
        patterns = new int[siteCount][];
        sitePatternIndices = new int[siteCount];
        weights = new double[siteCount];
        invariantCount = 0;
        int[] pattern;
        int site = 0;
        for (int i = from; i <= to; i += every) {
            pattern = siteList.getSitePattern(i);
            if (mask[i]) {
                if (!strip || !isInvariant(pattern) ||
                        (!isGapped(pattern) &&
                                !isAmbiguous(pattern) &&
                                !isUnknown(pattern))
                        ) {
                    sitePatternIndices[site] = addPattern(pattern);
                }  else {
                    sitePatternIndices[site] = -1;
                }
                site++;
            }
        }
    }
    private int addPattern(int[] pattern) {
        return addPattern(pattern, 1);
    }
    private int addPattern(int[] pattern, int weight) {
        for (int i = 0; i < patternCount; i++) {
            if (unique && comparePatterns(patterns[i], pattern)) {
                weights[i] += weight;
                return i;
            }
        }
        if (isInvariant(pattern)) {
            invariantCount += weight;
        }
        int index = patternCount;
        patterns[index] = pattern;
        weights[index] = weight;
        patternCount++;
        return index;
    }
    private boolean isGapped(int[] pattern) {
        int len = pattern.length;
        for (int i = 0; i < len; i++) {
            if (getDataType().isGapState(pattern[i])) {
                return true;
            }
        }
        return false;
    }
    private boolean isAmbiguous(int[] pattern) {
        int len = pattern.length;
        for (int i = 0; i < len; i++) {
            if (getDataType().isAmbiguousState(pattern[i])) {
                return true;
            }
        }
        return false;
    }
    private boolean isUnknown(int[] pattern) {
        int len = pattern.length;
        for (int i = 0; i < len; i++) {
            if (getDataType().isUnknownState(pattern[i])) {
                return true;
            }
        }
        return false;
    }
    private boolean isInvariant(int[] pattern) {
        int len = pattern.length;
        int state = pattern[0];
        for (int i = 1; i < len; i++) {
            if (pattern[i] != state) {
                return false;
            }
        }
        return true;
    }
    protected boolean comparePatterns(int[] pattern1, int[] pattern2) {
        int len = pattern1.length;
        for (int i = 0; i < len; i++) {
            if (pattern1[i] != pattern2[i]) {
                return false;
            }
        }
        return true;
    }
    public int getInvariantCount() {
        return invariantCount;
    }
    // **************************************************************
    // SiteList IMPLEMENTATION
    // **************************************************************
    public int getSiteCount() {
        return siteCount;
    }
    public int[] getSitePattern(int siteIndex) {
        final int sitePatternIndice = sitePatternIndices[siteIndex];
        return sitePatternIndice >= 0 ? patterns[sitePatternIndice] : null;
    }
    public int getPatternIndex(int siteIndex) {
        return sitePatternIndices[siteIndex];
    }
    public int getState(int taxonIndex, int siteIndex) {
        final int sitePatternIndice = sitePatternIndices[siteIndex];
        // is that right?
        return sitePatternIndice >= 0 ? patterns[sitePatternIndice][taxonIndex] : getDataType().getGapState();
    }
    // **************************************************************
    // PatternList IMPLEMENTATION
    // **************************************************************
    public int getPatternCount() {
        return patternCount;
    }
    public int getStateCount() {
        if (siteList == null) throw new RuntimeException("SitePatterns has no alignment");
        return siteList.getStateCount();
    }
    public int getPatternLength() {
        return getTaxonCount();
    }
    public int[] getPattern(int patternIndex) {
        return patterns[patternIndex];
    }
    public int getPatternState(int taxonIndex, int patternIndex) {
        return patterns[patternIndex][taxonIndex];
    }
    public double getPatternWeight(int patternIndex) {
        return weights[patternIndex];
    }
    public double[] getPatternWeights() {
        return weights;
    }
    public DataType getDataType() {
        if (siteList == null) throw new RuntimeException("SitePatterns has no alignment");
        return siteList.getDataType();
    }
    public double[] getStateFrequencies() {
        return PatternList.Utils.empiricalStateFrequencies(this);
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
    // **************************************************************
    // XHTMLable IMPLEMENTATION
    // **************************************************************
    public String toXHTML() {
        String xhtml = "<p><em>Pattern List</em>  pattern count = ";
        xhtml += getPatternCount();
        xhtml += "  invariant count = ";
        xhtml += getInvariantCount();
        xhtml += "</p>";
        xhtml += "<pre>";
        int count, state;
        int type = getDataType().getType();
        count = getPatternCount();
        int length, maxLength = 0;
        for (int i = 0; i < count; i++) {
            length = Integer.toString((int) getPatternWeight(i)).length();
            if (length > maxLength)
                maxLength = length;
        }
        for (int i = 0; i < count; i++) {
            length = Integer.toString(i + 1).length();
            for (int j = length; j < maxLength; j++)
                xhtml += " ";
            xhtml += Integer.toString(i + 1) + ": ";
            length = Integer.toString((int) getPatternWeight(i)).length();
            xhtml += Integer.toString((int) getPatternWeight(i));
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
