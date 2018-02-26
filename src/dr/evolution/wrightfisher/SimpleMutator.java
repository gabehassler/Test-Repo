package dr.evolution.wrightfisher;
import dr.math.MathUtils;
import dr.math.Poisson;
public class SimpleMutator extends Mutator {
	public SimpleMutator(double muRate, int stateSize) {
		this.stateSize = stateSize;
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
		byte newState;
		for (int i = 0; i < mutationCount; i++) {
			int pos = MathUtils.nextInt(genomeLength);
			newState = (byte)MathUtils.nextInt(stateSize-1);
			if (newState == sequence[i]) {
				newState = (byte)((newState + 1) % stateSize);
			}
			childSequence[pos] = newState;
			mutations[i] = new Mutation(pos, newState);
		}
		return mutations;
	}
	double muRate = 0.01;
	double poissonMean = -1;
	int genomeLength = -1;
	int stateSize = 2;
}