
package dr.inference.model;

import java.io.Serializable;


public interface ModelListener extends Serializable {

	void modelChangedEvent(Model model, Object object, int index);

    void modelRestored(Model model);
}
