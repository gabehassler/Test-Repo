package dr.evolution.alignment;
import dr.evolution.datatype.DataType;
import dr.evolution.sequence.SequenceList;
public interface Alignment extends SequenceList, SiteList
{
void setDataType(DataType dataType);
String getAlignedSequenceString(int sequenceIndex);
String getUnalignedSequenceString(int sequenceIndex);
public abstract class Abstract implements Alignment {
// **************************************************************
// PatternList IMPLEMENTATION
// **************************************************************
public int getPatternCount() {
return getSiteCount();
}
public int getInvariantCount() {
throw new RuntimeException("Not implemented yet");
}
public int getStateCount() {
return getDataType().getStateCount();
}
public int getPatternLength() {
return getSequenceCount();
}
public int[] getPattern(int patternIndex) {
return getSitePattern(patternIndex);
}
public int getPatternState(int taxonIndex, int patternIndex) {
return getState(taxonIndex, patternIndex);
}
public double getPatternWeight(int patternIndex) {
return 1.0;
}
public double[] getPatternWeights() {
int count = getSiteCount();
double[] weights = new double[count];
for (int i = 0; i < count; i++)
weights[i] = 1.0;
return weights;
}
public double[] getStateFrequencies() {
return PatternList.Utils.empiricalStateFrequencies(this);
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
}
