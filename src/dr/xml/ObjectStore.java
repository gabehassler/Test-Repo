
package dr.xml;

import dr.util.Identifiable;

import java.util.Collection;
import java.util.Set;

public interface ObjectStore {

    Object getObjectById(Object uid) throws ObjectNotFoundException;

    boolean hasObjectId(Object uid);

    void addIdentifiableObject(Identifiable object, boolean force);

    public Set getIdSet();

    public Collection getObjects();
}
