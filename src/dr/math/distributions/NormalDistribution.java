
package dr.math.distributions;

import dr.math.ErrorFunction;
import dr.math.MathUtils;
import dr.math.UnivariateFunction;

public class NormalDistribution implements Distribution, RandomGenerator {
    //
    // Public stuff
    //

    public NormalDistribution(double mean, double sd) {
        this.m = mean;
        this.sd = sd;
    }

    public double getMean() {
        return m;
    }

    public void setMean(double value) {
        m = value;
    }

    public double getSD() {
        return sd;
    }

    public void setSD(double value) {
        sd = value;
    }

    public double pdf(double x) {
        return pdf(x, m, sd);
    }

    public double logPdf(double x) {
        return logPdf(x, m, sd);
    }

    public double cdf(double x) {
        return cdf(x, m, sd);
    }

    public double quantile(double y) {
        return quantile(y, m, sd);
    }

    public double mean() {
        return mean(m, sd);
    }

    public double variance() {
        return variance(m, sd);
    }

    public final UnivariateFunction getProbabilityDensityFunction() {
        return pdfFunction;
    }

    private final UnivariateFunction pdfFunction = new UnivariateFunction() {
        public final double evaluate(double x) {
            return pdf(x);
        }

        public final double getLowerBound() {
            return Double.NEGATIVE_INFINITY;
        }

        public final double getUpperBound() {
            return Double.POSITIVE_INFINITY;
        }
    };

    public static double pdf(double x, double m, double sd) {
        double a = 1.0 / (Math.sqrt(2.0 * Math.PI) * sd);
        double b = -(x - m) * (x - m) / (2.0 * sd * sd);

        return a * Math.exp(b);
    }

    public static double logPdf(double x, double m, double sd) {
        double a = 1.0 / (Math.sqrt(2.0 * Math.PI) * sd);
        double b = -(x - m) * (x - m) / (2.0 * sd * sd);

        return Math.log(a) + b;
    }

    public static double cdf(double x, double m, double sd) {
        return cdf(x, m, sd, false);
//        double a = (x - m) / (Math.sqrt(2.0) * sd);
//
//        return 0.5 * (1.0 + ErrorFunction.erf(a));
    }

    public static double quantile(double z, double m, double sd) {
        return m + Math.sqrt(2.0) * sd * ErrorFunction.inverseErf(2.0 * z - 1.0);
    }

    public static double mean(double m, double sd) {
        return m;
    }

    public static double variance(double m, double sd) {
        return sd * sd;
    }


    public static double cdf(double x, double mu, double sigma, boolean log_p) {

        if (Double.isNaN(x) || Double.isNaN(mu) || Double.isNaN(sigma)) {
            return Double.NaN;
        }
        if (Double.isInfinite(x) && mu == x) { /* x-mu is NaN */
            return Double.NaN;
        }
        if (sigma <= 0) {
            if (sigma < 0) {
                return Double.NaN;
            }
            return (x < mu) ? 0.0 : 1.0;
        }
        double p = (x - mu) / sigma;
        if (Double.isInfinite(p)) {
            return (x < mu) ? 0.0 : 1.0;
        }
        return standardCDF(p, log_p);
    }

    public static double standardCDF(double x, boolean log_p) {
        boolean i_tail = false;
        if (Double.isNaN(x)) {
            return Double.NaN;
        }

        double xden, xnum, temp, del, eps, xsq, y;
        int i;
        double p = x, cp = Double.NaN;
        boolean lower, upper;
        eps = DBL_EPSILON * 0.5;
        lower = !i_tail;
        upper = i_tail;

        y = Math.abs(x);
        if (y <= 0.67448975) { /* Normal.quantile(3/4, 1, 0) = 0.67448975 */
            if (y > eps) {
                xsq = x * x;
                xnum = a[4] * xsq;
                xden = xsq;
                for (i = 0; i < 3; i++) {
                    xnum = (xnum + a[i]) * xsq;
                    xden = (xden + b[i]) * xsq;
                }
            } else {
                xnum = xden = 0.0;
            }
            temp = x * (xnum + a[3]) / (xden + b[3]);
            if (lower) {
                p = 0.5 + temp;
            }
            if (upper) {
                cp = 0.5 - temp;
            }
            if (log_p) {
                if (lower) {
                    p = Math.log(p);
                }
                if (upper) {
                    cp = Math.log(cp);
                }
            }
        } else if (y <= M_SQRT_32) {

            xnum = c[8] * y;
            xden = y;
            for (i = 0; i < 7; i++) {
                xnum = (xnum + c[i]) * y;
                xden = (xden + d[i]) * y;
            }
            temp = (xnum + c[7]) / (xden + d[7]);

            //do_del(y);
            //swap_tail;
            //#define do_del(X)							\
            xsq = ((int) (y * CUTOFF)) * 1.0 / CUTOFF;
            del = (y - xsq) * (y + xsq);
            if (log_p) {
                p = (-xsq * xsq * 0.5) + (-del * 0.5) + Math.log(temp);
                if ((lower && x > 0.0) || (upper && x <= 0.0)) {
                    cp = Math.log(1.0 - Math.exp(-xsq * xsq * 0.5) * Math.exp(-del * 0.5) * temp);
                }
            } else {
                p = Math.exp(-xsq * xsq * 0.5) * Math.exp(-del * 0.5) * temp;
                cp = 1.0 - p;
            }
            //#define swap_tail						\
            if (x > 0.0) {
                temp = p;
                if (lower) {
                    p = cp;
                }
                cp = temp;
            }
        }
        else if (log_p || (lower && -37.5193 < x && x < 8.2924)
                || (upper && -8.2924 < x && x < 37.5193)) {

            xsq = 1.0 / (x * x);
            xnum = p_[5] * xsq;
            xden = xsq;
            for (i = 0; i < 4; i++) {
                xnum = (xnum + p_[i]) * xsq;
                xden = (xden + q[i]) * xsq;
            }
            temp = xsq * (xnum + p_[4]) / (xden + q[4]);
            temp = (M_1_SQRT_2PI - temp) / y;

            //do_del(x);
            xsq = ((int) (x * CUTOFF)) * 1.0 / CUTOFF;
            del = (x - xsq) * (x + xsq);
            if (log_p) {
                p = (-xsq * xsq * 0.5) + (-del * 0.5) + Math.log(temp);
                if ((lower && x > 0.0) || (upper && x <= 0.0)) {
                    cp = Math.log(1.0 - Math.exp(-xsq * xsq * 0.5) * Math.exp(-del * 0.5) * temp);
                }
            } else {
                p = Math.exp(-xsq * xsq * 0.5) * Math.exp(-del * 0.5) * temp;
                cp = 1.0 - p;
            }
            //swap_tail;
            if (x > 0.0) {
                temp = p;
                if (lower) {
                    p = cp;
                }
                cp = temp;
            }
        } else { /* no log_p , large x such that probs are 0 or 1 */
            if (x > 0) {
                p = 1.0;
                cp = 0.0;
            } else {
                p = 0.0;
                cp = 1.0;
            }
        }
        return p;

    }

    // Private

    protected double m, sd;

    private static final double[] a = {
            2.2352520354606839287,
            161.02823106855587881,
            1067.6894854603709582,
            18154.981253343561249,
            0.065682337918207449113
    };
    private static final double[] b = {
            47.20258190468824187,
            976.09855173777669322,
            10260.932208618978205,
            45507.789335026729956
    };
    private static final double[] c = {
            0.39894151208813466764,
            8.8831497943883759412,
            93.506656132177855979,
            597.27027639480026226,
            2494.5375852903726711,
            6848.1904505362823326,
            11602.651437647350124,
            9842.7148383839780218,
            1.0765576773720192317e-8
    };
    private static final double[] d = {
            22.266688044328115691,
            235.38790178262499861,
            1519.377599407554805,
            6485.558298266760755,
            18615.571640885098091,
            34900.952721145977266,
            38912.003286093271411,
            19685.429676859990727
    };
    private static final double[] p_ = {
            0.21589853405795699,
            0.1274011611602473639,
            0.022235277870649807,
            0.001421619193227893466,
            2.9112874951168792e-5,
            0.02307344176494017303
    };
    private static final double[] q = {
            1.28426009614491121,
            0.468238212480865118,
            0.0659881378689285515,
            0.00378239633202758244,
            7.29751555083966205e-5
    };

    private static final int CUTOFF = 16; /* Cutoff allowing exact "*" and "/" */

    private static final double M_SQRT_32 = 5.656854249492380195206754896838; /* The square root of 32 */
    private static final double M_1_SQRT_2PI = 0.398942280401432677939946059934;
    private static final double DBL_EPSILON = 2.2204460492503131e-016;

    public static double standardTail(double x, boolean isUpper) {
        if (x < 0.0D) {
            isUpper = !isUpper;
            x = -x;
        }
        double d1;
        if ((x <= 8.0D) || ((isUpper) && (x <= 37.0D))) {
            double d2 = 0.5D * x * x;
            if (x >= 1.28D) {
                d1 = 0.398942280385D * Math.exp(-d2) / (x - 3.8052E-08D + 1.00000615302D / (x + 0.000398064794D + 1.98615381364D / (x - 0.151679116635D + 5.29330324926D / (x + 4.8385912808D - 15.150897245099999D / (x + 0.742380924027D + 30.789933034000001D / (x + 3.99019417011D))))));
            } else {
                d1 = 0.5D - x * (0.398942280444D - 0.399903438504D * d2 / (d2 + 5.75885480458D - 29.821355780800001D / (d2 + 2.62433121679D + 48.6959930692D / (d2 + 5.92885724438D))));
            }
        } else {
            d1 = 0.0D;
        }
        if (!isUpper) {
            d1 = 1.0D - d1;
        }
        return d1;
    }

    public static double tailCDF(double x, double mu, double sigma) {
        return standardTail((x - mu) / sigma, true);
    }

    public static double tailCDF(double x, double mu, double sigma, boolean isUpper) {
        return standardTail((x - mu) / sigma, isUpper);
    }


    public double tailCDF(double x) {
        return standardTail((x - this.m) / this.sd, true);
    }

    static void testTail(double x, double mu, double sigma) {
        double cdf1 = NormalDistribution.cdf(x, mu, sigma);
        double tail1 = 1.0 - cdf1;
        double cdf2 = NormalDistribution.cdf(x, mu, sigma, false);
        double tail2 = 1.0 - cdf2;
        double tail3 = NormalDistribution.tailCDF(x, mu, sigma);

        System.out.println(">" + x + " N(" + mu + ", " + sigma + ")");
        System.out.println("Original CDF: " + tail1);
        System.out.println("     New CDF: " + tail2);
        System.out.println("     tailCDF: " + tail3);
    }

    public static void main(String[] args) {
        testTail(0.1, 0.0, 1.0);
        System.out.println();
        testTail(1, 0.0, 1.0);
        System.out.println();
        testTail(5, 0.0, 1.0);
        System.out.println();
        testTail(7, 0.0, 1.0);
        System.out.println();
        testTail(8, 0.0, 1.0);
        System.out.println();
        testTail(8.25, 0.0, 1.0);
        System.out.println();
        testTail(10, 0.0, 1.0);
    }

    // RandomGenerator interface
    public Object nextRandom() {
        double eps = MathUtils.nextGaussian();
        eps *= getSD();
        eps += getMean();
        return eps;
    }

    public double logPdf(Object x) {
        double v = (Double) x;
        return logPdf(x);
    }
}
