
package dr.evomodel.substmodel;

import dr.evolution.datatype.Codons;
import dr.evolution.datatype.GeneticCode;

abstract public class AbstractCodonModel extends AbstractSubstitutionModel  {

	// Constructor
	public AbstractCodonModel(String name, Codons codonDataType, FrequencyModel freqModel)
	{
		super(name, codonDataType, freqModel);
		
		this.codonDataType = codonDataType;
		this.geneticCode = codonDataType.getGeneticCode();
	}
	
	protected void frequenciesChanged() {
		// Nothing to precalculate
	}
	
	protected void ratesChanged() {
		// Nothing to precalculate
	}
	
	protected Codons codonDataType;
	protected GeneticCode geneticCode;
}