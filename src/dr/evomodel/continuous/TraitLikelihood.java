package dr.evomodel.continuous;
import dr.evolution.tree.NodeRef;
import dr.evomodel.tree.TreeModel;
import dr.inference.model.AbstractModelLikelihood;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.xml.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.ArrayList;
public class TraitLikelihood extends AbstractModelLikelihood {
    public static final String TRAIT_LIKELIHOOD = "traitLikelihood";
    public static final String TRAIT_NAME = "traitName";
    public static final String JEFFERYS_PRIOR = "jefferysPrior";
    public static final String MODEL = "model";
    public static final String TREE = "tree";
    public TraitLikelihood(TreeModel treeModel, DiffusionModel diffusionModel, String traitName, boolean jeffreysPrior) {
        super(TRAIT_LIKELIHOOD);
        this.treeModel = treeModel;
        this.diffusionModel = diffusionModel;
        this.jeffreysPrior = jeffreysPrior;
        addModel(treeModel);
        addModel(diffusionModel);
        this.traitName = traitName;
    }
    // **************************************************************
    // ModelListener IMPLEMENTATION
    // **************************************************************
    protected void handleModelChangedEvent(Model model, Object object, int index) {
        likelihoodKnown = false;
    }
    // **************************************************************
    // VariableListener IMPLEMENTATION
    // **************************************************************
    protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
    }
    // **************************************************************
    // Model IMPLEMENTATION
    // **************************************************************
    protected void storeState() {
        storedLikelihoodKnown = likelihoodKnown;
        storedLogLikelihood = logLikelihood;
    }
    protected void restoreState() {
        likelihoodKnown = storedLikelihoodKnown;
        logLikelihood = storedLogLikelihood;
    }
    protected void acceptState() {
    } // nothing to do
    // **************************************************************
    // Likelihood IMPLEMENTATION
    // **************************************************************
    public Model getModel() {
        return this;
    }
    public final double getLogLikelihood() {
        if (!likelihoodKnown) {
            logLikelihood = calculateLogLikelihood();
            likelihoodKnown = true;
        }
        return logLikelihood;
    }
    public void makeDirty() {
        likelihoodKnown = false;
    }
    public double calculateLogLikelihood() {
        double logLikelihood = traitLogLikelihood(treeModel.getRoot());
        if (logLikelihood > maxLogLikelihood) {
            maxLogLikelihood = logLikelihood;
        }
        return logLikelihood;
    }
    public double getMaxLogLikelihood() {
        return maxLogLikelihood;
    }
    private double traitLogLikelihood(NodeRef node) {
        double logL = 0.0;
        if (!treeModel.isRoot(node)) {
            NodeRef parent = treeModel.getParent(node);
            Double parentTrait = (Double) treeModel.getNodeAttribute(parent, traitName);
            Double childTrait = (Double) treeModel.getNodeAttribute(node, traitName);
            double time = treeModel.getNodeHeight(parent) - treeModel.getNodeHeight(node);
            logL = diffusionModel.getLogLikelihood(parentTrait, childTrait, time);
        }
        int childCount = treeModel.getChildCount(node);
        for (int i = 0; i < childCount; i++) {
            logL += traitLogLikelihood(treeModel.getChild(node, i));
        }
        if (jeffreysPrior) {
            logL += Math.log(1 / diffusionModel.getD());
        }
        return logL;
    }
    // **************************************************************
    // XMLElement IMPLEMENTATION
    // **************************************************************
    public Element createElement(Document d) {
        throw new RuntimeException("Not implemented yet!");
    }
    // **************************************************************
    // XMLObjectParser
    // **************************************************************
    public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {
        public String getParserName() {
            return TRAIT_LIKELIHOOD;
        }
        public Object parseXMLObject(XMLObject xo) throws XMLParseException {
            DiffusionModel diffusionModel = (DiffusionModel) xo.getChild(DiffusionModel.class);
            TreeModel treeModel = (TreeModel) xo.getChild(TreeModel.class);
            String traitName = xo.getStringAttribute(TRAIT_NAME);
            boolean jeffreysPrior = xo.getBooleanAttribute(JEFFERYS_PRIOR);
            return new TraitLikelihood(treeModel, diffusionModel, traitName, jeffreysPrior);
        }
        //************************************************************************
        // AbstractXMLObjectParser implementation
        //************************************************************************
        public String getParserDescription() {
            return "Provides the likelihood of a continuous trait evolving on a tree by a " +
                    "given diffusion model.";
        }
        public XMLSyntaxRule[] getSyntaxRules() {
            return rules;
        }
        private final XMLSyntaxRule[] rules = {
                new StringAttributeRule(TRAIT_NAME, "The name of the trait for which a likelihood should be calculated"),
                AttributeRule.newBooleanRule(JEFFERYS_PRIOR),
                new ElementRule(DiffusionModel.class),
                new ElementRule(TreeModel.class)
        };
        public Class getReturnType() {
            return TraitLikelihood.class;
        }
    };
    TreeModel treeModel = null;
    DiffusionModel diffusionModel = null;
    String traitName = null;
    private boolean jeffreysPrior = false;
    ArrayList dataList = new ArrayList();
    private double logLikelihood;
    private double maxLogLikelihood = Double.NEGATIVE_INFINITY;
    private double storedLogLikelihood;
    private boolean likelihoodKnown = false;
    private boolean storedLikelihoodKnown = false;
}
