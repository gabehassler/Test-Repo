package dr.math;
import java.util.Arrays;
import java.util.HashMap;
public class MultivariateMonteCarloIntegral implements MultivariateIntegral {
    // bins is the number of divisions that each axis it split into (so the full number of bins is this to the
    // power of the dimension of the function
    // sampleSize is the number of samples PER BIN
    public MultivariateMonteCarloIntegral(int sampleSize, int bins) {
        this.sampleSize = sampleSize;
        this.bins = bins;
    }
    public MultivariateMonteCarloIntegral(int sampleSize) {
        this(sampleSize, 1);
    }
    public double integrate(MultivariateFunction f, double[] mins, double[] maxes) {
        int dim = f.getNumArguments();
        int totalBins = bins*dim;
        double[] steps = new double[dim];
        double totalArea=1;
        for(int i=0; i<dim; i++){
            totalArea *= (maxes[i]-mins[i]);
        }
        HashMap<Integer, double[]> binCorners = new HashMap<Integer, double[]>();
        double[] currentCorner = new double[dim];
        for(int index=0; index<totalBins; index++){
            binCorners.put(index, Arrays.copyOf(currentCorner, dim));
            int dimToCheck = 0;
            while(dimToCheck<dim){
                if(currentCorner[dimToCheck]+steps[dimToCheck]<maxes[dimToCheck]){
                    currentCorner[dimToCheck] += steps[dimToCheck];
                    break;
                } else {
                    currentCorner[dimToCheck] = mins[dimToCheck];
                }
                dimToCheck++;
            }
        }
        double integral = 0.0;
        for(int i=0; i<totalBins; i++){
            for (int j=1; j <= sampleSize; j++) {
                double[] sample = new double[dim];
                for(int k=0; k<sample.length; k++){
                    sample[k] = binCorners.get(i)[k] + MathUtils.nextDouble()*(steps[k]);
                }
                integral += f.evaluate(sample);
            }
        }
        integral *= totalArea/((double)sampleSize*totalBins);
        return integral;
    }
    protected int getSampleSize(){
        return sampleSize;
    }
    protected int getBins(){
        return bins;
    }
    private int sampleSize;
    private int bins;
}
