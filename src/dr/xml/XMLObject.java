package dr.xml;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
public class XMLObject {
public static final String missingValue = "NA";
public XMLObject(Element e) {
this.element = e;
}
public XMLObject(XMLObject obj, int index) {
this(obj.element);
nativeObject = ((List)obj.getNativeObject()).get(index);
}
public final int getChildCount() {
return children.size();
}
public Object getChild(int i) {
Object obj = getRawChild(i);
XMLObject xo = null;
if( obj instanceof XMLObject ) {
xo = (XMLObject) obj;
} else if( obj instanceof Reference ) {
xo = ((Reference) obj).getReferenceObject();
}
if( xo != null && xo.hasNativeObject() ) {
return xo.getNativeObject();
}
return obj;
}
public Object getChild(Class c) {
for (int i = 0; i < getChildCount(); i++) {
Object child = getChild(i);
if( c.isInstance(child) ) {
return child;
}
}
return null;
}
public XMLObject getChild(String name) {
for (int i = 0; i < getChildCount(); i++) {
Object child = getChild(i);
if( child instanceof XMLObject ) {
if( ((XMLObject) child).getName().equals(name) ) {
return (XMLObject) child;
}
}
}
return null;
}
public Object getElementFirstChild(String elementName) throws XMLParseException {
Object child = getChild(elementName);
if (child == null)
throw new XMLParseException("Child element called " + elementName +
" does not exist inside element " + getName());
if (!(child instanceof XMLObject))
throw new XMLParseException("Child element called " + elementName +
" inside element " + getName() + " is not an XMLObject.");
return ((XMLObject) child).getChild(0);
}
public String getChildName(int i) {
Object obj = getRawChild(i);
XMLObject xo;
if (obj instanceof XMLObject) {
xo = (XMLObject) obj;
} else if (obj instanceof Reference) {
xo = ((Reference) obj).getReferenceObject();
} else {
return "";
}
return xo.getName();
}
public boolean hasChildNamed(String name) {
final Object child = getChild(name);
return (child != null) && (child instanceof XMLObject);
}
public NamedNodeMap getAttributes() {
return element.getAttributes();
}
public boolean getBooleanChild(int i) throws XMLParseException {
return getBoolean(getChild(i));
}
public double getDoubleChild(int i) throws XMLParseException {
return getDouble(getChild(i));
}
public double[] getDoubleArrayChild(int i) throws XMLParseException {
return getDoubleArray(getChild(i));
}
public int getIntegerChild(int i) throws XMLParseException {
return getInteger(getChild(i));
}
public String getStringChild(int i) throws XMLParseException {
return getString(getChild(i));
}
public String[] getStringArrayChild(int i) throws XMLParseException {
return getStringArray(getChild(i));
}
public <T> T getAttribute(String name, T defaultValue) throws XMLParseException {
if (element.hasAttribute(name)) {
final String s = element.getAttribute(name);
for (Constructor c : defaultValue.getClass().getConstructors()) {
final Class[] classes = c.getParameterTypes();
if (classes.length == 1 && classes[0].equals(String.class)) {
try {
return (T) c.newInstance(s);
} catch (Exception e) {
throw new XMLParseException(" conversion of '" + s + "' to " +
defaultValue.getClass().getName() + " failed");
}
}
}
}
return defaultValue;
}
public Object getAttribute(String name) throws XMLParseException {
return getAndTest(name);
}
public boolean getBooleanAttribute(String name) throws XMLParseException {
return getBoolean(getAndTest(name));
}
public double getDoubleAttribute(String name) throws XMLParseException {
return getDouble(getAndTest(name));
}
public double[] getDoubleArrayAttribute(String name) throws XMLParseException {
return getDoubleArray(getAndTest(name));
}
public int[] getIntegerArrayAttribute(String name) throws XMLParseException {
return getIntegerArray(getAndTest(name));
}
public int getIntegerAttribute(String name) throws XMLParseException {
return getInteger(getAndTest(name));
}
public long getLongIntegerAttribute(String name) throws XMLParseException {
return getLongInteger(getAndTest(name));
}
public String getStringAttribute(String name) throws XMLParseException {
return getString(getAndTest(name));
}
public String[] getStringArrayAttribute(String name) throws XMLParseException {
return getStringArray(getAndTest(name));
}
public static boolean isDoubleArray(String s, List<Double> valueList) {
try {
StringTokenizer st = new StringTokenizer(s);
while (st.hasMoreTokens()) {
String token = st.nextToken();
Double d;
if (token.compareToIgnoreCase(missingValue) == 0)
d = Double.NaN;
else
d = new Double(token);
if (valueList != null) valueList.add(d);
}
return true;
} catch (NumberFormatException e) {
return false;
}
}
public static boolean isIntegerArray(String s, List<Integer> valueList) {
try {
StringTokenizer st = new StringTokenizer(s);
while (st.hasMoreTokens()) {
Integer d = new Integer(st.nextToken());
if (valueList != null) valueList.add(d);
}
return true;
} catch (NumberFormatException e) {
return false;
}
}
public final static String ID = "id";
public boolean hasId() {
return hasAttribute(ID);
}
public String getId() throws XMLParseException {
return getStringAttribute(ID);
}
public boolean hasAttribute(String name) {
return (element.hasAttribute(name));
}
public String getName() {
return element.getTagName();
}
public Object getNativeObject() {
return nativeObject;
}
public boolean hasNativeObject() {
return nativeObject != null;
}
public String toString() {
String prefix = getName();
if (hasId()) {
try {
prefix += ":" + getId();
} catch (XMLParseException e) {
// this shouldn't happen
assert false;
}
}
//if (nativeObject != null) return prefix + ":" + nativeObject.toString();
return prefix;
}
public String content() {
if (nativeObject != null) {
if (nativeObject instanceof dr.util.XHTMLable) {
return ((dr.util.XHTMLable) nativeObject).toXHTML();
} else {
return nativeObject.toString();
}
}
return "";
}
//*********************************************************************
// Package functions
//*********************************************************************
void addChild(Object child) {
if (child instanceof XMLObject ||
child instanceof Reference ||
child instanceof String) {
children.add(child);
} else throw new IllegalArgumentException();
}
public Object getRawChild(int i) {
return children.get(i);
}
public void setNativeObject(Object obj) {
nativeObject = obj;
}
boolean isReference(int child) {
return (getRawChild(child) instanceof Reference);
}
//*********************************************************************
// Static members
//*********************************************************************
//*********************************************************************
// Private methods
//*********************************************************************
private boolean getBoolean(Object obj) throws XMLParseException {
if (obj instanceof Boolean) return (Boolean) obj;
if (obj instanceof String) {
if (obj.equals("true")) return true;
if (obj.equals("false")) return false;
}
throw new XMLParseException("Expected a boolean (true|false), but got " + obj);
}
private double getDouble(Object obj) throws XMLParseException {
try {
if (obj instanceof Number) {
return ((Number) obj).doubleValue();
}
if (obj instanceof String) {
return Double.parseDouble((String) obj);
}
} catch (NumberFormatException nfe) {
throw new XMLParseException("Expected double precision number, but got " + obj);
}
throw new XMLParseException("Expected double precision number, but got " + obj);
}
private double[] getDoubleArray(Object obj) throws XMLParseException {
if (obj instanceof Number) return new double[]{((Number) obj).doubleValue()};
if (obj instanceof double[]) return (double[]) obj;
if (obj instanceof String) {
List<Double> valueList = new ArrayList<Double>();
if (isDoubleArray((String) obj, valueList)) {
double[] values = new double[valueList.size()];
for (int i = 0; i < values.length; i++) {
values[i] = valueList.get(i);
}
return values;
} else {
throw new XMLParseException("Expected array of double precision numbers, but got " + obj);
}
}
throw new XMLParseException("Expected array of double precision numbers, but got " + obj);
}
private int[] getIntegerArray(Object obj) throws XMLParseException {
if (obj instanceof Number) return new int[]{((Number) obj).intValue()};
if (obj instanceof int[]) return (int[]) obj;
if (obj instanceof String) {
ArrayList<Integer> valueList = new ArrayList<Integer>();
if (isIntegerArray((String) obj, valueList)) {
int[] values = new int[valueList.size()];
for (int i = 0; i < values.length; i++) {
values[i] = valueList.get(i);
}
return values;
} else {
throw new XMLParseException("Expected array of integers, but got " + obj);
}
}
throw new XMLParseException("Expected array of integers, but got " + obj);
}
private int getInteger(Object obj) throws XMLParseException {
if (obj instanceof Number) return ((Number) obj).intValue();
try {
return Integer.parseInt((String) obj);
} catch (NumberFormatException e) {
throw new XMLParseException("Expected integer, got " + obj);
}
}
private long getLongInteger(Object obj) throws XMLParseException {
if (obj instanceof Number) return ((Number) obj).longValue();
try {
return Long.parseLong((String) obj);
} catch (NumberFormatException e) {
throw new XMLParseException("Expected long integer, got " + obj);
}
}
private String getString(Object obj) throws XMLParseException {
if (obj instanceof String) return (String) obj;
throw new XMLParseException("Expected string, but got " + obj);
}
private String[] getStringArray(Object obj) throws XMLParseException {
if (obj instanceof String[]) return (String[]) obj;
if (obj instanceof String) {
ArrayList<String> stringList = new ArrayList<String>();
StringTokenizer st = new StringTokenizer((String) obj);
while (st.hasMoreTokens()) {
stringList.add(st.nextToken());
}
String[] strings = new String[stringList.size()];
for (int i = 0; i < strings.length; i++) {
strings[i] = stringList.get(i);
}
return strings;
}
throw new XMLParseException("Expected array of strings, but got " + obj);
}
private Object getAndTest(String name) throws XMLParseException {
if (element.hasAttribute(name)) {
return element.getAttribute(name);
}
throw new XMLParseException("'" + name + "' attribute was not found in " + element.getTagName() + " element.");
}
//*********************************************************************
// Private instance variables
//*********************************************************************
private final Vector<Object> children = new Vector<Object>();
private Element element = null;
private Object nativeObject;
// The objectStore representing the local scope of this element.
//	private ObjectStore store;
}
