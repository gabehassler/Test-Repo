package dr.evolution.wrightfisher;
public class Mutation {
	public Mutation(int position, byte state) {
		this.position = position;
		this.state = state;
	}
	// the position on the genome of this mutation
	public int position;
	// the state of this mutation
	public byte state;
}