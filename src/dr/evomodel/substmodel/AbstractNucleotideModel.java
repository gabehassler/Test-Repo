package dr.evomodel.substmodel;
import dr.evolution.datatype.Nucleotides;
abstract public class AbstractNucleotideModel extends AbstractSubstitutionModel  {
	double freqA, freqC, freqG, freqT,
            // A+G
            freqR,
            // C+T
            freqY;
	// Constructor
	public AbstractNucleotideModel(String name, FrequencyModel freqModel)
	{
		super(name, Nucleotides.INSTANCE, freqModel);
	}
	protected void frequenciesChanged() {
		// Nothing to precalculate
	}
	protected void ratesChanged() {
		// Nothing to precalculate
	}
	protected void calculateFreqRY() {
		freqA = freqModel.getFrequency(0);
		freqC = freqModel.getFrequency(1);
		freqG = freqModel.getFrequency(2);
		freqT = freqModel.getFrequency(3);
		freqR = freqA + freqG;
		freqY = freqC + freqT;
	}
	// *****************************************************************
	// Interface Model
	// *****************************************************************
	protected void storeState() { } // nothing to do
	protected void restoreState() {
		updateMatrix = true;
	}
	protected void acceptState() { } // nothing to do
}