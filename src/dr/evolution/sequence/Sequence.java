package dr.evolution.sequence;
import dr.evolution.datatype.DataType;
import dr.evolution.util.Taxon;
import dr.util.Attributable;
import dr.util.Identifiable;
import java.util.Iterator;
public class Sequence implements Identifiable, Attributable {
public Sequence() {
sequenceString = new StringBuffer();
}
public Sequence(String sequence) {
sequenceString = new StringBuffer();
setSequenceString(sequence);
}
public Sequence(Sequence sequence) {
// should clone taxon as well!
this(sequence.getTaxon(), sequence.getSequenceString());
}
public Sequence(Taxon taxon, String sequence) {
sequenceString = new StringBuffer();
setTaxon(taxon);
setSequenceString(sequence);
}
public DataType getDataType() {
return dataType;
}
public int getLength() {
return sequenceString.length();
}
public String getSequenceString() {
return sequenceString.toString();
}
public char getChar(int index) {
return sequenceString.charAt(index);
}
public int getState(int index) {
return dataType.getState(sequenceString.charAt(index));
}
public final void setState(int index, int state) {
sequenceString.setCharAt(index, dataType.getChar(state));
}
public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
sequenceString.getChars(srcBegin, srcEnd, dst, dstBegin);
}
public void setDataType(DataType dataType) {
this.dataType = dataType;
}
public DataType guessDataType() {
return DataType.guessDataType(sequenceString.toString());
}
public void setSequenceString(String sequence) {
sequenceString.setLength(0);
sequenceString.append(sequence.toUpperCase());
}
public void appendSequenceString(String sequence) {
sequenceString.append(sequence);
}
public void insertSequenceString(int offset, String sequence) {
sequenceString.insert(offset, sequence);
}
public void setTaxon(Taxon taxon) {
this.taxon = taxon;
}
public Taxon getTaxon() {
return taxon;
}
// **************************************************************
// Attributable IMPLEMENTATION
// **************************************************************
private Attributable.AttributeHelper attributes = null;
public void setAttribute(String name, Object value) {
if (attributes == null)
attributes = new Attributable.AttributeHelper();
attributes.setAttribute(name, value);
}
public Object getAttribute(String name) {
if (attributes == null)
return null;
else
return attributes.getAttribute(name);
}
public Iterator<String> getAttributeNames() {
if (attributes == null)
return null;
else
return attributes.getAttributeNames();
}
// **************************************************************
// Identifiable IMPLEMENTATION
// **************************************************************
protected String id = null;
public String getId() {
return id;
}
public void setId(String id) {
this.id = id;
}
// **************************************************************
// INSTANCE VARIABLES
// **************************************************************
protected Taxon taxon = null;
protected StringBuffer sequenceString = null;
protected DataType dataType = null;
}
