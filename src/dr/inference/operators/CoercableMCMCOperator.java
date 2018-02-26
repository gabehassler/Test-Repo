package dr.inference.operators;
public interface CoercableMCMCOperator extends MCMCOperator {
    public static final String AUTO_OPTIMIZE = "autoOptimize";
    double getCoercableParameter();
    void setCoercableParameter(double value);
    double getRawParameter();
    CoercionMode getMode();
}
