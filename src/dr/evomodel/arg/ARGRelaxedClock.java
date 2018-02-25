package dr.evomodel.arg;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evomodel.arg.ARGModel.Node;
import dr.evomodel.branchratemodel.AbstractBranchRateModel;
import dr.evomodel.branchratemodel.BranchRateModel;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.inference.model.Variable.ChangeType;
import dr.xml.*;
public class ARGRelaxedClock extends AbstractBranchRateModel {
public static final String ARG_LOCAL_CLOCK = "argLocalClock";
public static final String PARTITION = "partition";
private Parameter globalRateParameter;
private ARGModel arg;
private int partition;
public ARGRelaxedClock(String name) {
super(name);
}
public ARGRelaxedClock(String name, ARGModel arg, int partition, Parameter rate) {
super(name);
this.arg = arg;
this.partition = partition;
globalRateParameter = rate;
addModel(arg);
addVariable(rate);
}
protected void acceptState() {
}
protected void handleModelChangedEvent(Model model, Object object, int index) {
//do nothing
}
protected void handleVariableChangedEvent(Variable variable, int index, ChangeType type) {
//do nothing
}
protected void restoreState() {
}
protected void storeState() {
}
public double getBranchRate(Tree tree, NodeRef nodeRef) {
Node treeNode = (Node) nodeRef;
Node argNode = (Node) treeNode.mirrorNode;
return globalRateParameter.getParameterValue(0) * argNode.getRate(partition);
}
public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {
public String getParserDescription() {
return null;
}
public Class getReturnType() {
return ARGRelaxedClock.class;
}
public XMLSyntaxRule[] getSyntaxRules() {
return null;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
ARGModel arg = (ARGModel) xo.getChild(ARGModel.class);
int partition = xo.getAttribute(PARTITION, 0);
Parameter rate = (Parameter) xo.getChild(Parameter.class);
return new ARGRelaxedClock("", arg, partition, rate);
}
public String getParserName() {
return ARG_LOCAL_CLOCK;
}
};
}
