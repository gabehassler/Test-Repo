package dr.util;
import java.io.Serializable;
public interface Attribute<T> extends Serializable {
public final static String ATTRIBUTE = "att";
public final static String NAME = "name";
public final static String VALUE = "value";
String getAttributeName();
T getAttributeValue();
public class Default<T> implements Attribute<T> {
public Default(String name, T value) {
this.name = name;
this.value = value;
}
public String getAttributeName() {
return name;
}
public T getAttributeValue() {
return value;
}
public String toString() {
return name + ": " + value;
}
private final String name;
private final T value;
}
}
