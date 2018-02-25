package dr.inference.operators;
public abstract class AbstractCoercableOperator extends SimpleMCMCOperator implements CoercableMCMCOperator {
public CoercionMode mode = CoercionMode.DEFAULT;
public AbstractCoercableOperator(CoercionMode mode) {
this.mode = mode;
}
public final CoercionMode getMode() {
return mode;
}
}
