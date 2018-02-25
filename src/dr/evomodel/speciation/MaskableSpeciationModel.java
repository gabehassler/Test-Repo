package dr.evomodel.speciation;
public abstract class MaskableSpeciationModel extends SpeciationModel {
public MaskableSpeciationModel(String name, Type units) {
super(name, units);
}
// a model specific implementation that allows this speciation model
// to be partially masked by another -- useful in model averaging applications
public abstract void mask(SpeciationModel mask);
public abstract void unmask();
}
