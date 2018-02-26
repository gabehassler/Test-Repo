
package dr.app.beagle.evomodel.utilities;

import java.util.Set;

public interface HistoryFilter {
    public boolean filter(String source, String destination, double time);

    public String getDescription();

    public class Default implements HistoryFilter {
        public Default() {
            // Do nothing
        }

        public String getDescription() {
            return "Default";
        }

        public boolean filter(String source, String destination, double time) {
            return true;
        }
    }

    public class SetFilter implements HistoryFilter {
        final private Set<String> sources;
        final private Set<String> destinations;
        final double maxTime;
        final double minTime;

        public SetFilter(Set<String> sources, Set<String> destinations, double maxTime, double minTime) {
            this.sources = sources;
            this.destinations = destinations;
            this.maxTime = maxTime;
            this.minTime = minTime;
        }

        public String getDescription() {
            StringBuilder sb = new StringBuilder();
            sb.append(minTime).append(" <= event time <= ").append(maxTime);
            return sb.toString();
        }

        public boolean filter(String source, String destination, double time) {

            if (sources == null && destinations == null) {
                return time <= maxTime && time >= minTime;
            } // else

            return sources.contains(source) && destinations.contains(destination) &&
                    time <= maxTime && time >= minTime;
        }
    }
}
