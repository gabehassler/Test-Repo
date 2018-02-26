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
import java.util.StringTokenizer;
public class FastaImporter extends Importer implements SequenceImporter {
    public static final char FASTA_FIRST_CHAR = '>';
    public FastaImporter(Reader reader, DataType dataType) {
        this(reader, null, dataType);
    }
    public FastaImporter(Reader reader, Writer commentWriter, DataType dataType) {
        super(reader, commentWriter);
        setCommentDelimiters('\0', '\0', '\0');
        this.dataType = dataType;
    }
    public Alignment importAlignment() throws IOException, ImportException
    {
        SimpleAlignment alignment = null;
        try {
            // find fasta line start
            while (read() != FASTA_FIRST_CHAR) {
            }
            do {
                final String name = readLine().trim();
                StringBuffer seq = new StringBuffer();
                readSequence(seq, dataType, "" + FASTA_FIRST_CHAR, Integer.MAX_VALUE, "-", "?", "", "");
                if (alignment == null) {
                    alignment = new SimpleAlignment();
                }
                alignment.addSequence(new Sequence(new Taxon(name.toString()), seq.toString()));
            } while (getLastDelimiter() == FASTA_FIRST_CHAR);
        } catch (EOFException e) {
            // catch end of file the ugly way.
        }
        return alignment;
    }
    public SequenceList importSequences() throws IOException, ImportException {
        return importAlignment();
    }
    private DataType dataType;
    private int maxNameLength = 10;
}