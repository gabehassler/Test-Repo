
package dr.inference.operators;

import java.io.Serializable;

public interface MCMCOperator extends Serializable {

    public static final String WEIGHT = "weight";

// This attribute is now called AUTO_OPTIMIZE and is in CoercableMCMCOperator
//	public static final String ADAPT = "adapt";

    double operate() throws OperatorFailedException;

    void accept(double deviation);

    void reject();

    void reset();

    int getCount();

    int getAcceptCount();

    void setAcceptCount(int acceptCount);

    int getRejectCount();

    void setRejectCount(int rejectCount);

    double getMeanDeviation();

    double getSumDeviation();

    //double getSpan(boolean reset);

    void setSumDeviation(double sumDeviation);

    double getTargetAcceptanceProbability();

    double getMinimumAcceptanceLevel();

    double getMaximumAcceptanceLevel();

    double getMinimumGoodAcceptanceLevel();

    double getMaximumGoodAcceptanceLevel();

    String getPerformanceSuggestion();

    double getWeight();

    void setWeight(double weight);

    String getOperatorName();

    double getMeanEvaluationTime();

    void addEvaluationTime(long time);

    long getTotalEvaluationTime();

    class Utils {

        public static double getAcceptanceProbability(MCMCOperator op) {
            final int accepted = op.getAcceptCount();
            final int rejected = op.getRejectCount();
            return (double) accepted / (double) (accepted + rejected);
        }

        public static int getOperationCount(MCMCOperator op) {
            return op.getAcceptCount() + op.getRejectCount();
        }
    }
}
