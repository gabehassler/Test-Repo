package dr.evomodel.coalescent.structure;
import dr.evolution.coalescent.structure.ColouredTreeIntervals;
import dr.evolution.coalescent.structure.StructuredCoalescent;
import dr.evolution.coalescent.structure.StructuredIntervalList;
import dr.evolution.colouring.ColourChangeMatrix;
import dr.evomodel.tree.TreeModel;
import dr.inference.model.*;
import dr.xml.*;
import java.util.logging.Logger;
public class StructuredCoalescentLikelihood extends AbstractModelLikelihood {
    // PUBLIC STUFF
    public static final String STRUCTURED_COALESCENT_LIKELIHOOD = "structuredCoalescentLikelihood";
    public static final String META_POPULATION_MODEL = "metaPopulationModel";
    public static final String MIGRATION_MODEL = "migrationModel";
    public static final String POPULATION = "population";
    public StructuredCoalescentLikelihood(TreeModel treeModel, MetaPopulationModel metaPopulationModel, ColourSamplerModel colourSamplerModel, MigrationModel migrationModel) {
        this(STRUCTURED_COALESCENT_LIKELIHOOD, treeModel, metaPopulationModel, colourSamplerModel, migrationModel);
    }
    public StructuredCoalescentLikelihood(String name, TreeModel treeModel, MetaPopulationModel metaPopulationModel, ColourSamplerModel colourSamplerModel, MigrationModel migrationModel) {
        super(name);
        this.treeModel = treeModel;
        addModel(treeModel);
        this.metaPopulationModel = metaPopulationModel;
        addModel(metaPopulationModel);
        this.colourSamplerModel = colourSamplerModel;
        addModel(colourSamplerModel);
        this.migrationModel = migrationModel;
        addModel(migrationModel);
        addStatistic(migrationWaitingTimesStatistic);
        addStatistic(coalescentWaitingTimeStatistic);
    }
    // **************************************************************
    // ModelListener IMPLEMENTATION
    // **************************************************************
    protected final void handleModelChangedEvent(Model model, Object object, int index) {
        if (model == treeModel) {
            // treeModel has changed so recalculate the intervals
        } else {
            // a demographicModel has changed so we don't need to recalculate the intervals
        }
        likelihoodKnown = false;
    }
    // **************************************************************
    // VariableListener IMPLEMENTATION
    // **************************************************************
    protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        likelihoodKnown = false;
    }
    // **************************************************************
    // Model IMPLEMENTATION
    // **************************************************************
    protected final void storeState() {
        storedLogLikelihood = logLikelihood;
    }
    protected final void restoreState() {
        logLikelihood = storedLogLikelihood;
    }
    protected final void acceptState() {
    } // nothing to do
    // **************************************************************
    // Likelihood IMPLEMENTATION
    // **************************************************************
    public final Model getModel() {
        return this;
    }
    public final double getLogLikelihood() {
        likelihoodKnown = false;
        if (!likelihoodKnown) {
            logLikelihood = calculateLogLikelihood();
            likelihoodKnown = true;
        }
        return logLikelihood;
    }
    public final void makeDirty() {
        likelihoodKnown = false;
    }
    public double calculateLogLikelihood() {
        // make intervals
        StructuredIntervalList list = new ColouredTreeIntervals(treeModel, colourSamplerModel.getTreeColouring());
        ColourChangeMatrix mm = migrationModel.getMigrationMatrix();
        StructuredCoalescent sc = new StructuredCoalescent();
        double logL = sc.calculateLogLikelihood(colourSamplerModel.getTreeColouring(), list, mm, metaPopulationModel);
        return logL;
    }
    // ****************************************************************
    // Private and protected stuff
    // ****************************************************************
    private final Statistic migrationWaitingTimesStatistic = new Statistic.Abstract() {
        public String getStatisticName() {
            return "migrationWaitingTimes";
        }
        public int getDimension() {
            return metaPopulationModel.getPopulationCount();
        }
        public double getStatisticValue(int dim) {
            double[] migrationRates = migrationModel.getMigrationRates(0);
            return 1.0 / migrationRates[dim];
        }
    };
    private final Statistic coalescentWaitingTimeStatistic = new Statistic.Abstract() {
        public String getStatisticName() {
            return "coalescentWaitingTime";
        }
        public int getDimension() {
            return 1;
        }
        public double getStatisticValue(int dim) {
            double[] popSizes = metaPopulationModel.getPopulationSizes(0);
            int[] counts = colourSamplerModel.getLeafColourCounts();
            double sum = 0.0;
            for (int i = 0; i < popSizes.length; i++) {
                sum += ((double) counts[i]) / popSizes[i];
            }
            return 1.0 / sum;
        }
    };
    public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {
        public String getParserName() {
            return STRUCTURED_COALESCENT_LIKELIHOOD;
        }
        public Object parseXMLObject(XMLObject xo) throws XMLParseException {
            TreeModel treeModel = (TreeModel) xo.getChild(TreeModel.class);
            ColourSamplerModel colourSamplerModel = (ColourSamplerModel) xo.getChild(ColourSamplerModel.class);
            MigrationModel migrationModel = (MigrationModel) xo.getChild(MigrationModel.class);
            MetaPopulationModel metaPopulationModel = (MetaPopulationModel) xo.getChild(MetaPopulationModel.class);
            StructuredCoalescentLikelihood likelihood = new StructuredCoalescentLikelihood(treeModel, metaPopulationModel, colourSamplerModel, migrationModel);
            Logger.getLogger("dr.evomodel").info("Creating structured coalescent tree prior.");
            return likelihood;
        }
        //************************************************************************
        // AbstractXMLObjectParser implementation
        //************************************************************************
        public String getParserDescription() {
            return "This element represents a likelihood function for transmission.";
        }
        public Class getReturnType() {
            return StructuredCoalescentLikelihood.class;
        }
        public XMLSyntaxRule[] getSyntaxRules() {
            return rules;
        }
        private final XMLSyntaxRule[] rules = {
                new ElementRule(TreeModel.class, "The tree."),
                new ElementRule(ColourSamplerModel.class, "The colour sampler model."),
                new ElementRule(MigrationModel.class, "The migration model."),
                new ElementRule(MetaPopulationModel.class, "The meta-population model."),
        };
    };
    private TreeModel treeModel = null;
    private MetaPopulationModel metaPopulationModel = null;
    private ColourSamplerModel colourSamplerModel = null;
    private MigrationModel migrationModel = null;
    private boolean likelihoodKnown = false;
    private double logLikelihood;
    private double storedLogLikelihood;
}