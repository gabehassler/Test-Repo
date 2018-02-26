package dr.evomodel.speciation;
import dr.evolution.tree.Tree;
import dr.evolution.util.Units;
import dr.evomodelxml.speciation.SpeciationLikelihoodParser;
import dr.inference.model.*;
import java.util.List;
public class ModelAveragingSpeciationLikelihood extends AbstractModelLikelihood implements Units {
    // PUBLIC STUFF
    public ModelAveragingSpeciationLikelihood(List<Tree> trees, List<MaskableSpeciationModel> speciationModels,
                                              Variable<Integer> indexVariable, Variable<Double> maxIndexVariable, String id) {
        this(SpeciationLikelihoodParser.SPECIATION_LIKELIHOOD, trees, speciationModels, indexVariable, maxIndexVariable);
        setId(id);
    }
    public ModelAveragingSpeciationLikelihood(String name, List<Tree> trees, List<MaskableSpeciationModel> speciationModels,
                                              Variable<Integer> indexVariable, Variable<Double> maxIndexVariable) {
        super(name);
        this.trees = trees;
        this.speciationModels = speciationModels;
        if (trees.size() != speciationModels.size()) {
            throw new IllegalArgumentException("The number of trees and the number of speciation models should be equal.");
        }
        for (Tree tree : trees) {
            if (tree instanceof Model) {
                addModel((Model) tree);
            }
        }
        for (SpeciationModel speciationModel : speciationModels) {
            if (speciationModel != null) {
                addModel(speciationModel);
            }
        }
        if ( (indexVariable.getSize() + 1) != trees.size()) { // integer index parameter size = real size - 1
            throw new IllegalArgumentException("Index parameter must be same size as the number of trees.");
        }
        this.indexVariable = indexVariable;
        for (int i = 0; i < indexVariable.getSize(); i++) {
            indexVariable.setValue(i, i+1); // if starts all 0, the top value (i+1) of index will be missing
        }
        indexVariable.addBounds(new Bounds.Staircase(indexVariable));
        addVariable(indexVariable);
        for (int i = 0; i < maxIndexVariable.getSize(); i++) {
            maxIndexVariable.setValue(i, 0.0);
        }
        this.maxIndexVariable = maxIndexVariable;
        addVariable(maxIndexVariable);
    }
    // **************************************************************
    // ModelListener IMPLEMENTATION
    // **************************************************************
    protected final void handleModelChangedEvent(Model model, Object object, int index) {
        likelihoodKnown = false;
    }
    // **************************************************************
    // VariableListener IMPLEMENTATION
    // **************************************************************
    protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        likelihoodKnown = false;
    } // No parameters to respond to
    // **************************************************************
    // Model IMPLEMENTATION
    // **************************************************************
    protected final void storeState() {
        storedLikelihoodKnown = likelihoodKnown;
        storedLogLikelihood = logLikelihood;
    }
    protected final void restoreState() {
        likelihoodKnown = storedLikelihoodKnown;
        logLikelihood = storedLogLikelihood;
    }
    protected final void acceptState() {
    } // nothing to do
    // **************************************************************
    // Likelihood IMPLEMENTATION
    // **************************************************************
    public final Model getModel() {
        return this;
    }
    public final double getLogLikelihood() {
        if (!likelihoodKnown) {
            logLikelihood = calculateLogLikelihood();
            likelihoodKnown = true;
        }
        return logLikelihood;
    }
    public final void makeDirty() {
        likelihoodKnown = false;
    }
    private double calculateLogLikelihood() {
        double logL = 0;
        // Rule: index k cannot be appeared unless k-1 appeared
        if (!isValidate(indexVariable.getValues())) { 
//                output("illegal index variable", indexVariable);
            return Double.NEGATIVE_INFINITY;
        }
        for (int i = 0; i < trees.size(); i++) {
            MaskableSpeciationModel model = speciationModels.get(i);
            if (i > 0) {
                SpeciationModel mask = speciationModels.get(indexVariable.getValue(i-1)); // integer index parameter size = real size - 1
                if (model != mask) {
                    model.mask(mask);
                } else {
                    model.unmask();
                }
            }
            logL += model.calculateTreeLogLikelihood(trees.get(i));
        }
        Double maxI = (double) (int) getMaxIndex(indexVariable.getValues());
        maxIndexVariable.setValue(0, maxI);
        return logL;
    }
    private boolean isValidate(Integer[] pattern) {
        // Rule: index k cannot be appeared unless k-1 appeared before it appears
        int[] indexFreq = new int[pattern.length];
        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i] > 0) // not validate 0
                indexFreq[pattern[i] - 1] += 1; // integer index parameter size = real size - 1
            if (i > 0 && (pattern[i] - pattern[i - 1] > 1)) {
                for (int f = 0; f < i; f++) {
                    if (indexFreq[f] < 1) return false;
                }
            }
        }
        return true;
    }
    private int getMaxIndex(Integer[] pattern) {
        int max = 0;
        for (int p : pattern) {
            if (p > max) {
                max = p;
            }
        }
        return max;
    }
    private void output(String message, Variable<Integer> indexVariable) {
        System.out.print(message + ": ");
        for (int i = 0; i < indexVariable.getSize(); i++) {
            System.out.print(indexVariable.getValue(i) + "\t");
        }
        System.out.println();
    }
    // **************************************************************
    // Loggable IMPLEMENTATION
    // **************************************************************
    public final dr.inference.loggers.LogColumn[] getColumns() {
        String columnName = getId();
        if (columnName == null) columnName = getModelName() + ".likelihood";
        return new dr.inference.loggers.LogColumn[]{
                new LikelihoodColumn(columnName)
        };
    }
    private final class LikelihoodColumn extends dr.inference.loggers.NumberColumn {
        public LikelihoodColumn(String label) {
            super(label);
        }
        public double getDoubleValue() {
            return getLogLikelihood();
        }
    }
    // **************************************************************
    // Units IMPLEMENTATION
    // **************************************************************
    public final void setUnits(Type u) {
        for (SpeciationModel speciationModel : speciationModels) {
            speciationModel.setUnits(u);
        }
    }
    public final Type getUnits() {
        return speciationModels.get(0).getUnits();
    }
    // ****************************************************************
    // Private and protected stuff
    // ****************************************************************
    List<MaskableSpeciationModel> speciationModels = null;
    List<Tree> trees = null;
    Variable<Integer> indexVariable = null; // integer index parameter size = real size - 1
    Variable<Double> maxIndexVariable = null;
    private double logLikelihood;
    private double storedLogLikelihood;
    private boolean likelihoodKnown = false;
    private boolean storedLikelihoodKnown = false;
}