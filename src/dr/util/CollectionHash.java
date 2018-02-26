
package dr.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;


// todo not used?
//
//public class CollectionHash {
//
//    private final Hashtable table;
//
//    public CollectionHash() { table = new Hashtable(); }
//
//    public void put(Object key, Object o) {
//		Collection c = (Collection)table.get(key);
//
//		if (c != null) {
//		    c.add(o);
//		} else {
//		    Collection newc = new ArrayList();
//		    newc.add(o);
//		    table.put(key, newc);
//		}
//    }
//
//    public Object get(Object key) {
//		Collection c = (Collection)table.get(key);
//
//		if (c == null) return null;
//
//		return c.iterator().next();
//    }
//
//    public Enumeration keys() {
//		return table.keys();
//    }
//
//    public Object[] getAll(Object key) {
//		return getCollection(key).toArray();
//    }
//
//    public Collection getCollection(Object key) {
//		return (Collection)table.get(key);
//    }
//
//    public int getSize(Object key) {
//        Collection collection = (Collection)table.get(key);
//        if (collection == null) return 0;
//        return collection.size();
//    }
//
//    public void remove(Object key) {
//    	table.remove(key);
//    }
//}
