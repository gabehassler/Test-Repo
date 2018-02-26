package dr.evomodel.approxPopTree;

import dr.evolution.alignment.Patterns;
import dr.evolution.tree.NodeRef;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;

import java.util.LinkedList;

public class CoalescentPopulationMRCAModel extends AbstractPopulationMRCAModel {
    public CoalescentPopulationMRCAModel(String name, double populationTime) {
        super(name, populationTime);
    }

    public double getMRCATime(LinkedList<NodeRef> nodes) {
        return 0;  //AUTOGENERATED METHOD IMPLEMENTATION
    }

    public double drawMRCATime(LinkedList<NodeRef> nodes) {
        return 0;  //AUTOGENERATED METHOD IMPLEMENTATION
    }

    public double[][] getMRCAPartials(LinkedList<NodeRef> nodes, Patterns patterns) {
        return new double[0][];  //AUTOGENERATED METHOD IMPLEMENTATION
    }

    public double[][] drawMRCAPartials(LinkedList<NodeRef> nodes, Patterns patterns) {
        return new double[0][];  //AUTOGENERATED METHOD IMPLEMENTATION
    }

    protected void handleModelChangedEvent(Model model, Object object, int index) {
        //AUTOGENERATED METHOD IMPLEMENTATION
    }

    protected void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        //AUTOGENERATED METHOD IMPLEMENTATION
    }

    protected void storeState() {
        //AUTOGENERATED METHOD IMPLEMENTATION
    }

    protected void restoreState() {
        //AUTOGENERATED METHOD IMPLEMENTATION
    }

    protected void acceptState() {
        //AUTOGENERATED METHOD IMPLEMENTATION
    }
}
