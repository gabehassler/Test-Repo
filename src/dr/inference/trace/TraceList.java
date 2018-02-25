package dr.inference.trace;
import java.util.List;
public interface TraceList {
String getName();
int getTraceCount();
int getTraceIndex(String name);
String getTraceName(int index);
int getBurnIn();
int getStateCount();
int getBurninStateCount();
int getStepSize();
long getMaxState();
boolean isIncomplete();
List getValues(int index, int fromIndex, int toIndex);
List getValues(int index);
List getBurninValues(int index);
TraceDistribution getDistributionStatistics(int traceIndex);
TraceCorrelation getCorrelationStatistics(int traceIndex);
// create TraceCorrelation regarding Trace
void analyseTrace(int index);
Trace getTrace(int index);
//    public interface D extends TraceList {
//        Double[] getValues(int index, int length);
//        Double[] getValues(int index, int length, int offset);
//        Double[] getBurninValues(int index, int length);
//    }
}