package dr.evolution.coalescent.structure;
import dr.evolution.coalescent.IntervalList;
public interface StructuredIntervalList extends IntervalList {
    public int getLineageCount(int interval, int population);
    public Event getEvent(int interval);
    public int getPopulationCount();
}
