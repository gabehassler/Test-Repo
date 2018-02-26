
package dr.evolution.util;

import dr.util.Identifiable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface TaxonList extends Identifiable, Iterable<Taxon> {

	public int getTaxonCount();

	public Taxon getTaxon(int taxonIndex);

	public String getTaxonId(int taxonIndex);

	int getTaxonIndex(String id);

	int getTaxonIndex(Taxon taxon);

    List<Taxon> asList();

	public Object getTaxonAttribute(int taxonIndex, String name);

	class Utils {

		public static boolean hasAttribute(TaxonList taxa, int index, String name) {
			return taxa.getTaxonAttribute(index, name) != null;
		}

		public static Set<String> getTaxonListIdSet(TaxonList taxa) {
			Set<String> taxaSet = new HashSet<String>();
			for (int i =0; i < taxa.getTaxonCount(); i++) {
				taxaSet.add(taxa.getTaxonId(i));
			}
			return taxaSet;
		}

        public static int findDuplicateTaxon(TaxonList taxonList) {
            Set<String> taxaSet = new HashSet<String>();
                        for (int i = 0; i < taxonList.getTaxonCount(); i++) {
                Taxon taxon = taxonList.getTaxon(i);
                if (taxaSet.contains(taxon.getId())) {
                    return i;
                }
                taxaSet.add(taxon.getId());
            }
            return -1;
        }

        public static boolean areTaxaIdentical(TaxonList taxa1, TaxonList taxa2) {
            if (taxa1.getTaxonCount() != taxa2.getTaxonCount()) {
                return false;
            }
            for (int i =0; i < taxa1.getTaxonCount(); i++) {
                if (taxa2.getTaxonIndex(taxa1.getTaxon(i)) == -1) {
                    return false;
                }
            }
            return true;
        }


	}

	class MissingTaxonException extends Exception {
		private static final long serialVersionUID = 1864895946392309485L;

		public MissingTaxonException(String message) { super(message); }
	}
}
