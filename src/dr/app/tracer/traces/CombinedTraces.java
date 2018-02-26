
package dr.app.tracer.traces;

import dr.inference.trace.*;

import java.util.ArrayList;
import java.util.List;



public class CombinedTraces extends FilteredTraceList { //implements TraceList {

    public CombinedTraces(String name, LogFileTraces[] traceLists) throws TraceException {

        if (traceLists == null || traceLists.length < 1) {
            throw new TraceException("Must have at least 1 Traces object in a CombinedTraces");
        }

        this.name = name;
        this.traceLists = new LogFileTraces[traceLists.length];
        this.traceLists[0] = traceLists[0];

        for (int i = 1; i < traceLists.length; i++) {
            if (traceLists[i].getTraceCount() != traceLists[0].getTraceCount()) {
                throw new TraceException("Cannot add to a CombinedTraces: the count of new traces do not match existing traces");
            }

            if (traceLists[i].getStepSize() != traceLists[0].getStepSize()) {
                throw new TraceException("Cannot add to a CombinedTraces: the step sizes of the new traces do not match existing traces");
            }

            for (int j = 0; j < traceLists[0].getTraceCount(); j++) {
                if (!traceLists[i].getTraceName(j).equals(traceLists[0].getTraceName(j))) {
                    throw new TraceException("Cannot add to a CombinedTraces: new traces do not match existing traces");
                } else if (traceLists[i].getTrace(j).getTraceType() != traceLists[0].getTrace(j).getTraceType()) {
                    throw new TraceException("Cannot add to a CombinedTraces: new traces type do not match existing type");
                }
            }
            this.traceLists[i] = traceLists[i];
        }


    }

    public String getName() {
        return name;
    }

    public int getTraceCount() {
        return traceLists[0].getTraceCount();
    }

    public int getTraceIndex(String name) {
        return traceLists[0].getTraceIndex(name);
    }

    public String getTraceName(int index) {
        return traceLists[0].getTraceName(index);
    }

    public int getStateCount() {
        int sum = 0;
        for (LogFileTraces traceList : traceLists) {
            sum += traceList.getStateCount();
        }
        return sum;
    }

    public int getBurninStateCount() {
        return 0;
    }


    public boolean isIncomplete() {
        return false;
    }

    public int getBurnIn() {
        return 0;
    }

    public int getStepSize() {
        return traceLists[0].getStepSize();
    }

    public long getMaxState() {
        return getStateCount() * getStepSize();
    }

    public List getValues(int index, int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("not available");
    }

    public List getValues(int index) {
        List valuesList = new ArrayList();
        for (LogFileTraces traceList : traceLists) {
            valuesList.addAll(traceList.getValues(index));
        }
        return valuesList;
    }

    public List getBurninValues(int index) {
        throw new UnsupportedOperationException("getBurninValues is not a valid operation on CombinedTracers");
    }

    public TraceDistribution getDistributionStatistics(int index) {
        return getCorrelationStatistics(index);
    }

    public TraceCorrelation getCorrelationStatistics(int index) {
        if (traceStatistics == null) {
            return null;
            // this can happen if the ESS has not been calculated yet.
//	    throw new RuntimeException("No ESS for combined traces? This is not supposed to happen.");
        }
        return traceStatistics[index];
    }

    public void analyseTrace(int index) {
        // no offset: burnin is handled inside each TraceList we own and invisible to us.
        if (traceStatistics == null) {
            traceStatistics = new TraceCorrelation[getTraceCount()];            
        }

        Trace trace = getTrace(index);

        if (trace != null)
            traceStatistics[index] = new TraceCorrelation(getValues(index), trace.getTraceType(), getStepSize()); 
    }

    public Trace getTrace(int index) {
        for (LogFileTraces traceList : traceLists) {
            if (traceList.getTrace(index).getTraceType() != traceLists[0].getTrace(index).getTraceType()) {
                return null; // trace type not comparable
            }
        }
        return traceLists[0].getTrace(index);
    }

    public int getTraceListCount() {
        return traceLists.length;
    }

    public TraceList getTraceList(int index) {
        return traceLists[index];
    }

    //************************************************************************
    // private methods
    //************************************************************************

    private LogFileTraces[] traceLists = null;

    private TraceCorrelation[] traceStatistics = null;

    private String name;

    //************* Filter ******************

    @Override
    public boolean hasFilter(int traceIndex) {
        return traceLists[0].hasFilter(traceIndex);
    }

    @Override
    public void setFilter(int traceIndex, Filter filter) {
        for (LogFileTraces traceList : traceLists) {
            traceList.setFilter(traceIndex, filter);
        }
        this.refreshStatistics();
    }

    @Override
    public Filter getFilter(int traceIndex) {
        return traceLists[0].getFilter(traceIndex);
    }

    @Override
    public void removeFilter(int traceIndex) {
        for (LogFileTraces traceList : traceLists) {
            traceList.removeFilter(traceIndex);
        }
        this.refreshStatistics();
    }

    @Override
    public void removeAllFilters() {
        for (LogFileTraces traceList : traceLists) {
            traceList.removeAllFilters();
        }
        this.refreshStatistics();
    }

    @Override
    protected void refreshStatistics() {
        for (int i = 0; i < getTraceCount(); i++) {
            analyseTrace(i);
        }
    }
}