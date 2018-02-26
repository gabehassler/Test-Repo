package dr.evomodel.substmodel;
import dr.evolution.datatype.AminoAcids;
public abstract class AbstractAminoAcidModel extends AbstractSubstitutionModel
{
	// Constructor
	public AbstractAminoAcidModel(String name, FrequencyModel freqModel)
	{
		super(name, AminoAcids.INSTANCE, freqModel);
	}
}
