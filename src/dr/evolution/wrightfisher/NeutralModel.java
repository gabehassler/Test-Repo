package dr.evolution.wrightfisher;
public class NeutralModel extends FitnessFunction {
	public double getFitness(byte[] sequence) { return 1.0; }
	public double getFitnessFactor(int pos, byte newState, byte oldState) { return 1.0; }
	public double[][] getFitnessTable() {
		return null;
	}
	public void initializeToFittest(byte[] genome) {
		for (int i = 0; i < genome.length; i++) {
			genome[i] = 0;
		}
	}
}