package dr.evomodel.continuous;
import dr.evolution.tree.Tree;
import dr.inference.distribution.ParametricMultivariateDistributionModel;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.inferencexml.distribution.MultivariateNormalDistributionModelParser;
import dr.math.distributions.MultivariateNormalDistribution;
import dr.math.distributions.RandomGenerator;
public class TreeTraitNormalDistributionModel extends AbstractModel implements ParametricMultivariateDistributionModel, RandomGenerator {
    public TreeTraitNormalDistributionModel(FullyConjugateMultivariateTraitLikelihood traitModel, Parameter rootValue, boolean conditionOnRoot) {
        super(MultivariateNormalDistributionModelParser.NORMAL_DISTRIBUTION_MODEL);
        this.traitModel = traitModel;
        if (rootValue != null) {
            this.rootValue = rootValue.getParameterValues();
        }
        this.conditionOnRoot = conditionOnRoot;
        dim = traitModel.getTreeModel().getExternalNodeCount() * traitModel.getDimTrait();
        addModel(traitModel);
        distributionKnown = false;
        //System.err.println("trait vector: " + traitModel.getTreeTraits()[0].getTraitString(traitModel.treeModel, traitModel.treeModel.getExternalNode(2)));
        //System.exit(0);
    }
    public TreeTraitNormalDistributionModel(FullyConjugateMultivariateTraitLikelihood traitModel, boolean conditionOnRoot) {
        this(traitModel, null, conditionOnRoot);
    }
    public Tree getTree() {
        return traitModel.getTreeModel();
    }
    // *****************************************************************
    // Interface MultivariateDistribution
    // *****************************************************************
    public double logPdf(double[] x) {
        checkDistribution();
        return distribution.logPdf(x);
    }
    public double[][] getScaleMatrix() {
        checkDistribution();
        return distribution.getScaleMatrix();
    }
    public double[] getMean() {
        checkDistribution();
        return distribution.getMean();
    }
    public String getType() {
        return "TreeTraitMVN";
    }
    public int getDimTrait() {
        return traitModel.dimTrait;
    }
    // *****************************************************************
    // Interface Model
    // *****************************************************************
    public void handleModelChangedEvent(Model model, Object object, int index) {
        distributionKnown = false;
    }
    protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        distributionKnown = false;
    }
    protected void storeState() {
        storedDistribution = distribution;
        storedDistributionKnown = distributionKnown;
    }
    protected void restoreState() {
        distributionKnown = storedDistributionKnown;
        distribution = storedDistribution;
    }
    protected void acceptState() {
    } // no additional state needs accepting
    // **************************************************************
    // Private instance variables and functions
    // **************************************************************
    private void checkDistribution() {
        if (!distributionKnown) {
            mean = null;
            precision = null;
            distribution = createNewDistribution();
            distributionKnown = true;
        }
    }
    private MultivariateNormalDistribution createNewDistribution() {
        return new MultivariateNormalDistribution(computeMean(), computePrecision());
    }
    private double[] computeMean() {
        if (traitModel.strengthOfSelection != null) {
            return MultivariateTraitUtils.computeTreeTraitMeanOU(traitModel, rootValue, conditionOnRoot);
        } else {
            return MultivariateTraitUtils.computeTreeTraitMean(traitModel, rootValue, conditionOnRoot);
        }
    }
    private double[][] computePrecision() {
        return MultivariateTraitUtils.computeTreeTraitPrecision(traitModel, conditionOnRoot);
    }
    private final FullyConjugateMultivariateTraitLikelihood traitModel;
    private double[] mean;
    private double[][] precision;
    private MultivariateNormalDistribution distribution;
    private MultivariateNormalDistribution storedDistribution;
    private boolean distributionKnown;
    private boolean storedDistributionKnown;
    // RandomGenerator interface
    public double[] nextRandom() {
        checkDistribution();
        return distribution.nextMultivariateNormal();
    }
    public double logPdf(Object x) {
        checkDistribution();
        return distribution.logPdf(x);
    }
    private final boolean conditionOnRoot;
    private double[][] precisionMatrix = null;
    private double[] rootValue;
    private final int dim;
 public static final String TREE_TRAIT_NORMAL = "treeTraitNormalDistribution";
 public static final String CONDITION = "conditionOnRoot";
  public static XMLObjectParser TREE_TRAIT_MODEL = new AbstractXMLObjectParser() {
      public String getParserName() {
          return TREE_TRAIT_NORMAL;
      }
      public Object parseXMLObject(XMLObject xo) throws XMLParseException {
              System.err.println("I AM IN THE RIGHT PARSER");
          boolean conditionOnRoot = xo.getAttribute(CONDITION, false);
          FullyConjugateMultivariateTraitLikelihood traitModel = (FullyConjugateMultivariateTraitLikelihood)
                  xo.getChild(FullyConjugateMultivariateTraitLikelihood.class);
          TreeTraitNormalDistributionModel treeTraitModel = new TreeTraitNormalDistributionModel(traitModel, conditionOnRoot);
          return treeTraitModel;
      }
      public XMLSyntaxRule[] getSyntaxRules() {
          return rules;
      }
      private final XMLSyntaxRule[] rules = {
              AttributeRule.newBooleanRule(CONDITION, true),
              new ElementRule(FullyConjugateMultivariateTraitLikelihood.class)
      };
      public String getParserDescription() {
          return "Parses TreeTraitNormalDistributionModel";
      }
      public Class getReturnType() {
          return TreeTraitNormalDistributionModel.class;
      }
  };
}
