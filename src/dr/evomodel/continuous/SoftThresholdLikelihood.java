package dr.evomodel.continuous;
public interface SoftThresholdLikelihood {
   public void setPathParameter(double beta);
   public double getLikelihoodCorrection();
}
