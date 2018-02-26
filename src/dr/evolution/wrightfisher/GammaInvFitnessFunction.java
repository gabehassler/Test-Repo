package dr.evolution.wrightfisher;
import dr.math.MathUtils;
import dr.math.distributions.GammaDistribution;
public class GammaInvFitnessFunction extends FitnessFunction {
    public GammaInvFitnessFunction(int genomeLength, double alpha, double beta, double pInv, int stateSize, boolean randomFittest) {
        GammaDistribution gamma = new GammaDistribution(beta, alpha);
        fitness = new double[genomeLength][stateSize];
        fittest = new byte[genomeLength];
        int fitpos = 0;
        for (int i = 0; i < genomeLength; i++) {
            if (randomFittest) {
                fitpos = MathUtils.nextInt(stateSize);
            }
            fitness[i][fitpos] = 1.0;
            fittest[i] = (byte) fitpos;
            for (int j = 0; j < stateSize; j++) {
                if (j != fitpos) {
                    if (MathUtils.nextDouble() > pInv) {
                        double prob = Math.round(MathUtils.nextDouble() * 1000.0) / 1000.0;
                        while ((prob <= 0.0) || (prob >= 1.0)) {
                            prob = Math.round(MathUtils.nextDouble() * 1000.0) / 1000.0;
                        }
                        fitness[i][j] = Math.max(0.0, 1.0 - gamma.quantile(prob));
                    } else {
                        fitness[i][j] = 0.0;
                    }
                }
            }
        }
    }
    public final double getFitness(byte[] sequence) {
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
        for (int j = 0; j < fitness[0].length; j++) {
            for (int i = 0; i < fitness.length; i++) {
                System.out.print((Math.round(fitness[i][j] * 1000.0) / 1000.0) + "\t");
            }
            System.out.println();
        }
        return fitness;
    }
    public void initializeToFittest(byte[] genome) {
        System.arraycopy(fittest, 0, genome, 0, fittest.length);
    }
    double[][] fitness;
    byte[] fittest = null;
}