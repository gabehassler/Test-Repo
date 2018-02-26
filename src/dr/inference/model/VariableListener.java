
package dr.inference.model;

public interface VariableListener {

    void variableChangedEvent(Variable variable, int index, Variable.ChangeType type);
}
