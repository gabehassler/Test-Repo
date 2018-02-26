package dr.inference.mcmc;
import dr.inference.markovchain.MarkovChainListener;
import dr.inference.model.Model;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
public class DebugChainListener implements MarkovChainListener {
    private MCMC mcmc;
    public DebugChainListener(MCMC mcmc, final long writeState, final boolean isRepeating) {
        this.mcmc = mcmc;
        this.writeState = writeState;
        this.isRepeating = isRepeating;
    }
    // MarkovChainListener interface *******************************************
    public void currentState(long state, Model currentModel) {
        if (state == writeState || (isRepeating && state > 0 && (state % writeState == 0))) {
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(Calendar.getInstance().getTime());
            DebugUtils.writeStateToFile(new File("beast_debug_" + timeStamp), state, mcmc.getMarkovChain().getCurrentScore());
        }
    }
    public void bestState(long state, Model bestModel) { }
    public void finished(long chainLength) { }
    private final long writeState;
    private final boolean isRepeating;
}
