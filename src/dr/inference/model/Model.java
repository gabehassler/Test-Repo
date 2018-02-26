package dr.inference.model;
import dr.util.Identifiable;
import java.io.Serializable;
import java.util.*;
public interface Model extends Identifiable, Serializable {
	void addModelListener(ModelListener listener);
    void removeModelListener(ModelListener listener);
	void storeModelState();
	void restoreModelState();
	void acceptModelState();
	boolean isValidState();
	int getModelCount();
	Model getModel(int i);
	int getVariableCount();
	Variable getVariable(int i);
	//Parameter getParameter(String name);
	String getModelName();
    boolean isUsed();
	public class ListenerHelper implements Serializable {
		public void fireModelChanged(Model model) {
			fireModelChanged(model, model, -1);
		}
		public void fireModelChanged(Model model, Object object) {
			fireModelChanged(model, object, -1);
		}
		public void fireModelChanged(Model model, Object object, int index) {
			if (listeners != null) {
                for (ModelListener listener : listeners) {
                    listener.modelChangedEvent(model, object, index);
                }
            }
		}
		public void addModelListener(ModelListener listener) {
			if (listeners == null) {
				listeners = new java.util.ArrayList<ModelListener>();
			}
			listeners.add(listener);
		}
		public void removeModelListener(ModelListener listener) {
			if (listeners != null) {
				listeners.remove(listener);
			}
		}
        public void addModelRestoreListener(ModelListener listener) {
            if (restoreListeners == null) {
                restoreListeners = new java.util.ArrayList<ModelListener>();
            }
            restoreListeners.add(listener);
        }
        public void fireModelRestored(Model model) {
            if (restoreListeners != null) {
                for (ModelListener listener : restoreListeners ) {
                    listener.modelRestored(model);
                }
            }
        }
        public int getListenerCount() {
            return listeners != null ? listeners.size() : 0;
        }
        private ArrayList<ModelListener> listeners = null;
        private ArrayList<ModelListener> restoreListeners = null;
    }
    // set to store all created models
    final static Set<Model> FULL_MODEL_SET = new HashSet<Model>(); 
}
