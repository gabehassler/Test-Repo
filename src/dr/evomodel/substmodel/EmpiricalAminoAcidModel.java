package dr.evomodel.substmodel;
import dr.evolution.datatype.AminoAcids;
import dr.inference.model.Parameter;
public class EmpiricalAminoAcidModel extends AbstractAminoAcidModel {
public EmpiricalAminoAcidModel(EmpiricalRateMatrix rateMatrix, FrequencyModel freqModel) {
super(rateMatrix.getName(), freqModel);
if (freqModel == null) {
areFrequenciesConstant = true;
double[] freqs = rateMatrix.getEmpiricalFrequencies();
this.freqModel = new FrequencyModel(AminoAcids.INSTANCE, new Parameter.Default(freqs));
}
this.rateMatrix = rateMatrix;
}
protected void frequenciesChanged() {
// Nothing to precalculate
}
protected void ratesChanged() {
// Nothing to precalculate
}
protected void setupRelativeRates() {
double[] rates = rateMatrix.getEmpiricalRates();
System.arraycopy(rates, 0, relativeRates, 0, relativeRates.length);
}
// *****************************************************************
// Interface Model
// *****************************************************************
protected void storeState() {
} // nothing to do
protected void restoreState() {
updateMatrix = !areFrequenciesConstant;
}
protected void acceptState() {
} // nothing to do
private EmpiricalRateMatrix rateMatrix;
private boolean areFrequenciesConstant = false;
}
