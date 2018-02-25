package dr.evolution.datatype;
public interface CodonTable {
char getAminoAcidChar(int codonState);
int getAminoAcidState(int codonState);
char[][] getCodonsFromAminoAcidState(int aminoAcidState);
char[][] getCodonsFromAminoAcidChar(char aminoAcidChar);
int[] getAmbiguousCodonFromAminoAcidState(int aminoAcid);
int[] getStopCodonIndices();
int getStopCodonCount();
}
