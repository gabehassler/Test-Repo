package dr.app.beagle.evomodel.sitemodel;
import beagle.Beagle;
import dr.app.beagle.evomodel.substmodel.EigenDecomposition;
import dr.app.beagle.evomodel.substmodel.FrequencyModel;
import dr.app.beagle.evomodel.substmodel.SubstitutionModel;
import dr.app.beagle.evomodel.treelikelihood.BufferIndexHelper;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evomodel.tree.TreeModel;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.util.Author;
import dr.util.Citable;
import dr.util.Citation;
import java.util.ArrayList;
import java.util.List;
@Deprecated // Switching to BranchModel
public class ExternalInternalBranchSubstitutionModel extends AbstractModel implements BranchSubstitutionModel, Citable {
    public ExternalInternalBranchSubstitutionModel(List<SubstitutionModel> substModelList, List<FrequencyModel> frequencyModelList) {
        super("ExternalInternalBranchSubstitutionModel");
        if (substModelList.size() != 2) {
            throw new IllegalArgumentException("ExternalInternalBranchSubstitutionModel requires two SubstitutionModels");
        }
        if (frequencyModelList.size() != 1) {
            throw new IllegalArgumentException("ExternalInternalBranchSubstitutionModel requires one FrequencyModel");
        }
        this.substModelList = substModelList;
        this.frequencyModelList = frequencyModelList;
        for (SubstitutionModel model : substModelList) {
            addModel(model);
        }
        for (FrequencyModel model : frequencyModelList) {
            addModel(model);
        }
    }
	@Override
	public void setEigenDecomposition(Beagle beagle, int eigenIndex,
			BufferIndexHelper bufferHelper, int dummy) {
        EigenDecomposition ed = getEigenDecomposition(eigenIndex, dummy);
        beagle.setEigenDecomposition(
//                offsetIndex,
        		eigenIndex,
                ed.getEigenVectors(),
                ed.getInverseEigenVectors(),
                ed.getEigenValues());
	}    
    public int getBranchIndex(final Tree tree, final NodeRef node, int bufferIndex) {
        return (tree.isExternal(node) ? 1 : 0);
    }
    public EigenDecomposition getEigenDecomposition(int branchIndex, int categoryIndex) {
        return substModelList.get(branchIndex).getEigenDecomposition();
    }
    public SubstitutionModel getSubstitutionModel(int branchIndex, int categoryIndex) {
        return substModelList.get(branchIndex);
    }
    public double[] getStateFrequencies(int categoryIndex) {
        return frequencyModelList.get(categoryIndex).getFrequencies();
    }
    public boolean canReturnComplexDiagonalization() {
        for (SubstitutionModel model : substModelList) {
            if (model.canReturnComplexDiagonalization()) {
                return true;
            }
        }
        return false;
    }
    public int getEigenCount() {
        return 2;
    }
    protected void handleModelChangedEvent(Model model, Object object, int index) {
        fireModelChanged();
    }
    protected void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
    }
    protected void storeState() {
    }
    protected void restoreState() {
    }
    protected void acceptState() {
    }
    public void updateTransitionMatrices( Beagle beagle,
            int eigenIndex,
            BufferIndexHelper bufferHelper,
            final int[] probabilityIndices,
            final int[] firstDerivativeIndices,
            final int[] secondDervativeIndices,
            final double[] edgeLengths,
            int count) {
        beagle.updateTransitionMatrices(eigenIndex, probabilityIndices, firstDerivativeIndices,
                secondDervativeIndices, edgeLengths, count);
    }
    private final List<SubstitutionModel> substModelList;
    private final List<FrequencyModel> frequencyModelList;
    public List<Citation> getCitations() {
        List<Citation> citations = new ArrayList<Citation>();
        citations.add(
                new Citation(
                        new Author[]{
                                new Author("P", "Lemey"),
                                new Author("MA", "Suchard")
                        },
                        Citation.Status.IN_PREPARATION
                )
        );
        return citations;
    }
	@Override
	public int getExtraBufferCount(TreeModel treeModel) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void setFirstBuffer(int bufferCount) {
		// TODO Auto-generated method stub
	}
}