package dr.inference.model;
import dr.inference.parallel.MPISerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.List;
public abstract class AbstractModel implements Model, ModelListener, VariableListener, StatisticList, MPISerializable {
    public AbstractModel(String name) {
        this.name = name;
    }
    public void addModel(Model model) {
        if (!models.contains(model)) {
            models.add(model);
            model.addModelListener(this);
        }
    }
    public void removeModel(Model model) {
        models.remove(model);
        model.removeModelListener(this);
    }
    public int getModelCount() {
        return models.size();
    }
    public final Model getModel(int i) {
        return models.get(i);
    }
    public final void addVariable(Variable variable) {
        if (!variables.contains(variable)) {
            variables.add(variable);
            variable.addVariableListener(this);
        }
        // parameters are also statistics
        if (variable instanceof Statistic) addStatistic((Statistic) variable);
    }
    public final void removeVariable(Variable variable) {
        variables.remove(variable);
        variable.removeVariableListener(this);
        // parameters are also statistics
        if (variable instanceof Statistic) removeStatistic((Statistic) variable);
    }
    public final boolean hasVariable(Variable parameter) {
        return variables.contains(parameter);
    }
    public void addModelListener(ModelListener listener) {
        listenerHelper.addModelListener(listener);
    }
    public void removeModelListener(ModelListener listener) {
        listenerHelper.removeModelListener(listener);
    }
    public void addModelRestoreListener(ModelListener listener) {
        listenerHelper.addModelRestoreListener(listener);
    }
    public boolean isUsed() {
        return listenerHelper.getListenerCount() > 0;
    }
    public void fireModelChanged() {
        listenerHelper.fireModelChanged(this, this, -1);
    }
    public void fireModelChanged(Object object) {
        listenerHelper.fireModelChanged(this, object, -1);
    }
    public void fireModelChanged(Object object, int index) {
        listenerHelper.fireModelChanged(this, object, index);
    }
    public final int getVariableCount() {
        return variables.size();
    }
    public final Variable getVariable(int i) {
        return variables.get(i);
    }
    // **************************************************************
    // MPI IMPLEMENTATION
    // **************************************************************
    public void sendState(int toRank) {
        // Iterate through child models
        for (Model model : models) {
            ((AbstractModel) model).sendState(toRank);
        }
        // Send current model parameters
        for (Variable variable : variables) {
            if (variable instanceof Parameter.Abstract) ((Parameter.Abstract) variable).sendState(toRank);
        }
    }
    public void sendStateNoParameters(int toRank) {
        // Iterate through child models
        for (Model model : models) {
            ((AbstractModel) model).sendState(toRank);
        }
    }
    public void receiveStateNoParameters(int fromRank) {
        for (Model model : models) {
            ((AbstractModel) model).receiveState(fromRank);
        }
    }
    public void receiveState(int fromRank) {
        for (Model model : models) {
            ((AbstractModel) model).receiveState(fromRank);
        }
        // Send current model parameters
        for (Variable variable : variables) {
            if (variable instanceof Parameter.Abstract)
                ((Parameter.Abstract) variable).receiveState(fromRank);
        }
    }
    // **************************************************************
    // ModelListener IMPLEMENTATION
    // **************************************************************
    public final void modelChangedEvent(Model model, Object object, int index) {
//		String message = "  model: " + getModelName() + "/" + getId() + "  component: " + model.getModelName();
//		if (object != null) {
//			message += " object: " + object;
//		}
//		if (index != -1) {
//			message += " index: " + index;
//		}
//		System.out.println(message);
        handleModelChangedEvent(model, object, index);
    }
    // do nothing by default
    public void modelRestored(Model model) {
    }
    abstract protected void handleModelChangedEvent(Model model, Object object, int index);
    // **************************************************************
    // VariableListener IMPLEMENTATION
    // **************************************************************
    public final void variableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        handleVariableChangedEvent(variable, index, type);
        // AR - I am not sure this is required and may be overruling modelChange events on parts of the
        // model. If a parameter changes it should be handleVariableChangedEvent() job to fireModelChanged
        // events
        listenerHelper.fireModelChanged(this, variable, index);
    }
    protected abstract void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type);
    // **************************************************************
    // Model IMPLEMENTATION
    // **************************************************************
    public final void storeModelState() {
        if (isValidState) {
            //System.out.println("STORE MODEL: " + getModelName() + "/" + getId());
            for (Model m : models) {
                m.storeModelState();
            }
            for (Variable variable : variables) {
                variable.storeVariableValues();
            }
            storeState();
            isValidState = false;
        }
    }
    public final void restoreModelState() {
        if (!isValidState) {
            //System.out.println("RESTORE MODEL: " + getModelName() + "/" + getId());
            for (Variable variable : variables) {
                variable.restoreVariableValues();
            }
            for (Model m : models) {
                m.restoreModelState();
            }
            restoreState();
            isValidState = true;
            listenerHelper.fireModelRestored(this);
        }
    }
    public final void acceptModelState() {
        if (!isValidState) {
            //System.out.println("ACCEPT MODEL: " + getModelName() + "/" + getId());
            for (Variable variable : variables) {
                variable.acceptVariableValues();
            }
            for (Model m : models) {
                m.acceptModelState();
            }
            acceptState();
            isValidState = true;
        }
    }
    public boolean isValidState() {
        return isValidState;
    }
    public final String getModelName() {
        return name;
    }
    protected abstract void storeState();
    protected abstract void restoreState();
    protected abstract void acceptState();
    // **************************************************************
    // StatisticList IMPLEMENTATION
    // **************************************************************
    public final void addStatistic(Statistic statistic) {
        if (!statistics.contains(statistic)) {
            statistics.add(statistic);
        }
    }
    public final void removeStatistic(Statistic statistic) {
        statistics.remove(statistic);
    }
    public int getStatisticCount() {
        return statistics.size();
    }
    public Statistic getStatistic(int i) {
        return statistics.get(i);
    }
    public final Statistic getStatistic(String name) {
        for (int i = 0; i < getStatisticCount(); i++) {
            Statistic statistic = getStatistic(i);
            if (name.equals(statistic.getStatisticName())) {
                return statistic;
            }
        }
        return null;
    }
    // **************************************************************
    // Identifiable IMPLEMENTATION
    // **************************************************************
    private String id = null;
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public String toString() {
        if (id != null) {
            return id;
        } else if (name != null) {
            return name;
        }
        return super.toString();
    }
    // **************************************************************
    // XMLElement IMPLEMENTATION
    // **************************************************************
    public Element createElement(Document d) {
        throw new RuntimeException("Not implemented!");
    }
    boolean isValidState = true;
    protected Model.ListenerHelper listenerHelper = new Model.ListenerHelper();
    private final ArrayList<Model> models = new ArrayList<Model>();
    private final ArrayList<Variable> variables = new ArrayList<Variable>();
    private final ArrayList<Statistic> statistics = new ArrayList<Statistic>();
    private final String name;
}
