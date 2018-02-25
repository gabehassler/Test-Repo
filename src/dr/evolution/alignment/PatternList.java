package dr.evolution.alignment;
import dr.evolution.datatype.DataType;
import dr.evolution.util.TaxonList;
import dr.util.Identifiable;
public interface PatternList extends TaxonList, Identifiable {
int getPatternCount();
int getStateCount();
int getPatternLength();
int[] getPattern(int patternIndex);
int getPatternState(int taxonIndex, int patternIndex);
double getPatternWeight(int patternIndex);
double[] getPatternWeights();
DataType getDataType();
double[] getStateFrequencies();
public static class Utils {
public static double[] empiricalStateFrequencies(PatternList patternList) {
return empiricalStateFrequenciesPAUP(patternList);
}
public static double[] empiricalStateFrequenciesPAUP(PatternList patternList) {
int i, j, k;
double total, sum, x, w, difference;
DataType dataType = patternList.getDataType();
int stateCount = patternList.getStateCount();
int patternLength = patternList.getPatternLength();
int patternCount = patternList.getPatternCount();
double[] freqs = equalStateFrequencies(patternList);
double[] tempFreq = new double[stateCount];
int[] pattern;
boolean[] state;
int count = 0;
do {
for (i = 0; i < stateCount; i++)
tempFreq[i] = 0.0;
total = 0.0;
for (i = 0; i < patternCount; i++) {
pattern = patternList.getPattern(i);
w = patternList.getPatternWeight(i);
for (k = 0; k < patternLength; k++) {
state = dataType.getStateSet(pattern[k]);
sum = 0.0;
for (j = 0; j < stateCount; j++)
if (state[j])
sum += freqs[j];
for (j = 0; j < stateCount; j++) {
if (state[j]) {
x = (freqs[j] * w) / sum;
tempFreq[j] += x;
total += x;
}
}
}
}
difference = 0.0;
for (i = 0; i < stateCount; i++) {
difference += Math.abs((tempFreq[i] / total) - freqs[i]);
freqs[i] = tempFreq[i] / total;
}
count ++;
} while (difference > 1E-8 && count < 1000);
return freqs;
}
public static double[] empiricalStateFrequenciesMrBayes(PatternList patternList) {
DataType dataType = patternList.getDataType();
int stateCount = patternList.getStateCount();
int patternLength = patternList.getPatternLength();
int patternCount = patternList.getPatternCount();
double[] freqs = equalStateFrequencies(patternList);
double sumTotal = 0.0;
double[] sumFreq = new double[stateCount];
for (int i = 0; i < patternCount; i++) {
int[] pattern = patternList.getPattern(i);
double w = patternList.getPatternWeight(i);
for (int k = 0; k < patternLength; k++) {
boolean[] state = dataType.getStateSet(pattern[k]);
double sum = 0.0;
for (int j = 0; j < stateCount; j++) {
if (state[j]) {
sum += freqs[j];
}
}
for (int j = 0; j < stateCount; j++) {
if (state[j]) {
double x = (freqs[j] * w) / sum;
sumFreq[j] += x;
sumTotal += x;
}
}
}
}
for (int i = 0; i < stateCount; i++) {
freqs[i] = sumFreq[i] / sumTotal;
}
return freqs;
}
public static double[] equalStateFrequencies(PatternList patternList) {
int i, n = patternList.getStateCount();
double[] freqs = new double[n];
double f = 1.0 / n;
for (i = 0; i < n; i++)
freqs[i] = f;
return freqs;
}
}
}
