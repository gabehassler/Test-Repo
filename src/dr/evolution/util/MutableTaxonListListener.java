package dr.evolution.util;
public interface MutableTaxonListListener {
	void taxonAdded(TaxonList taxonList, Taxon taxon);
	void taxonRemoved(TaxonList taxonList, Taxon taxon);
	void taxaChanged(TaxonList taxonList);
}
