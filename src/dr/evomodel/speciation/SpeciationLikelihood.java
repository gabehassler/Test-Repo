
package dr.evomodel.speciation;

import dr.evolution.tree.Tree;
import dr.evolution.util.Taxon;
import dr.evolution.util.Units;
import dr.evomodelxml.speciation.SpeciationLikelihoodParser;
import dr.inference.model.AbstractModelLikelihood;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;

import java.util.Set;

public class SpeciationLikelihood extends AbstractModelLikelihood implements Units {

    // PUBLIC STUFF
    public SpeciationLikelihood(Tree tree, SpeciationModel speciationModel, Set<Taxon> exclude, String id) {
        this(SpeciationLikelihoodParser.SPECIATION_LIKELIHOOD, tree, speciationModel, exclude);
        setId(id);
    }

    public SpeciationLikelihood(Tree tree, SpeciationModel speciationModel, String id) {
        this(tree, speciationModel, null, id);
    }

    public SpeciationLikelihood(String name, Tree tree, SpeciationModel speciationModel, Set<Taxon> exclude) {

        super(name);

        this.tree = tree;
        this.speciationModel = speciationModel;
        this.exclude = exclude;

        if (tree instanceof Model) {
            addModel((Model) tree);
        }
        if (speciationModel != null) {
            addModel(speciationModel);
        }
    }

    public SpeciationLikelihood(Tree tree, SpeciationModel specModel, String id, CalibrationPoints calib) {
        this(tree, specModel, id);
        this.calibration = calib;
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
        if (exclude != null) {
            return speciationModel.calculateTreeLogLikelihood(tree, exclude);
        }

        if ( calibration != null ) {
            return speciationModel.calculateTreeLogLikelihood(tree, calibration);
        }

        return speciationModel.calculateTreeLogLikelihood(tree);
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
        speciationModel.setUnits(u);
    }

    public final Type getUnits() {
        return speciationModel.getUnits();
    }

    @Override
    public String prettyName() {
        String s = speciationModel.getClass().getName();
        String[] parts = s.split("\\.");
        s = parts[parts.length - 1];
        if( speciationModel.getId() != null ) {
           s = s + '/' + speciationModel.getId();
        }
        s = s + '(' + tree.getId() + ')';
        return s;
    }
    // ****************************************************************
    // Private and protected stuff
    // ****************************************************************

    SpeciationModel speciationModel = null;

    Tree tree = null;
    private final Set<Taxon> exclude;

    private CalibrationPoints calibration;

    private double logLikelihood;
    private double storedLogLikelihood;
    private boolean likelihoodKnown = false;
    private boolean storedLikelihoodKnown = false;
}