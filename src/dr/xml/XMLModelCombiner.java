package dr.xml;
import org.jdom.output.XMLOutputter;
import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.io.OutputStream;
import java.util.HashMap;
public class XMLModelCombiner implements ListModel {
private XMLModelFile from;
private XMLModelFile to;
private XMLIDMapping mapping;
public XMLModelCombiner(XMLModelFile model1, XMLModelFile model2) {
from = model1;
to = model2;
mapping = new XMLIDMapping();
}
public void writeXML(OutputStream ostream) {
from.prefixIdentifiedNames("from", mapping.getFromNames(), true);
to.prefixIdentifiedNames("to", mapping.getToNames(), false);
XMLOutputter outputter = new XMLOutputter();
from.print(outputter, ostream);
to.print(outputter, ostream);
}
public int getSize() {
return mapping.getFromNames().size();
}
public Object getElementAt(int i) {
return null;  //AUTOGENERATED METHOD IMPLEMENTATION
}
public void addListDataListener(ListDataListener listDataListener) {
//AUTOGENERATED METHOD IMPLEMENTATION
}
public void removeListDataListener(ListDataListener listDataListener) {
//AUTOGENERATED METHOD IMPLEMENTATION
}
public class XMLIDMapping {
HashMap<String, String> fromMap;
HashMap<String, String> toMap;
public XMLIDMapping() {
fromMap = new HashMap<String, String>();
toMap = new HashMap<String, String>();
}
public void addRule(String cName, String m1Name, String m2Name) {
fromMap.put(cName, m1Name);
toMap.put(cName, m2Name);
}
public void removeRule(String cName) {
fromMap.remove(cName);
toMap.remove(cName);
}
public HashMap<String, String> getFromNames() {
return fromMap;
}
public HashMap<String, String> getToNames() {
return toMap;
}
}
//    public static void main(String[] args){
//        XMLModelCombiner mc = new XMLModelCombiner(new XMLModelFile("small1.xml"), new XMLModelFile("small2.xml"));
//
//
//    }
}