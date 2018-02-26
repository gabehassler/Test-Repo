
package dr.inference.markovchain;

import java.io.Serializable;

public interface Acceptor extends Serializable {

	boolean accept(double oldScore, double newScore, double hastingsRatio, double[] logr);
}
