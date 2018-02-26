package dr.evolution.colouring;
import dr.evolution.tree.ColourChange;
import dr.evolution.tree.Tree;
import dr.math.MathUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
// This is probably redundant but is left in case it is needed as a comparison.
//public class TwoColourSampler implements ColourSampler {
abstract class TwoColourSampler implements ColourSampler {
    // m[0] is the rate of migration from population 1 to population 0
    // m[1] is the rate of migration from population 0 to population 1
    public TwoColourSampler() {
    }
    public int getColourCount() {
        return 2;
    }
    public TreeColouring sampleTreeColouring(Tree tree, ColourChangeMatrix colourChangeMatrix, double[] N) {
        return null;
    }
    private final double conditionalSurvivalProbability(int x, double v, int y, double t, double[] m) {
        // the total rate of migration
        double mt = m[0]+m[1];
        if (v < 0 || v > t) {
            throw new IllegalArgumentException("v must be non-negative and not exceed t\n v="+v+" t="+t);
        } else {
            double a,b,c,d;
            if (x==y) {
                a = m[0]*m[1];
                b = -a;
                c = m[1-x];
                d = m[x];
            } else {
                a = m[1-y];
                b = m[1-x];
                c = 1;
                d = -1;
            }
            double exponent = (b*c-a*d)/(c*d*-mt);
            double C = Math.pow(c*Math.exp(-mt*t)+d, -exponent);
            return C * Math.exp(b*v/d) * Math.pow( d + c * Math.exp(-mt*(t-v)), exponent);
        }
    }
    private double randomConditionalSurvivalTime(int x, int y, double t, double[] m) throws NoEventException {
    	double U = MathUtils.nextDouble();
        double vLeft = 0.0;
        double vRight = t;
        double fLeft = 1.0-U;
    	double fRight = conditionalSurvivalProbability(x,t,y,t, m) - U;
    	if (fRight >= 0.0) {
    		if (x != y) {
    			// No event but colours at ends are not identical
    			throw new IllegalArgumentException("Problem in randomConditionalSurvivalTime for x="+x+", y="+y+" t="+t+" U="+U+"fRight="+fRight);
    		}
    		throw new NoEventException();
    	}
        // We are solving the equation "conditionalSurvivalprobability(x,v,y,t,m) - U == 0"
        // for v.  The solution is bound by [vLeft,vRight], with corresponding function
        // values fLeft and fRight.  The algorithm is basically Newton-Raphson, using an approximate
    	// derivative.  Two limits bracketing the solution are kept as well, and a fallback is
    	// used if the Newton iteration jumps out of the bracket.  This gives a robust and efficient
    	// root finder.
    	double vNew = vLeft;   // the bracket position that has changed
    	double vOld = vRight;  // the previous value of vNew
    	double fNew = fLeft;
    	double fOld = fRight;
    	while (Math.abs(vNew-vOld)>1e-9) {
    		// Calculate proposed new v, using the changed bracket value.  
    		// This is expected to give the highest performance, but may run out of the bracket
    		double vProp = vNew - fNew/((fOld-fNew)/(vOld-vNew));
    		if ((vLeft >= vProp) || (vProp >= vRight)) {
    			// Outside of bracket, so use safe option
    			vProp = vLeft - fLeft/((fRight-fLeft)/(vRight-vLeft));
    		}
    		// New function value
			double fProp = conditionalSurvivalProbability(x,vProp,y,t, m) - U;
			vOld = vNew;
			fOld = fNew;
			vNew = vProp;
			fNew = fProp;
			// Update bracket
			if (fProp < 0.0) {
				vRight = vProp;
				fRight = fProp;
			} else {
				vLeft = vProp;
				fLeft = fProp;
			}
    	}
    	return vNew;
    }
    public ColourChange randomConditionalMigrationEvent(int currentColour, double currentHeight,
                                                        int childColour, double childHeight, double[] m)
            throws NoEventException {
        // Draw a valid time (or throw NoEventException)
    	if (currentHeight < childHeight) {
    		throw new IllegalArgumentException("currentHeight "+currentHeight+" is below childHeight="+childHeight);
    	}
        double time = randomConditionalSurvivalTime(currentColour, childColour, currentHeight-childHeight, m);
        // Return the corresponding event
        return new ColourChange( currentHeight-time, 1-currentColour );
    }
    public List<ColourChange> sampleConditionalMigrationEvents2(int parentColour, double parentHeight,
    		int childColour, double childHeight, double[] m) {
    	List<ColourChange> colourChanges = new ArrayList<ColourChange>();
    	if (parentHeight < childHeight) {
    		throw new IllegalArgumentException("sampleConditionalMigrationEvents: parentHeight="+parentHeight+" childHeight="+childHeight+", not good.");
    	}
        // Sample migration events, going from the parent (current) to the child, forward in natural time
        // until a NoEventException breaks the loop.  Migration events are returned as ColourChange-s
        try {
        	int currentColour = parentColour;
        	double currentHeight = parentHeight;
            while (true) { 
                ColourChange nextEvent = 
                	randomConditionalMigrationEvent(currentColour, currentHeight, childColour, childHeight, m);
                // We abuse the ColorChange interface, which is supposed to record events going up the tree,
                // (in the coalescent direction), for the duration of this loop.  So, getColourAbove should 
                // really read 'getColourBelow'
                currentHeight = nextEvent.getTime();
                currentColour = nextEvent.getColourAbove();   // colour *below* current height
                // record event
                colourChanges.add(nextEvent);
            }
        } catch (NoEventException nee) {
            // no more events
        }
        // Reverse the list
        reverseColourChangeList( colourChanges, parentColour);
    	return colourChanges;
    }
     public List<ColourChange> sampleConditionalMigrationEvents(int parentColour, double parentHeight,
    		int childColour, double childHeight, double[] m) {
    	List<ColourChange> colourChanges = new ArrayList<ColourChange>();
    	int currentColour;
    	double currentHeight;
    	// Reject until we get the child colour
    	do {
    		colourChanges.clear();
    		currentColour = parentColour;
    		currentHeight = parentHeight;
    		// Sample events until we reach the child
    		do { 
    			// Sample a waiting time
    			double totalRate = m[ 1-currentColour ];
        		double U = MathUtils.nextDouble();
        		// Neat trick (Rasmus Nielsen): 
        		// If colours of parent and child differ, sample conditioning on at least 1 event
        		if ((parentColour != childColour) && (colourChanges.size() == 0)) {
        			double minU = Math.exp( -totalRate * (parentHeight-childHeight) );
        			U = minU + U*(1.0-minU);
        		}
        		// Calculate the waiting time, and update currentHeight
        		double time = -Math.log( U )/totalRate;
        		currentHeight -= time;
        		if (currentHeight > childHeight) {
               		// Not yet reached the child.  "Sample" an event
            		currentColour = 1 - currentColour;
            		// Add it to the list
            		colourChanges.add( new ColourChange( currentHeight, currentColour ) );
        		}
    		} while (currentHeight > childHeight);
    	} while (currentColour != childColour);
        // Reverse the list
        reverseColourChangeList( colourChanges, parentColour);
    	return colourChanges;
    }
     class NoEventException extends Exception {
		private static final long serialVersionUID = -6860022343065166240L;
     }
    public double migrationEventProposalDensity(int x, int y, double t, double[] m) {
        // Bugfix: Did not use correct interpretation of m[] array
        if (x==y) {
            return Math.exp(-t*m[1-x]);
        } else {
            return Math.exp(-t*m[1-x])*m[1-x];
        }
    }
    public final double[] equilibrium(double[] m) {
        double mt = m[0]+m[1];
        return new double[] {m[0]/mt, m[1]/mt};
    }
     protected final void reverseColourChangeList( List<ColourChange> colourChanges, int parentColour ) {
    	Collections.reverse( colourChanges );
    	int colour;
    	for (int i=0; i < colourChanges.size(); i++) {
    		if (i<colourChanges.size()-1) {
    			colour = (colourChanges.get( i+1 )).getColourAbove();
    		} else {
    			colour = parentColour;
    		}
    		(colourChanges.get( i )).setColourAbove( colour );
    	}
    }
    public static void main(String[] args) {
         TwoColourSampler model = new TwoColourSampler();
         double[] m = new double[] {0.2, 0.5};
         int iterations = 500000;
         long time = System.currentTimeMillis();
         for (int j = 0; j < 4; j++) {
             double[] times = new double[iterations];
             for (int i = 0; i < iterations; i++) {
                 List events = model.sampleConditionalMigrationEvents2(j/2,1.0,j%2,0.0,m);
                 if (events.size()==0) {
                 	times[i] = 1.0;
                 } else {
                	times[i] = 1.0-((ColourChange)events.get(0)).getTime();
                 }
             }
             System.out.println("Analytic:"+(j/2) + "\t" + (j%2) + "\t" + DiscreteStatistics.mean(times) + "\t" + (DiscreteStatistics.stdev(times)/Math.sqrt(100000.0)));
         }
         System.out.println("time taken:" + (System.currentTimeMillis()-time) + "ms");
         time = System.currentTimeMillis();
         for (int j = 0; j < 4; j++) {
             double[] times = new double[iterations];
             for (int i = 0; i < iterations; i++) {
                 List events = model.sampleConditionalMigrationEvents(j/2,1.0,j%2,0.0,m);
                 if (events.size()==0) {
                 	times[i] = 1.0;
                 } else {
                 	times[i] = 1.0-((ColourChange)events.get(0)).getTime();
                 }
             }
             System.out.println("Rejection sampling:"+(j/2) + "\t" + (j%2) + "\t" + DiscreteStatistics.mean(times) + "\t" + (DiscreteStatistics.stdev(times)/Math.sqrt(100000.0)));
         }
         System.out.println("time taken:" + (System.currentTimeMillis()-time) + "ms");
     }
}
