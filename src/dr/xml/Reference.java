package dr.xml;
public class Reference {
	public Reference(XMLObject referenceObject) { 
		this.referenceObject = referenceObject;
	}
	public XMLObject getReferenceObject() { return referenceObject; }
	public String toString() {
		return "@" + referenceObject.toString();
	}
	private final XMLObject referenceObject;
}
