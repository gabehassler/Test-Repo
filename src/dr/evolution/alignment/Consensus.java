package dr.evolution.alignment;
import dr.evolution.datatype.DataType;
import dr.evolution.sequence.Sequence;
import dr.evolution.util.Taxon;
public class Consensus {
int[] counts;
int[] consensus;
int total;
String name;
DataType dataType = null;
public Consensus(String name, Alignment alignment, boolean ignoreGaps) {
this.name = name;
dataType = alignment.getDataType();
int[][] frequencies = new int[alignment.getSiteCount()][dataType.getAmbiguousStateCount()];
for (int i = 0; i < alignment.getSequenceCount(); i++) {
for (int j = 0; j < alignment.getSiteCount(); j++) {
int state = alignment.getState(i, j);
if (ignoreGaps) {
if (state < dataType.getStateCount()) {
frequencies[j][state] += 1;
}
} else {
frequencies[j][state] += 1;
}
}
}
counts = new int[alignment.getSiteCount()];
total = alignment.getSequenceCount();
consensus = new int[alignment.getSiteCount()];
for (int i = 0; i < alignment.getSiteCount(); i++) {
int maxState = 0;
int maxFreq = frequencies[i][0];
for (int j = 1; j < frequencies[i].length; j++) {
int freq = frequencies[i][j];
if (freq > maxFreq) {
maxState = j;
maxFreq = freq;
}
}
consensus[i] = maxState;
counts[i] = maxFreq;
}
}
public double getReliability(int site) {
return (double)counts[site]/(double)total;
}
public int getState(int site) {
return consensus[site];    
}
public final Sequence getConsensusSequence() {
StringBuffer buffer = new StringBuffer();
for (int i = 0; i < consensus.length; i++) {
buffer.append(dataType.getChar(getState(i)));
}
Sequence sequence = new Sequence(new Taxon(name),buffer.toString());
sequence.setDataType(dataType);
return sequence;
}
}
