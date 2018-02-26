
package dr.evolution.wrightfisher;

public interface Genome {
	Genome replicate(Mutator mutator, FitnessFunction fitnessFunction);

	byte[] getSequence();

	int hammingDistance(Genome other);

	String getDNASequenceString();

	String getAminoAcidSequenceString();

	Genome getParent();

    double getFitness();

	void mark();

	void unmark();

	int getMarks();

    Mutation[] getMutations();

	int getGenomeLength();
}
