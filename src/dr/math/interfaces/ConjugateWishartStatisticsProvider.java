
package dr.math.interfaces;

import dr.math.distributions.WishartSufficientStatistics;


public interface ConjugateWishartStatisticsProvider {
    WishartSufficientStatistics getWishartStatistics();
}
