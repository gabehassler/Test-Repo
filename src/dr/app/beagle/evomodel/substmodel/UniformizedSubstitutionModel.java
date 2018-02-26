package dr.app.beagle.evomodel.substmodel;
import dr.inference.markovjumps.MarkovJumpsType;
import dr.inference.markovjumps.StateHistory;
import dr.inference.markovjumps.SubordinatedProcess;
import dr.inference.markovjumps.UniformizedStateHistory;
import dr.inference.model.Model;
import java.util.logging.Logger;
public class UniformizedSubstitutionModel extends MarkovJumpsSubstitutionModel {
    public UniformizedSubstitutionModel(SubstitutionModel substModel) {
        this(substModel, MarkovJumpsType.COUNTS);
    }
    public UniformizedSubstitutionModel(SubstitutionModel substModel, MarkovJumpsType type) {
        this(substModel, type, 1);
    }
    public UniformizedSubstitutionModel(SubstitutionModel substModel, MarkovJumpsType type, int numSimulants) {
        super(substModel, type);
        this.numSimulants = numSimulants;
        updateSubordinator = true;
    }
    protected void setupStorage() {
        super.setupStorage();
        tmp = new double[stateCount * stateCount];
    }
    protected void storeState() {
        storedSubordinator = subordinator;
    }
    protected void restoreState() {
        subordinator = storedSubordinator;
    }
    private void constructSubordinator() {
        substModel.getInfinitesimalMatrix(tmp);
        subordinator = new SubordinatedProcess(tmp, stateCount);
        updateSubordinator = false;
    }
    protected void handleModelChangedEvent(Model model, Object object, int index) {
        if (model == substModel) {
            updateSubordinator = true;
        }
        super.handleModelChangedEvent(model, object, index);
    }
    public void setSaveCompleteHistory(boolean in) {
        saveCompleteHistory = in;
    }
    public void computeCondStatMarkovJumps(double time,
                                           double[] countMatrix) {
        throw new IllegalArgumentException("Not implemented for UniformizedSubstitutionModel");
    }
    public void computeCondStatMarkovJumps(double time,
                                           double[] transitionProbs,
                                           double[] countMatrix) {
        throw new IllegalArgumentException("Not implemented for UniformizedSubstitutionModel");
    }
    public void computeJointStatMarkovJumps(double time,
                                            double[] countMatrix) {
        throw new IllegalArgumentException("Not implemented for UniformizedSubstitutionModel");
    }
    public double computeCondStatMarkovJumps(int startingState,
                                             int endingState,
                                             double time) {
        substModel.getTransitionProbabilities(time, tmp);
        return computeCondStatMarkovJumps(startingState, endingState, time,
                tmp[startingState * stateCount + endingState]);
    }
    public String getCompleteHistory() {
        return getCompleteHistory(null, null);
    }
   public String getCompleteHistory(Double newStartTime, Double newEndTime) {
        return getCompleteHistory(-1, newStartTime, newEndTime);
   }
    public String getCompleteHistory(int site, Double newStartTime, Double newEndTime) {
        if (newStartTime != null && newEndTime != null) {
            // Rescale time of events
            completeHistory.rescaleTimesOfEvents(newStartTime, newEndTime);
        }
        return completeHistory.toStringChanges(site, dataType); //, 0.0);
    }
    public int getNumberOfJumpsInCompleteHistory() {
        return completeHistory == null ? -1 : completeHistory.getNumberOfJumps();
    }
    public double computeCondStatMarkovJumps(int startingState,
                                             int endingState,
                                             double time,
                                             double transitionProbability) {
        if (updateSubordinator) {
            constructSubordinator();
        }
        double total = 0;
        for (int i = 0; i < numSimulants; i++) {
            StateHistory history = null;
            try {
                history = UniformizedStateHistory.simulateConditionalOnEndingState(
                        0.0,
                        startingState,
                        time,
                        endingState,
                        transitionProbability,
                        stateCount,
                        subordinator
                );
            } catch (SubordinatedProcess.Exception e) {
                if (RETURN_NAN) {
                    if (reportWarning) {
                        Logger.getLogger("dr.app.beagle").info(
                                "Unable to compute a robust count; this is most likely due to poor starting values."
                        );
                    }
                    reportWarning = false;
                    return Double.NaN;
                }
                // Error in uniformization; try rejection sampling
                System.err.println("Attempting rejection sampling after uniformization failure");
                substModel.getInfinitesimalMatrix(tmp);
                int attempts = 0;
                boolean success = false;
                while (!success) {
                    if (attempts >= maxRejectionAttempts) {
                        throw new RuntimeException("Rejection sampling failure, after uniformization failure");
                    }
                    history = StateHistory.simulateUnconditionalOnEndingState(0.0, startingState, time, tmp, stateCount);
                    if (history.getEndingState() == endingState) {
                        success = true;
                    }
                    attempts++;
                }
            }
            total += getProcessForSimulant(history);
            if (saveCompleteHistory) {
                if (numSimulants == 1) {
                    completeHistory = history;
                } else {
                    throw new RuntimeException("Use single simulant when saving complete histories");
                }
            }
        }
        return total / (double) numSimulants;
    }
   public StateHistory getStateHistory() {
       return completeHistory;
   }
    private final int numSimulants;
    private boolean updateSubordinator;
    private SubordinatedProcess subordinator;
    private SubordinatedProcess storedSubordinator;
    private boolean saveCompleteHistory = false;
    private StateHistory completeHistory = null;
    private double[] tmp;
    private static int maxRejectionAttempts = 100000;
    private static final boolean RETURN_NAN = true;
    private static boolean reportWarning = true;
}
