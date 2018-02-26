package dr.evolution.wrightfisher;
public abstract class FitnessFunction {
	public abstract double getFitness(byte[] sequence);
	public abstract double getFitnessFactor(int pos, byte newState, byte oldState);
	public abstract double[][] getFitnessTable();
	public abstract void initializeToFittest(byte[] genome);
}