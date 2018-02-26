
package dr.inference.trace;

import java.util.List;

public class TraceCorrelation<T> extends TraceDistribution<T> {
    final int stepSize;

    public TraceCorrelation(List<T> values, TraceFactory.TraceType traceType, int stepSize) {
        super(values, traceType, stepSize);
        this.stepSize = stepSize;

        if (isValid) {
            analyseCorrelation(values, stepSize);
        }
    }

    public double getStdErrorOfMean() {
        return stdErrorOfMean;
    }

    public double getACT() {
        return ACT;
    }

    private void analyseCorrelation(List<T> values, int stepSize) {
//        this.values = values; // move to TraceDistribution(T[] values)

        if (getTraceType() == TraceFactory.TraceType.DOUBLE
                || getTraceType() == TraceFactory.TraceType.INTEGER) {
            double[] doubleValues = new double[values.size()];
            for (int i = 0; i < values.size(); i++) {
                doubleValues[i] = ((Number) values.get(i)).doubleValue();
            }
            analyseCorrelationContinuous(doubleValues, stepSize);

        } else if (getTraceType() == TraceFactory.TraceType.STRING) {


        } else {
            throw new RuntimeException("Trace type is not recognized");
        }
    }

    private void analyseCorrelationContinuous(double[] values, int stepSize) {

        final int samples = values.length;
        int maxLag = Math.min(samples - 1, MAX_LAG);

        double[] gammaStat = new double[maxLag];
        //double[] varGammaStat = new double[maxLag];
        double varStat = 0.0;
        //double varVarStat = 0.0;
        //double assVarCor = 1.0;
        //double del1, del2;

        for (int lag = 0; lag < maxLag; lag++) {
            for (int j = 0; j < samples - lag; j++) {
                final double del1 = values[j] - mean;
                final double del2 = values[j + lag] - mean;
                gammaStat[lag] += (del1 * del2);
                //varGammaStat[lag] += (del1*del1*del2*del2);
            }

            gammaStat[lag] /= ((double) (samples - lag));
            //varGammaStat[lag] /= ((double) samples-lag);
            //varGammaStat[lag] -= (gammaStat[0] * gammaStat[0]);

            if (lag == 0) {
                varStat = gammaStat[0];
                //varVarStat = varGammaStat[0];
                //assVarCor = 1.0;
            } else if (lag % 2 == 0) {
                // fancy stopping criterion :)
                if (gammaStat[lag - 1] + gammaStat[lag] > 0) {
                    varStat += 2.0 * (gammaStat[lag - 1] + gammaStat[lag]);
                    // varVarStat += 2.0*(varGammaStat[lag-1] + varGammaStat[lag]);
                    // assVarCor  += 2.0*((gammaStat[lag-1] * gammaStat[lag-1]) + (gammaStat[lag] * gammaStat[lag])) / (gammaStat[0] * gammaStat[0]);
                }
                // stop
                else
                    maxLag = lag;
            }
        }

        // standard error of mean
        stdErrorOfMean = Math.sqrt(varStat / samples);

        // auto correlation time
        ACT = stepSize * varStat / gammaStat[0];

        // effective sample size
        ESS = (stepSize * samples) / ACT;

        // standard deviation of autocorrelation time
        stdErrOfACT = (2.0 * Math.sqrt(2.0 * (2.0 * (double) (maxLag + 1)) / samples) * (varStat / gammaStat[0]) * stepSize);

        isValid = true;
    }

    //************************************************************************
    // private methods
    //************************************************************************

    protected double stdErrorOfMean;
    protected double stdErrorOfVariance;
    protected double ACT;
    protected double stdErrOfACT;

    private static final int MAX_LAG = 2000;
}