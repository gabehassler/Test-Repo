package dr.evolution.datatype;
import java.io.Serializable;
import java.util.*;
public abstract class DataType implements Serializable {
public static final String DATA_TYPE = "dataType";
public static final int NUCLEOTIDES = 0;
public static final int AMINO_ACIDS = 1;
public static final int CODONS = 2;
public static final int TWO_STATES = 3;
public static final int GENERAL = 4;
public static final int COVARION = 5;
public static final int MICRO_SAT = 6;
public static final int P2PTYPE = 7;
public static final int CONTINUOUS = 8;
public static final char UNKNOWN_CHARACTER = '?';
public static final char GAP_CHARACTER = '-';
protected int stateCount;
protected int ambiguousStateCount;
// this map contains all dataTypes in the class loader that have added themselves
static private Map<String, DataType> registeredDataTypes = null;
private static void lazyRegisterDataTypes() {
if (registeredDataTypes == null) {
registeredDataTypes = new Hashtable<String, DataType>();
registerDataType(Nucleotides.DESCRIPTION, Nucleotides.INSTANCE);
registerDataType(AminoAcids.DESCRIPTION, AminoAcids.INSTANCE);
registerDataType(Codons.DESCRIPTION + "-" + GeneticCode.UNIVERSAL.getName(), Codons.UNIVERSAL);
registerDataType(Codons.DESCRIPTION + "-" + GeneticCode.VERTEBRATE_MT.getName(), Codons.VERTEBRATE_MT);
registerDataType(Codons.DESCRIPTION + "-" + GeneticCode.YEAST.getName(), Codons.YEAST);
registerDataType(Codons.DESCRIPTION + "-" + GeneticCode.MOLD_PROTOZOAN_MT.getName(), Codons.MOLD_PROTOZOAN_MT);
registerDataType(Codons.DESCRIPTION + "-" + GeneticCode.MYCOPLASMA.getName(), Codons.MYCOPLASMA);
registerDataType(Codons.DESCRIPTION + "-" + GeneticCode.INVERTEBRATE_MT.getName(), Codons.INVERTEBRATE_MT);
registerDataType(Codons.DESCRIPTION + "-" + GeneticCode.CILIATE.getName(), Codons.CILIATE);
registerDataType(Codons.DESCRIPTION + "-" + GeneticCode.ECHINODERM_MT.getName(), Codons.ECHINODERM_MT);
registerDataType(Codons.DESCRIPTION + "-" + GeneticCode.EUPLOTID_NUC.getName(), Codons.EUPLOTID_NUC);
registerDataType(Codons.DESCRIPTION + "-" + GeneticCode.BACTERIAL.getName(), Codons.BACTERIAL);
registerDataType(Codons.DESCRIPTION + "-" + GeneticCode.ALT_YEAST.getName(), Codons.ALT_YEAST);
registerDataType(Codons.DESCRIPTION + "-" + GeneticCode.ASCIDIAN_MT.getName(), Codons.ASCIDIAN_MT);
registerDataType(Codons.DESCRIPTION + "-" + GeneticCode.FLATWORM_MT.getName(), Codons.FLATWORM_MT);
registerDataType(Codons.DESCRIPTION + "-" + GeneticCode.BLEPHARISMA_NUC.getName(), Codons.BLEPHARISMA_NUC);
registerDataType(Codons.DESCRIPTION + "-" + GeneticCode.NO_STOPS.getName(), Codons.NO_STOPS);
registerDataType(TwoStates.DESCRIPTION, TwoStates.INSTANCE);
registerDataType(HiddenNucleotides.DESCRIPTION, HiddenNucleotides.INSTANCE);
registerDataType(TwoStateCovarion.DESCRIPTION, TwoStateCovarion.INSTANCE);
registerDataType(HiddenCodons.DESCRIPTION + "2-" + GeneticCode.UNIVERSAL.getName(), HiddenCodons.UNIVERSAL_HIDDEN_2);
registerDataType(HiddenCodons.DESCRIPTION + "3-" + GeneticCode.UNIVERSAL.getName(), HiddenCodons.UNIVERSAL_HIDDEN_3);
registerDataType(NewHiddenNucleotides.DESCRIPTION + "1", NewHiddenNucleotides.NUCLEOTIDE_HIDDEN_1);
registerDataType(NewHiddenNucleotides.DESCRIPTION + "2", NewHiddenNucleotides.NUCLEOTIDE_HIDDEN_2);
registerDataType(NewHiddenNucleotides.DESCRIPTION + "3", NewHiddenNucleotides.NUCLEOTIDE_HIDDEN_3);
registerDataType(GeneralDataType.DESCRIPTION, GeneralDataType.INSTANCE);
registerDataType(Microsatellite.DESCRIPTION, Microsatellite.INSTANCE);
registerDataType(P2P.DESCRIPTION, P2P.INSTANCE);
registerDataType(ContinuousDataType.DESCRIPTION, ContinuousDataType.INSTANCE);
}
}
public static void registerDataType(String name, DataType dataType) {
lazyRegisterDataTypes();
registeredDataTypes.put(name, dataType);
}
public static DataType getRegisteredDataTypeByName(String name) {
lazyRegisterDataTypes();
return registeredDataTypes.get(name);
}
public static String[] getRegisteredDataTypeNames() {
lazyRegisterDataTypes();
Set<String> set = registeredDataTypes.keySet();
List<String> keys = new ArrayList<String>(set);
String[] names = new String[keys.size()];
for (int i = 0; i < names.length; i++) {
names[i] = keys.get(i);
}
return names;
}
public static DataType guessDataType(String sequence) {
// count A, C, G, T, U, N
long numNucs = 0;
long numChars = 0;
long numBins = 0;
for (int i = 0; i < sequence.length(); i++) {
char c = sequence.charAt(i);
int s = Nucleotides.INSTANCE.getState(c);
if (s != Nucleotides.UNKNOWN_STATE && s != Nucleotides.GAP_STATE) {
numNucs++;
}
if (c != '-' && c != '?') numChars++;
if (c == '0' || c == '1') numBins++;
}
if (numChars == 0) {
numChars = 1;
}
// more than 85 % frequency advocates nucleotide data
if ((double) numNucs / (double) numChars > 0.85) {
return Nucleotides.INSTANCE;
} else if ((double) numBins / (double) numChars > 0.2) {
return TwoStates.INSTANCE;
} else {
return AminoAcids.INSTANCE;
}
}
public abstract char[] getValidChars();
public int getStateCount() {
return stateCount;
}
public int getAmbiguousStateCount() {
return ambiguousStateCount;
}
public int getState(String code) {
return getState(code.charAt(0));
}
public int getState(char c) {
return (int) c - 'A';
}
public int getUnknownState() {
return stateCount;
}
public int getGapState() {
return stateCount + 1;
}
public char getChar(int state) {
return (char) (state + 'A');
}
public String getCode(int state) {
return String.valueOf(getChar(state));
}
public String getTriplet(int state) {
return " " + getChar(state) + " ";
}
public int[] getStates(int state) {
int[] states;
if (!isAmbiguousState(state)) {
states = new int[1];
states[0] = state;
} else {
states = new int[stateCount];
for (int i = 0; i < stateCount; i++) {
states[i] = i;
}
}
return states;
}
public boolean[] getStateSet(int state) {
boolean[] stateSet = new boolean[stateCount];
if (!isAmbiguousState(state)) {
for (int i = 0; i < stateCount; i++) {
stateSet[i] = false;
}
stateSet[state] = true;
} else {
for (int i = 0; i < stateCount; i++) {
stateSet[i] = true;
}
}
return stateSet;
}
public double getObservedDistance(int state1, int state2) {
if (!isAmbiguousState(state1) && !isAmbiguousState(state2) && state1 != state2) {
return 1.0;
}
return 0.0;
}
public double getObservedDistanceWithAmbiguity(int state1, int state2) {
boolean[] stateSet1 = getStateSet(state1);
boolean[] stateSet2 = getStateSet(state2);
double sumMatch = 0.0;
double sum1 = 0.0;
double sum2 = 0.0;
for (int i = 0; i < stateCount; i++) {
if (stateSet1[i]) {
sum1 += 1.0;
if (stateSet1[i] == stateSet2[i]) {
sumMatch += 1.0;
}
}
if (stateSet2[i]) {
sum2 += 1.0;
}
}
return (1.0 - (sumMatch / (sum1 * sum2)));
}
public String toString() {
return getDescription();
}
public abstract String getDescription();
public abstract int getType();
public boolean isAmbiguousChar(char c) {
return isAmbiguousState(getState(c));
}
public boolean isUnknownChar(char c) {
return isUnknownState(getState(c));
}
public boolean isGapChar(char c) {
return isGapState(getState(c));
}
public boolean isAmbiguousState(int state) {
return (state >= stateCount);
}
public boolean isUnknownState(int state) {
return (state == getUnknownState());
}
public boolean isGapState(int state) {
return (state == getGapState());
}
public String getName() {
switch (getType()) {
case DataType.NUCLEOTIDES:
return "Nucleotide";
case DataType.AMINO_ACIDS:
return "Amino Acid";
case DataType.CODONS:
return "Codon";
case DataType.TWO_STATES:
return "Binary";
case DataType.COVARION:
return "Covarion";
case DataType.GENERAL:
return "Discrete Traits";
case DataType.CONTINUOUS:
return "Continuous Traits";
case DataType.MICRO_SAT:
return "Microsatellite";
default:
throw new IllegalArgumentException("Unsupported data type");
}
}
@Override
public boolean equals(Object o) {
if (this == o) return true;
if (!(o instanceof DataType)) return false;
DataType dataType = (DataType) o;
if (this.getType() != dataType.getType()) return false;
return true;
}
@Override
public int hashCode() {
return getType();
}
}
