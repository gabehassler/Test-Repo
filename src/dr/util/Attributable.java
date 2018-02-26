package dr.util;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
public interface Attributable extends Serializable {
	public final static String ATTRIBUTE = "att";
	public final static String NAME = "name";
	public final static String VALUE = "value";
	public void setAttribute(String name, Object value);
	public Object getAttribute(String name);
	Iterator<String> getAttributeNames();
	public static final class AttributeHelper implements Attributable {
		public void setAttribute(String name, Object value) {
			attributes.put(name, value);
		}
		public Object getAttribute(String name) {
			return attributes.get(name);
		}
		public boolean containsAttribute(String name) {
			return attributes.containsKey(name);
		}
		public Iterator<String> getAttributeNames() {
			return attributes.keySet().iterator();
		}
		// **************************************************************
		// INSTANCE VARIABLE
		// **************************************************************
		private final HashMap<String, Object> attributes = new HashMap<String, Object>();
	}
}
