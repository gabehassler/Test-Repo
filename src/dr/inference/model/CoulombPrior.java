package dr.inference.model;
import java.util.ArrayList;
public class CoulombPrior extends Likelihood.Abstract {
public CoulombPrior(double beta) {
super(null);
this.beta = beta;
}
public void addData(Statistic data) {
dataList.add(data);
}
protected ArrayList<Statistic> dataList = new ArrayList<Statistic>();
protected boolean getLikelihoodKnown() {
return false;
}
public double calculateLogLikelihood() {
double logL = 0.0;
for (Statistic statistic : dataList) {
for (int j = 0; j < statistic.getDimension(); j++) {
logL += -beta / statistic.getStatisticValue(j);
}
}
return logL;
}
public String prettyName() {
String s = "Coulomb" + "(";
for (Statistic statistic : dataList) {
s = s + statistic.getStatisticName() + ",";
}
return s.substring(0, s.length() - 1) + ")";
}
private final double beta;
}
