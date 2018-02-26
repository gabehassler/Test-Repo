
package dr.inference.trace;

public interface TracesListener {

    void traceNames(String[] names);

    void traceRow(int state, double[] values);
}
