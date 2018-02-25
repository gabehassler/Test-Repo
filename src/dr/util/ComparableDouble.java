package dr.util;
public class ComparableDouble implements Comparable {
private final double value;
public ComparableDouble(double d) {
value = d;
}
public int compareTo(Object o) {
ComparableDouble cd = (ComparableDouble)o;
if (value < cd.value) {
return -1;
} else if (value > cd.value) {
return 1;
} else return 0;
}
public boolean equals(Object o) {
ComparableDouble cd = (ComparableDouble)o;
return cd.value == value;
}
public double doubleValue() {
return value;
}
public String toString() {
return value + "";
}
}
