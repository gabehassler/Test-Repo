package dr.evolution.io;
import dr.evolution.alignment.Alignment;
import dr.evolution.alignment.SimpleAlignment;
import dr.evolution.datatype.DataType;
import dr.evolution.sequence.Sequence;
import dr.evolution.sequence.SequenceList;
import dr.evolution.util.Taxon;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
public class PhylipSequentialImporter extends Importer implements SequenceImporter { 
public PhylipSequentialImporter(Reader reader, DataType dataType, int maxNameLength) {
super(reader);
setCommentDelimiters('\0', '\0', '\0');
this.dataType = dataType;
this.maxNameLength = maxNameLength;
}
public PhylipSequentialImporter(Reader reader, Writer commentWriter, DataType dataType, int maxNameLength) {
super(reader, commentWriter);
setCommentDelimiters('\0', '\0', '\0');
this.dataType = dataType;
this.maxNameLength = maxNameLength;
}
public Alignment importAlignment() throws IOException, Importer.ImportException
{
SimpleAlignment alignment = null;
try {
int taxonCount = readInteger();
int siteCount = readInteger();
String firstSeq = null;
for (int i = 0; i < taxonCount; i++) {
StringBuffer name = new StringBuffer();
char ch = read();
int n = 0;
while (!Character.isWhitespace(ch) && (maxNameLength < 1 || n < maxNameLength)) {
name.append(ch);
ch = read();
n++;
}
StringBuffer seq = new StringBuffer(siteCount);
readSequence(seq, dataType, "", siteCount, "-", "?", ".", firstSeq);
if (firstSeq == null) { firstSeq = seq.toString(); }
if (alignment == null) {
alignment = new SimpleAlignment();
}
alignment.addSequence(new Sequence(new Taxon(name.toString()), seq.toString()));
}
} catch (EOFException e) { }	
return alignment;
}
public SequenceList importSequences() throws IOException, ImportException {
return importAlignment();
}
private DataType dataType;
private int maxNameLength = 10;	
}
