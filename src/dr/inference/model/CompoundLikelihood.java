
package dr.inference.model;

import dr.app.beagle.evomodel.branchmodel.lineagespecific.BeagleBranchLikelihood;
import dr.util.NumberFormatter;
import dr.xml.Reportable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class CompoundLikelihood implements Likelihood, Reportable {

    public final static boolean UNROLL_COMPOUND = true;

    public final static boolean EVALUATION_TIMERS = true;
    public final long[] evaluationTimes;
    public final int[] evaluationCounts;

    public CompoundLikelihood(int threads, Collection<Likelihood> likelihoods) {

        int i = 0;
        for (Likelihood l : likelihoods) {
            addLikelihood(l, i, true);
            i++;
        }

        if (threads < 0 && this.likelihoods.size() > 1) {
            // asking for an automatic threadpool size and there is more than one likelihood to compute
            threadCount = this.likelihoods.size();
        } else if (threads > 0) {
            threadCount = threads;
        } else {
            // no thread pool requested or only one likelihood
            threadCount = 0;
        }

        if (threadCount > 0) {
            pool = Executors.newFixedThreadPool(threadCount);
//        } else if (threads < 0) {
//            // create a cached thread pool which should create one thread per likelihood...
//            pool = Executors.newCachedThreadPool();
        } else {
            pool = null;
        }

        if (EVALUATION_TIMERS) {
            evaluationTimes = new long[this.likelihoods.size()];
            evaluationCounts = new int[this.likelihoods.size()];
        } else {
            evaluationTimes = null;
            evaluationCounts = null;
        }
    }

    public CompoundLikelihood(Collection<Likelihood> likelihoods) {

        pool = null;
        threadCount = 0;

        int i = 0;
        for (Likelihood l : likelihoods) {
            addLikelihood(l, i, false);
            i++;
        }

        if (EVALUATION_TIMERS) {
            evaluationTimes = new long[this.likelihoods.size()];
            evaluationCounts = new int[this.likelihoods.size()];
        } else {
            evaluationTimes = null;
            evaluationCounts = null;
        }
    }

//    public CompoundLikelihood(BeagleBranchLikelihoods bbl) {
//
//        pool = null;
//        threadCount = 0;
//        evaluationTimes = null;
//        evaluationCounts = null;
//        
//    }
    
    protected void addLikelihood(Likelihood likelihood, int index, boolean addToPool) {

        // unroll any compound likelihoods
        if (UNROLL_COMPOUND && addToPool && likelihood instanceof CompoundLikelihood) {
        	
            for (Likelihood l : ((CompoundLikelihood)likelihood).getLikelihoods()) {
                addLikelihood(l, index, addToPool);
            }
            
        } else {
        	
            if (!likelihoods.contains(likelihood)) {

                likelihoods.add(likelihood);
                if (likelihood.getModel() != null) {
                    compoundModel.addModel(likelihood.getModel());
                }

                if (likelihood.evaluateEarly()) {
                	
                    earlyLikelihoods.add(likelihood);
                    
                } else {
                	
                    // late likelihood list is used to evaluate them if the thread pool is not being used...
                    lateLikelihoods.add(likelihood);

                    if (addToPool) {
                        likelihoodCallers.add(new LikelihoodCaller(likelihood, index));
                    }
                }
                
//            } else {
            	
            	//TODO: hack in branch likes here
//            	likelihoods.add(likelihood);
            	
            }// END: contains check
            
        }//END: if unroll check
        
    }//END: addLikelihood

    public int getLikelihoodCount() {
        return likelihoods.size();
    }

    public final Likelihood getLikelihood(int i) {
        return likelihoods.get(i);
    }

    public List<Likelihood> getLikelihoods() {
        return likelihoods;
    }

    public List<Callable<Double>> getLikelihoodCallers() {
        return likelihoodCallers;
    }

    // **************************************************************
    // Likelihood IMPLEMENTATION
    // **************************************************************

    public Model getModel() {
        return compoundModel;
    }

//    // todo: remove in release
//    static int DEBUG = 0;

    public double getLogLikelihood() {

        double logLikelihood = evaluateLikelihoods(earlyLikelihoods);

        if( logLikelihood == Double.NEGATIVE_INFINITY ) {
            return Double.NEGATIVE_INFINITY;
        }

        if (pool == null) {
            // Single threaded
            logLikelihood += evaluateLikelihoods(lateLikelihoods);
        } else {

            try {
                List<Future<Double>> results = pool.invokeAll(likelihoodCallers);

                for (Future<Double> result : results) {
                    double logL = result.get();
                    logLikelihood += logL;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

//        if( DEBUG > 0 ) {
//            int t = DEBUG; DEBUG = 0;
//            System.err.println(getId() + ": " + getDiagnosis(0) + " = " + logLikelihood);
//            DEBUG = t;
//        }

        if (DEBUG_PARALLEL_EVALUATION) {
            System.err.println("");
        }
        return logLikelihood;
    }

    private double evaluateLikelihoods(ArrayList<Likelihood> likelihoods) {
        double logLikelihood = 0.0;
        int i = 0;
        for (Likelihood likelihood : likelihoods) {
            if (EVALUATION_TIMERS) {
                // this code is only compiled if EVALUATION_TIMERS is true
                long time = System.nanoTime();
                double l = likelihood.getLogLikelihood();
                evaluationTimes[i] += System.nanoTime() - time;
                evaluationCounts[i] ++;

                if( l == Double.NEGATIVE_INFINITY )
                    return Double.NEGATIVE_INFINITY;

                logLikelihood += l;

                i++;
            } else {
                final double l = likelihood.getLogLikelihood();
                // if the likelihood is zero then short cut the rest of the likelihoods
                // This means that expensive likelihoods such as TreeLikelihoods should
                // be put after cheap ones such as BooleanLikelihoods
                if( l == Double.NEGATIVE_INFINITY )
                    return Double.NEGATIVE_INFINITY;
                logLikelihood += l;
            }
        }

        return logLikelihood;
    }

    public void makeDirty() {
        for( Likelihood likelihood : likelihoods ) {
            likelihood.makeDirty();
        }
    }

    public boolean evaluateEarly() {
        return false;
    }

    public String getDiagnosis() {
        return getDiagnosis(0);
    }

    public String getDiagnosis(int indent) {
        String message = "";
        boolean first = true;

        final NumberFormatter nf = new NumberFormatter(6);

        for( Likelihood lik : likelihoods ) {

            if( !first ) {
                message += ", ";
            } else {
                first = false;
            }

            if (indent >= 0) {
                message += "\n";
                for (int i = 0; i < indent; i++) {
                    message += " ";
                }
            }
            message += lik.prettyName() + "=";

            if( lik instanceof CompoundLikelihood ) {
                final String d = ((CompoundLikelihood) lik).getDiagnosis(indent < 0 ? -1 : indent + 2);
                if( d != null && d.length() > 0 ) {
                    message += "(" + d;

                    if (indent >= 0) {
                        message += "\n";
                        for (int i = 0; i < indent; i++) {
                            message += " ";
                        }
                    }
                    message += ")";
                }
            } else {

                final double logLikelihood = lik.getLogLikelihood();
                if( logLikelihood == Double.NEGATIVE_INFINITY ) {
                    message += "-Inf";
                } else if( Double.isNaN(logLikelihood) ) {
                    message += "NaN";
                } else if( logLikelihood == Double.POSITIVE_INFINITY ) {
                    message += "+Inf";
                } else {
                    message += nf.formatDecimal(logLikelihood, 4);
                }
            }
        }
        message += "\n";
        for (int i = 0; i < indent; i++) {
            message += " ";
        }
        message += "Total = " + this.getLogLikelihood();

        return message;
    }

    public String toString() {
        return getId();
        // really bad for debugging
        //return Double.toString(getLogLikelihood());
    }

    public String prettyName() {
        return Abstract.getPrettyName(this);
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed() {
        used = true;
        for (Likelihood l : likelihoods) {
            l.setUsed();
        }
    }

    public int getThreadCount() {
        return threadCount;
    }
    
    public long[] getEvaluationTimes() {
    	return evaluationTimes;
    }
    
    public int[] getEvaluationCounts() {
    	return evaluationCounts;
    }
    
    public void resetEvaluationTimes() {
    	for (int i = 0; i < evaluationTimes.length; i++) {
    		evaluationTimes[i] = 0;
    		evaluationCounts[i] = 0;
    	}
    }


    // **************************************************************
    // Loggable IMPLEMENTATION
    // **************************************************************

    public dr.inference.loggers.LogColumn[] getColumns() {
        return new dr.inference.loggers.LogColumn[]{
                new LikelihoodColumn(getId() == null ? "likelihood" : getId())
        };
    }

    private class LikelihoodColumn extends dr.inference.loggers.NumberColumn {
        public LikelihoodColumn(String label) {
            super(label);
        }

        public double getDoubleValue() {
            return getLogLikelihood();
        }
    }

    // **************************************************************
    // Reportable IMPLEMENTATION
    // **************************************************************

    public String getReport() {
        return getReport(0);
    }

    public String getReport(int indent) {
        if (EVALUATION_TIMERS) {
            String message = "\n";
            boolean first = true;

            final NumberFormatter nf = new NumberFormatter(6);

            int index = 0;
            for( Likelihood lik : likelihoods ) {

                if( !first ) {
                    message += ", ";
                } else {
                    first = false;
                }

                if (indent >= 0) {
                    message += "\n";
                    for (int i = 0; i < indent; i++) {
                        message += " ";
                    }
                }
                message += lik.prettyName() + "=";

                if( lik instanceof CompoundLikelihood ) {
                    final String d = ((CompoundLikelihood) lik).getReport(indent < 0 ? -1 : indent + 2);
                    if( d != null && d.length() > 0 ) {
                        message += "(" + d;

                        if (indent >= 0) {
                            message += "\n";
                            for (int i = 0; i < indent; i++) {
                                message += " ";
                            }
                        }
                        message += ")";
                    }
                } else {
                    double secs = (double)evaluationTimes[index] / 1.0E9;
                    message += evaluationCounts[index] + " evaluations in " +
                            nf.format(secs) + " secs (" +
                            nf.format(secs / evaluationCounts[index]) + " secs/eval)";
                }
                index++;
            }

            return message;
        } else {
            return "No evaluation timer report available";
        }
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

    private boolean used = false;

    private final int threadCount;

    private final ExecutorService pool;

    private final ArrayList<Likelihood> likelihoods = new ArrayList<Likelihood>();
    private final CompoundModel compoundModel = new CompoundModel("compoundModel");

    private final ArrayList<Likelihood> earlyLikelihoods = new ArrayList<Likelihood>();
    private final ArrayList<Likelihood> lateLikelihoods = new ArrayList<Likelihood>();

    private final List<Callable<Double>> likelihoodCallers = new ArrayList<Callable<Double>>();

    class LikelihoodCaller implements Callable<Double> {

        public LikelihoodCaller(Likelihood likelihood, int index) {
            this.likelihood = likelihood;
            this.index = index;
        }

        public Double call() throws Exception {
            if (DEBUG_PARALLEL_EVALUATION) {
                System.err.print("Invoking thread #" + index + " for " + likelihood.getId() + ": ");
            }
            if (EVALUATION_TIMERS) {
                long time = System.nanoTime();
                double logL = likelihood.getLogLikelihood();
                evaluationTimes[index] += System.nanoTime() - time;
                evaluationCounts[index] ++;
                return logL;
            }
            return likelihood.getLogLikelihood();
        }

        private final Likelihood likelihood;
        private final int index;
    }

    public static final boolean DEBUG_PARALLEL_EVALUATION = false;

}

