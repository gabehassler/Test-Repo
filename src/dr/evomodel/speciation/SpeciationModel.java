package dr.evomodel.speciation;
import dr.evolution.tree.Tree;
import dr.evolution.util.Taxon;
import dr.evolution.util.Units;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import java.util.Set;
public abstract class SpeciationModel extends AbstractModel implements Units {
    private Units.Type units;
    public SpeciationModel(String name, Type units) {
        super(name);
        setUnits(units);
    }
    public abstract double calculateTreeLogLikelihood(Tree tree);
    public abstract double calculateTreeLogLikelihood(Tree tree, Set<Taxon> exclude);
    // True if Yule.
    //
    // Not abstract - non supporting derived classes do not need to override anything
    public boolean isYule() {
        return false;
    }
    // Likelihood for the speciation model conditional on monophyly and calibration densities in
    // 'calibration'.
    //
    // The likelihood enforces the monophyly, so there is no need to specify it again in the XML.
    //
    public double calculateTreeLogLikelihood(Tree tree, CalibrationPoints calibration) {
        return Double.NEGATIVE_INFINITY;
    }
    protected void handleModelChangedEvent(Model model, Object object, int index) {
        // no intermediates need to be recalculated...
    }
    protected void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        // no intermediates need to be recalculated...
    }
    protected void storeState() {
    } // no additional state needs storing
    protected void restoreState() {
    } // no additional state needs restoring
    protected void acceptState() {
    } // no additional state needs accepting
    public void setUnits(Units.Type u) {
        units = u;
    }
    public Units.Type getUnits() {
        return units;
    }
}
