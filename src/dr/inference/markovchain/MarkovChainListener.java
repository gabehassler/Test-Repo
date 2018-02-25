package dr.inference.markovchain;
import dr.inference.model.Model;
import java.io.Serializable;
public interface MarkovChainListener extends Serializable {
void bestState(long state, Model bestModel);
void currentState(long state, Model currentModel);
void finished(long chainLength);
}
