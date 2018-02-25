package test.dr.app.beagle;
import dr.app.beagle.evomodel.substmodel.*;
import dr.evolution.datatype.Nucleotides;
import dr.evolution.datatype.TwoStates;
import dr.inference.markovjumps.MarkovJumpsType;
import dr.inference.model.Parameter;
import dr.math.matrixAlgebra.Vector;
import test.dr.inference.trace.TraceCorrelationAssert;
import java.util.List;
public class HiddenMarkovRatesTest extends TraceCorrelationAssert {
public HiddenMarkovRatesTest(String name) {
super(name);
}
private double[] getBinaryFreqs(int index) {
return new double[]{0.7, 0.3};
}
private double[] getHKYFreqs(int index) {
return new double[]{0.25, 0.25, 0.25, 0.25};
}
private double getKappa(int index) {
return 10.0;
}
private int getNumberReplicates(int index) {
return 1000000;
}
private double getLength(int index) {
return 1.5;
}
public void testHiddenRates() {
final int index = 0;
double[] freqs = getBinaryFreqs(index);
FrequencyModel binaryFreqModel = new FrequencyModel(TwoStates.INSTANCE, freqs);
int relativeTo = 0;
Parameter ratesParameter = new Parameter.Default(0);
GeneralSubstitutionModel binaryModel = new GeneralSubstitutionModel("binary", TwoStates.INSTANCE, binaryFreqModel, ratesParameter, relativeTo);
UniformizedSubstitutionModel uSM = new UniformizedSubstitutionModel(binaryModel, MarkovJumpsType.REWARDS);
uSM.setSaveCompleteHistory(true);
double[] rewardRegister = new double[]{0.0, 1.0};
uSM.setRegistration(rewardRegister);
final double[] hkyFreqs = getHKYFreqs(index);
FrequencyModel hkyFreqModel = new FrequencyModel(Nucleotides.INSTANCE, hkyFreqs);
final double kappa = getKappa(index);
final HKY hky = new HKY(kappa, hkyFreqModel);
final double length = getLength(index);
double[] resultCompleteHistory = new double[16];
final int replicates = getNumberReplicates(index);
double result = 0.0;
for (int r = 0; r < replicates; ++r) {
result += oneCompleteHistoryReplicate(resultCompleteHistory, hky, uSM, length);
}
result /= replicates;
normalize(resultCompleteHistory, replicates);
System.out.println("Averaged probabilities");
System.out.println(result);
System.out.println(new Vector(resultCompleteHistory));
System.out.println();
double[] intermediate = new double[16];
hky.getTransitionProbabilities(result, intermediate);
System.out.println("Intermediate using above average reward");
System.out.println(result);
System.out.println(new Vector(intermediate));
System.out.println();
double[] resultExpected = new double[16];
UniformizedSubstitutionModel expectedUSM = new UniformizedSubstitutionModel(binaryModel, MarkovJumpsType.REWARDS, replicates);
expectedUSM.setRegistration(rewardRegister);
result = oneCompleteHistoryReplicate(resultExpected, hky, expectedUSM, length);
System.out.println("Averaged reward");
System.out.println(result);
System.out.println(new Vector(resultExpected));
System.out.println();
double[] originalProbs = new double[16];
hky.getTransitionProbabilities(length, originalProbs);
System.out.println("Original probabilities");
System.out.println(new Vector(originalProbs));
System.out.println();
}
double oneCompleteHistoryReplicate(double[] result, final SubstitutionModel hky,
final UniformizedSubstitutionModel uSM, final double length) {
double reward = uSM.computeCondStatMarkovJumps(1, 1, length);
if (DEBUG) reward = DEBUG_REWARD;
double[] tmp = new double[hky.getDataType().getStateCount() * hky.getDataType().getStateCount()];
hky.getTransitionProbabilities(reward, tmp);
increment(result, tmp);
return reward;
}
private static final boolean DEBUG = false;
private static final double DEBUG_REWARD = 2.0;
private void normalize(double[] a, final int count) {
for (int i = 0; i < a.length; ++i) {
a[i] /= count;
}
}
private void increment(double[] out, final double[] in) {
for (int i = 0; i < out.length; ++i) {
out[i] += in[i];
}
}
private double[] getTransitionProbabilities(SubstitutionModel substModel, List<Double> times) {
final int stateCount = substModel.getDataType().getStateCount();
double[] result = getIdentityMatrix(stateCount);
double[] tmp = new double[stateCount * stateCount];
for (Double time : times) {
substModel.getTransitionProbabilities(time, tmp);
result = matrixMultiplication(result, tmp, stateCount);
}
return result;
}
private double[] matrixMultiplication(double[] a, double[] b, int dim) {
double[] result = new double[dim * dim];
for (int i = 0; i < dim; ++i) {
for (int j = 0; j < dim; ++j) {
for (int k = 0; k < dim; ++k) {
result[i * dim + j] += a[i * dim + k] * b[k * dim + j];
}
}
}
return result;
}
private double[] getIdentityMatrix(int stateCount) {
double[] result = new double[stateCount * stateCount];
for (int i = 0; i < stateCount; ++i) {
result[i * stateCount + i] = 1.0;
}
return result;
}
}