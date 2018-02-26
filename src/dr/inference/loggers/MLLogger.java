
package dr.inference.loggers;

import dr.inference.model.Likelihood;

public class MLLogger extends MCLogger {

    private final Likelihood likelihood;
    private double bestLikelihood;
    private long bestState;
    private String[] bestValues = null;
    private int logEvery = 0;

    public MLLogger(Likelihood likelihood, LogFormatter formatter, int logEvery) {

        super(formatter, logEvery, false);

        this.likelihood = likelihood;
    }

    public void startLogging() {
        bestLikelihood = Double.NEGATIVE_INFINITY;
        bestState = 0;
        bestValues = new String[getColumnCount()];

        if (logEvery > 0) {
            String[] labels = new String[getColumnCount() + 1];

            labels[0] = "state";

            for (int i = 0; i < getColumnCount(); i++) {
                labels[i + 1] = getColumnLabel(i);
            }

            logLabels(labels);
        }

        super.startLogging();
    }

    public void log(long state) {

        double lik;

        lik = likelihood.getLogLikelihood();

        if (lik > bestLikelihood) {

            for (int i = 0; i < getColumnCount(); i++) {
                bestValues[i] = getColumnFormatted(i);
            }

            bestState = state;
            bestLikelihood = lik;

            if (logEvery == 1) {

                String[] values = new String[getColumnCount() + 1];

                values[0] = Long.toString(bestState);

                System.arraycopy(bestValues, 0, values, 1, getColumnCount());

                logValues(values);
            }
        }

        if (logEvery > 1 && (state % logEvery == 0)) {

            String[] values = new String[getColumnCount() + 1];

            values[0] = Long.toString(bestState);

            System.arraycopy(bestValues, 0, values, 1, getColumnCount());

            logValues(values);
        }
    }

    public void stopLogging() {
        final int columnCount = getColumnCount();
        String[] values = new String[columnCount + 2];

        values[0] = Long.toString(bestState);
        values[1] = Double.toString(bestLikelihood);

        System.arraycopy(bestValues, 0, values, 2, columnCount);

        if (logEvery > 0) {
            logValues(values);
        } else {
            String[] labels = new String[columnCount + 2];

            labels[0] = "state";
            labels[1] = "ML";

            for (int i = 0; i < columnCount; i++) {
                labels[i + 2] = getColumnLabel(i);
            }

            logLabels(labels);
            logValues(values);
        }

        super.stopLogging();
    }
}
