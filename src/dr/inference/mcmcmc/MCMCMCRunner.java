package dr.inference.mcmcmc;
import dr.inference.markovchain.MarkovChain;
public class MCMCMCRunner extends Thread {
public MCMCMCRunner(MarkovChain markovChain, long length, long totalLength, boolean disableCoerce) {
this.markovChain = markovChain;
this.length = length;
this.totalLength = totalLength;
this.disableCoerce = disableCoerce;
}
public void run() {
long i = 0;
while (i < totalLength) {
markovChain.runChain(length, disableCoerce/*, 0*/);
i += length;
chainDone();
if (i < totalLength) {
while (isChainDone()) {
try {
synchronized(this) {
wait();
}
} catch (InterruptedException e) {
// continue...
}
}
}
}
}
private synchronized void chainDone() {
chainDone = true;
}
public synchronized boolean isChainDone() {
return chainDone;
}
public synchronized void continueChain() {
this.chainDone = false;
notify();
}
private final MarkovChain markovChain;
private final long length;
private final long totalLength;
private final boolean disableCoerce;
private boolean chainDone;
}
