
package dr.evomodel.substmodel;

import dr.evomodel.substmodel.NucModelType;
import dr.inference.model.Parameter;
import dr.inference.model.Statistic;
import dr.inference.model.Variable;


public class HKY extends AbstractNucleotideModel {

    private double tsTv;

    private Variable<Double> kappaParameter = null;

    private boolean updateIntermediates = true;

    private double beta, A_R, A_Y;
    private double tab1A, tab2A, tab3A;
    private double tab1C, tab2C, tab3C;
    private double tab1G, tab2G, tab3G;
    private double tab1T, tab2T, tab3T;

    public HKY(double kappa, FrequencyModel freqModel) {
        this(new Parameter.Default(kappa), freqModel);
    }
    public HKY(Variable kappaParameter, FrequencyModel freqModel) {

        super(NucModelType.HKY.getXMLName(), freqModel);
        this.kappaParameter = kappaParameter;
        addVariable(kappaParameter);
        kappaParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
        updateIntermediates = true;

        addStatistic(tsTvStatistic);
    }

    public void setKappa(double kappa) {
        kappaParameter.setValue(0, kappa);
        updateMatrix = true;
    }

    public final double getKappa() {
        return kappaParameter.getValue(0);
    }

    public void setTsTv(double tsTv) {
        this.tsTv = tsTv;
        calculateFreqRY();
        setKappa((tsTv * freqR * freqY) / (freqA * freqG + freqC * freqT));
    }

    public double getTsTv() {
        calculateFreqRY();
        tsTv = (getKappa() * (freqA * freqG + freqC * freqT)) / (freqR * freqY);

        return tsTv;
    }

    protected void frequenciesChanged() {
        // frequencyModel changed
        updateIntermediates = true;
    }

    private void calculateIntermediates() {

        calculateFreqRY();

        // small speed up - reduce calculations. Comments show original code

        // (C+T) / (A+G)
        final double r1 = (1 / freqR) - 1;
        tab1A = freqA * r1;

        tab3A = freqA / freqR;
        tab2A = 1 - tab3A;        // (freqR-freqA)/freqR;

        final double r2 = 1 / r1; // ((1 / freqY) - 1);
        tab1C = freqC * r2;

        tab3C = freqC / freqY;
        tab2C = 1 - tab3C;       // (freqY-freqC)/freqY; assert  tab2C + tab3C == 1.0;

        tab1G = freqG * r1;
        tab3G = tab2A;            // 1 - tab3A; // freqG/freqR;
        tab2G = tab3A;            // 1 - tab3G; // (freqR-freqG)/freqR;

        tab1T = freqT * r2;

        tab3T = tab2C;            // 1 - tab3C;  // freqT/freqY;
        tab2T = tab3C;            // 1 - tab3T; // (freqY-freqT)/freqY; //assert tab2T + tab3T == 1.0 ;

        updateMatrix = true;
        updateIntermediates = false;
    }

    public void getTransitionProbabilities(double distance, double[] matrix) {
        synchronized (this) {
            if (updateIntermediates) {
                calculateIntermediates();
            }

            if (updateMatrix) {
                setupMatrix();
            }
        }

        final double xx = beta * distance;
        final double bbR = Math.exp(xx * A_R);
        final double bbY = Math.exp(xx * A_Y);

        final double aa = Math.exp(xx);
        final double oneminusa = 1 - aa;

        final double t1Aaa = (tab1A * aa);
        matrix[0] = freqA + t1Aaa + (tab2A * bbR);

        matrix[1] = freqC * oneminusa;
        final double t1Gaa = (tab1G * aa);
        matrix[2] = freqG + t1Gaa - (tab3G * bbR);
        matrix[3] = freqT * oneminusa;

        matrix[4] = freqA * oneminusa;
        final double t1Caa = (tab1C * aa);
        matrix[5] = freqC + t1Caa + (tab2C * bbY);
        matrix[6] = freqG * oneminusa;
        final double t1Taa = (tab1T * aa);
        matrix[7] = freqT + t1Taa - (tab3T * bbY);

        matrix[8] = freqA + t1Aaa - (tab3A * bbR);
        matrix[9] = matrix[1];
        matrix[10] = freqG + t1Gaa + (tab2G * bbR);
        matrix[11] = matrix[3];

        matrix[12] = matrix[4];
        matrix[13] = freqC + t1Caa - (tab3C * bbY);
        matrix[14] = matrix[6];
        matrix[15] = freqT + t1Taa + (tab2T * bbY);
    }

    public void setupMatrix() {
        final double kappa = getKappa();
        beta = -1.0 / (2.0 * (freqR * freqY + kappa * (freqA * freqG + freqC * freqT)));

        A_R = 1.0 + freqR * (kappa - 1);
        A_Y = 1.0 + freqY * (kappa - 1);

        updateMatrix = false;
    }

    protected void setupRelativeRates() {
    }

    public double[][] getEigenVectors() {
        synchronized (this) {
            if (updateMatrix) {
                setupEigenSystem();
            }
        }
        return Evec;
    }

    public double[][] getInverseEigenVectors() {
        synchronized (this) {
            if (updateMatrix) {
                setupEigenSystem();
            }
        }
        return Ievc;
    }

    public double[] getEigenValues() {
        synchronized (this) {
            if (updateMatrix) {
                setupEigenSystem();
            }
        }
        return Eval;
    }

    protected void setupEigenSystem() {
        if (!eigenInitialised)
            initialiseEigen();

        final double kappa = getKappa();

        // left eigenvector #1
        Ievc[0][0] = freqA; // or, evec[0] = pi;
        Ievc[0][1] = freqC;
        Ievc[0][2] = freqG;
        Ievc[0][3] = freqT;

        // left eigenvector #2
        Ievc[1][0] =  freqA * freqY;
        Ievc[1][1] = -freqC * freqR;
        Ievc[1][2] =  freqG * freqY;
        Ievc[1][3] = -freqT * freqR;

        Ievc[2][1] =  1; // left eigenvectors 3 = (0,1,0,-1); 4 = (1,0,-1,0)
        Ievc[2][3] = -1;

        Ievc[3][0] =  1;
        Ievc[3][2] = -1;

        Evec[0][0] =  1; // right eigenvector 1 = (1,1,1,1)'
        Evec[1][0] =  1;
        Evec[2][0] =  1;
        Evec[3][0] =  1;

        // right eigenvector #2
        Evec[0][1] =  1.0/freqR;
        Evec[1][1] = -1.0/freqY;
        Evec[2][1] =  1.0/freqR;
        Evec[3][1] = -1.0/freqY;

        // right eigenvector #3
        Evec[1][2] =  freqT / freqY;
        Evec[3][2] = -freqC / freqY;

        // right eigenvector #4
        Evec[0][3] =  freqG / freqR;
        Evec[2][3] = -freqA / freqR;

        // eigenvectors
        beta = -1.0 / (2.0 * (freqR * freqY + kappa * (freqA * freqG + freqC * freqT)));

        A_R = 1.0 + freqR * (kappa - 1);
        A_Y = 1.0 + freqY * (kappa - 1);

        Eval[1] = beta;
        Eval[2] = beta*A_Y;
        Eval[3] = beta*A_R;

        updateMatrix = false;
    }

    protected void initialiseEigen() {

        Eval = new double[stateCount];
        Evec = new double[stateCount][stateCount];
        Ievc = new double[stateCount][stateCount];

        eigenInitialised = true;
        updateMatrix = true;
    }
    // *****************************************************************
    // Interface Model
    // *****************************************************************

    public void restoreState() {
        super.restoreState();
        updateIntermediates = true;
    }

    // **************************************************************
    // XHTMLable IMPLEMENTATION
    // **************************************************************

    public String toXHTML() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("<em>HKY Model</em> Ts/Tv = ");
        buffer.append(getTsTv());
        buffer.append(" (kappa = ");
        buffer.append(getKappa());
        buffer.append(")");

        return buffer.toString();
    }

    //
    // Private stuff
    //

    private final Statistic tsTvStatistic = new Statistic.Abstract() {

        public String getStatisticName() {
            return "tsTv";
        }

        public int getDimension() {
            return 1;
        }

        public double getStatisticValue(int dim) {
            return getTsTv();
        }

    };
}
