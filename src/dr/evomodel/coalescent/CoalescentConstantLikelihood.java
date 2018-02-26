package dr.evomodel.coalescent;
import jebl.math.Binomial;
import dr.evolution.coalescent.TreeIntervals;
import dr.evomodel.tree.TreeModel;
import dr.inference.model.Likelihood;
public class CoalescentConstantLikelihood extends Likelihood.Abstract {
	public final static boolean COALESCENT_EVENTS_ONLY = false;
	private TreeModel treeModel;
	private TreeIntervals intervals;
	// PUBLIC STUFF
	public CoalescentConstantLikelihood(TreeModel treeModel) {
		super(treeModel);
		this.treeModel = treeModel;
		this.intervals = new TreeIntervals(treeModel);
	}
    // **************************************************************
	// Likelihood IMPLEMENTATION
	// **************************************************************
	public double calculateLogLikelihood() {
		intervals.setIntervalsUnknown();
		final int nIntervals = intervals.getIntervalCount();
		//System.err.println(treeModel);
		//System.err.println("Interval count: " + nIntervals);
		double logPDF = 0.0;
        for (int i = 0; i < nIntervals; i++) {
        	//System.err.println("Lineage count " + i + ": " + intervals.getLineageCount(i));
        	//System.err.println("Interval time " + i + ": " + intervals.getIntervalTime(i));
        	//System.err.println("Coalescent event " + i + ": " + intervals.getCoalescentEvents(i));
        	if (COALESCENT_EVENTS_ONLY) {
        		if (intervals.getCoalescentEvents(i) > 0) {
        			logPDF += Math.log(Binomial.choose2(intervals.getLineageCount(i)));
        		}
        	} else if (intervals.getLineageCount(i) > 2) {
       			logPDF += Math.log(Binomial.choose2(intervals.getLineageCount(i)));
       			//System.err.println("PDF: " + Binomial.choose2(intervals.getLineageCount(i)));
       		}
        }
        //START TEST CONTEMPORANEOUS
        for (int i = 5; i > 2; --i) {
            test += Math.log(Binomial.choose2(i));
        }
        if (test != logPDF) {
        	System.err.println(test + "    " + logPDF);
        	System.exit(0);
        }*/
        //END TEST CONTEMPORANEOUS
        //System.err.println("logPDF = " + (-logPDF) + "\n");
        return -logPDF;
	}
}