package dr.evomodel.substmodel;
import dr.evolution.datatype.AminoAcids;
import dr.evolution.datatype.DataType;
import dr.util.Citable;
public interface EmpiricalRateMatrix extends Citable {
String getName();
DataType getDataType();
double[] getEmpiricalRates();
double[] getEmpiricalFrequencies();
public abstract class Abstract implements EmpiricalRateMatrix {
public Abstract(String name, DataType dataType) {
this.name = name;
this.dataType = dataType;
}
public final String getName() { return name; }
public final DataType getDataType() { return dataType; }
public final double[] getEmpiricalRates() { return rates; }
public final double[] getEmpiricalFrequencies() { return frequencies; }
protected double[] rates = null;
protected double[] frequencies = null;
private String name;
protected DataType dataType;
}
public abstract class AbstractAminoAcid extends Abstract {
public AbstractAminoAcid(String name) {
super(name, AminoAcids.INSTANCE);
int n = dataType.getStateCount();
rates = new double[(n * (n - 1)) / 2];
frequencies = new double[n];
}
public final void setEmpiricalRates(double[][]matrix, String aminoAcidOrder) {
int k = 0;
for (int i = 0; i < dataType.getStateCount(); i++) {
int u = aminoAcidOrder.indexOf(dataType.getChar(i));
for (int j = i + 1; j < dataType.getStateCount(); j++) {
int v = aminoAcidOrder.indexOf(dataType.getChar(j));
if (u < v) {
rates[k] = matrix[u][v];
} else {
rates[k] = matrix[v][u];
}
k++;
}
}
}
public final void setEmpiricalFrequencies(double[]freqs, String aminoAcidOrder) {
double sum = 0.0;
for (int i = 0; i < dataType.getStateCount(); i++) {
int u = aminoAcidOrder.indexOf(dataType.getChar(i));
frequencies[i] = freqs[u];
sum += frequencies[i];
}
// normalize - we should probably detect large discrepancies but the empirical
// matrices have numerical rounding that cause small discrepancies.
for (int i = 0; i < dataType.getStateCount(); i++) {
frequencies[i] /= sum;
}
}
}
}