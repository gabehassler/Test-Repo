package dr.inference.parallel;
public interface MPISerializable {
public void sendState(int toRank);
public void receiveState(int fromRank);
}
