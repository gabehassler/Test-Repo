package dr.app.beauti.alignmentviewer;
public interface AlignmentBuffer {
int getSequenceCount();
int getSiteCount();
String getTaxonLabel(int i);
String[] getStateTable();
void getStates(int sequenceIndex, int fromSite, int toSite, byte[] states);
void addAlignmentBufferListener(AlignmentBufferListener listener);
}
