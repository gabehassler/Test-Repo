package dr.evomodel.indel;
import dr.evolution.alignment.Alignment;
import dr.evomodel.sitemodel.GammaSiteModel;
import dr.evomodel.tree.TreeModel;
import dr.evomodelxml.indel.TKF91LikelihoodParser;
import dr.inference.model.*;
public class TKF91Likelihood extends AbstractModelLikelihood {
public TKF91Likelihood(TreeModel treeModel, Alignment alignment, GammaSiteModel siteModel, TKF91Model tkfModel) {
super(TKF91LikelihoodParser.TKF91_LIKELIHOOD);
if (siteModel.getAlphaParameter() != null)
throw new IllegalArgumentException("TKF91 model cannot handle gamma-distributed rates");
if (siteModel.getPInvParameter() != null)
throw new IllegalArgumentException("TKF91 model cannot handle invariant sites");
addModel(siteModel);
addModel(tkfModel);
addModel(treeModel);
this.treeModel = treeModel;
this.alignment = alignment;
this.siteModel = siteModel;
this.tkfModel = tkfModel;
recursion = new dr.evomodel.indel.HomologyRecursion();
recursion.init(
treeModel,
alignment,
siteModel.getSubstitutionModel(),
siteModel.getMutationRateParameter().getParameterValue(0),
tkfModel.getLengthDistributionValue(),
tkfModel.getDeathRate(1));
addStatistic(new AlignmentLengthStatistic());
}
public class AlignmentLengthStatistic extends Statistic.Abstract {
public double getStatisticValue(int dim) {
return alignment.getSiteCount();
}
public int getDimension() {
return 1;
}
public String getStatisticName() {
return "alignmentLength";
}
}
public void acceptState() {
//throw new RuntimeException("Not implemented!");
}
protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
//throw new RuntimeException("Not implemented!");
}
public void handleModelChangedEvent(Model m, Object o, int i) {
//throw new RuntimeException("Not implemented!");
}
public void setAlignment(Alignment a) {
alignment = a;
//System.out.println("Set new alignment");
}
protected void storeState() {
//super.storeState();
//storedAlignment = alignment;
//System.out.println("Stored alignment");
}
protected void restoreState() {
//super.restoreState();
//alignment = storedAlignment;
//System.out.println("restored alignment");
}
public Alignment getAlignment() {
return alignment;
}
public GammaSiteModel getSiteModel() {
return siteModel;
}
public TreeModel getTreeModel() {
return treeModel;
}
public void makeDirty() { // this is always dirty
}
public Model getModel() {
return this;
}
public double getLogLikelihood() {
recursion.init(
treeModel,
alignment,
siteModel.getSubstitutionModel(),
siteModel.getMutationRateParameter().getParameterValue(0),
tkfModel.getLengthDistributionValue(),
tkfModel.getDeathRate(1));
double logL = recursion.recursion();
//System.out.println("logL = " + logL);
return logL;
}
// **************************************************************
// Identifiable IMPLEMENTATION
// **************************************************************
private String id = null;
public void setId(String id) {
this.id = id;
}
public String getId() {
return id;
}
public String toString() {
if (id != null) {
return id;
}
return super.toString();
}
protected boolean getLikelihoodKnown() {
return false;
}
private final TreeModel treeModel;
private Alignment alignment;
//private Alignment storedAlignment;
private final GammaSiteModel siteModel;
private final TKF91Model tkfModel;
private dr.evomodel.indel.HomologyRecursion recursion = null;
}