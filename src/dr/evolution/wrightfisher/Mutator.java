package dr.evolution.wrightfisher;
import dr.math.matrixAlgebra.Matrix;
public abstract class Mutator {
	Mutator() {}
	public Mutator(Matrix mutationRates) {
		this.mutationRates = mutationRates;
	}
	public Mutator(double mutationRate, int stateSize) {
		double noMutation = 1.0 - ((stateSize-1)*mutationRate);
		double[][] rates = new double[stateSize][stateSize];
		for (int i = 0; i < stateSize; i++) {
			for (int j = 0; j < stateSize; j++) {
				if (i != j) {
					rates[i][j] = mutationRate;
				} else rates[i][i] = noMutation;
			}
		}
		this.mutationRates = new Matrix( rates);
	}
	public abstract Mutation[] mutate(byte[] sequence, byte[] childSequence);
	Matrix mutationRates = null;
}