
package dr.evolution.datatype;


public final class GeneticCode implements CodonTable {

	public static final String GENETIC_CODE = "geneticCode";
	
	public static final int UNIVERSAL_ID = 0;
	public static final int VERTEBRATE_MT_ID = 1;
	public static final int YEAST_ID = 2;
	public static final int MOLD_PROTOZOAN_MT_ID = 3;
	public static final int MYCOPLASMA_ID = 4;
	public static final int INVERTEBRATE_MT_ID = 5;
	public static final int CILIATE_ID = 6;
	public static final int ECHINODERM_MT_ID = 7;
	public static final int EUPLOTID_NUC_ID = 8;
	public static final int BACTERIAL_ID = 9;
	public static final int ALT_YEAST_ID = 10;
	public static final int ASCIDIAN_MT_ID = 11;
	public static final int FLATWORM_MT_ID = 12;
	public static final int BLEPHARISMA_NUC_ID = 13;
	public static final int NO_STOPS_ID = 14;

	public static final String[] GENETIC_CODE_TABLES = {
		// Universal
		"KNKNTTTTRSRSIIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVV*Y*YSSSS*CWCLFLF",
		// Vertebrate Mitochondrial
		"KNKNTTTT*S*SMIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVV*Y*YSSSSWCWCLFLF",
		// Yeast
		"KNKNTTTTRSRSMIMIQHQHPPPPRRRRTTTTEDEDAAAAGGGGVVVV*Y*YSSSSWCWCLFLF",
		// Mold Protozoan Mitochondrial
		"KNKNTTTTRSRSIIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVV*Y*YSSSSWCWCLFLF",
		// Mycoplasma
		"KNKNTTTTRSRSIIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVV*Y*YSSSSWCWCLFLF",
		// Invertebrate Mitochondrial
		"KNKNTTTTSSSSMIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVV*Y*YSSSSWCWCLFLF",
		// Ciliate
		"KNKNTTTTRSRSIIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVVQYQYSSSS*CWCLFLF",
		// Echinoderm Mitochondrial
		"NNKNTTTTSSSSIIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVV*Y*YSSSSWCWCLFLF",
		// Euplotid Nuclear
		"KNKNTTTTRSRSIIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVV*Y*YSSSSCCWCLFLF",
		// Bacterial
		"KNKNTTTTRSRSIIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVV*Y*YSSSS*CWCLFLF",
		// Alternative Yeast
		"KNKNTTTTRSRSIIMIQHQHPPPPRRRRLLSLEDEDAAAAGGGGVVVV*Y*YSSSS*CWCLFLF",
		// Ascidian Mitochondrial
		"KNKNTTTTGSGSMIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVV*Y*YSSSSWCWCLFLF",
		// Flatworm Mitochondrial
		"NNKNTTTTSSSSIIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVVYY*YSSSSWCWCLFLF",
		// Blepharisma Nuclear
		"KNKNTTTTRSRSIIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVV*YQYSSSS*CWCLFLF",
		// No stops
		"KNKNTTTTRSRSIIMIQHQHPPPPRRRRLLLLEDEDAAAAGGGGVVVVYYQYSSSSWCWCLFLF"
	};

	public static final String[] GENETIC_CODE_NAMES = {
		"universal", "vertebrateMitochondrial", "yeast", "moldProtozoanMitochondrial",
		"mycoplasma", "invertebrateMitochondrial", "ciliate", "echinodermMitochondrial",
		"euplotidNuclear", "bacterial", "alternativeYeast", "ascidianMitochondrial",
		"flatwormMitochondrial", "blepharismaNuclear", "noStops"
	};

	public static final String[] GENETIC_CODE_DESCRIPTIONS = {
		"Universal", "Vertebrate Mitochondrial", "Yeast", "Mold Protozoan Mitochondrial",
		"Mycoplasma", "Invertebrate Mitochondrial", "Ciliate", "Echinoderm Mitochondrial",
		"Euplotid Nuclear", "Bacterial", "Alternative Yeast", "Ascidian Mitochondrial",
		"Flatworm Mitochondrial", "Blepharisma Nuclear", "Test case with no stop codons"
	};

	public static final GeneticCode UNIVERSAL = new GeneticCode(UNIVERSAL_ID);
	public static final GeneticCode VERTEBRATE_MT = new GeneticCode(VERTEBRATE_MT_ID);
	public static final GeneticCode YEAST = new GeneticCode(YEAST_ID);
	public static final GeneticCode MOLD_PROTOZOAN_MT = new GeneticCode(MOLD_PROTOZOAN_MT_ID);
	public static final GeneticCode MYCOPLASMA = new GeneticCode(MYCOPLASMA_ID);
	public static final GeneticCode INVERTEBRATE_MT = new GeneticCode(INVERTEBRATE_MT_ID);
	public static final GeneticCode CILIATE = new GeneticCode(CILIATE_ID);
	public static final GeneticCode ECHINODERM_MT = new GeneticCode(ECHINODERM_MT_ID);
	public static final GeneticCode EUPLOTID_NUC = new GeneticCode(EUPLOTID_NUC_ID);
	public static final GeneticCode BACTERIAL = new GeneticCode(BACTERIAL_ID);
	public static final GeneticCode ALT_YEAST = new GeneticCode(ALT_YEAST_ID);
	public static final GeneticCode ASCIDIAN_MT = new GeneticCode(ASCIDIAN_MT_ID);
	public static final GeneticCode FLATWORM_MT = new GeneticCode(FLATWORM_MT_ID);
	public static final GeneticCode BLEPHARISMA_NUC = new GeneticCode(BLEPHARISMA_NUC_ID);
	public static final GeneticCode NO_STOPS = new GeneticCode(NO_STOPS_ID);

	public static final GeneticCode[] GENETIC_CODES = {
		UNIVERSAL, VERTEBRATE_MT, YEAST, MOLD_PROTOZOAN_MT, MYCOPLASMA, INVERTEBRATE_MT,
		CILIATE, ECHINODERM_MT, EUPLOTID_NUC, BACTERIAL, ALT_YEAST, ASCIDIAN_MT,
		FLATWORM_MT, BLEPHARISMA_NUC, NO_STOPS
	};

	public GeneticCode(int geneticCode) {
		
		this.geneticCode = geneticCode;
		codeTable = GENETIC_CODE_TABLES[geneticCode];
	}
	
	public String getName() {
		return GENETIC_CODE_NAMES[geneticCode];
	}
	
	public String getDescription() {
		return GENETIC_CODE_DESCRIPTIONS[geneticCode];
	}
	
	public char getAminoAcidChar(int codonState) {
		if (codonState == Codons.UNKNOWN_STATE)
			return AminoAcids.UNKNOWN_CHARACTER;
		else if (codonState == Codons.GAP_STATE)
			return AminoAcids.GAP_CHARACTER;
			
		return codeTable.charAt(codonState);
	}
	
	public int getAminoAcidState(int codonState) {
		if (codonState == Codons.UNKNOWN_STATE)
			return AminoAcids.UNKNOWN_STATE;
		else if (codonState == Codons.GAP_STATE)
			return AminoAcids.GAP_STATE;
			
		return AminoAcids.AMINOACID_STATES[getAminoAcidChar(codonState)];
	}

	public boolean isStopCodon(int codonState) {
		return (getAminoAcidState(codonState) == AminoAcids.STOP_STATE);
	}

	public char[][] getCodonsFromAminoAcidState(int aminoAcidState) {
		throw new RuntimeException("not yet implemented");
	}

	public char[][] getCodonsFromAminoAcidChar(char aminoAcidChar) {
		throw new RuntimeException("not yet implemented");
	}

	public int[] getAmbiguousCodonFromAminoAcidState(int aminoAcid) {
		throw new RuntimeException("not yet implemented");
	}

	public int[] getStopCodonIndices() {
	
		int i, j, n = getStopCodonCount();
		int[] indices = new int[n];
		
		j = 0;
		for (i = 0; i < 64; i++) {
			if (codeTable.charAt(i) == AminoAcids.STOP_CHARACTER) {
				indices[j] = i;
				j++;
			}
		}
		
		return indices;
	}

	public int getStopCodonCount() {
		int i, count = 0;
		
		for (i = 0; i < 64; i++) {
			if (codeTable.charAt(i) == AminoAcids.STOP_CHARACTER)
				count++;
		}
		
		return count;
	}
	
	private int geneticCode;
	private String codeTable;

}
