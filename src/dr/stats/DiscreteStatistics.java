
package dr.stats;

import dr.util.HeapSort;

public class DiscreteStatistics {
    public static double mean(double[] x) {
        double m = 0;
        int count = x.length;
        for(double aX : x) {
            if( Double.isNaN(aX) ) {
                count--;
            } else {
                m += aX;
            }
        }

        return m / (double) count;
    }

    public static double median(double[] x, int[] indices) {

        int pos = x.length / 2;
        if (x.length % 2 == 1) {
            return x[indices[pos]];
        } else {
            return (x[indices[pos - 1]] + x[indices[pos]]) / 2.0;
        }
    }


    public static double meanSquaredError(double[] x, double trueValue) {

        if (x == null || x.length == 0) {
            throw new IllegalArgumentException();
        }

        double total = 0;
        for(double sample : x) {
            total += (sample - trueValue) * (sample - trueValue);
        }
        total /= x.length;       
        return total;
    }

    public static double median(double[] x) {

        if (x == null || x.length == 0) {
            throw new IllegalArgumentException();
        }

        int[] indices = new int[x.length];
        HeapSort.sort(x, indices);

        return median(x, indices);
    }


    public static double variance(double[] x, double mean) {
        double var = 0;
        int count = x.length;
        for(double aX : x) {
            if( Double.isNaN(aX) ) {
                count--;
            } else {
                double diff = aX - mean;
                var += diff * diff;
            }
        }

        if (count < 2) {
            count = 1; // to avoid division by zero
        } else {
            count = count - 1; // for ML estimate
        }

        return var / (double) count;
    }


    @SuppressWarnings({"SuspiciousNameCombination"})
    public static double covariance(double[] x, double[] y) {

        return covariance(x, y, mean(x), mean(y), stdev(x), stdev(y));
    }

    public static double covariance(double[] x, double[] y, double xmean, double ymean, double xstdev, double ystdev) {

        if (x.length != y.length) throw new IllegalArgumentException("x and y arrays must be same length!");

        int count = x.length;
        double covar = 0.0;
        for (int i = 0; i < x.length; i++) {
            if (Double.isNaN(x[i]) || Double.isNaN(y[i])) {
                count --;
            } else {
                covar += (x[i] - xmean) * (y[i] - ymean);
            }
        }
        covar /= count;
        covar /= (xstdev * ystdev);
        return covar;
    }

    public static double skewness(double[] x) {

        double mean = mean(x);
        double stdev = stdev(x);
        double skew = 0.0;
        double len = x.length;

        for (double xv : x) {
            double diff = xv - mean;
            diff /= stdev;

            skew += (diff * diff * diff);
        }

        skew *= (len / ((len - 1) * (len - 2)));

        return skew;
    }

    public static double stdev(double[] x) {
        return Math.sqrt(variance(x));
    }

    public static double variance(double[] x) {
        final double m = mean(x);
        return variance(x, m);
    }


    public static double varianceSampleMean(double[] x, double mean) {
        return variance(x, mean) / (double) x.length;
    }

    public static double varianceSampleMean(double[] x) {
        return variance(x) / (double) x.length;
    }


    public static double quantile(double q, double[] x, int[] indices) {
        if (q < 0.0 || q > 1.0) throw new IllegalArgumentException("Quantile out of range");

        if (q == 0.0) {
            // for q==0 we have to "invent" an entry smaller than the smallest x

            return x[indices[0]] - 1.0;
        }

        return x[indices[(int) Math.ceil(q * indices.length) - 1]];
    }

    public static double quantile(double q, double[] x) {
        int[] indices = new int[x.length];
        HeapSort.sort(x, indices);

        return quantile(q, x, indices);
    }

    public static double quantile(double q, double[] x, int count) {
        int[] indices = new int[count];
        HeapSort.sort(x, indices);

        return quantile(q, x, indices);
    }

    public static double[] HPDInterval(double proportion, double[] x, int[] indices) {

        double minRange = Double.MAX_VALUE;
        int hpdIndex = 0;

        final int diff = (int) Math.round(proportion * (double) x.length);
        for (int i = 0; i <= (x.length - diff); i++) {
            final double minValue = x[indices[i]];
            final double maxValue = x[indices[i + diff - 1]];
            final double range = Math.abs(maxValue - minValue);
            if (range < minRange) {
                minRange = range;
                hpdIndex = i;
            }
        }

        return new double[]{x[indices[hpdIndex]], x[indices[hpdIndex + diff - 1]]};
    }

    public static double cdf(double z, double[] x, int[] indices) {
        int i;
        for (i = 0; i < x.length; i++) {
            if (x[indices[i]] > z) break;
        }

        return (double) i / (double) x.length;
    }

    public static double cdf(double z, double[] x) {
        int[] indices = new int[x.length];
        HeapSort.sort(x, indices);

        return cdf(z, x, indices);
    }

    public static double max(double[] x) {
        double max = x[0];
        for (int i = 1; i < x.length; i++) {
            if (x[i] > max) max = x[i];
        }
        return max;
    }

    public static double min(double[] x) {
        double min = x[0];
        for (int i = 1; i < x.length; i++) {
            if (x[i] < min) min = x[i];
        }
        return min;
    }

    public static double geometricMean(double[] x) {
        double gm = 0;
        int len = x.length;
        for (int i = 0; i < len; i++)
        {
            gm += Math.log(x[i]);
        }

        return Math.exp(gm/(double) len);
    }
}
