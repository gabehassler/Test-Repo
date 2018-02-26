package dr.inference.ml;
import dr.inference.loggers.Logger;
import dr.inference.markovchain.MarkovChain;
import dr.inference.markovchain.MarkovChainListener;
import dr.inference.model.Likelihood;
import dr.inference.model.Model;
import dr.inference.operators.OperatorSchedule;
import dr.util.Identifiable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
public class MLOptimizer implements Runnable, Identifiable {
	private final Likelihood likelihood;
	private OperatorSchedule schedule;
	private final Logger[] loggers;
	int chainLength;
	private String id = null;
	public MLOptimizer(String id,
		int chainLength,
		Likelihood likelihood,
		OperatorSchedule schedule,
		Logger[] loggers) {
		this.id = id;
		mc = new MarkovChain(null, likelihood, schedule, new GreatDelugeCriterion(0.2), 2000, 1, MarkovChain.EVALUATION_TEST_THRESHOLD, false);
        //mc = new MarkovChain(null, likelihood, schedule, new HillClimbingCriterion(), false);
		this.chainLength = chainLength;
		this.likelihood = likelihood;
		this.loggers = loggers;
		setOperatorSchedule(schedule);
		//initialize transients
		currentState = 0;
	}
	public void run() {
		chain();
	}
	public void chain() {
        currentState = 0;
		if (loggers != null) {
            for (Logger logger : loggers) {
                logger.startLogging();
            }
        }
		timer.start();
		mc.reset();
		timer.start();
			mc.addMarkovChainListener(chainListener);
			mc.runChain(getChainLength(), true/*, 0*/);
			mc.removeMarkovChainListener(chainListener);
		timer.stop();
	}
	public Likelihood getLikelihood() { return likelihood; }
	public dr.util.Timer getTimer() { return timer; }
	public void setOperatorSchedule(OperatorSchedule sched) {
		this.schedule = sched;
	}
	public OperatorSchedule getOperatorSchedule() { return schedule; }
	public final int getChainLength() { return chainLength; }
	// TRANSIENT PUBLIC METHODS *****************************************
	public final long getCurrentState() { return currentState; }
	public final double getProgress() {
		return (double)currentState / chainLength;
	}
    private final MarkovChainListener chainListener = new MarkovChainListener() {
	// for receiving messages from subordinate MarkovChain
        public void currentState(long state, Model currentModel) {
            currentState = state;
            if (loggers != null) {
                for (Logger logger : loggers) {
                    logger.log(state);
                }
            }
        }
        public void bestState(long state, Model bestModel) {
            currentState = state;
        }
        public void bestLklModel(long state, Model bestModel) {
            currentState = state;
        }
        public void finished(long chainLength) {
            currentState = chainLength;
            if (loggers != null) {
                for (Logger logger : loggers) {
                    logger.log(currentState);
                    logger.stopLogging();
                }
            }
                NumberFormatter formatter = new NumberFormatter(8);
                System.out.println();
                System.out.println("Operator analysis");
                for (int i =0; i < schedule.getOperatorCount(); i++) {
                    MCMCOperator op = schedule.getOperator(i);
                    double acceptanceProb = MCMCOperator.Utils.getAcceptanceProbability(op);
                    System.out.println(formatter.formatToFieldWidth(op.getOperatorName(), 30) + "\t" + formatter.formatDecimal(op.getMeanDeviation(), 2) + "\t" + formatter.formatDecimal(acceptanceProb, 4));
                }
                System.out.println();
            }
        }
    };
	public Element createElement(Document d) {
		throw new RuntimeException("Not implemented!");
	}
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	// PRIVATE TRANSIENTS
	private long currentState;
    private final dr.util.Timer timer = new dr.util.Timer();
	private MarkovChain mc = null;
}
