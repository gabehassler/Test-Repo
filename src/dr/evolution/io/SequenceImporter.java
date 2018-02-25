package dr.evolution.io;
import dr.evolution.alignment.Alignment;
import dr.evolution.sequence.SequenceList;
import java.io.IOException;
public interface SequenceImporter { 
Alignment importAlignment() throws IOException, Importer.ImportException;
SequenceList importSequences() throws IOException, Importer.ImportException;
}
