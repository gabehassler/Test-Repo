package dr.evomodelxml.branchratemodel;
import dr.evomodel.branchratemodel.*;
import dr.evomodel.tree.TreeModel;
import dr.inference.model.Parameter;
import dr.xml.*;
import java.util.logging.Logger;
public class LatentStateBranchRateModelParser extends AbstractXMLObjectParser {
public static final String LATENT_TRANSITION_RATE = "latentTransitionRate";
public static final String LATENT_TRANSITION_FREQUENCY = "latentTransitionFrequency";
public static final String LATENT_STATE_PROPORTIONS = "latentStateProportions";
public String getParserName() {
return SericolaLatentStateBranchRateModel.LATENT_STATE_BRANCH_RATE_MODEL;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
BranchRateModel nonLatentRateModel = (BranchRateModel) xo.getChild(BranchRateModel.class);
TreeModel tree = (TreeModel) xo.getChild(TreeModel.class);
Parameter latentTransitionRateParameter = (Parameter) xo.getElementFirstChild(LATENT_TRANSITION_RATE);
Parameter latentTransitionFrequencyParameter = (Parameter) xo.getElementFirstChild(LATENT_TRANSITION_FREQUENCY);
CountableBranchCategoryProvider branchCategoryProvider = (CountableBranchCategoryProvider)xo.getChild(CountableBranchCategoryProvider.class);
Parameter latentStateProportionParameter = null;
if (xo.hasChildNamed(LATENT_STATE_PROPORTIONS)) {
latentStateProportionParameter = (Parameter) xo.getElementFirstChild(LATENT_STATE_PROPORTIONS);
}
Logger.getLogger("dr.evomodel").info("Creating a latent state branch rate model");
return new LatentStateBranchRateModel(LatentStateBranchRateModel.LATENT_STATE_BRANCH_RATE_MODEL,
tree, nonLatentRateModel,
latentTransitionRateParameter, latentTransitionFrequencyParameter, /* 0/1 CTMC have two parameters */
latentStateProportionParameter, branchCategoryProvider);
//        return new SericolaLatentStateBranchRateModel(SericolaLatentStateBranchRateModel.LATENT_STATE_BRANCH_RATE_MODEL,
//                tree, nonLatentRateModel,
//                latentTransitionRateParameter, latentTransitionFrequencyParameter, /* 0/1 CTMC have two parameters */
//                latentStateProportionParameter, branchCategoryProvider);
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public String getParserDescription() {
return "This element provides a model with a latent state where no evolution occurs but condition on being non-latent at the nodes.";
}
public Class getReturnType()  {
return SericolaLatentStateBranchRateModel.class;
}
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private XMLSyntaxRule[] rules = new XMLSyntaxRule[]{
new ElementRule(BranchRateModel.class, "A branch rate model to provide the rates for the non-latent state"),
new ElementRule(TreeModel.class, "The tree on which this will operate"),
new ElementRule(CountableBranchCategoryProvider.class, true),
new ElementRule(LATENT_TRANSITION_RATE, Parameter.class, "A parameter which gives the instantaneous rate of switching to and from the latent state", false),
new ElementRule(LATENT_TRANSITION_FREQUENCY, Parameter.class, "A parameter which gives the rate bias of switching to and from the latent state", false),
new ElementRule(LATENT_STATE_PROPORTIONS, Parameter.class, "The proportion of each branch which is spend in a latent state", true)
};
}