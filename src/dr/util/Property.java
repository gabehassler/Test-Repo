package dr.util;
import java.lang.reflect.Method;
public class Property implements Attribute {
private Object object = null;
private Method getter = null;
private Object argument = null;
private String name = null;
public Property(Object object, String name) {
this(object, name, null);
}
public Property(Object object, String name, Object argument) {
this.name = name;
this.argument = argument;
this.object = object;
StringBuffer getterName = new StringBuffer("get");
getterName.append(name.substring(0, 1).toUpperCase());
getterName.append(name.substring(1));
Class c = object.getClass();
//System.out.println(getterName + "(" + argument + ")");
try {
if (argument != null)
getter = c.getMethod(getterName.toString(), new Class[]{argument.getClass()});
else
getter = c.getMethod(getterName.toString(), (Class[]) null);
} catch (NoSuchMethodException e) {
}
}
public Method getGetter() {
return getter;
}
//public Object getObject() { return object; }
public String getAttributeName() {
if (argument == null) return name;
return name + "." + argument;
}
public Object getAttributeValue() {
if (object == null || getter == null)
return null;
Object result = null;
Object[] args = null;
if (argument != null)
args = new Object[]{argument};
try {
result = getter.invoke(object, args);
} catch (Exception e) {
e.printStackTrace(System.out);
throw new RuntimeException(e.getMessage());
}
return result;
}
public String getPropertyName() {
return name;
}
public String toString() {
return getAttributeValue().toString();
}
}
