package dr.evolution.alignment;
import dr.app.bss.XMLExporter;
import dr.app.tools.NexusExporter;
import dr.evolution.datatype.Codons;
import dr.evolution.datatype.DataType;
import dr.evolution.datatype.GeneralDataType;
import dr.evolution.sequence.Sequence;
import dr.evolution.sequence.Sequences;
import dr.evolution.util.Taxon;
import dr.evolution.util.TaxonList;
import dr.util.NumberFormatter;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
@SuppressWarnings("serial")
public class SimpleAlignment extends Sequences implements Alignment, dr.util.XHTMLable {
// **************************************************************
// INSTANCE VARIABLES
// **************************************************************
private OutputType outputType = OutputType.FASTA;
private DataType dataType = null;
private int siteCount = 0;
private boolean siteCountKnown = false;
private boolean countStatistics = !(dataType instanceof Codons) && !(dataType instanceof GeneralDataType);
// **************************************************************
// SimpleAlignment METHODS
// **************************************************************
public SimpleAlignment() {
}
public SimpleAlignment(Alignment a, TaxonList taxa) {
for (int i = 0; i < taxa.getTaxonCount(); i++) {
Taxon taxon = taxa.getTaxon(i);
Sequence sequence = a.getSequence(a.getTaxonIndex(taxon));
addSequence(sequence);
}
}
public void setOutputType(OutputType out) {
outputType = out;
}
public List<Sequence> getSequences() {
return Collections.unmodifiableList(sequences);
}
public void updateSiteCount() {
siteCount = 0;
int i, len, n = getSequenceCount();
for (i = 0; i < n; i++) {
len = getSequence(i).getLength();
if (len > siteCount)
siteCount = len;
}
siteCountKnown = true;
}
// **************************************************************
// Alignment IMPLEMENTATION
// **************************************************************
public void setDataType(DataType dataType) {
this.dataType = dataType;
}
public int getSiteCount(DataType dataType) {
return getSiteCount();
}
public char getChar(int sequenceIndex, int siteIndex) {
return getSequence(sequenceIndex).getChar(siteIndex);
}
public String getAlignedSequenceString(int sequenceIndex) {
return getSequence(sequenceIndex).getSequenceString();
}
public String getUnalignedSequenceString(int sequenceIndex) {
StringBuffer unaligned = new StringBuffer();
for (int i = 0, n = getSiteCount(); i < n; i++) {
int state = getState(sequenceIndex, i);
if (!dataType.isGapState(state)) {
unaligned.append(dataType.getChar(state));
}
}
return unaligned.toString();
}
// **************************************************************
// Sequences METHODS
// **************************************************************
public void addSequence(Sequence sequence) {
if (dataType == null) {
if (sequence.getDataType() == null) {
dataType = sequence.guessDataType();
sequence.setDataType(dataType);
} else {
setDataType(sequence.getDataType());
}
} else if (sequence.getDataType() == null) {
sequence.setDataType(dataType);
} else if (dataType != sequence.getDataType()) {
throw new IllegalArgumentException("Sequence's dataType does not match the alignment's");
}
int invalidCharAt = getInvalidChar(sequence.getSequenceString(), dataType);
if (invalidCharAt >= 0)
throw new IllegalArgumentException("Sequence of " + sequence.getTaxon().getId()
+ " contains invalid char \'" + sequence.getChar(invalidCharAt) + "\' at index " + invalidCharAt);
super.addSequence(sequence);
updateSiteCount();
}
public void insertSequence(int position, Sequence sequence) {
if (dataType == null) {
if (sequence.getDataType() == null) {
dataType = sequence.guessDataType();
sequence.setDataType(dataType);
} else {
setDataType(sequence.getDataType());
}
} else if (sequence.getDataType() == null) {
sequence.setDataType(dataType);
} else if (dataType != sequence.getDataType()) {
throw new IllegalArgumentException("Sequence's dataType does not match the alignment's");
}
int invalidCharAt = getInvalidChar(sequence.getSequenceString(), dataType);
if (invalidCharAt >= 0)
throw new IllegalArgumentException("Sequence of " + sequence.getTaxon().getId()
+ " contains invalid char \'" + sequence.getChar(invalidCharAt) + "\' at index " + invalidCharAt);
super.insertSequence(position, sequence);
}
protected int getInvalidChar(String sequence, DataType dataType) {
final char[] validChars = dataType.getValidChars();
if (validChars != null) {
String validString = new String(validChars);
for (int i = 0; i < sequence.length(); i++) {
char c = sequence.charAt(i);
if (validString.indexOf(c) < 0) return i;
}
}
return -1;
}
// **************************************************************
// SiteList IMPLEMENTATION
// **************************************************************
public int getSiteCount() {
if (!siteCountKnown)
updateSiteCount();
return siteCount;
}
public int[] getSitePattern(int siteIndex) {
Sequence seq;
int i, n = getSequenceCount();
int[] pattern = new int[n];
for (i = 0; i < n; i++) {
seq = getSequence(i);
if (siteIndex >= seq.getLength())
pattern[i] = dataType.getGapState();
else
pattern[i] = seq.getState(siteIndex);
}
return pattern;
}
public int getPatternIndex(int siteIndex) {
return siteIndex;
}
public int getState(int taxonIndex, int siteIndex) {
Sequence seq = getSequence(taxonIndex);
if (siteIndex >= seq.getLength()) {
return dataType.getGapState();
}
return seq.getState(siteIndex);
}
public void setState(int taxonIndex, int siteIndex, int state) {
Sequence seq = getSequence(taxonIndex);
if (siteIndex >= seq.getLength()) {
throw new IllegalArgumentException();
}
seq.setState(siteIndex, state);
}
// **************************************************************
// PatternList IMPLEMENTATION
// **************************************************************
public int getPatternCount() {
return getSiteCount();
}
public int getInvariantCount() {
int invariantSites = 0;
for (int i = 0; i < getSiteCount(); i++) {
int[] pattern = getSitePattern(i);
if (Patterns.isInvariant(pattern)) {
invariantSites++;
}
}
return invariantSites;
}
public int getUniquePatternCount() {
Patterns patterns = new Patterns(this);
return patterns.getPatternCount();
}
public int getInformativeCount() {
Patterns patterns = new Patterns(this);
int informativeCount = 0;
for (int i = 0; i < patterns.getPatternCount(); i++) {
int[] pattern = patterns.getPattern(i);
if (isInformative(pattern)) {
informativeCount += patterns.getPatternWeight(i);
}
}
return informativeCount;
}
public int getSingletonCount() {
Patterns patterns = new Patterns(this);
int singletonCount = 0;
for (int i = 0; i < patterns.getPatternCount(); i++) {
int[] pattern = patterns.getPattern(i);
if (!Patterns.isInvariant(pattern) && !isInformative(pattern)) {
singletonCount += patterns.getPatternWeight(i);
}
}
return singletonCount;
}
private boolean isInformative(int[] pattern) {
int[] stateCounts = new int[getStateCount()];
for (int j = 0; j < pattern.length; j++) {
stateCounts[pattern[j]]++;
}
boolean oneStateGreaterThanOne = false;
boolean secondStateGreaterThanOne = false;
for (int j = 0; j < stateCounts.length; j++) {
if (stateCounts[j] > 1) {
if (!oneStateGreaterThanOne) {
oneStateGreaterThanOne = true;
} else {
secondStateGreaterThanOne = true;
}
}
}
return secondStateGreaterThanOne;
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
double[] weights = new double[siteCount];
for (int i = 0; i < siteCount; i++)
weights[i] = 1.0;
return weights;
}
public DataType getDataType() {
return dataType;
}
public double[] getStateFrequencies() {
return PatternList.Utils.empiricalStateFrequencies(this);
}
public void setReportCountStatistics(boolean report) {
countStatistics = report;
}
public String toString() {
return outputType.makeOutputString(this);   // generic delegation to ease extensibility
}// END: toString
public String toXHTML() {
String xhtml = "<p><em>Alignment</em> data type = ";
xhtml += getDataType().getDescription();
xhtml += ", no. taxa = ";
xhtml += getTaxonCount();
xhtml += ", no. sites = ";
xhtml += getSiteCount();
xhtml += "</p>";
xhtml += "<pre>";
int length, maxLength = 0;
for (int i = 0; i < getTaxonCount(); i++) {
length = getTaxonId(i).length();
if (length > maxLength)
maxLength = length;
}
for (int i = 0; i < getTaxonCount(); i++) {
length = getTaxonId(i).length();
xhtml += getTaxonId(i);
for (int j = length; j <= maxLength; j++)
xhtml += " ";
xhtml += getAlignedSequenceString(i) + "\n";
}
xhtml += "</pre>";
return xhtml;
}
public enum OutputType {
FASTA("fasta", "fsa") {
@Override
public String makeOutputString(SimpleAlignment alignment) {
NumberFormatter formatter = new NumberFormatter(6);
StringBuffer buffer = new StringBuffer();
if (alignment.countStatistics) {
buffer.append("Site count = ").append(alignment.getSiteCount()).append("\n");
buffer.append("Invariant sites = ").append(alignment.getInvariantCount()).append("\n");
buffer.append("Singleton sites = ").append(alignment.getSingletonCount()).append("\n");
buffer.append("Parsimony informative sites = ").append(alignment.getInformativeCount()).append("\n");
buffer.append("Unique site patterns = ").append(alignment.getUniquePatternCount()).append("\n\n");
}
for (int i = 0; i < alignment.getSequenceCount(); i++) {
String name = formatter.formatToFieldWidth(alignment.getTaxonId(i), 10);
buffer.append(">" + name + "\n");
buffer.append(alignment.getAlignedSequenceString(i) + "\n");
}
return buffer.toString();
}
},
NEXUS("nexus", "nxs") {
@Override
public String makeOutputString(SimpleAlignment alignment) {
StringBuffer buffer = new StringBuffer();
try {
File tmp = File.createTempFile("tempfile", ".tmp");
PrintStream ps = new PrintStream(tmp);
NexusExporter nexusExporter = new NexusExporter(ps);
buffer.append(nexusExporter.exportAlignment(alignment));
} catch (IllegalArgumentException e) {
e.printStackTrace();
} catch (IOException e) {
e.printStackTrace();
}
return buffer.toString();
}// END: makeOutputString
},
XML("xml", "xml") {
@Override
public String makeOutputString(SimpleAlignment alignment) {
StringBuffer buffer = new StringBuffer();
try {
XMLExporter xmlExporter = new XMLExporter();
buffer.append(xmlExporter.exportAlignment(alignment));
} catch (IllegalArgumentException e) {
e.printStackTrace();
} catch (IOException e) {
e.printStackTrace();
}
return buffer.toString();
}// END: makeOutputString
};
//        public static OutputType getValue(String str) {
//			if (FASTA.name().equalsIgnoreCase(str)) {
//				return FASTA;
//			} else if (NEXUS.name().equalsIgnoreCase(str)) {
//				return NEXUS;
//			} else if (XML.name().equalsIgnoreCase(str)) {
//				return XML;
//			}
//			return null;
//		}// END: getValue
//        
//        public static Enum[] getValues() {
//        	
//        	Enum values[] = new Enum[values().length];
//        	
//        	int i = 0;
//        	for(Enum value : OutputType.values()) {
//        		
//        		values[i] = getValue(value.toString());// value;
//        		i++;
//        		
//        	}
//        	
//        	return values;
//        }
private final String text;
private final String extension;
private OutputType(String text, String extension) {
this.text = text;
this.extension = extension;
}
public String getText() {
return text;
}
public String getExtension() {
return extension;
}
public abstract String makeOutputString(SimpleAlignment alignment);
public static OutputType parseFromString(String text) {
for (OutputType type : OutputType.values()) {
if (type.getText().compareToIgnoreCase(text) == 0) {
return type;
}
}
return null;
}
public static OutputType parseFromExtension(String extension) {
for (OutputType type : OutputType.values()) {
if (type.getExtension().compareToIgnoreCase(extension) == 0) {
return type;
}
}
return null;
}
}
}// END: class