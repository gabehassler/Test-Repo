package dr.inference.trace;
public class TraceCustomized extends Trace{
public TraceCustomized(String name) { // traceType = TraceFactory.TraceType.DOUBLE;
super(name);
}
public void addValues(Trace<Double> t) {
Double r = 1.0;
for (Double v : t.values) {
Double newV = 2.0 / (1.0 + v * r);
super.values.add(newV);
}
}
}