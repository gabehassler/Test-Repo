package dr.inference.model;
import java.io.Serializable;
import java.util.ArrayList;
public interface Bounds<V> extends Serializable {
V getUpperLimit(int dimension);
V getLowerLimit(int dimension);
int getBoundsDimension();
public class Int implements Bounds<Integer> {
int size = 1;
int lower = java.lang.Integer.MIN_VALUE;
int upper = java.lang.Integer.MAX_VALUE;
public Int(int size, int lower, int upper) {
this.size = size;
this.lower = lower;
this.upper = upper;
}
public Int(Variable<Integer> variable, int lower, int upper) {
this.size = variable.getSize();
this.lower = lower;
this.upper = upper;
}
public Integer getUpperLimit(int dimension) {
return upper;
}
public Integer getLowerLimit(int dimension) {
return lower;
}
public int getBoundsDimension() {
return size;
}
}
public class Staircase implements Bounds<Integer> {
int size = 0;
public Staircase(int size) {
this.size = size;
}
public Staircase(Variable<Integer> variable) {
this.size = variable.getSize();
}
public Integer getUpperLimit(int dimension) {
return dimension + 1; // integer index parameter size = real size - 1
}
public Integer getLowerLimit(int dimension) {
return 0;
}
public int getBoundsDimension() {
return size;
}
private ArrayList<Bounds<Integer>> bounds = null;
public void addBounds(Bounds<Integer> boundary) {
if (boundary.getBoundsDimension() != size) {
throw new IllegalArgumentException("Incorrect dimension of bounds, expected " +
size + " but received " + boundary.getBoundsDimension());
}
if (bounds == null) {
bounds = new ArrayList<Bounds<Integer>>();
}
bounds.add(boundary);
}        
}
}	
