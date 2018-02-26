package dr.app.beagle.evomodel.branchmodel;
import dr.app.beagle.evomodel.substmodel.FrequencyModel;
import dr.app.beagle.evomodel.substmodel.SubstitutionModel;
import dr.evolution.tree.NodeRef;
import dr.inference.model.Model;
import java.util.List;
public interface BranchModel extends Model  {
    Mapping getBranchModelMapping(final NodeRef branch);
    List<SubstitutionModel> getSubstitutionModels();
    SubstitutionModel getRootSubstitutionModel();
    FrequencyModel getRootFrequencyModel();
    boolean requiresMatrixConvolution();
    public interface Mapping {
        int[] getOrder();
        double[] getWeights();
    }
    public final static Mapping DEFAULT = new Mapping() {
        public int[] getOrder() {
            return new int[] { 0 };
        }
        public double[] getWeights() {
            return new double[] { 1.0 };
        }
    };
}
