
package dr.evomodel.speciation;

import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.util.Taxon;
import dr.evolution.util.Units;

import java.util.Set;

public abstract class UltrametricSpeciationModel extends SpeciationModel implements Units {

    public UltrametricSpeciationModel(String modelName, Type units) {
        super(modelName, units);
    }

    public abstract double logTreeProbability(int taxonCount);

    public abstract double logNodeProbability(Tree tree, NodeRef node);

    public boolean analyticalMarginalOK() {
       return false;
    }

    public double getMarginal(Tree tree, CalibrationPoints calibration) {
       return calibration.getCorrection(tree, -1);
    }

    public abstract boolean includeExternalNodesInLikelihoodCalculation();

    public final double calculateTreeLogLikelihood(Tree tree) {
        final int taxonCount = tree.getExternalNodeCount();
        double logL = logTreeProbability(taxonCount);

        for (int j = 0; j < tree.getInternalNodeCount(); j++) {
            logL += logNodeProbability(tree, tree.getInternalNode(j));
        }

        if (includeExternalNodesInLikelihoodCalculation()) {
            for (int j = 0; j < taxonCount; j++) {
                logL += logNodeProbability(tree, tree.getExternalNode(j));
            }
        }

        return logL;
    }

    public double calculateTreeLogLikelihood(Tree tree, Set<Taxon> exclude) {
        final int taxonCount = tree.getExternalNodeCount() - exclude.size();

        double[] lnL = {logTreeProbability(taxonCount)};

        calculateNodeLogLikelihood(tree, tree.getRoot(), exclude, lnL);

        return lnL[0];
    }

    private int calculateNodeLogLikelihood(Tree tree, NodeRef node, Set<Taxon> exclude, double[] lnL) {
        if (tree.isExternal(node)) {
            if (!exclude.contains(tree.getNodeTaxon(node))) {
                if (includeExternalNodesInLikelihoodCalculation()) {
                    lnL[0] += logNodeProbability(tree, node);
                }

                // this tip is included in the subtree...
                return 1;
            }

            // this tip is excluded from the subtree...
            return 0;
        } else {
            int count = 0;
            for (int i = 0; i < tree.getChildCount(node); i++) {
                NodeRef child = tree.getChild(node, i);
                count += calculateNodeLogLikelihood(tree, child, exclude, lnL);
            }

            if (count == 2) {
                // this node is included in the subtree...
                lnL[0] += logNodeProbability(tree, node);
            }

            // if at least one of the children has included tips then return 1 otherwise 0
            return count > 0 ? 1 : 0;
        }
    }

    @Override
    public double calculateTreeLogLikelihood(Tree tree, CalibrationPoints calibration) {
        double logL = calculateTreeLogLikelihood(tree);
        double mar = getMarginal(tree, calibration);
        logL += mar;
        return logL;
    }
}