package dr.evolution.alignment;
import dr.evolution.datatype.AminoAcids;
import dr.evolution.datatype.Codons;
import dr.evolution.datatype.DataType;
import dr.evolution.datatype.Nucleotides;
import dr.evolution.sequence.Sequence;
import dr.evolution.util.Taxon;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
public abstract class WrappedAlignment extends Alignment.Abstract {
protected WrappedAlignment(Alignment alignment) {
this.alignment = alignment;
}
public abstract int getState(int taxonIndex, int siteIndex);
public void setDataType(DataType dataType) {
// do nothing by default
}
public String getAlignedSequenceString(int sequenceIndex) {
StringBuffer buffer = new StringBuffer();
for (int i = 0, n = getSiteCount(); i < n; i++) {
buffer.append(getDataType().getChar(getState(sequenceIndex, i)));
}
return buffer.toString();
}
public String getUnalignedSequenceString(int sequenceIndex) {
StringBuffer unaligned = new StringBuffer();
for (int i = 0, n = getSiteCount(); i < n; i++) {
int state = getState(sequenceIndex, i);
if (!getDataType().isGapState(state)) {
unaligned.append(getDataType().getChar(state));
}
}
return unaligned.toString();
}
public DataType getDataType() {
return alignment.getDataType();
}
public int getSiteCount() {
return alignment.getSiteCount();
}
public int[] getSitePattern(int siteIndex) {
int i, n = getSequenceCount();
int[] pattern = new int[n];
for (i = 0; i < n; i++) {
pattern[i] = getState(i, siteIndex);
}
return pattern;
}
public int getPatternIndex(int siteIndex) {
return alignment.getPatternIndex(siteIndex);
}
public int getSequenceCount() {
return alignment.getSequenceCount();
}
public Sequence getSequence(int index) {
return alignment.getSequence(index);
}
public void setSequenceAttribute(int index, String name, Object value) {
alignment.setSequenceAttribute(index, name, value);
}
public Object getSequenceAttribute(int index, String name) {
return alignment.getSequenceAttribute(index, name);
}
public int getTaxonCount() {
return alignment.getTaxonCount();
}
public Taxon getTaxon(int taxonIndex) {
return alignment.getTaxon(taxonIndex);
}
public String getTaxonId(int taxonIndex) {
return alignment.getTaxonId(taxonIndex);
}
public int getTaxonIndex(String id) {
return alignment.getTaxonIndex(id);
}
public int getTaxonIndex(Taxon taxon) {
return alignment.getTaxonIndex(taxon);
}
public List<Taxon> asList() {
List<Taxon> taxa = new ArrayList<Taxon>();
for (int i = 0, n = getTaxonCount(); i < n; i++) {
taxa.add(getTaxon(i));
}
return taxa;
}
public String toString() {
dr.util.NumberFormatter formatter = new dr.util.NumberFormatter(6);
StringBuffer buffer = new StringBuffer();
for (int i = 0; i < getSequenceCount(); i++) {
String name = formatter.formatToFieldWidth(getTaxonId(i), 10);
buffer.append(">").append(name).append("\n");
buffer.append(getAlignedSequenceString(i)).append("\n");
}
return buffer.toString();
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
return alignment.getTaxonAttribute(taxonIndex, name);
}
protected Alignment alignment = null;
}
