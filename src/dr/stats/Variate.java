package dr.stats;
import java.util.ArrayList;
import java.util.List;
public interface Variate<T> {
int getCount();
T get(int index);
T getMin();
T getMax();
T getRange();
T getSum();
T getMean();
double getQuantile(T q);
void add(T value);
void add(Variate values);
void add(T[] values);
void remove(int index);
void removeAll();
public abstract class N<Number> implements Variate<Number> {
protected List<Number> values = new ArrayList<Number>();
//        public N() {}
//        public N(List<Number> values) {
//            add(values);
//        }
public int getCount() {
return values.size();
}
public void add(Variate values) {
for (int i = 0; i < values.getCount(); i++) {
add((Number) values.get(i));
}
}
public void add(Number value) {
values.add(value);
}
public Number get(int index) {
return values.get(index);
}
public void remove(int index) {
values.remove(index);
}
public void removeAll() {
values.clear();
}
public void add(List<Number> values) {
this.values.addAll(values);
}
public void add(Number[] values) {
for (Number value : values) {
add(value);
}
}
}
public class D extends N<Double> {
public D() {}
public D(List<Double> values) {
//            super(values);
add(values);
}
public D(Double[] values) {
add(values);
}
public Double getMin() {
Double minValue = java.lang.Double.POSITIVE_INFINITY;
for (Double value : values) {
if (value < minValue)
minValue = value;
}
return minValue;
}
public Double getMax() {
Double maxValue = java.lang.Double.NEGATIVE_INFINITY;
for (Double value : values) {
if (value > maxValue)
maxValue = value;
}
return maxValue;
}
public Double getRange() {
return getMin() - getMax();
}
public Double getMean() {
return getSum() / getCount();
}
public Double getSum() {
Double sum = 0.0;
for (Double value : values) {
sum += value;
}
return sum;
}
public double getQuantile(Double q) {
double[] dv = new double[values.size()];
for (int i = 0; i < values.size(); i++) {
dv[i] = values.get(i);
}
return DiscreteStatistics.quantile(q.doubleValue(), dv);
}
}
public class I extends N<Integer> {
public I() {}
public I(List<Integer> values) {
//            super(values);
add(values);
}
public I(Integer[] values) {
add(values);
}
public Integer getMin() {
Integer minValue = java.lang.Integer.MAX_VALUE;
for (Integer value : values) {
if (value < minValue)
minValue = value;
}
return minValue;
}
public Integer getMax() {
Integer maxValue = java.lang.Integer.MIN_VALUE;
for (Integer value : values) {
if (value > maxValue)
maxValue = value;
}
return maxValue;
}
public Integer getRange() {
return getMin() - getMax();
}
public Integer getMean() {
return getSum() / getCount();
}
public Integer getSum() {
Integer sum = 0;
for (Integer value : values) {
sum += value;
}
return sum;
}
public double getQuantile(Integer q) {
double[] dv = new double[values.size()];
for (int i = 0; i < values.size(); i++) {
dv[i] = values.get(i).doubleValue();
}
return DiscreteStatistics.quantile(q.doubleValue(), dv);
}
}
}