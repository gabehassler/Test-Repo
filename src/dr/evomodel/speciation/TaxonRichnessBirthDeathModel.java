package dr.evomodel.speciation;
import dr.evolution.util.Units;
import dr.inference.model.Parameter;
public class TaxonRichnessBirthDeathModel extends RandomLocalYuleModel {
    public TaxonRichnessBirthDeathModel(
            Parameter birthRates,
            Parameter indicators,
            Parameter meanRate,
            boolean ratesAsMultipliers,
            Type units,
            int dp) {
        super(birthRates, indicators, meanRate, ratesAsMultipliers, units, dp);
    }
}
