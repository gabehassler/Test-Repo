
package dr.app.beauti.options;

import dr.app.beauti.types.OperatorType;
import dr.app.beauti.types.PriorType;
import dr.app.beauti.types.StartingTreeType;
import dr.app.beauti.types.TreePriorType;
import dr.evolution.datatype.PloidyType;
import dr.evolution.tree.Tree;

import java.util.List;

public class PartitionTreeModel extends PartitionOptions {

    private static final long serialVersionUID = 4829401415152235625L;

    private PartitionTreePrior treePrior;

    private StartingTreeType startingTreeType = StartingTreeType.RANDOM;
    private Tree userStartingTree = null;

    private boolean isNewick = true;
    private boolean fixedTree = false;
//    private double initialRootHeight = 1.0;

    //TODO if use EBSP and *BEAST, validate Ploidy of every PD is same for each tree that the PD(s) belongs to
    // BeastGenerator.checkOptions()
    private PloidyType ploidyType = PloidyType.AUTOSOMAL_NUCLEAR;

    public PartitionTreeModel(BeautiOptions options, AbstractPartitionData partition) {
        super(options, partition.getName());
    }

    public PartitionTreeModel(BeautiOptions options, String name, PartitionTreeModel source) {
        super(options, name);

        treePrior = source.treePrior;
        startingTreeType = source.startingTreeType;
        userStartingTree = source.userStartingTree;

        isNewick = source.isNewick;
        fixedTree = source.fixedTree;
//        initialRootHeight = source.initialRootHeight;
        ploidyType = source.ploidyType;
    }

    protected void initModelParametersAndOpererators() {

        createParameter("tree", "The tree");
        createParameter("treeModel.internalNodeHeights", "internal node heights of the tree (except the root)");
        createParameter("treeModel.allInternalNodeHeights", "internal node heights of the tree");
        createParameterTree(this, "treeModel.rootHeight", "root height of the tree", true, 1.0);

        //TODO treeBitMove should move to PartitionClockModelTreeModelLink, after Alexei finish
        createOperator("treeBitMove", "Tree", "Swaps the rates and change locations of local clocks", "tree",
                OperatorType.TREE_BIT_MOVE, -1.0, treeWeights);

        createScaleOperator("treeModel.rootHeight", demoTuning, demoWeights);
        createOperator("uniformHeights", "Internal node heights", "Draws new internal node heights uniformally",
                "treeModel.internalNodeHeights", OperatorType.UNIFORM, -1, branchWeights);

        createOperator("subtreeSlide", "Tree", "Performs the subtree-slide rearrangement of the tree", "tree",
                OperatorType.SUBTREE_SLIDE, 1.0, treeWeights);
        createOperator("narrowExchange", "Tree", "Performs local rearrangements of the tree", "tree",
                OperatorType.NARROW_EXCHANGE, -1, treeWeights);
        createOperator("wideExchange", "Tree", "Performs global rearrangements of the tree", "tree",
                OperatorType.WIDE_EXCHANGE, -1, demoWeights);
        createOperator("wilsonBalding", "Tree", "Performs the Wilson-Balding rearrangement of the tree", "tree",
                OperatorType.WILSON_BALDING, -1, demoWeights);
    }

    public void selectParameters(List<Parameter> parameters) {
        setAvgRootAndRate();

        getParameter("tree");
        getParameter("treeModel.internalNodeHeights");
        getParameter("treeModel.allInternalNodeHeights");

        Parameter rootHeightParameter = getParameter("treeModel.rootHeight");
        if (rootHeightParameter.priorType == PriorType.NONE_TREE_PRIOR || !rootHeightParameter.isPriorEdited()) {
            rootHeightParameter.initial = getInitialRootHeight();
            rootHeightParameter.truncationLower = options.maximumTipHeight;
            rootHeightParameter.uniformLower = options.maximumTipHeight;
            rootHeightParameter.isTruncated = true;
        }

        if (options.useStarBEAST) {
            rootHeightParameter.isCalibratedYule = treePrior.getNodeHeightPrior() == TreePriorType.SPECIES_YULE_CALIBRATION;
        } else {
            rootHeightParameter.isCalibratedYule = treePrior.getNodeHeightPrior() == TreePriorType.YULE_CALIBRATION;
            parameters.add(rootHeightParameter);
        }
    }

    public void selectOperators(List<Operator> operators) {
        setAvgRootAndRate();

        // if not a fixed tree then sample tree space
        if (!fixedTree) {
            Operator subtreeSlideOp = getOperator("subtreeSlide");
            if (!subtreeSlideOp.tuningEdited) {
                subtreeSlideOp.tuning = getInitialRootHeight() / 10.0;
            }

            operators.add(subtreeSlideOp);
            operators.add(getOperator("narrowExchange"));
            operators.add(getOperator("wideExchange"));
            operators.add(getOperator("wilsonBalding"));
        }

        operators.add(getOperator("treeModel.rootHeight"));
        operators.add(getOperator("uniformHeights"));
    }

    /////////////////////////////////////////////////////////////

    public PartitionTreePrior getPartitionTreePrior() {
        return treePrior;
    }

    public void setPartitionTreePrior(PartitionTreePrior treePrior) {
        options.clearDataPartitionCaches();
        this.treePrior = treePrior;
    }

    public StartingTreeType getStartingTreeType() {
        return startingTreeType;
    }

    public void setStartingTreeType(StartingTreeType startingTreeType) {
        this.startingTreeType = startingTreeType;
    }

    public Tree getUserStartingTree() {
        return userStartingTree;
    }

    public void setUserStartingTree(Tree userStartingTree) {
        this.userStartingTree = userStartingTree;
    }

    public boolean isNewick() {
        return isNewick;
    }

    public void setNewick(boolean newick) {
        isNewick = newick;
    }

    public void setPloidyType(PloidyType ploidyType) {
        this.ploidyType = ploidyType;
    }

    public PloidyType getPloidyType() {
        return ploidyType;
    }

    public double getInitialRootHeight() {
        return getAvgRootAndRate()[0];
    }

//    public void setInitialRootHeight(double initialRootHeight) {
//        this.initialRootHeight = initialRootHeight;
//    }

//    private void calculateInitialRootHeightPerTree() {
//		initialRootHeight = options.clockModelOptions
//                .calculateInitialRootHeightAndRate(options.getDataPartitions(this)) [0];
//    }

    public String getPrefix() {
        String prefix = "";
        if (options.getPartitionTreeModels().size() > 1) { //|| options.isSpeciesAnalysis()
            // There is more than one active partition model
            prefix += getName() + ".";
        }
        return prefix;
    }

    public int getDimension() { // n-1
        return options.getTaxonCount(options.getDataPartitions(this)) - 1;
    }

    public int getTaxonCount() {
        return options.getTaxonCount(options.getDataPartitions(this));
    }
}
