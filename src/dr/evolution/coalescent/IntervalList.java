package dr.evolution.coalescent;
import dr.evolution.util.Units;
public interface IntervalList extends Units {
int getIntervalCount();
int getSampleCount();
double getInterval(int i);
int getLineageCount(int i);
int getCoalescentEvents(int i);
IntervalType getIntervalType(int i);
double getTotalDuration();
boolean isBinaryCoalescent();
boolean isCoalescentOnly();
public class Utils {	
public static int getLineageCount(IntervalList intervals, double t) {
int i = 0;
while (i < intervals.getIntervalCount() && t > intervals.getInterval(i)) { 
t -= intervals.getInterval(i);
i+= 1; 
}
if (i == intervals.getIntervalCount()) return 1;
return intervals.getLineageCount(i);
}
public static double getDelta(IntervalList intervals) {
// Assumes ultrametric tree!
if (!intervals.isCoalescentOnly()) {
throw new IllegalArgumentException("Assumes ultrametric tree!");
}
int n = intervals.getIntervalCount();
int numTips = n + 1;
double transTreeDepth = 0.0;
double cumInts = 0.0;
double sum = 0.0;
// transform intervals
for (int j=0; j<n; j++) { // move from tips to root
double transInt = intervals.getInterval(j) * 
dr.math.Binomial.choose2(intervals.getLineageCount(j)); // coalescent version
//intLenCopy[j] = getInterval(j)*getLineageCount(j); // birth-death version
// don't include the last interval so put this before...
sum += cumInts;
// ...incrementing the cumInts
cumInts += transInt;
transTreeDepth += transInt;
}
double halfTreeDepth = transTreeDepth / 2.0;
sum *= (1.0/(numTips-2.0));
double top = halfTreeDepth - sum;
double bottom = transTreeDepth * Math.sqrt((1.0/(12.0*(numTips-2.0))));
return (top / bottom);
}
}
}