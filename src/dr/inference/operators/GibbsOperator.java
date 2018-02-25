package dr.inference.operators;
public interface GibbsOperator extends MCMCOperator {
int getStepCount();
void setPathParameter(double beta);
}
