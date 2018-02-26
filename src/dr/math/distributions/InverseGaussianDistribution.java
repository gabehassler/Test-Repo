
package dr.math.distributions;

import dr.math.UnivariateFunction;
import dr.math.interfaces.OneVariableFunction;
import dr.math.iterations.BisectionZeroFinder;
import dr.math.iterations.NewtonZeroFinder;

public class InverseGaussianDistribution implements Distribution {
    //
    // Public stuff
    //

    public InverseGaussianDistribution(double mean, double shape) {
        this.m = mean;
        this.shape = shape;
        this.sd = calculateSD(mean, shape);
    }

    public double getMean() {
        return m;
    }

    public void setMean(double value) {
        m = value;
    }

    public double getShape() {
        return shape;
    }

    public void setShape(double value) {
        shape = value;
        sd = calculateSD(m, shape);
    }

    public static double calculateSD(double mean, double shape) {
        return Math.sqrt((mean * mean * mean) / shape);
    }
    //public double getSD() {
    //return sd;
    //}

    //public void setSD(double value) {
    //sd = value;
    //}

    public double pdf(double x) {
        return pdf(x, m, shape);
    }

    public double logPdf(double x) {
        return logPdf(x, m, shape);
    }

    public double cdf(double x) {
        return cdf(x, m, shape);
    }

    public double quantile(double y) {
        return quantile(y, m, shape);
    }

    public double mean() {
        return mean(m, shape);
    }

    public double variance() {
        return variance(m, shape);
    }

    public final UnivariateFunction getProbabilityDensityFunction() {
        return pdfFunction;
    }

    private UnivariateFunction pdfFunction = new UnivariateFunction() {
        public final double evaluate(double x) {
            return pdf(x);
        }

        public final double getLowerBound() {
            return 0.0;
            //return Double.NEGATIVE_INFINITY;
        }

        public final double getUpperBound() {
            return Double.POSITIVE_INFINITY;
        }
    };

    public static double pdf(double x, double m, double shape) {
        double a = Math.sqrt(shape / (2.0 * Math.PI * x * x * x));
        double b = ((-shape) * (x - m) * (x - m)) / (2.0 * m * m * x);
        return a * Math.exp(b);
    }

    public static double logPdf(double x, double m, double shape) {
        double a = Math.sqrt(shape / (2.0 * Math.PI * x * x * x));
        double b = ((-shape) * (x - m) * (x - m)) / (2.0 * m * m * x);
        return Math.log(a) + b;
    }

    public static double cdf(double x, double m, double shape) {
        if (x <= 0 || m <= 0 || shape <= 0) {
            return Double.NaN;
        }
        double a = Math.sqrt(shape / x);
        double b = x / m;
        //double p1 = NormalDistribution.cdf(a*(b - 1.0),0.0,1.0);
        double p1 = NormalDistribution.cdf(a * (b - 1.0), 0.0, 1.0, false);
        //double p2 = NormalDistribution.cdf(-a*(b + 1.0),0.0,1.0);
        double p2 = NormalDistribution.cdf(-a * (b + 1.0), 0.0, 1.0, false);
        if (p2 == 0.0) {
            return p1;
        }
        else {
            double c=2.0 * shape / m;
            if (c>=0x1.fffffffffffffP+1023) {// Double.MAX_EXPONENT is Java 1.6 feature
                return Double.POSITIVE_INFINITY;
            }
            return p1 + Math.exp(c) * p2;
        }

//        double a = Math.sqrt(shape / (2.0 * x)) * ((x / m) - 1);
//        double b = (1.0 + ErrorFunction.erf(a));
//        double c = Math.sqrt(shape / (2.0 * x)) * ((x / m) + 1);
//        double d = ((2.0 * shape) / m) + Math.log(1 - ErrorFunction.erf(c));
//        return 0.5*b + 0.5*Math.exp(d);
    }

    public static double quantile(double z, double m, double shape) {
        if(z < 0.01 || z > 0.99) {
            throw new RuntimeException("Quantile is too low/high to calculate (numerical estimation for extreme values is incomplete");
        }

        double initialGuess;
        if (shape / m > 2.0) {
            initialGuess=(NormalDistribution.quantile(z,0.0,1.0)-0.5*Math.sqrt(m/shape))/Math.sqrt(shape/m);
            initialGuess=m*Math.exp(initialGuess);
        }
        else {
            initialGuess=shape/(GammaDistribution.quantile(1.0-z,0.5,1.0)*2.0);
            if (initialGuess > m / 2.0) {		// too large for the gamma approx
                initialGuess=m*Math.exp(GammaDistribution.quantile(z,0.5,1.0)*0.1);  // this seems to work for the upper tail ???
            }
        }
//        double phi = shape / m;
//        if(phi>50.0) {
            // Use Normal Distribution
//            initialGuess = (NormalDistribution.quantile(z, m,Math.sqrt(m*m*m/shape)));//-0.5*Math.sqrt(m/shape))/Math.sqrt(m*m*m/shape);
//        }

        final InverseGaussianDistribution f = new InverseGaussianDistribution(m, shape);
        final double y = z;
        NewtonZeroFinder zeroFinder = new NewtonZeroFinder(new OneVariableFunction() {
            public double value (double x) {
                return f.cdf(x) - y;
            }
        }, initialGuess);
        zeroFinder.evaluate();

        if(Double.isNaN(zeroFinder.getResult()) || zeroFinder.getPrecision() > 0.000005) {
            zeroFinder = new NewtonZeroFinder(new OneVariableFunction() {
                public double value (double x) {
                    return f.cdf(x) - y;
                }
            }, initialGuess);
            zeroFinder.initializeIterations();
            int i;
            double previousPrecision = 0.0, previousResult = Double.NaN;
            double max = 10000.0, min = 0.00001;
            for(i=0; i < 50; i++) {
                zeroFinder.evaluateIteration();
                double precision = f.cdf(zeroFinder.getResult()) - z;
                if((previousPrecision > 0 && precision < 0) || (previousPrecision < 0 && precision > 0))  {
                    max = Math.max(previousResult, zeroFinder.getResult());
                    min = Math.min(previousResult, zeroFinder.getResult());
                    max = Math.min(10000.0, max);
                    break;
                }

                previousPrecision = precision;
                previousResult = zeroFinder.getResult();

            }
            return calculateZeroFinderApproximation(z, m, shape, min, max, initialGuess);
        }
        return zeroFinder.getResult();
    }

    private static double calculateZeroFinderApproximation(double z, double m, double shape, double min, double max, double initialGuess) {
        final InverseGaussianDistribution f = new InverseGaussianDistribution(m, shape);
        final double y = z;
        BisectionZeroFinder bisectionZeroFinder = new BisectionZeroFinder(new OneVariableFunction() {
            public double value(double x) {
                return f.cdf(x) - y;
            }
        }, min, max);
        bisectionZeroFinder.setInitialValue(initialGuess);
        bisectionZeroFinder.initializeIterations();

        double bestValue = Double.NaN; /* I found that the converged value is not necesssarily the best */
        double bestPrecision = 10;
        double precision = 10;
        double previousPrecision = 10;
        int count = 0;
        while(precision > 0.001 &&  count < 10) {
            bisectionZeroFinder.evaluateIteration();
            precision = Math.abs(f.cdf(bisectionZeroFinder.getResult()) - z);
            if(precision < bestPrecision) {
                bestPrecision = precision;
                bestValue = bisectionZeroFinder.getResult();
            }
            else if(previousPrecision == precision) {
                count++;
            }
            previousPrecision = precision;
        }
        bisectionZeroFinder.finalizeIterations();
        //return bisectionZeroFinder.getResult();
        return bestValue;
    }


    private static double calculateShiftedGammaApproximation(double z, double m, double shape) {
        double a = (3 * m * m) / (4 * shape);
        double b = (m / 3);
        double nu  = (8 * shape) / (9 * m);
        return a * ChiSquareDistribution.quantile(z, nu) + b;
    }

    private static double calculateShiftedGammaApproximationWithRIG(double z, double m, double shape) {
        double a = (3 * shape + 8 * m)/(4 * shape * (shape + 2 * m));
        double b = (shape + 3 * m)/(m * (3 * shape + 8 * m));
        double nu = (8 * Math.pow((shape + 2 * m), 3)) / (m * Math.pow((8 * m + 3 * shape), 2));
        double y_hat = a * ChiSquareDistribution.quantile(z, nu) + b;
        return 1 / y_hat;
    }

    private static double calculateZeroFinderApproximation(double z, double m, double shape, int numIterations, double min, double max) {
        final InverseGaussianDistribution f = new InverseGaussianDistribution(m, shape);
        final double y = z;
        BisectionZeroFinder bisectionZeroFinder = new BisectionZeroFinder(new OneVariableFunction() {
            public double value(double x) {
                return f.cdf(x) - y;
            }
        }, min, max);
        //}, 0.0001, 100000);

        bisectionZeroFinder.setMaximumIterations(numIterations);
        bisectionZeroFinder.evaluate();
        return bisectionZeroFinder.getResult();
    }
    
    public static double mean(double m, double shape) {
        return m;
    }

    public static double variance(double m, double shape) {
        double sd = calculateSD(m, shape);
        return sd * sd;
    }

    // Private

    protected double m, sd, shape;

}
