
package dr.math.interfaces;

public interface PointSeriesWithErrors extends PointSeries {
double weightAt(int n);
}