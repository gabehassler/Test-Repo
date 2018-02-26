package dr.evolution.util;
import dr.evoxml.util.XMLUnits;
import java.io.Serializable;
public interface Units extends Serializable {
    public enum Type {
        SUBSTITUTIONS(XMLUnits.SUBSTITUTIONS), GENERATIONS(XMLUnits.GENERATIONS),
        DAYS(XMLUnits.DAYS), MONTHS(XMLUnits.MONTHS), YEARS(XMLUnits.YEARS);
        Type(String name) {
            this.name = name;
        }
        public String toString() {
            return name;
        }
        private final String name;
    }
    Type getUnits();
    void setUnits(Type units);
    // array of unit names
    // second dimension is to allow synonyms -- first element is default
    final public String[][] UNIT_NAMES = {{"substitutions", "mutations"}, {"generations"}, {"days"}, {"months"}, {"years"}};
    public class Utils {
        public static String getDefaultUnitName(Type i) {
            return UNIT_NAMES[i.ordinal()][0];
        }
    }
}