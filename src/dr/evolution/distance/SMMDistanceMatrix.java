package dr.evolution.distance;
import dr.evolution.alignment.PatternList;
public class SMMDistanceMatrix extends DistanceMatrix{
    public SMMDistanceMatrix(PatternList patterns) {
        super(patterns);
    }
    protected double calculatePairwiseDistance(int taxon1, int taxon2) {
        int[] pattern = patterns.getPattern(0);
        int state1 = pattern[taxon1];
        int state2 = pattern[taxon2];
        double distance = 0.0;
        if (!dataType.isAmbiguousState(state1) && !dataType.isAmbiguousState(state2))
            distance = Math.abs(state1 - state2);
        return distance;
    }
}
