package dr.evolution.util;
import dr.util.Identifiable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
public class Taxa implements MutableTaxonList, Identifiable, Comparable<Taxa> {
	private final ArrayList<MutableTaxonListListener> mutableTaxonListListeners = new ArrayList<MutableTaxonListListener>();
	ArrayList<Taxon> taxa = new ArrayList<Taxon>();
    private String id = null;
	public Taxa() {
	}
    public Taxa(String id) {
		this.id = id;
	}
    public Taxa(TaxonList list) {
        addTaxa(list);
    }
    public Taxa(Collection<Taxon> taxa) {
        addTaxa(taxa);
    }
	public int addTaxon(Taxon taxon) {
        int index = getTaxonIndex(taxon);
        if (index == -1) {
            taxa.add(taxon);
            fireTaxonAdded(taxon);
            index = taxa.size() - 1;
        }
        return index;
    }
	public boolean removeTaxon(Taxon taxon) {
		boolean success = taxa.remove(taxon);
		if (success) {
			fireTaxonRemoved(taxon);
		}
		return success;
	}
    public void addTaxa(TaxonList taxa) {
        for(int nt = 0; nt < taxa.getTaxonCount(); ++nt) {
            addTaxon(taxa.getTaxon(nt));
        }
    }
    public void addTaxa(Collection<Taxon> taxa) {
        for(Taxon taxon : taxa) {
            addTaxon(taxon);
        }
    }
    public void removeTaxa(TaxonList taxa) {
        for(int nt = 0; nt < taxa.getTaxonCount(); ++nt) {
            removeTaxon(taxa.getTaxon(nt));
        }
    }
    public void removeTaxa(Collection<Taxon> taxa) {
        for(Taxon taxon : taxa) {
            removeTaxon(taxon);
        }
    }
    public void removeAllTaxa() {
		taxa.clear();
		fireTaxonRemoved(null);
	}
	public int getTaxonCount() {
		return taxa.size();
	}
	public Taxon getTaxon(int taxonIndex) {
		return taxa.get(taxonIndex);
	}
	public String getTaxonId(int taxonIndex) {
		return (taxa.get(taxonIndex)).getId();
	}
	public void setTaxonId(int taxonIndex, String id) {
		(taxa.get(taxonIndex)).setId(id);
		fireTaxaChanged();
	}
	public int getTaxonIndex(String id) {
		for (int i = 0; i < taxa.size(); i++) {
			if (getTaxonId(i).equals(id)) return i;
		}
		return -1;
	}
	public int getTaxonIndex(Taxon taxon) {
		for (int i = 0; i < taxa.size(); i++) {
			if (getTaxon(i) == taxon) return i;
		}
		return -1;
	}
    public List<Taxon> asList() {
        return new ArrayList<Taxon>(taxa);
    }
    public boolean contains(Taxon taxon) {
        return taxa.contains(taxon);
    }
    public boolean containsAny(TaxonList taxonList) {
        for (int i = 0; i < taxonList.getTaxonCount(); i++) {
            Taxon taxon = taxonList.getTaxon(i);
            if (taxa.contains(taxon)) {
                return true;
            }
        }
        return false;
    }
    public boolean containsAny(Collection<Taxon> taxa) {
        for (Taxon taxon : taxa) {
            if (taxa.contains(taxon)) {
                return true;
            }
        }
        return false;
    }
	public boolean containsAll(TaxonList taxonList) {
		for (int i = 0; i < taxonList.getTaxonCount(); i++) {
			Taxon taxon = taxonList.getTaxon(i);
			if (!taxa.contains(taxon)) {
				return false;
			}
		}
		return true;
	}
    public boolean containsAll(Collection<Taxon> taxa) {
        for (Taxon taxon : taxa) {
            if (!taxa.contains(taxon)) {
                return false;
            }
        }
        return true;
    }
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public int compareTo(Taxa o) {
		return getId().compareTo(o.getId());
	}
	public String toString() { return id; }
    public Iterator<Taxon> iterator() {
        return taxa.iterator();
    }
	public void setTaxonAttribute(int taxonIndex, String name, Object value) {
		Taxon taxon = getTaxon(taxonIndex);
		taxon.setAttribute(name, value);
		fireTaxaChanged();
	}
	public Object getTaxonAttribute(int taxonIndex, String name) {
		Taxon taxon = getTaxon(taxonIndex);
		return taxon.getAttribute(name);
	}
	public void addMutableTaxonListListener(MutableTaxonListListener listener) {
		mutableTaxonListListeners.add(listener);
	}
	private void fireTaxonAdded(Taxon taxon) {
        for (MutableTaxonListListener mutableTaxonListListener : mutableTaxonListListeners) {
            mutableTaxonListListener.taxonAdded(this, taxon);
        }
    }
	private void fireTaxonRemoved(Taxon taxon) {
        for (MutableTaxonListListener mutableTaxonListListener : mutableTaxonListListeners) {
            mutableTaxonListListener.taxonRemoved(this, taxon);
        }
    }
	private void fireTaxaChanged() {
        for (MutableTaxonListListener mutableTaxonListListener : mutableTaxonListListeners) {
            mutableTaxonListListener.taxaChanged(this);
        }
    }
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof TaxonList)) return false;
        return Utils.areTaxaIdentical(this, (TaxonList)o);
    }
}
