package dr.evomodel.arg;
import dr.evolution.colouring.TreeColouring;
import dr.evolution.tree.Tree;
import dr.evomodel.branchratemodel.BranchRateModel;
import dr.evomodel.coalescent.structure.ColourSamplerModel;
import dr.inference.loggers.LogFormatter;
import dr.inference.loggers.MCLogger;
import dr.inference.loggers.MLLogger;
import dr.inference.loggers.TabDelimitedFormatter;
import dr.inference.model.Likelihood;
import dr.xml.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
public class OldTreeLogger extends MCLogger {
    public static final String LOG_TREE = "logTree";
    public static final String NEXUS_FORMAT = "nexusFormat";
    public static final String USING_RATES = "usingRates";
    public static final String BRANCH_LENGTHS = "branchLengths";
    public static final String TIME = "time";
    public static final String SUBSTITUTIONS = "substitutions";
    // The following were in MCLogger; where did they go?
    public static final String LOG = "log";
    public static final String ECHO = "echo";
    public static final String ECHO_EVERY = "echoEvery";
    public static final String TITLE = "title";
    public static final String FILE_NAME = "fileName";
    public static final String FORMAT = "format";
    public static final String TAB = "tab";
    public static final String HTML = "html";
    public static final String PRETTY = "pretty";
    public static final String LOG_EVERY = "logEvery";
    public static final String COLUMNS = "columns";
    public static final String COLUMN = "column";
    public static final String LABEL = "label";
    public static final String SIGNIFICANT_FIGURES = "sf";
    public static final String DECIMAL_PLACES = "dp";
    public static final String WIDTH = "width";
    private Tree tree;
    private BranchRateModel branchRateModel = null;
    private String rateLabel;
    private ColourSamplerModel colourSamplerModel = null;
    private String colouringLabel;
    private Likelihood likelihood = null;
    private String likelihoodLabel;
    private boolean nexusFormat = false;
    public boolean usingRates = false;
    public boolean substitutions = false;
    public OldTreeLogger(Tree tree, BranchRateModel branchRateModel, String rateLabel,
                         ColourSamplerModel colourSamplerModel, String colouringLabel,
                         Likelihood likelihood, String likelihoodLabel,
                         LogFormatter formatter, int logEvery, boolean nexusFormat, boolean substitutions) {
        super(formatter, logEvery, false);
        this.nexusFormat = nexusFormat;
        this.branchRateModel = branchRateModel;
        this.rateLabel = rateLabel;
        this.colourSamplerModel = colourSamplerModel;
        this.colouringLabel = colouringLabel;
        this.likelihood = likelihood;
        this.likelihoodLabel = likelihoodLabel;
        if (branchRateModel != null) {
            this.substitutions = substitutions;
        }
        this.tree = tree;
    }
    public void startLogging() {
        if (nexusFormat) {
            int taxonCount = tree.getTaxonCount();
            logLine("#NEXUS");
            logLine("");
            logLine("Begin taxa;");
            logLine("\tDimensions ntax=" + taxonCount + ";");
            logLine("\tTaxlabels");
            for (int i = 0; i < taxonCount; i++) {
                logLine("\t\t" + tree.getTaxon(i).getId());
            }
            logLine("\t\t;");
            logLine("End;");
            logLine("");
            logLine("Begin trees;");
            if (!useTaxonLabels()) {
                // This is needed if the trees use numerical taxon labels
                logLine("\tTranslate");
                for (int i = 0; i < taxonCount; i++) {
                    int k = i + 1;
                    if (k < taxonCount) {
                        logLine("\t\t" + k + " " + tree.getTaxonId(i) + ",");
                    } else {
                        logLine("\t\t" + k + " " + tree.getTaxonId(i));
                    }
                }
                logLine("\t\t;");
            }
        }
    }
    public void log(long state) {
        if (logEvery <= 0 || ((state % logEvery) == 0)) {
            StringBuffer buffer = new StringBuffer("tree STATE_");
            buffer.append(state);
            if (likelihood != null) {
                buffer.append(" [&");
                buffer.append(likelihoodLabel);
                buffer.append("=");
                buffer.append(likelihood.getLogLikelihood());
                buffer.append("]");
            }
            buffer.append(additionalInfo());
            buffer.append(" = [&R] ");
            TreeColouring colouring = null;
            if (colourSamplerModel != null) {
                colouring = colourSamplerModel.getTreeColouring();
            }
            Tree printTree = getPrintTree();
            if (substitutions) {
                Tree.Utils.newick(printTree, printTree.getRoot(), useTaxonLabels(), Tree.BranchLengthType.LENGTHS_AS_SUBSTITUTIONS,
                        null, branchRateModel, null, null, buffer);
            } else {
                Tree.Utils.newick(printTree, printTree.getRoot(), useTaxonLabels(), Tree.BranchLengthType.LENGTHS_AS_TIME,
                        null, branchRateModel, null, null, buffer);
            }
            buffer.append(";");
            logLine(buffer.toString());
        }
    }
    protected String additionalInfo() {
        return "";
    }
    protected Tree getPrintTree() {
        return tree;
    }
    protected Tree getTree() {
        return tree;
    }
    protected boolean useTaxonLabels() {
        return false;
    }
    public void stopLogging() {
        logLine("End;");
        super.stopLogging();
    }
}