package dr.inference.mcmc;
import dr.inference.markovchain.MarkovChain;
public class MCMCOptions {
    private final long chainLength;
    private final long fullEvaluationCount;
    private final int minOperatorCountForFullEvaluation;
    private final double evaluationTestThreshold;
    private final boolean coercion;
    private final long coercionDelay;
    private final double temperature;
    public MCMCOptions(long chainLength) {
        this(chainLength, 2000, 1, MarkovChain.EVALUATION_TEST_THRESHOLD, true, 0, 1.0);
    }
    public MCMCOptions(long chainLength, long fullEvaluationCount, int minOperatorCountForFullEvaluation, double evaluationTestThreshold, boolean coercion, long coercionDelay, double temperature) {
        this.chainLength = chainLength;
        this.fullEvaluationCount = fullEvaluationCount;
        this.minOperatorCountForFullEvaluation = minOperatorCountForFullEvaluation;
        this.evaluationTestThreshold = evaluationTestThreshold;
        this.coercion = coercion;
        this.coercionDelay = coercionDelay;
        this.temperature = temperature;
    }
    public final long getChainLength() {
        return chainLength;
    }
    public final long getFullEvaluationCount() {
        return fullEvaluationCount;
    }
    public double getEvaluationTestThreshold() {
        return evaluationTestThreshold;
    }
    public final boolean useCoercion() {
        return coercion;
    }
    public final long getCoercionDelay() {
        return coercionDelay;
    }
    public final double getTemperature() {
        return temperature;
    }
    public int minOperatorCountForFullEvaluation() {
        return minOperatorCountForFullEvaluation;
    }
}
