package dr.evolution.util;
public interface MutableTaxonList extends TaxonList {
	int addTaxon(Taxon taxon);
	boolean removeTaxon(Taxon taxon);
	public void setTaxonId(int taxonIndex, String id);
	public void setTaxonAttribute(int taxonIndex, String name, Object value);
	void addMutableTaxonListListener(MutableTaxonListListener listener);
}
