package dr.evomodel.continuous;
import dr.evolution.tree.MultivariateTraitTree;
import dr.evomodel.tree.TreeModel;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
public interface MissingTraits {
public void handleMissingTips();
public boolean isCompletelyMissing(int index);
public boolean isPartiallyMissing(int index);
void computeWeightedAverage(double[] meanCache, int meanOffset0, double precision0,
int meanOffset1, double precision1, int meanThisOffset, int dim);
abstract class Abstract implements MissingTraits {
protected static final boolean DEBUG = false;
Abstract(MultivariateTraitTree treeModel, List<Integer> missingIndices, int dim) {
this.treeModel = treeModel;
this.dim = dim;
this.missingIndices = missingIndices;
completelyMissing = new boolean[treeModel.getNodeCount()];
Arrays.fill(completelyMissing, 0, treeModel.getExternalNodeCount(), false);
Arrays.fill(completelyMissing, treeModel.getExternalNodeCount(), treeModel.getNodeCount(), true); // All internal and root nodes are missing
}
final protected MultivariateTraitTree treeModel;
final protected int dim;
final protected List<Integer> missingIndices;
final protected boolean[] completelyMissing;
}
public class CompletelyMissing extends Abstract {
CompletelyMissing(MultivariateTraitTree treeModel, List<Integer> missingIndices, int dim) {
super(treeModel, missingIndices, dim);
}
public void handleMissingTips() {
for (Integer i : missingIndices) {
int whichTip = i / dim;
Logger.getLogger("dr.evomodel").info(
"\tMarking taxon " + treeModel.getTaxonId(whichTip) + " as completely missing");
completelyMissing[whichTip] = true;
}
}
public boolean isCompletelyMissing(int index) {
return completelyMissing[index];
}
public boolean isPartiallyMissing(int index) {
return false;
}
public void computeWeightedAverage(double[] meanCache,
int meanOffset0, double precision0,
int meanOffset1, double precision1,
int meanThisOffset, int dim) {
IntegratedMultivariateTraitLikelihood.computeWeightedAverage(
meanCache, meanOffset0, precision0,
meanCache, meanOffset1, precision1,
meanCache, meanThisOffset, dim);
}
}
public class PartiallyMissing extends Abstract {
PartiallyMissing(TreeModel treeModel, List<Integer> missingIndices, int dim) {
super(treeModel, missingIndices, dim);
}
public void handleMissingTips() {
throw new RuntimeException("Not yet implemented");
}
public boolean isCompletelyMissing(int index) {
throw new RuntimeException("Not yet implemented");
}
public boolean isPartiallyMissing(int index) {
throw new RuntimeException("Not yet implemented");
}
public void computeWeightedAverage(double[] meanCache, int meanOffset0, double precision0, int meanOffset1, double precision1, int meanThisOffset, int dim) {
throw new RuntimeException("Not yet implemented");
}
}
}
