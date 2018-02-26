package dr.app.beauti.options;
import dr.app.beauti.types.OperatorType;
import dr.evomodelxml.tree.MicrosatelliteSamplerTreeModelParser;
import java.util.List;
public class MicrosatelliteOptions extends ModelOptions {
    private static final long serialVersionUID = -814539657791957173L;
    private final BeautiOptions options;
    public MicrosatelliteOptions(BeautiOptions options) {
        this.options = options;
//        initParametersAndOperators();
    }
    // has to call after data is imported
    public void initParametersAndOperators() {
        //=============== microsat ======================
        for (PartitionPattern partitionData : options.getPartitionPattern()) {
            createParameter(partitionData.getName() + "." + MicrosatelliteSamplerTreeModelParser.TREE_MICROSATELLITE_SAMPLER_MODEL +
                    ".internalNodesParameter", "Microsatellite sampler tree internal node parameter");
            createOperator(partitionData.getName() + "." + "microsatInternalNodesParameter", partitionData.getName() + " microsat internal nodes",
                    "Random integer walk on microsatellite sampler tree internal node parameter",
                    partitionData.getName() + "." + MicrosatelliteSamplerTreeModelParser.TREE_MICROSATELLITE_SAMPLER_MODEL + ".internalNodesParameter",
                    OperatorType.RANDOM_WALK_INT, 1.0, branchWeights);
        }
    }
    public void selectParameters(List<Parameter> params) {
        for (PartitionPattern partitionData : options.getPartitionPattern()) {
            getParameter(partitionData.getName() + "." + MicrosatelliteSamplerTreeModelParser.TREE_MICROSATELLITE_SAMPLER_MODEL +
                    ".internalNodesParameter");
        }
    }
    public void selectOperators(List<Operator> ops) {
        for (PartitionPattern partitionData : options.getPartitionPattern()) {
            ops.add(getOperator(partitionData.getName() + "." + "microsatInternalNodesParameter"));
        }
    }
}
