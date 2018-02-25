package dr.app.beagle.evomodel.sitemodel;
import beagle.Beagle;
import dr.app.beagle.evomodel.substmodel.EigenDecomposition;
import dr.app.beagle.evomodel.substmodel.SubstitutionModel;
import dr.app.beagle.evomodel.treelikelihood.BufferIndexHelper;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evomodel.tree.TreeModel;
import dr.inference.model.Model;
@Deprecated // Switching to BranchModel
public interface BranchSubstitutionModel extends Model {
EigenDecomposition getEigenDecomposition(int modelIndex, int categoryIndex);
SubstitutionModel getSubstitutionModel(int modelIndex, int categoryIndex);
double[] getStateFrequencies(int categoryIndex);
public int getBranchIndex(final Tree tree, final NodeRef node, int bufferIndex);
public int getEigenCount();
boolean canReturnComplexDiagonalization();
void updateTransitionMatrices(
Beagle beagle,
int eigenIndex,
BufferIndexHelper bufferHelper,
final int[] probabilityIndices,
final int[] firstDerivativeIndices,
final int[] secondDervativeIndices,
final double[] edgeLengths,
int count);
int getExtraBufferCount(TreeModel treeModel);
void setFirstBuffer(int bufferCount);
void setEigenDecomposition(Beagle beagle, int eigenIndex, BufferIndexHelper bufferHelper, int dummy);
}
