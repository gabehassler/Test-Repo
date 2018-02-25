package dr.evomodel.indel;
import dr.evolution.util.Units;
import dr.inference.model.AbstractModel;
public abstract class IndelModel extends AbstractModel implements Units {
public static final String INDEL_MODEL = "indelModel";
//
// Public stuff
//
public IndelModel(String name) { 
super(name);
units = Units.Type.GENERATIONS;
}
//
// functions that define an indel model (left for subclass)
//
public abstract double getBirthRate(int length);
public abstract double getDeathRate(int length);
private Type units;
public void setUnits(Type u)
{
units = u;
}
public Type getUnits()
{
return units;
}
}
