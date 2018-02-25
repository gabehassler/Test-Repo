package dr.app.beagle.evomodel.newtreelikelihood;
import dr.inference.model.Model;
public interface SiteModel extends Model {
int getCategoryCount();
double[] getCategoryRates();
double[] getCategoryProportions();
double getRateForCategory(int category);
double getProportionForCategory(int category);
}