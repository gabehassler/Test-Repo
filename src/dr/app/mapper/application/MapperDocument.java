
package dr.app.mapper.application;

import dr.evolution.util.*;
import dr.evolution.util.Date;
import dr.inference.trace.TraceList;
import dr.util.DataTable;

import java.util.*;

public final class MapperDocument {

    public enum MeasurementType {
        INTERVAL,
        POINT,
        THRESHOLD,
        MISSING
    }

    public MapperDocument() {
    }

    public void addTaxa(Collection<Taxon> newTaxa) {
        for (Taxon taxon : newTaxa) {
            addTaxon(taxon);
        }
        fireTaxaChanged();
    }

    private void addTaxon(Taxon taxon) {
        taxa.add(taxon);
        taxonMap.put(taxon.getId(), taxon);
    }

    public Taxon getTaxon(String id) {
        return taxonMap.get(id);
    }

    public List<Taxon> getTaxa() {
        return taxa;
    }

    public void addTable(DataTable<String[]> dataTable) {
        // column indices in table
        final int COLUMN_LABEL = 0;
        final int SERUM_STRAIN = 2;
        final int ROW_LABEL = 1;
        final int VIRUS_STRAIN = 3;
        final int SERUM_DATE = 4;
        final int VIRUS_DATE = 5;
        final int TITRE = 6;

        List<String> strainNames = new ArrayList<String>();

        List<String> virusNames = new ArrayList<String>();
        List<String> serumNames = new ArrayList<String>();
        Map<String, Double> strainDateMap = new HashMap<String, Double>();

        int thresholdCount = 0;

        double earliestDate = Double.POSITIVE_INFINITY;
        for (int i = 0; i < dataTable.getRowCount(); i++) {
            String[] values = dataTable.getRow(i);
            String columnLabel = values[COLUMN_LABEL];

            String columnStrainName  = values[COLUMN_LABEL];

            Taxon columnStrain = getTaxon(columnStrainName);
            if (columnStrain == null) {
                columnStrain = new Taxon(columnStrainName);
//                double date = Double.parseDouble(values[SERUM_DATE]);
//                columnStrain.setDate(new Date(date, false, new Date(0.0, Units.Type.YEARS, false)));
//                addTaxon(columnStrain);
            }

            String rowLabel = values[ROW_LABEL];
            String rowStrainName  = values[VIRUS_STRAIN];

            Taxon rowStrain = getTaxon(rowStrainName);
            if (rowStrain == null) {
                rowStrain = new Taxon(rowStrainName);
//                double date = Double.parseDouble(values[VIRUS_DATE]);
//                rowStrain.setDate(new Date(date, false, new Date(0.0, Units.Type.YEARS, false)));
                addTaxon(rowStrain);
            }

            boolean isThreshold = false;
            double rawTitre = Double.NaN;
            if (values[TITRE].length() > 0) {
                try {
                    rawTitre = Double.parseDouble(values[TITRE]);
                } catch (NumberFormatException nfe) {
                    // check if threshold below
                    if (values[TITRE].contains("<")) {
                        rawTitre = Double.parseDouble(values[TITRE].replace("<",""));
                        isThreshold = true;
                        thresholdCount++;
                    }
                    // check if threshold above
                    if (values[TITRE].contains(">")) {
                        throw new IllegalArgumentException("Error in measurement: unsupported greater than threshold at row " + (i+1));
                    }
                }
            }

            MeasurementType type = (isThreshold ? MeasurementType.THRESHOLD : MeasurementType.POINT);
            Measurement measurement = new Measurement(columnLabel, columnStrain, rowLabel, rowStrain, type, rawTitre);

            measurements.add(measurement);

        }

        fireTaxaChanged();
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    private final List<Taxon> taxa = new ArrayList<Taxon>();
    private final Map<String, Taxon> taxonMap = new HashMap<String, Taxon>();

    private final List<TraceList> traceLists = new ArrayList<TraceList>();

    private final List<Measurement> measurements = new ArrayList<Measurement>();
    private final List<String> columnLabels = new ArrayList<String>();
    private final List<String> rowLabels = new ArrayList<String>();

    public class Measurement {
        private Measurement(final String column, final Taxon columnStrain, final String row, final Taxon rowStrain, final MeasurementType type, final double titre) {
            this.column = column;
            this.columnStrain = columnStrain;
            this.row = row;
            this.rowStrain = rowStrain;

            this.type = type;
            this.titre = titre;
            this.log2Titre = Math.log(titre) / Math.log(2);
        }

        final String column;
        final String row;
        final Taxon columnStrain;
        final Taxon rowStrain;

        final MeasurementType type;
        final double titre;
        final double log2Titre;

    };

    // Listeners and broadcasting
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public void fireTaxaChanged() {
        for (Listener listener : listeners) {
            listener.taxaChanged();
        }
    }

    private final List<Listener> listeners = new ArrayList<Listener>();

    public interface Listener {
        void taxaChanged();
    }

}
