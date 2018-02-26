
package dr.math;

import java.text.NumberFormat;
import java.text.ParseException;

import dr.util.NumberFormatter;

public class MathUtils {

	private MathUtils() {
	}

	private static final MersenneTwisterFast random = MersenneTwisterFast.DEFAULT_INSTANCE;

	// Chooses one category if a cumulative probability distribution is given
	public static int randomChoice(double[] cf) {

		double U = MathUtils.nextDouble();

		int s;
		if (U <= cf[0]) {
			s = 0;
		} else {
			for (s = 1; s < cf.length; s++) {
				if (U <= cf[s] && U > cf[s - 1]) {
					break;
				}
			}
		}

		return s;
	}


	public static int randomChoicePDF(double[] pdf) {

		double U = MathUtils.nextDouble() * getTotal(pdf);
		for (int i = 0; i < pdf.length; i++) {

			U -= pdf[i];
			if (U < 0.0) {
				return i;
			}

		}
		for (int i = 0; i < pdf.length; i++) {
			System.out.println(i + "\t" + pdf[i]);
		}
		throw new Error("randomChoicePDF falls through -- negative, infinite or NaN components in input " +
                "distribution, or all zeroes?");
	}

    public static int randomChoiceLogPDF(double[] logpdf) {

        double scalingFactor=Double.NEGATIVE_INFINITY;

        for (double aLogpdf : logpdf) {
            if (aLogpdf > scalingFactor) {
                scalingFactor = aLogpdf;
            }
        }

        if(scalingFactor == Double.NEGATIVE_INFINITY){
            throw new Error("randomChoiceLogPDF falls through -- all -INF components in input distribution");
        }

        for(int j=0; j<logpdf.length; j++){
            logpdf[j] = logpdf[j] - scalingFactor;
        }

        double[] pdf = new double[logpdf.length];

        for(int j=0; j<logpdf.length; j++){
            pdf[j] = Math.exp(logpdf[j]);
        }

        return randomChoicePDF(pdf);

    }

	public static double[] getNormalized(double[] array) {
		double[] newArray = new double[array.length];
		double total = getTotal(array);
		for (int i = 0; i < array.length; i++) {
			newArray[i] = array[i] / total;
		}
		return newArray;
	}


	public static double getTotal(double[] array, int start, int end) {
		double total = 0.0;
		for (int i = start; i < end; i++) {
			total += array[i];
		}
		return total;
	}

	public static double getTotal(double[] array) {
		return getTotal(array, 0, array.length);

	}

	// ===================== (Synchronized) Static access methods to the private random instance ===========

	public static long getSeed() {
		synchronized (random) {
			return random.getSeed();
		}
	}

	public static void setSeed(long seed) {
		synchronized (random) {
			random.setSeed(seed);
		}
	}

	public static byte nextByte() {
		synchronized (random) {
			return random.nextByte();
		}
	}

	public static boolean nextBoolean() {
		synchronized (random) {
			return random.nextBoolean();
		}
	}

	public static void nextBytes(byte[] bs) {
		synchronized (random) {
			random.nextBytes(bs);
		}
	}

	public static char nextChar() {
		synchronized (random) {
			return random.nextChar();
		}
	}

	public static double nextGaussian() {
		synchronized (random) {
			return random.nextGaussian();
		}
	}
	
	//Mean = alpha / lambda
	//Variance = alpha / (lambda*lambda)

	public static double nextGamma(double alpha, double lambda) {
		synchronized (random) {
			return random.nextGamma(alpha, lambda);
		}
	}

    //Mean = alpha/(alpha+beta)
    //Variance = (alpha*beta)/(alpha+beta)^2*(alpha+beta+1)

    public static double nextBeta(double alpha, double beta){
        double x = nextGamma(alpha, 1);
        double y = nextGamma(beta, 1);
        return x/(x+y);
    }


	public static double nextDouble() {
		synchronized (random) {
			return random.nextDouble();
		}
	}

	public static double randomLogDouble() {
		return Math.log(nextDouble());
	}

	public static double nextExponential(double lambda) {
		synchronized (random) {
			return -1.0 * Math.log(1 - random.nextDouble()) / lambda;
		}
	}

	public static double nextInverseGaussian(double mu, double lambda) {
		synchronized (random) {
            double v = random.nextGaussian();   // sample from a normal distribution with a mean of 0 and 1 standard deviation
            double y = v * v;
            double x = mu + (mu * mu * y)/(2 * lambda) - (mu/(2 * lambda)) * Math.sqrt(4 * mu * lambda * y + mu * mu * y * y);
            double test = MathUtils.nextDouble();  // sample from a uniform distribution between 0 and 1
            if (test <= (mu) / (mu + x)) {
                return x;
            }
            else {
                return (mu * mu) / x;
            }
		}
	}


	public static float nextFloat() {
		synchronized (random) {
			return random.nextFloat();
		}
	}

	public static long nextLong() {
		synchronized (random) {
			return random.nextLong();
		}
	}

	public static short nextShort() {
		synchronized (random) {
			return random.nextShort();
		}
	}

	public static int nextInt() {
		synchronized (random) {
			return random.nextInt();
		}
	}

	public static int nextInt(int n) {
		synchronized (random) {
			return random.nextInt(n);
		}
	}

    public static double uniform(double low, double high) {
        return low + nextDouble() * (high - low);
    }

	public static void shuffle(int[] array) {
		synchronized (random) {
			random.shuffle(array);
		}
	}

	public static void shuffle(int[] array, int numberOfShuffles) {
		synchronized (random) {
			random.shuffle(array, numberOfShuffles);
		}
	}

	public static int[] shuffled(int l) {
		synchronized (random) {
			return random.shuffled(l);
		}
	}


	public static int[] sampleIndicesWithReplacement(int length) {
		synchronized (random) {
			int[] result = new int[length];
			for (int i = 0; i < length; i++)
				result[i] = random.nextInt(length);
			return result;
		}
	}

	public static void permute(int[] array) {
		synchronized (random) {
			random.permute(array);
		}
	}

	public static int[] permuted(int l) {
		synchronized (random) {
			return random.permuted(l);
		}
	}


    public static double logHyperSphereVolume(int dimension, double radius) {
        return dimension * (0.5723649429247001 + Math.log(radius)) +
                -GammaFunction.lnGamma(dimension / 2.0 + 1.0);
    }

    public static double hypot(double a, double b) {
	double r;
	if (Math.abs(a) > Math.abs(b)) {
		r = b/a;
		r = Math.abs(a)*Math.sqrt(1+r*r);
	} else if (b != 0) {
		r = a/b;
		r = Math.abs(b)*Math.sqrt(1+r*r);
	} else {
		r = 0.0;
	}
	return r;
    }
    
    public static double round(double value, int sf) {
        NumberFormatter formatter = new NumberFormatter(sf);
        try {
            return NumberFormat.getInstance().parse(formatter.format(value)).doubleValue();
        } catch (ParseException e) {
            return value;
        }
    }
}
