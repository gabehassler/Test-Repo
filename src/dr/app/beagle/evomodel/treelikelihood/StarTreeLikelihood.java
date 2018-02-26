
package dr.app.beagle.evomodel.treelikelihood;

import dr.app.beagle.evomodel.sitemodel.BranchSubstitutionModel;
import dr.app.beagle.evomodel.sitemodel.SiteRateModel;
import dr.evolution.alignment.PatternList;
import dr.evolution.tree.NodeRef;
import dr.evomodel.branchratemodel.BranchRateModel;
import dr.evomodel.tree.TreeModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;


public class StarTreeLikelihood extends OldBeagleTreeLikelihood {

    public StarTreeLikelihood(PatternList patternList, TreeModel treeModel,
                              BranchSubstitutionModel branchSubstitutionModel, SiteRateModel siteRateModel,
                              BranchRateModel branchRateModel, boolean useAmbiguities,
                              PartialsRescalingScheme rescalingScheme,
                               Map<Set<String>, Parameter> partialsRestrictions) {
        super(patternList, treeModel, branchSubstitutionModel, siteRateModel, branchRateModel, null, useAmbiguities,
                rescalingScheme, partialsRestrictions);

        // Modify tree into star
        forceStarTree(treeModel);

        rootHeightParameter = treeModel.getRootHeightParameter();

        // Print info to screen
        StringBuilder sb = new StringBuilder();
        sb.append("Building a star-tree sequence likelihood model.  Please cite:");
        Logger.getLogger("dr.app.beagle.evomodel").info(sb.toString());
    }

    private void forceStarTree(TreeModel treeModel) {
        double rootHeight = treeModel.getNodeHeight(treeModel.getRoot());
        for (int i = 0; i < treeModel.getInternalNodeCount(); ++i) {
            NodeRef node = treeModel.getInternalNode(i);
            if (node != treeModel.getRoot()) {
                treeModel.setNodeHeight(node, rootHeight);
            }
        }
        fixedTree = true;
    }

    protected void handleModelChangedEvent(Model model, Object object, int index) {

        boolean validUpdate = true;

        if (fixedTree && model == treeModel) {
            if (SAMPLE_ROOT) {
                if (object instanceof TreeModel.TreeChangedEvent) {
                    TreeModel.TreeChangedEvent event = (TreeModel.TreeChangedEvent) object;
                    if (event.getNode() != treeModel.getRoot()) {
                        validUpdate = false;
                    }
                } else if (object != rootHeightParameter) {
                    validUpdate = false;
                }
            } else {
                validUpdate = false;
            }

//            if (validUpdate) {
//                forceStarTree(treeModel);
//            }
        }
        if (validUpdate) {
            super.handleModelChangedEvent(model, object, index);
        } else {
            throw new IllegalArgumentException(
                    "Invalid operator; do not sample tree structure or internal node heights");
        }
    }

    private final Parameter rootHeightParameter;
    private boolean fixedTree = false;

    private static boolean SAMPLE_ROOT = false;
}
