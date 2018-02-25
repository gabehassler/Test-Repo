package dr.inference.markovjumps;
import dr.inference.model.Parameter;
public interface MarkovJumpsRegisterAcceptor {
public void addRegister(Parameter addRegisterParameter,
MarkovJumpsType type,
boolean scaleByTime);
}
