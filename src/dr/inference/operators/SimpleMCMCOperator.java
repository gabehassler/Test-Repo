
package dr.inference.operators;

import dr.inference.model.Likelihood;
import dr.inference.prior.Prior;

public abstract class SimpleMCMCOperator implements MCMCOperator {

    public double getTargetAcceptanceProbability() {
        return targetAcceptanceProb;
    }

    public void setTargetAcceptanceProbability(double tap) {
        targetAcceptanceProb = tap;
    }

    public double getMinimumAcceptanceLevel() {
        return 0.05;
    }

    public double getMaximumAcceptanceLevel() {
        return 0.50;
    }

    public double getMinimumGoodAcceptanceLevel() {
        return 0.10;
    }

    public double getMaximumGoodAcceptanceLevel() {
        return 0.40;
    }

    public abstract String getOperatorName();

    public final double getWeight() {
        return weight;
    }

    public void setPathParameter(double beta) {
        throw new IllegalArgumentException("Path parameter has no effect on Metropolis-Hastings kernels." +
        "\nGibbs samplers need an implementation for use in power-posteriors");
    }

    public final void setWeight(double w) {
        if( w > 0 ) {
            weight = w;
        } else {
            throw new IllegalArgumentException(
                    "Weight must be a positive real, but tried to set weight to "
                            + w);
        }
    }

    public void accept(double deviation) {
        lastDeviation = deviation;

        if( !operateAllowed ) {
            operateAllowed = true;
            acceptCount += 1;
            sumDeviation += deviation;

//            spanDeviation[0] = Math.min(spanDeviation[0], deviation);
//            spanDeviation[1] = Math.max(spanDeviation[1], deviation);
//            spanCount += 1;
        } else {
            throw new RuntimeException(
                    "Accept/reject methods called twice without operate called in between!");
        }
    }

    public void reject() {
        if( !operateAllowed ) {
            operateAllowed = true;
            rejectCount += 1;
        } else {
            throw new RuntimeException(
                    "Accept/reject methods called twice without operate called in between!");
        }
    }

    public void reset() {
        operateAllowed = true;
        acceptCount = 0;
        rejectCount = 0;
        lastDeviation = 0.0;
        sumDeviation = 0.0;
    }

    public final int getCount() {
        return acceptCount + rejectCount;
    }

    public final int getAcceptCount() {
        return acceptCount;
    }

    public final void setAcceptCount(int acceptCount) {
        this.acceptCount = acceptCount;
    }

    public final int getRejectCount() {
        return rejectCount;
    }

    public final void setRejectCount(int rejectCount) {
        this.rejectCount = rejectCount;
    }

    public final double getMeanDeviation() {
        return sumDeviation / acceptCount;
    }

    public final double getDeviation() {
        return lastDeviation;
    }

    public final double getSumDeviation() {
        return sumDeviation;
    }

    public final void setSumDeviation(double sumDeviation) {
        this.sumDeviation = sumDeviation;
    }

//    public double getSpan(boolean reset) {
//        double span = 0;
//        if( spanDeviation[1] > spanDeviation[0] && spanCount > 2000 ) {
//            span = spanDeviation[1] - spanDeviation[0];
//
//            if( reset ) {
//                spanDeviation[0] = Double.MAX_VALUE;
//                spanDeviation[1] = -Double.MAX_VALUE;
//                spanCount = 0;
//            }
//        }
//        return span;
//    }

    public final double operate() throws OperatorFailedException {
        if( operateAllowed ) {
            operateAllowed = false;
            return doOperation();
        } else {
            throw new RuntimeException(
                    "Operate called twice without accept/reject in between!");
        }
    }

    public final double operate(Prior prior, Likelihood likelihood)
            throws OperatorFailedException {
        if( operateAllowed ) {
            operateAllowed = false;
            return doOperation(prior, likelihood);
        } else {
            throw new RuntimeException(
                    "Operate called twice without accept/reject in between!");
        }
    }

    public final double getAcceptanceProbability() {
        return (double) acceptCount / (double) (acceptCount + rejectCount);
    }

    public double doOperation(Prior prior, Likelihood likelihood)
            throws OperatorFailedException {
        return 0.0;
    }

    public double getMeanEvaluationTime() {
        return (double) sumEvaluationTime / (double) (acceptCount + rejectCount);
    }

    public long getTotalEvaluationTime() {
        return sumEvaluationTime;
    }

    public void addEvaluationTime(long time) {
        sumEvaluationTime += time;
    }

    public abstract double doOperation() throws OperatorFailedException;

    private double weight = 1.0;
    private int acceptCount = 0;
    private int rejectCount = 0;

    private double sumDeviation = 0.0;
    private double lastDeviation = 0.0;

    private boolean operateAllowed = true;
    private double targetAcceptanceProb = 0.234;

    private long sumEvaluationTime = 0;

//    private final double[] spanDeviation = {Double.MAX_VALUE, -Double.MAX_VALUE};
//    private int spanCount = 0;
}
