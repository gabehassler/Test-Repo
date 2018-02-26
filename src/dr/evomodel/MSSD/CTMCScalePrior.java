package dr.evomodel.MSSD;
import dr.app.beagle.evomodel.substmodel.SubstitutionModel;
import dr.evolution.tree.Tree;
import dr.evomodel.tree.TreeModel;
import dr.inference.model.AbstractModelLikelihood;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.math.GammaFunction;
public class CTMCScalePrior extends AbstractModelLikelihood {
    final private Parameter ctmcScale;
    final private TreeModel treeModel;
    private double treeLength;
    private boolean treeLengthKnown;
    final private boolean reciprocal;
    final private SubstitutionModel substitutionModel;
    final private boolean trial;
    private static final double logGammaOneHalf = GammaFunction.lnGamma(0.5);
    public CTMCScalePrior(String name, Parameter ctmcScale, TreeModel treeModel) {
        this(name, ctmcScale, treeModel, false);
    }
    public CTMCScalePrior(String name, Parameter ctmcScale, TreeModel treeModel, boolean reciprocal) {
        this(name, ctmcScale, treeModel, reciprocal, null);
    }
    public CTMCScalePrior(String name, Parameter ctmcScale, TreeModel treeModel, boolean reciprocal,
                          SubstitutionModel substitutionModel) {
        this(name, ctmcScale, treeModel, reciprocal, substitutionModel, false);
    }
    public CTMCScalePrior(String name, Parameter ctmcScale, TreeModel treeModel, boolean reciprocal,
                          SubstitutionModel substitutionModel, boolean trial) {
        super(name);
        this.ctmcScale = ctmcScale;
        this.treeModel = treeModel;
        addModel(treeModel);
        treeLengthKnown = false;
        this.reciprocal = reciprocal;
        this.substitutionModel = substitutionModel;
        this.trial = trial;
    }
    private void updateTreeLength() {
        treeLength = Tree.Utils.getTreeLength(treeModel, treeModel.getRoot());
    }
    protected void handleModelChangedEvent(Model model, Object object, int index) {
        if (model == treeModel) {
            treeLengthKnown = false;
        }
    }
    protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
    }
    protected void storeState() {
    }
    protected void restoreState() {
        treeLengthKnown = false;
    }
    protected void acceptState() {
    }
    public Model getModel() {
        return this;
    }
    private double calculateTrialLikelihood() {
        double totalTreeTime = Tree.Utils.getTreeLength(treeModel, treeModel.getRoot());
        double[] eigenValues = substitutionModel.getEigenDecomposition().getEigenValues();
        // Find second largest
        double lambda2 = Double.NEGATIVE_INFINITY;
        for (double l : eigenValues) {
            if (l > lambda2 && l < 0.0) {
                lambda2 = l;
            }
        }
        lambda2 = -lambda2;
        double logNormalization = 0.5 * Math.log(lambda2) - logGammaOneHalf;
        double logLike = 0;
        for (int i = 0; i < ctmcScale.getDimension(); ++i) {
            double ab = ctmcScale.getParameterValue(i) * totalTreeTime;
            logLike += logNormalization - 0.5 * Math.log(ab) - ab * lambda2;
        }
        return logLike;
    }
    public double getLogLikelihood() {
//        if (!treeLengthKnown) {
//            updateTreeLength();
//            treeLengthKnown = true;
//        }
//        double totalTreeTime = treeLength;
        if (trial) return calculateTrialLikelihood();
        double totalTreeTime = Tree.Utils.getTreeLength(treeModel, treeModel.getRoot());
        if (reciprocal) {
            totalTreeTime = 1.0 / totalTreeTime;
        }
        if (substitutionModel != null) {
            double[] eigenValues = substitutionModel.getEigenDecomposition().getEigenValues();
            // Find second largest
            double lambda2 = Double.NEGATIVE_INFINITY;
            for (double l : eigenValues) {
                if (l > lambda2 && l < 0.0) {
                    lambda2 = l;
                }
            }
            totalTreeTime *= -lambda2; // TODO Should this be /=?
        }
        double logNormalization = 0.5 * Math.log(totalTreeTime) - logGammaOneHalf;
        double logLike = 0;
        for (int i = 0; i < ctmcScale.getDimension(); ++i) {
            double ab = ctmcScale.getParameterValue(i);
            logLike += logNormalization - 0.5 * Math.log(ab) - ab * totalTreeTime; // TODO Change to treeLength and confirm results
        }
        return logLike;
    }
    public void makeDirty() {
        treeLengthKnown = false;
    }
}
