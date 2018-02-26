package dr.inference.mcmc;
import dr.inference.loggers.LogColumn;
import dr.inference.loggers.Loggable;
import dr.inference.loggers.Logger;
import dr.inference.markovchain.MarkovChain;
import dr.inference.markovchain.MarkovChainDelegate;
import dr.inference.markovchain.MarkovChainListener;
import dr.inference.model.Likelihood;
import dr.inference.model.Model;
import dr.inference.operators.*;
import dr.inference.prior.Prior;
import dr.util.Identifiable;
import dr.util.NumberFormatter;
import dr.xml.Spawnable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
public class MCMC implements Identifiable, Spawnable, Loggable {
    public final static String LOAD_DUMP_FILE = "load.dump.file";
    public final static String DUMP_STATE = "dump.state";
    public final static String DUMP_EVERY = "dump.every";
    // Experimental
    public final static boolean TEST_CLONING = false;
    public MCMC(String id) {
        this.id = id;
    }
    public void init(
            MCMCOptions options,
            Likelihood likelihood,
            OperatorSchedule schedule,
            Logger[] loggers) {
        init(options, likelihood, Prior.UNIFORM_PRIOR, schedule, loggers, new MarkovChainDelegate[0]);
    }
    public void init(
            MCMCOptions options,
            Likelihood likelihood,
            OperatorSchedule schedule,
            Logger[] loggers,
            MarkovChainDelegate[] delegates) {
        init(options, likelihood, Prior.UNIFORM_PRIOR, schedule, loggers, delegates);
    }
    public void init(
            MCMCOptions options,
            Likelihood likelihood,
            Prior prior,
            OperatorSchedule schedule,
            Logger[] loggers) {
        init(options, likelihood, prior, schedule, loggers, new MarkovChainDelegate[0]);
    }
    public void init(
            MCMCOptions options,
            Likelihood likelihood,
            Prior prior,
            OperatorSchedule schedule,
            Logger[] loggers,
            MarkovChainDelegate[] delegates) {
        MCMCCriterion criterion = new MCMCCriterion();
        criterion.setTemperature(options.getTemperature());
        mc = new MarkovChain(prior, likelihood, schedule, criterion,
                options.getFullEvaluationCount(), options.minOperatorCountForFullEvaluation(),
                options.getEvaluationTestThreshold(),
                options.useCoercion());
        this.options = options;
        this.loggers = loggers;
        this.schedule = schedule;
        //initialize transients
        currentState = 0;
        for(MarkovChainDelegate delegate : delegates) {
            delegate.setup(options, schedule, mc);
        }
        this.delegates = delegates;
        dumpStateFile = System.getProperty(LOAD_DUMP_FILE);
        if (System.getProperty(DUMP_STATE) != null) {
            long debugWriteState = Long.parseLong(System.getProperty(DUMP_STATE));
            mc.addMarkovChainListener(new DebugChainListener(this, debugWriteState, false));
        }
        if (System.getProperty(DUMP_EVERY) != null) {
            long debugWriteEvery = Long.parseLong(System.getProperty(DUMP_EVERY));
            mc.addMarkovChainListener(new DebugChainListener(this, debugWriteEvery, true));
        }
    }
    public void init(long chainlength,
                     Likelihood likelihood,
                     MCMCOperator[] operators,
                     Logger[] loggers) {
        MCMCOptions options = new MCMCOptions(chainlength);
        MCMCCriterion criterion = new MCMCCriterion();
        criterion.setTemperature(1);
        OperatorSchedule schedule = new SimpleOperatorSchedule();
        for (MCMCOperator operator : operators) schedule.addOperator(operator);
        init(options, likelihood, Prior.UNIFORM_PRIOR, schedule, loggers);
    }
    public MarkovChain getMarkovChain() {
        return mc;
    }
    public Logger[] getLoggers() {
        return loggers;
    }
    public MCMCOptions getOptions() {
        return options;
    }
    public OperatorSchedule getOperatorSchedule() {
        return schedule;
    }
    public void run() {
        chain();
    }
    public void chain() {
        stopping = false;
        currentState = 0;
        timer.start();
        if (loggers != null) {
            for (Logger logger : loggers) {
                logger.startLogging();
            }
        }
        if (!stopping) {
            if (dumpStateFile != null) {
                double[] savedLnL = new double[1];
                long loadedState = DebugUtils.readStateFromFile(new File(dumpStateFile), savedLnL);
                mc.setCurrentLength(loadedState);
                double lnL = mc.evaluate();
                if (lnL != savedLnL[0]) {
                   throw new RuntimeException("Dumped lnL does not match loaded state");
                }
            }
            mc.addMarkovChainListener(chainListener);
            for(MarkovChainDelegate delegate : delegates) {
                mc.addMarkovChainDelegate(delegate);
            }
            long chainLength = getChainLength();
            final long coercionDelay = getCoercionDelay();
            if (coercionDelay > 0) {
                // Run the chain for coercionDelay steps with coercion disabled
                mc.runChain(coercionDelay, true);
                chainLength -= coercionDelay;
                // reset operator acceptance levels
                for (int i = 0; i < schedule.getOperatorCount(); i++) {
                    schedule.getOperator(i).reset();
                }
            }
            if (TEST_CLONING) {
                // TEST Code for cloning the MarkovChain to a file to distribute amongst processors
                double lnL1 = mc.getCurrentScore();
                // Write the MarkovChain out and back in again...
                DebugUtils.writeStateToFile(new File("beast.state"), currentState, mc.getCurrentScore());
                DebugUtils.readStateFromFile(new File("beast.state"), null);
                double lnL2 = mc.evaluate();
                if (lnL1 != lnL2) {
                    throw new RuntimeException("Likelihood different after state load");
                }
                // TEST Code end
            }
            mc.runChain(chainLength, false);
            mc.terminateChain();
            mc.removeMarkovChainListener(chainListener);
            for(MarkovChainDelegate delegate : delegates) {
                mc.removeMarkovChainDelegate(delegate);
            }
        }
        timer.stop();
    }
    @Override
    public LogColumn[] getColumns() {
        return new LogColumn[] { new LogColumn() {
            @Override
            public void setLabel(String label) {
            }
            @Override
            public String getLabel() {
                return "time";
            }
            @Override
            public void setMinimumWidth(int minimumWidth) {
            }
            @Override
            public int getMinimumWidth() {
                return 0;
            }
            @Override
            public String getFormatted() {
                return Double.toString(getTimer().toSeconds());
            }
        }   };
    }
    private final MarkovChainListener chainListener = new MarkovChainListener() {
        // MarkovChainListener interface *******************************************
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
        public void finished(long chainLength) {
            currentState = chainLength;
            if (loggers != null) {
                for (Logger logger : loggers) {
                    logger.log(currentState);
                    logger.stopLogging();
                }
            }
            // OperatorAnalysisPrinter class can do the job now
            if (showOperatorAnalysis) {
                showOperatorAnalysis(System.out);
            }
            if (operatorAnalysisFile != null) {
                try {
                    PrintStream out = new PrintStream(new FileOutputStream(operatorAnalysisFile));
                    showOperatorAnalysis(out);
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // How should premature finish be flagged?
        }
    };
    private void showOperatorAnalysis(PrintStream out) {
        out.println();
        out.println("Operator analysis");
        out.println(formatter.formatToFieldWidth("Operator", 50) +
                formatter.formatToFieldWidth("Tuning", 9) +
                formatter.formatToFieldWidth("Count", 11) +
                formatter.formatToFieldWidth("Time", 9) +
                formatter.formatToFieldWidth("Time/Op", 9) +
                formatter.formatToFieldWidth("Pr(accept)", 11) +
                (options.useCoercion() ? "" : " Performance suggestion"));
        for (int i = 0; i < schedule.getOperatorCount(); i++) {
            final MCMCOperator op = schedule.getOperator(i);
            if (op instanceof JointOperator) {
                JointOperator jointOp = (JointOperator) op;
                for (int k = 0; k < jointOp.getNumberOfSubOperators(); k++) {
                    out.println(formattedOperatorName(jointOp.getSubOperatorName(k))
                                    + formattedParameterString(jointOp.getSubOperator(k))
                                    + formattedCountString(op)
                                    + formattedTimeString(op)
                                    + formattedTimePerOpString(op)
                                    + formattedProbString(jointOp)
                                    + (options.useCoercion() ? "" : formattedDiagnostics(jointOp, MCMCOperator.Utils.getAcceptanceProbability(jointOp)))
                    );
                }
            } else {
                out.println(formattedOperatorName(op.getOperatorName())
                                + formattedParameterString(op)
                                + formattedCountString(op)
                                + formattedTimeString(op)
                                + formattedTimePerOpString(op)
                                + formattedProbString(op)
                                + (options.useCoercion() ? "" : formattedDiagnostics(op, MCMCOperator.Utils.getAcceptanceProbability(op)))
                );
            }
        }
        out.println();
    }
    private String formattedOperatorName(String operatorName) {
        return formatter.formatToFieldWidth(operatorName, 50);
    }
    private String formattedParameterString(MCMCOperator op) {
        String pString = "        ";
        if (op instanceof CoercableMCMCOperator && ((CoercableMCMCOperator) op).getMode() != CoercionMode.COERCION_OFF) {
            pString = formatter.formatToFieldWidth(formatter.formatDecimal(((CoercableMCMCOperator) op).getRawParameter(), 3), 8);
        }
        return pString;
    }
    private String formattedCountString(MCMCOperator op) {
        final int count = op.getCount();
        return formatter.formatToFieldWidth(Integer.toString(count), 10) + " ";
    }
    private String formattedTimeString(MCMCOperator op) {
        final long time = op.getTotalEvaluationTime();
        return formatter.formatToFieldWidth(Long.toString(time), 8) + " ";
    }
    private String formattedTimePerOpString(MCMCOperator op) {
        final double time = op.getMeanEvaluationTime();
        return formatter.formatToFieldWidth(formatter.formatDecimal(time, 2), 8) + " ";
    }
    private String formattedProbString(MCMCOperator op) {
        final double acceptanceProb = MCMCOperator.Utils.getAcceptanceProbability(op);
        return formatter.formatToFieldWidth(formatter.formatDecimal(acceptanceProb, 4), 11) + " ";
    }
    private String formattedDiagnostics(MCMCOperator op, double acceptanceProb) {
        String message = "good";
        if (acceptanceProb < op.getMinimumGoodAcceptanceLevel()) {
            if (acceptanceProb < (op.getMinimumAcceptanceLevel() / 10.0)) {
                message = "very low";
            } else if (acceptanceProb < op.getMinimumAcceptanceLevel()) {
                message = "low";
            } else message = "slightly low";
        } else if (acceptanceProb > op.getMaximumGoodAcceptanceLevel()) {
            double reallyHigh = 1.0 - ((1.0 - op.getMaximumAcceptanceLevel()) / 10.0);
            if (acceptanceProb > reallyHigh) {
                message = "very high";
            } else if (acceptanceProb > op.getMaximumAcceptanceLevel()) {
                message = "high";
            } else message = "slightly high";
        }
        String performacsMsg;
        if (op instanceof GibbsOperator) {
            performacsMsg = "none (Gibbs operator)";
        } else {
            final String suggestion = op.getPerformanceSuggestion();
            performacsMsg = message + "\t" + suggestion;
        }
        return performacsMsg;
    }
    public Prior getPrior() {
        return mc.getPrior();
    }
    public Likelihood getLikelihood() {
        return mc.getLikelihood();
    }
    public dr.util.Timer getTimer() {
        return timer;
    }
    public final long getChainLength() {
        return options.getChainLength();
    }
    // TRANSIENT PUBLIC METHODS *****************************************
    public final long getCurrentState() {
        return currentState;
    }
    public final double getProgress() {
        return (double) currentState / (double) options.getChainLength();
    }
    public final boolean isAdapting() {
        return isAdapting;
    }
    public void pleaseStop() {
        stopping = true;
        mc.pleaseStop();
    }
    public boolean isStopped() {
        return mc.isStopped();
    }
    public boolean getSpawnable() {
        return spawnable;
    }
    private boolean spawnable = true;
    public void setSpawnable(boolean spawnable) {
        this.spawnable = spawnable;
    }
    //PRIVATE METHODS *****************************************
    protected long getCoercionDelay() {
        long delay = options.getCoercionDelay();
        if (delay < 0) {
            delay = (long)(options.getChainLength() / 100);
        }
        if (options.useCoercion()) return delay;
        for (int i = 0; i < schedule.getOperatorCount(); i++) {
            MCMCOperator op = schedule.getOperator(i);
            if (op instanceof CoercableMCMCOperator) {
                if (((CoercableMCMCOperator) op).getMode() == CoercionMode.COERCION_ON) return delay;
            }
        }
        return -1;
    }
    public void setShowOperatorAnalysis(boolean soa) {
        showOperatorAnalysis = soa;
    }
    public void setOperatorAnalysisFile(File operatorAnalysisFile) {
        this.operatorAnalysisFile = operatorAnalysisFile;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    // PRIVATE TRANSIENTS
    private String dumpStateFile = null;
    //private FileLogger operatorLogger = null;
    protected final boolean isAdapting = true;
    protected boolean stopping = false;
    protected boolean showOperatorAnalysis = true;
    protected File operatorAnalysisFile = null;
    protected final dr.util.Timer timer = new dr.util.Timer();
    protected long currentState = 0;
    //private int stepsPerReport = 1000;
    protected final NumberFormatter formatter = new NumberFormatter(8);
    protected MarkovChain mc;
    protected MCMCOptions options;
    protected Logger[] loggers;
    protected OperatorSchedule schedule;
    private MarkovChainDelegate[] delegates;
    private String id = null;
}
