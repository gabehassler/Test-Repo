
package dr.inference.markovchain;

import dr.inference.mcmc.MCMCOptions;
import dr.inference.operators.OperatorSchedule;

public interface MarkovChainDelegate {

    void setup(MCMCOptions options, OperatorSchedule schedule, MarkovChain markovChain);

    void currentState(long state);

    void currentStateEnd(long state);

	void finished(long chainLength);
}
