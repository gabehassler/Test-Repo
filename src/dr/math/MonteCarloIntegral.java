
package dr.math;

public class MonteCarloIntegral implements Integral {

	public MonteCarloIntegral(int sampleSize) {
		this.sampleSize = sampleSize;
	}

	public double integrate(UnivariateFunction f, double min, double max) {
	
		double integral = 0.0;
		
		double range = (max - min);
		for (int i =1; i <= sampleSize; i++) {
			integral += f.evaluate((MathUtils.nextDouble() * range) + min);
		}
		integral *= range/(double)sampleSize;
		return integral;
	}
	
	private int sampleSize;
}
