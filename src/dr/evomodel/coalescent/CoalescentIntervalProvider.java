package dr.evomodel.coalescent;
import dr.evolution.coalescent.IntervalType;
public interface CoalescentIntervalProvider {
public int getCoalescentIntervalDimension();
public double getCoalescentInterval(int i);
public int getCoalescentIntervalLineageCount(int i);
public IntervalType getCoalescentIntervalType(int i);
}
