package dr.evolution.alignment;
import dr.evolution.datatype.DataType;
import dr.evolution.sequence.Sequence;
import dr.evolution.util.Taxon;
import java.util.*;
public class GapStrippedAlignment extends Alignment.Abstract
{
Alignment alignment;
boolean[] hasGap = null;
public GapStrippedAlignment(Alignment a) {
this.alignment = a;
DataType dataType = a.getDataType();
hasGap = new boolean[a.getSiteCount()];
for (int i = 0; i < hasGap.length; i++) {
for (int j = 0; j < a.getSequenceCount(); j++) {
if (dataType.isGapState(alignment.getState(j,i))) {
hasGap[i] = true;
break;
}
}
}
}
public final void setDataType(DataType dataType) {
throw new UnsupportedOperationException();
//alignment.setDataType(dataType);
}
public final String getAlignedSequenceString(int sequenceIndex) {
return getSequence(sequenceIndex).getSequenceString();
}
public final String getUnalignedSequenceString(int sequenceIndex) {
return getSequence(sequenceIndex).getSequenceString();
}
public final int getSequenceCount() {
return alignment.getSequenceCount();
}
public final Sequence getSequence(int sequenceIndex) {
DataType dataType = getDataType();
StringBuffer buffer = new StringBuffer();
for (int i = 0; i < alignment.getSiteCount(); i++) {
if (!hasGap[i]) {
buffer.append(dataType.getChar(alignment.getState(sequenceIndex,i)));
}
}
return new Sequence(buffer.toString());
}
public final void setSequenceAttribute(int index, String name, Object value) {
throw new UnsupportedOperationException();
}
public final Object getSequenceAttribute(int index, String name) {
throw new UnsupportedOperationException();
}
public final int getTaxonCount() {
return alignment.getTaxonCount();
}
public final Taxon getTaxon(int taxonIndex) {
return alignment.getTaxon(taxonIndex);
}
public final String getTaxonId(int taxonIndex) {
return alignment.getTaxonId(taxonIndex);
}
public final int getTaxonIndex(String id) {
return alignment.getTaxonIndex(id);
}
public final int getTaxonIndex(Taxon taxon) {
return alignment.getTaxonIndex(taxon);
}
public final Object getTaxonAttribute(int taxonIndex, String name) {
return alignment.getTaxonAttribute(taxonIndex, name);
}
public List<Taxon> asList() {
return alignment.asList();
}
public Iterator<Taxon> iterator() {
return alignment.iterator();
}
public final int getSiteCount() {
int siteCount = 0;
for (int i = 0; i < hasGap.length; i++) {
if (!hasGap[i]) siteCount += 1;
}
return siteCount;
}
public final int[] getSitePattern(int siteIndex) {
return alignment.getSitePattern(fullIndex(siteIndex));
}
public final int getPatternIndex(int siteIndex) {
return alignment.getPatternIndex(fullIndex(siteIndex));
}
public final int getState(int taxonIndex, int siteIndex) {
return alignment.getState(taxonIndex, fullIndex(siteIndex));
}
public final DataType getDataType() {
return alignment.getDataType();
}
private final int fullIndex(int gapStrippedIndex) {
int index = 0;
int fullIndex = 0;
while (index < gapStrippedIndex) {
if (!hasGap[fullIndex]) index += 1;
fullIndex += 1;
}
return fullIndex;
}
}