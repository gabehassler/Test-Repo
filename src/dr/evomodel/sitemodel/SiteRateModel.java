
package dr.evomodel.sitemodel;

import dr.inference.model.Model;


public interface SiteRateModel extends Model {

    int getCategoryCount();

    double[] getCategoryRates();

    double[] getCategoryProportions();

    double getRateForCategory(int category);

    double getProportionForCategory(int category);

}
