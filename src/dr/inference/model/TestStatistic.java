package dr.inference.model;
import dr.util.Attribute;
public class TestStatistic extends BooleanStatistic {
private Attribute attribute = null;
private Attribute attribute2 = null;
private double testValue1, testValue2;
private final int mode;
public static final int EQUALS = 0;
public static final int GREATER_THAN = 1;
public static final int LESS_THAN = 2;
public static final int INSIDE = 3;
public static final int OUTSIDE = 4;
public TestStatistic(String name, Attribute attr, double value, int mode) {
super(name);
attribute = attr;
testValue1 = value;
this.mode = mode;
}
public TestStatistic(String name, Attribute attr, double value1, double value2, int mode) {
super(name);
attribute = attr;
testValue1 = value1;
testValue2 = value2;
this.mode = mode;
}
public TestStatistic(String name, Attribute attr1, Attribute attr2, int mode) {
super(name);
attribute = attr1;
attribute2 = attr2;
this.mode = mode;
}
public int getDimension() {
return 1;
}
public boolean getBoolean(int i) {
double num;
if (attribute instanceof Statistic) {
num = ((Statistic) attribute).getStatisticValue(0);
} else {
num = ((Number) attribute.getAttributeValue()).doubleValue();
}
if (attribute2 != null) {
if (attribute2 instanceof Statistic) {
testValue1 = ((Statistic) attribute2).getStatisticValue(0);
} else {
testValue1 = ((Number) attribute2.getAttributeValue()).doubleValue();
}
}
switch (mode) {
case EQUALS:
if (num == testValue1) return true;
break;
case GREATER_THAN:
if (num > testValue1) return true;
break;
case LESS_THAN:
if (num < testValue1) return true;
break;
case INSIDE:
if (num > testValue1 && num < testValue2) return true;
break;
case OUTSIDE:
if (num < testValue1 && num > testValue2) return true;
break;
}
return false;
}
}
