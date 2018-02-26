package dr.inference.markovjumps;
import dr.math.MathUtils;
public class UniformizedStateHistory extends StateHistory {
    public UniformizedStateHistory(double startingTime, int startingState, int stateCount, double[] lambda) {
        this(startingTime, startingState, stateCount, new SubordinatedProcess(lambda, stateCount));
    }
    protected UniformizedStateHistory(double startingTime, int startingState, int stateCount,
                                      SubordinatedProcess subordinator) {
        super(startingTime, startingState, stateCount);
        this.subordinator = subordinator;
    }
    public SubordinatedProcess getSubordinatedProcess() {
        return subordinator;
    }
    public static StateHistory simulateUnconditionalOnEndingState(double startingTime,
                                                                  int startingState,
                                                                  double endingTime,
                                                                  double[] lambda,
                                                                  int stateCount) {
        throw new RuntimeException("Impossible to simulate an unconditioned CTMC using Uniformization");
    }
    public static StateHistory simulateConditionalOnEndingState(double startingTime,
                                                                int startingState,
                                                                double endingTime,
                                                                int endingState,
                                                                double transitionProbability,
                                                                double[] lambda,
                                                                int stateCount) throws SubordinatedProcess.Exception {
        return simulateConditionalOnEndingState(startingTime, startingState, endingTime, endingState,
                transitionProbability, stateCount, new SubordinatedProcess(lambda, stateCount));
    }
    public static StateHistory simulateConditionalOnEndingState(double startingTime,
                                                                int startingState,
                                                                double endingTime,
                                                                int endingState,
                                                                double transitionProbability,
                                                                int stateCount,
                                                                SubordinatedProcess subordinator) throws SubordinatedProcess.Exception {
        StateHistory history = new UniformizedStateHistory(startingTime, startingState, stateCount, subordinator);
        double timeDuration = endingTime - startingTime;
        int stateChanges = subordinator.drawNumberOfChanges(startingState, endingState, timeDuration,
                transitionProbability);
        if (stateChanges == 0) {
            // Do nothing
        } else if (stateChanges == 1) {
            if (startingState == endingState) {
                // Do nothing, just a single pseudo-transition
            } else {
                double transitionTime = (timeDuration) * MathUtils.nextDouble();
                history.addChange(new StateChange(startingTime + transitionTime, endingState));
            }
        } else { // More than one transition; real work to do
            double[] transitionTimes = subordinator.drawTransitionTimes(timeDuration, stateChanges);
            int currentState = startingState;
            for (int i = 1; i < stateChanges; i++) {
                int nextState = subordinator.drawNextChainState(currentState, endingState, stateChanges, i);
                if (nextState != currentState) {
                    history.addChange(new StateChange(startingTime + transitionTimes[i-1], nextState));
                    currentState = nextState;
                }
            }
            if (currentState != endingState) {
                history.addChange(new StateChange(startingTime + transitionTimes[stateChanges-1], endingState));
            }
        }
        history.addEndingState(new StateChange(endingTime, endingState));
        return history;
    }
    final private SubordinatedProcess subordinator;
}
