package dr.util;
import java.io.Serializable;
public interface Identifiable extends Serializable {
    public String getId();
    public void setId(String id);
}
