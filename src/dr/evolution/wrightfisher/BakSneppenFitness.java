package dr.evolution.wrightfisher;
public class BakSneppenFitness extends FitnessFunction {
public BakSneppenFitness(double M) {
if (M >= 1.0) this.M = M;
else throw new IllegalArgumentException("M must be greater than or equal to 1");
}
public double getFitness(byte[] sequence) {
double fitness = 0.0;
double currentFitness = 1.0;
for (int i = 0; i < sequence.length; i++) {
if (sequence[i] == 0) fitness += currentFitness;
currentFitness /= M;
}
return fitness;
}
public double getFitnessFactor(int pos, byte state1, byte state2) {
throw new UnsupportedOperationException();
}
public double[][] getFitnessTable() {
throw new UnsupportedOperationException();
}
public void initializeToFittest(byte[] genome) {
throw new UnsupportedOperationException();
}
private double M = 2.0;
}