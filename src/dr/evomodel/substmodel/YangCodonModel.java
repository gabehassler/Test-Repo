package dr.evomodel.substmodel;
import dr.evolution.datatype.AminoAcids;
import dr.evolution.datatype.Codons;
import dr.evolution.datatype.Nucleotides;
import dr.evomodelxml.substmodel.YangCodonModelParser;
import dr.inference.model.Parameter;
import dr.inference.model.Statistic;
public class YangCodonModel extends AbstractCodonModel {
	protected Parameter kappaParameter;
	protected Parameter omegaParameter;
	protected byte[] rateMap;
	public YangCodonModel(Codons codonDataType,
							Parameter omegaParameter,
							Parameter kappaParameter,
							FrequencyModel freqModel)
	{
		super(YangCodonModelParser.YANG_CODON_MODEL, codonDataType, freqModel);
		this.omegaParameter = omegaParameter;
		addVariable(omegaParameter);
		omegaParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0,
				omegaParameter.getDimension()));
		this.kappaParameter = kappaParameter;
		addVariable(kappaParameter);
		kappaParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0,
				kappaParameter.getDimension()));
		constructRateMap();
        addStatistic(synonymousRateStatistic);
	}
	public void setKappa(double kappa) {
		kappaParameter.setParameterValue(0, kappa);
		updateMatrix = true;
	}
	public double getKappa() { return kappaParameter.getParameterValue(0); }
	public void setOmega(double omega) {
		omegaParameter.setParameterValue(0, omega);
		updateMatrix = true;
	}
	public double getOmega() { return omegaParameter.getParameterValue(0); }
    public double getSynonymousRate() {
        double k = getKappa();
        double o = getOmega();
        return ((31.0 * k) + 36.0) / ((31.0 * k) + 36.0 + (138.0 * o) + (58.0 * o * k));
    }
    public double getNonSynonymousRate() {
        return 0;
    }
	public void setupRelativeRates()
	{
		double kappa = getKappa();
		double omega = getOmega();
		for (int i = 0; i < rateCount; i++) {
			switch (rateMap[i]) {
				case 0: relativeRates[i] = 0.0; break;			// codon changes in more than one codon position
				case 1: relativeRates[i] = kappa; break;		// synonymous transition
				case 2: relativeRates[i] = 1.0; break;			// synonymous transversion
				case 3: relativeRates[i] = kappa * omega; break;// non-synonymous transition
				case 4: relativeRates[i] = omega; break;		// non-synonymous transversion
			}
		}
	}
	protected void constructRateMap()
	{
		int u, v, i1, j1, k1, i2, j2, k2;
		byte rateClass;
		int[] codon;
		int cs1, cs2, aa1, aa2;
		int i = 0;
		rateMap = new byte[rateCount];
		for (u = 0; u < stateCount; u++) {
			codon = codonDataType.getTripletStates(u);
			i1 = codon[0];
			j1 = codon[1];
			k1 = codon[2];
			cs1 = codonDataType.getState(i1, j1, k1);
			aa1 = geneticCode.getAminoAcidState(codonDataType.getCanonicalState(cs1));
			for (v = u + 1; v < stateCount; v++) {
				codon = codonDataType.getTripletStates(v);
				i2 = codon[0];
				j2 = codon[1];
				k2 = codon[2];
				cs2 = codonDataType.getState(i2, j2, k2);
				aa2 = geneticCode.getAminoAcidState(codonDataType.getCanonicalState(cs2));
				rateClass = -1;
				if (i1 != i2) {
					if ( (i1 == 0 && i2 == 2) || (i1 == 2 && i2 == 0) || // A <-> G
						 (i1 == 1 && i2 == 3) || (i1 == 3 && i2 == 1) ) { // C <-> T
						rateClass = 1; // Transition at position 1
					} else {
						rateClass = 2; // Transversion at position 1
					}
				}
				if (j1 != j2) {
					if (rateClass == -1) {
						if ( (j1 == 0 && j2 == 2) || (j1 == 2 && j2 == 0) || // A <-> G
							 (j1 == 1 && j2 == 3) || (j1 == 3 && j2 == 1) ) { // C <-> T
							rateClass = 1; // Transition
						} else {
							rateClass = 2; // Transversion
						}
					} else
						rateClass = 0; // Codon changes at more than one position
				}
				if (k1 != k2) {
					if (rateClass == -1) {
						if ( (k1 == 0 && k2 == 2) || (k1 == 2 && k2 == 0) || // A <-> G
							 (k1 == 1 && k2 == 3) || (k1 == 3 && k2 == 1) ) { // C <-> T
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
				rateMap[i] = rateClass;
				i++;
			}
		}
	}
    public void printRateMap()
    {
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
                    if ( (i1 == 0 && i2 == 2) || (i1 == 2 && i2 == 0) || // A <-> G
                         (i1 == 1 && i2 == 3) || (i1 == 3 && i2 == 1) ) { // C <-> T
                        rateClass = 1; // Transition at position 1
                    } else {
                        rateClass = 2; // Transversion at position 1
                    }
                }
                if (j1 != j2) {
                    if (rateClass == -1) {
                        if ( (j1 == 0 && j2 == 2) || (j1 == 2 && j2 == 0) || // A <-> G
                             (j1 == 1 && j2 == 3) || (j1 == 3 && j2 == 1) ) { // C <-> T
                            rateClass = 1; // Transition
                        } else {
                            rateClass = 2; // Transversion
                        }
                    } else
                        rateClass = 0; // Codon changes at more than one position
                }
                if (k1 != k2) {
                    if (rateClass == -1) {
                        if ( (k1 == 0 && k2 == 2) || (k1 == 2 && k2 == 0) || // A <-> G
                             (k1 == 1 && k2 == 3) || (k1 == 3 && k2 == 1) ) { // C <-> T
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
    // **************************************************************
    // XHTMLable IMPLEMENTATION
    // **************************************************************
	public String toXHTML() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<em>Yang Codon Model</em> kappa = ");
		buffer.append(getKappa());
		buffer.append(", omega = ");
		buffer.append(getOmega());
		return buffer.toString();
	}
    private Statistic synonymousRateStatistic = new Statistic.Abstract() {
        public String getStatisticName() {
            return "synonymousRate";
        }
        public int getDimension() { return 1; }
        public double getStatisticValue(int dim) {
            return getSynonymousRate();
        }
    };
        public String getStatisticName() {
            return "nonSynonymousRate";
        }
        public int getDimension() { return 1; }
        public double getStatisticValue(int dim) {
            return getNonSynonymousRate();
        }
    };*/
}
