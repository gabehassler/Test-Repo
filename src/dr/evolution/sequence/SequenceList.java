package dr.evolution.sequence;
import dr.evolution.util.TaxonList;
public interface SequenceList extends TaxonList {
	public int getSequenceCount();
	public Sequence getSequence(int i);
	public void setSequenceAttribute(int index, String name, Object value);
	public Object getSequenceAttribute(int index, String name);
}
