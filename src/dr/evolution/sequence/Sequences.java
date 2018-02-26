
package dr.evolution.sequence;

import dr.evolution.util.Taxon;
import dr.util.Attributable;
import dr.util.Identifiable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class Sequences implements SequenceList, Attributable, Identifiable {

    public Sequences() {
    }

    // **************************************************************
    // SequenceList IMPLEMENTATION
    // **************************************************************

    public int getSequenceCount() {
        return sequences.size();
    }

    public Sequence getSequence(int index) {
        return sequences.get(index);
    }

    public void setSequenceAttribute(int index, String name, Object value) {
        Sequence sequence = getSequence(index);
        sequence.setAttribute(name, value);
    }

    public Object getSequenceAttribute(int index, String name) {
        Sequence sequence = getSequence(index);
        return sequence.getAttribute(name);
    }

    // **************************************************************
    // TaxonList IMPLEMENTATION
    // **************************************************************

    public int getTaxonCount() {
        return getSequenceCount();
    }

    public Taxon getTaxon(int taxonIndex) {
        return getSequence(taxonIndex).getTaxon();
    }

    public String getTaxonId(int taxonIndex) {
        Taxon taxon = getTaxon(taxonIndex);
        if (taxon != null)
            return taxon.getId();
        else
            throw new IllegalArgumentException("Illegal taxon index:" + taxonIndex);
    }

    public int getTaxonIndex(String id) {
        for (int i = 0, n = getTaxonCount(); i < n; i++) {
            if (getTaxonId(i).equals(id)) return i;
        }
        return -1;
    }

    public int getTaxonIndex(Taxon taxon) {
        for (int i = 0, n = getTaxonCount(); i < n; i++) {
            if (getTaxon(i) == taxon) return i;
        }
        return -1;
    }

    public List<Taxon> asList() {
        List<Taxon> taxa = new ArrayList<Taxon>();
        for (int i = 0, n = getTaxonCount(); i < n; i++) {
            taxa.add(getTaxon(i));
        }
        return taxa;
    }

    public void setTaxonAttribute(int taxonIndex, String name, Object value) {
        Taxon taxon = getTaxon(taxonIndex);
        if (taxon != null)
            taxon.setAttribute(name, value);
        else
            setSequenceAttribute(taxonIndex, name, value);
    }

    public Object getTaxonAttribute(int taxonIndex, String name) {
        Taxon taxon = getTaxon(taxonIndex);
        if (taxon != null)
            return taxon.getAttribute(name);
        else
            return getSequenceAttribute(taxonIndex, name);
    }

    public Iterator<Taxon> iterator() {
        return new Iterator<Taxon>() {
            private int index = -1;

            public boolean hasNext() {
                return index < getTaxonCount() - 1;
            }

            public Taxon next() {
                index++;
                return getTaxon(index);
            }

            public void remove() { /* do nothing */ }
        };
    }

    // **************************************************************
    // Identifiable IMPLEMENTATION
    // **************************************************************

    protected String id = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // **************************************************************
    // Sequences METHODS
    // **************************************************************

    public void addSequence(Sequence sequence) {
        sequences.add(sequence);
    }

    public void insertSequence(int position, Sequence sequence) {
        sequences.insertElementAt(sequence, position);
    }

    public Sequence removeSequence(int index) {
        Sequence sequence = getSequence(index);
        sequences.removeElementAt(index);

        return sequence;
    }

    // **************************************************************
    // Attributable IMPLEMENTATION
    // **************************************************************

    public void setAttribute(String name, Object value) {
        if (attributes == null)
            attributes = new Attributable.AttributeHelper();
        attributes.setAttribute(name, value);
    }

    public Object getAttribute(String name) {
        if (attributes == null)
            return null;
        else
            return attributes.getAttribute(name);
    }

    public Iterator<String> getAttributeNames() {
        if (attributes == null)
            return null;
        else
            return attributes.getAttributeNames();
    }

    // **************************************************************
    // INSTANCE VARIABLES
    // **************************************************************

    protected final Vector<Sequence> sequences = new Vector<Sequence>();

    private Attributable.AttributeHelper attributes = null;
}
