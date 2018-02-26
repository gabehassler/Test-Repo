
package dr.app.beagle.evomodel.substmodel;

import dr.evolution.datatype.AminoAcids;
import dr.evolution.datatype.Codons;
import dr.evolution.datatype.GeneticCode;
import dr.evolution.datatype.Nucleotides;

public abstract class AbstractCodonModel extends BaseSubstitutionModel {
//	protected Parameter kappaParameter;

//	protected Parameter omegaParameter;

    protected byte[] rateMap;

    protected Codons codonDataType;
    protected GeneticCode geneticCode;


//    public AbstractCodonModel(Codons codonDataType, Parameter omegaParameter, Parameter kappaParameter,
//                              FrequencyModel freqModel) {
//        this(codonDataType, omegaParameter, kappaParameter, freqModel,
//                new DefaultEigenSystem(codonDataType.getStateCount()));
//    }
    
    public AbstractCodonModel(String name, Codons codonDataType, FrequencyModel freqModel, EigenSystem eigenSystem) {
    	super(name, codonDataType, freqModel, eigenSystem);
    	
    	this.codonDataType = codonDataType;
        this.geneticCode = codonDataType.getGeneticCode();

        constructRateMap();

        updateMatrix = true;
    }

                              FrequencyModel freqModel, EigenSystem eigenSystem) {
        super("GY94", codonDataType, freqModel, eigenSystem);

        this.codonDataType = codonDataType;
        this.geneticCode = codonDataType.getGeneticCode();

        constructRateMap();

        updateMatrix = true;
    }*/

    protected void frequenciesChanged() {
    }

    protected void ratesChanged() {
    }

    abstract protected void setupRelativeRates(double[] rates);

    protected void constructRateMap() {
        // Refactored into static function, since CodonProductChains need this functionality
        rateMap = Codons.constructRateMap(rateCount, stateCount, codonDataType, geneticCode);
    }

    public void printRateMap() {
        int u, v, i1, j1, k1, i2, j2, k2;
        byte rateClass;
        int[] codon;
        int cs1, cs2, aa1, aa2;

        System.out.print("\t");
        for (v = 0; v < stateCount; v++) {
            codon = codonDataType.getTripletStates(v);
            i2 = codon[0];
            j2 = codon[1];
            k2 = codon[2];

            System.out.print("\t" + Nucleotides.INSTANCE.getChar(i2));
            System.out.print(Nucleotides.INSTANCE.getChar(j2));
            System.out.print(Nucleotides.INSTANCE.getChar(k2));
        }
        System.out.println();

        System.out.print("\t");
        for (v = 0; v < stateCount; v++) {
            codon = codonDataType.getTripletStates(v);
            i2 = codon[0];
            j2 = codon[1];
            k2 = codon[2];

            cs2 = codonDataType.getState(i2, j2, k2);
            aa2 = geneticCode.getAminoAcidState(codonDataType.getCanonicalState(cs2));
            System.out.print("\t" + AminoAcids.INSTANCE.getChar(aa2));
        }
        System.out.println();

        for (u = 0; u < stateCount; u++) {

            codon = codonDataType.getTripletStates(u);
            i1 = codon[0];
            j1 = codon[1];
            k1 = codon[2];

            System.out.print(Nucleotides.INSTANCE.getChar(i1));
            System.out.print(Nucleotides.INSTANCE.getChar(j1));
            System.out.print(Nucleotides.INSTANCE.getChar(k1));

            cs1 = codonDataType.getState(i1, j1, k1);
            aa1 = geneticCode.getAminoAcidState(codonDataType.getCanonicalState(cs1));

            System.out.print("\t" + AminoAcids.INSTANCE.getChar(aa1));

            for (v = 0; v < stateCount; v++) {

                codon = codonDataType.getTripletStates(v);
                i2 = codon[0];
                j2 = codon[1];
                k2 = codon[2];

                cs2 = codonDataType.getState(i2, j2, k2);
                aa2 = geneticCode.getAminoAcidState(codonDataType.getCanonicalState(cs2));

                rateClass = -1;
                if (i1 != i2) {
                    if ((i1 == 0 && i2 == 2) || (i1 == 2 && i2 == 0) || // A <-> G
                            (i1 == 1 && i2 == 3) || (i1 == 3 && i2 == 1)) { // C <-> T
                        rateClass = 1; // Transition at position 1
                    } else {
                        rateClass = 2; // Transversion at position 1
                    }
                }
                if (j1 != j2) {
                    if (rateClass == -1) {
                        if ((j1 == 0 && j2 == 2) || (j1 == 2 && j2 == 0) || // A <-> G
                                (j1 == 1 && j2 == 3) || (j1 == 3 && j2 == 1)) { // C <-> T
                            rateClass = 1; // Transition
                        } else {
                            rateClass = 2; // Transversion
                        }
                    } else
                        rateClass = 0; // Codon changes at more than one position
                }
                if (k1 != k2) {
                    if (rateClass == -1) {
                        if ((k1 == 0 && k2 == 2) || (k1 == 2 && k2 == 0) || // A <-> G
                                (k1 == 1 && k2 == 3) || (k1 == 3 && k2 == 1)) { // C <-> T
                            rateClass = 1; // Transition
                        } else {
                            rateClass = 2; // Transversion
                        }
                    } else
                        rateClass = 0; // Codon changes at more than one position
                }

                if (rateClass != 0) {
                    if (aa1 != aa2) {
                        rateClass += 2; // Is a non-synonymous change
                    }
                }

                System.out.print("\t" + rateClass);

            }
            System.out.println();

        }
    }
}