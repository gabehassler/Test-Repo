package dr.xml;
import java.util.Comparator;
public class ClassComparator implements Comparator<Class> {
	public int compare(Class c1, Class c2) {
		String name1 = getName(c1);
		String name2 = getName(c2);
		return name1.compareTo(name2);
	}
	protected static String getName(Class c1) {
		String name = c1.getName();
		return name.substring(name.lastIndexOf('.')+1);
	}
	public static final ClassComparator INSTANCE = new ClassComparator();
}
