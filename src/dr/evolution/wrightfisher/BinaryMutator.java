package dr.evolution.wrightfisher;
import dr.math.MathUtils;
import dr.math.Poisson;
public class BinaryMutator extends Mutator {
public BinaryMutator(double muRate) {
this.muRate = muRate;
}
public Mutation[] mutate(byte[] sequence, byte[] childSequence) {
if (genomeLength != sequence.length) {
genomeLength = sequence.length;
poissonMean = genomeLength * muRate;
}
int mutationCount = Poisson.nextPoisson(poissonMean);
Mutation[] mutations = new Mutation[mutationCount];
System.arraycopy(sequence, 0, childSequence, 0, genomeLength);
for (int i = 0; i < mutationCount; i++) {
int pos = MathUtils.nextInt(genomeLength);
byte state = (byte)((sequence[pos] + 1) % 2);
childSequence[pos] = state;
mutations[i] = new Mutation(pos, state);
}
return mutations;
}
double muRate = 0.01;
double poissonMean = -1;
int genomeLength = -1;
}