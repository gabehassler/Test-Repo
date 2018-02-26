package dr.math;
public class Binomial
{
	//
	// Public stuff; lnGamma is used, so parameters can be doubles.
	//
	public static double logChoose(double n, double k){
		return GammaFunction.lnGamma(n + 1.0) -	GammaFunction.lnGamma(k + 1.0) 
			- GammaFunction.lnGamma(n - k + 1.0);
	}
	public static double choose(double n, double k)
	{
		n = Math.floor(n + 0.5);
		k = Math.floor(k + 0.5);
		double lchoose = GammaFunction.lnGamma(n + 1.0) -
		GammaFunction.lnGamma(k + 1.0) - GammaFunction.lnGamma(n - k + 1.0);
		return Math.floor(Math.exp(lchoose) + 0.5);
	}
	public static double choose2(int n)
	{
		// not sure how much overhead there is with try-catch blocks
		// i.e. would an if statement be better?
		try {
			return choose2LUT[n];
		} catch (ArrayIndexOutOfBoundsException e) {
			if( n < 0 ) {
                return 0;
            }
			while (maxN < n) {
				maxN += 1000;
			}
			initialize();
			return choose2LUT[n];
		}
	}
	private static void initialize() {
		choose2LUT = new double[maxN+1];
		choose2LUT[0] = 0;
		choose2LUT[1] = 0;
		choose2LUT[2] = 1;
		for (int i = 3; i <= maxN; i++) {
			choose2LUT[i] = ((double) (i*(i-1))) * 0.5;
		}
	}
	private static int maxN = 5000;
	private static double[] choose2LUT;
	static {
		initialize();
	}
}
