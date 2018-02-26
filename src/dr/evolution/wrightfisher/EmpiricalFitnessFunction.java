package dr.evolution.wrightfisher;
import dr.math.MathUtils;
public class EmpiricalFitnessFunction extends FitnessFunction {
	public EmpiricalFitnessFunction(int genomeLength, double[] mutationFitnesses, int stateSize, boolean isRandom) {
		fitness = new double[genomeLength][stateSize];
		logFitness = new double[genomeLength][stateSize];
		int index = 0;
		for (int i = 0; i < genomeLength; i++) {
			fitness[i][0] = 1.0;
			for (int j = 1; j < stateSize; j++) {
				if (isRandom) {
					fitness[i][j] = mutationFitnesses[MathUtils.nextInt(mutationFitnesses.length)];
				} else {
					fitness[i][j] = mutationFitnesses[index];
					index = (index + 1) % mutationFitnesses.length;
				}
			}
		}
		// tabulate log fitnesses
		for (int i = 0; i < stateSize; i++) {
			for (int j = 0; j < genomeLength; j++) {
				logFitness[j][i] = Math.log(fitness[j][i]);
			}
		}
	}
	public final double getFitness(byte[] sequence) {
		for (int i = 0; i < sequence.length; i++) {
			totalFitness += logFitness[i][sequence[i]];
		}
		return Math.exp(totalFitness);
		double totalFitness = 1.0;
		for (int i = 0; i < sequence.length; i++) {
			totalFitness *= fitness[i][sequence[i]];
		}
		return totalFitness;
	}
	public double getFitnessFactor(int pos, byte newState, byte oldState) {
		return fitness[pos][newState] / fitness[pos][oldState];
	}
	public final double[][] getFitnessTable() {
		return fitness;
	}
	public void initializeToFittest(byte[] genome) {
		for (int i = 0; i < genome.length; i++) {
			genome[i] = 0;
		}
	}
	double[][] fitness;
	double[][] logFitness;
}