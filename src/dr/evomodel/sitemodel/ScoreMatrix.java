package dr.evomodel.sitemodel;
import dr.evolution.alignment.SitePatterns;
public class ScoreMatrix {
    SiteModel siteModel;
    double time;
    double[] matrix;
    double[] logp;
    // base frequencies
    double[][] logOddScores;
    double[][] pscores;
    int stateCount;
    public ScoreMatrix(SiteModel siteModel, double time) {
        this.siteModel = siteModel;
        logp = siteModel.getSubstitutionModel().getFrequencyModel().getFrequencies();
        for (int i = 0; i < logp.length; i++) {
            logp[i] = Math.log(logp[i]);
        }
        stateCount = siteModel.getFrequencyModel().getFrequencyCount();
        matrix = new double[stateCount*stateCount];
        pscores = new double[stateCount][stateCount];
        logOddScores = new double[stateCount][stateCount];
        setTime(time);
    }
    public void setTime(double time) {
        this.time = time;
        siteModel.getSubstitutionModel().getTransitionProbabilities(time, matrix);
        //base frequencies
        for (int i = 0; i < stateCount; i++) {
            for (int j = 0; j < stateCount; j++) {
                pscores[i][j] = Math.log(matrix[i*stateCount + j]);
                logOddScores[i][j] = pscores[i][j] - logp[i] - logp[j];
            }
        }
    }
    public final double getScore(int i, int j) {
        return pscores[i][j] - logp[j];
    }
    public final double getScore(SitePatterns patterns) {
        if (patterns.getPatternLength() != 2) throw new IllegalArgumentException();
        double logL = 0.0;
        for (int i = 0; i < patterns.getPatternCount(); i++) {
            double weight = patterns.getPatternWeight(i);
            int[] pattern = patterns.getPattern(i);
            int x = pattern[0];
            int y = pattern[1];
            if (x < stateCount && y < stateCount) {
                logL += getScore(x,y) * weight;
            }
        }
        return logL;
    }
}
