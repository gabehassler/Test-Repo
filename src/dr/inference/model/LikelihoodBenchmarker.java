package dr.inference.model;
import java.util.List;
import java.util.logging.Logger;
public class LikelihoodBenchmarker {
    public LikelihoodBenchmarker(List<Likelihood> likelihoods, int iterationCount) {
        for (Likelihood likelihood : likelihoods) {
            long startTime = System.nanoTime();
            for (int i = 0; i < iterationCount; i++) {
                likelihood.makeDirty();
                likelihood.getLogLikelihood();
            }
            long endTime = System.nanoTime();
            double seconds = (endTime - startTime) * 1E-9;
            Logger.getLogger("dr.app.beagle").info(
                    "Benchmark " + likelihood.getId() + "(" + likelihood.getClass().getName() + "): " +
                            seconds + " sec");
        }
    }
}
