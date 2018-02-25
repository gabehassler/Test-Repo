package dr.evolution.alignment;
import dr.evolution.datatype.DataType;
import dr.evolution.util.Taxon;
import dr.evolution.util.TaxonList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
public class Patterns implements PatternList {
public static final int COUNT_INCREMENT = 100;
protected int patternCount = 0;
protected int patternLength = 0;
protected double[] weights = new double[COUNT_INCREMENT];
protected int[][] patterns = new int[COUNT_INCREMENT][];
protected DataType dataType = null;
protected TaxonList taxonList = null;
public Patterns(DataType dataType) {
this.dataType = dataType;
}
public Patterns(DataType dataType, TaxonList taxonList) {
this.dataType = dataType;
this.taxonList = taxonList;
patternLength = taxonList.getTaxonCount();
}
public Patterns(SiteList siteList) {
addPatterns(siteList, 0, 0, 1);
}
public Patterns(List<SiteList> siteLists) {
for (SiteList siteList : siteLists) {
addPatterns(siteList, 0, 0, 1);
}
}
public Patterns(SiteList siteList, int from, int to, int every) {
addPatterns(siteList, from, to, every);
}
public Patterns(SiteList siteList, int from, int to, int every, int subSet, int subSetCount) {
addPatterns(siteList, from, to, every);
subSetPatterns(subSet, subSetCount);
}
public Patterns(PatternList patternList) {
addPatterns(patternList);
}
public Patterns(PatternList patternList, int subSet, int subSetCount) {
addPatterns(patternList);
subSetPatterns(subSet, subSetCount);
}
private void subSetPatterns(int subSet, int subSetCount) {
if (subSetCount > 0) {
// if we are using subSetCount then cut it down to only the subset we want...
int div = patternCount / subSetCount;
int rem = patternCount % subSetCount;
int start = 0;
for (int i = 0; i < subSet; i++) {
start += div + (i < rem ? 1 : 0);
}
int newPatternCount = div;
if (subSet < rem) {
newPatternCount++;
}
int[][] newPatterns = new int[newPatternCount][];
double[] newWeights = new double[newPatternCount];
for (int i = 0; i < newPatternCount; i++) {
newPatterns[i] = patterns[start + i];
newWeights[i] = weights[start + i];
}
patterns = newPatterns;
weights = newWeights;
patternCount = newPatternCount;
}
}
public void addPatterns(SiteList siteList, int from, int to, int every) {
if (siteList == null) {
return;
}
if (taxonList == null) {
taxonList = siteList;
patternLength = taxonList.getTaxonCount();
}
if (dataType == null) {
dataType = siteList.getDataType();
} else if (dataType != siteList.getDataType()) {
throw new IllegalArgumentException("Patterns' existing DataType does not match that of added SiteList");
}
if (from < 0)
from = 0;
if (to <= 0)
to = siteList.getSiteCount() - 1;
if (every <= 0)
every = 1;
for (int i = from; i <= to; i += every) {
int[] pattern = siteList.getSitePattern(i);
// don't add patterns that are all gaps or all ambiguous
if (pattern != null && (!isInvariant(pattern) ||
(!isGapped(pattern) &&
!isAmbiguous(pattern) &&
!isUnknown(pattern)))) {
addPattern(pattern, 1.0);
}
}
}
public void addPatterns(PatternList patternList) {
if (patternList == null) {
return;
}
if (taxonList == null) {
taxonList = patternList;
patternLength = taxonList.getTaxonCount();
}
if (dataType == null) {
dataType = patternList.getDataType();
} else if (dataType != patternList.getDataType()) {
throw new IllegalArgumentException("Patterns' existing DataType does not match that of added PatternList");
}
for (int i = 0; i < patternList.getPatternCount(); i++) {
int[] pattern = patternList.getPattern(i);
// don't add patterns that are all gaps or all ambiguous
if (!isInvariant(pattern) ||
(!isGapped(pattern) &&
!isAmbiguous(pattern) &&
!isUnknown(pattern))) {
addPattern(pattern, patternList.getPatternWeight(i));
}
}
}
public void addPattern(int[] pattern) {
addPattern(pattern, 1.0);
}
public void addPattern(int[] pattern, double weight) {
if (patternLength == 0) {
patternLength = pattern.length;
}
if (patternLength != 0 && pattern.length != patternLength) {
throw new IllegalArgumentException("Added pattern's length (" + pattern.length + ") does not match those of existing patterns (" + patternLength + ")");
}
for (int i = 0; i < patternCount; i++) {
if (comparePatterns(patterns[i], pattern)) {
weights[i] += weight;
return;
}
}
if (patternCount == patterns.length) {
int[][] newPatterns = new int[patternCount + COUNT_INCREMENT][];
double[] newWeights = new double[patternCount + COUNT_INCREMENT];
for (int i = 0; i < patternCount; i++) {
newPatterns[i] = patterns[i];
newWeights[i] = weights[i];
}
patterns = newPatterns;
weights = newWeights;
}
patterns[patternCount] = pattern;
weights[patternCount] = weight;
patternCount++;
}
public void removePattern(int[] pattern) {
int index = -1;
for (int i = 0; i < patternCount; i++) {
if (comparePatterns(patterns[i], pattern)) {
index = i;
break;
}
}
if (index == -1) throw new IllegalArgumentException("Pattern not found");
weights[index] -= 1;
if (weights[index] == 0 && patternCount > 1) {
patterns[index] = patterns[patternCount - 1];
patterns[patternCount - 1] = null;
weights[index] = weights[patternCount - 1];
patternCount--;
}
}
public void removeAllPatterns() {
patternCount = 0;
for (int i = 0; i < patterns.length; i++) patterns[i] = null;
}
protected boolean isGapped(int[] pattern) {
int len = pattern.length;
for (int i = 0; i < len; i++) {
if (getDataType().isGapState(pattern[i])) {
return true;
}
}
return false;
}
protected boolean isAmbiguous(int[] pattern) {
int len = pattern.length;
for (int i = 0; i < len; i++) {
if (getDataType().isAmbiguousState(pattern[i])) {
return true;
}
}
return false;
}
protected boolean isUnknown(int[] pattern) {
int len = pattern.length;
for (int i = 0; i < len; i++) {
if (getDataType().isUnknownState(pattern[i])) {
return true;
}
}
return false;
}
protected static boolean isInvariant(int[] pattern) {
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
// **************************************************************
// PatternList IMPLEMENTATION
// **************************************************************
public int getPatternCount() {
return patternCount;
}
public int getStateCount() {
return dataType.getStateCount();
}
public int getPatternLength() {
return patternLength;
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
double[] w = new double[weights.length];
for (int i = 0; i < weights.length; i++) w[i] = weights[i];
return w;
}
public DataType getDataType() {
return dataType;
}
public double[] getStateFrequencies() {
return PatternList.Utils.empiricalStateFrequencies(this);
}
// **************************************************************
// TaxonList IMPLEMENTATION
// **************************************************************
public int getTaxonCount() {
if (taxonList == null) throw new RuntimeException("Patterns has no TaxonList");
return taxonList.getTaxonCount();
}
public Taxon getTaxon(int taxonIndex) {
if (taxonList == null) throw new RuntimeException("Patterns has no TaxonList");
return taxonList.getTaxon(taxonIndex);
}
public String getTaxonId(int taxonIndex) {
if (taxonList == null) throw new RuntimeException("Patterns has no TaxonList");
return taxonList.getTaxonId(taxonIndex);
}
public int getTaxonIndex(String id) {
if (taxonList == null) throw new RuntimeException("Patterns has no TaxonList");
return taxonList.getTaxonIndex(id);
}
public int getTaxonIndex(Taxon taxon) {
if (taxonList == null) throw new RuntimeException("Patterns has no TaxonList");
return taxonList.getTaxonIndex(taxon);
}
public List<Taxon> asList() {
if (taxonList == null) throw new RuntimeException("Patterns has no TaxonList");
return taxonList.asList();
}
public Iterator<Taxon> iterator() {
if (taxonList == null) throw new RuntimeException("Patterns has no TaxonList");
return taxonList.iterator();
}
public Object getTaxonAttribute(int taxonIndex, String name) {
if (taxonList == null) throw new RuntimeException("Patterns has no TaxonList");
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
// ========= Mask =========
// indexes to mask sth., e.g. taxon index whose state is unknown character in microsatellite
protected Set<Integer> maskSet = new HashSet<Integer>();
// no duplication, if duplicate, not add
public boolean addMask(int index) {
return maskSet.add(index);
}
public boolean isMasked(int index) {
return maskSet.contains(index);
}
public boolean hasMask() {
return maskSet.size() > 0;
}
public void clearMask() {
maskSet.clear();
}
public Set<Integer> getMaskSet() {
return maskSet;
}
public Taxon getTaxonMasked(int taxonIndex) {
if (taxonList == null) throw new RuntimeException("Patterns has no TaxonList");
if (isMasked(taxonIndex)) {
return null;
}
return taxonList.getTaxon(taxonIndex);
}
}