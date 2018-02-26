package dr.evomodel.coalescent;
import dr.evolution.coalescent.IntervalType;
import dr.evolution.coalescent.TreeIntervals;
import dr.evolution.tree.Tree;
import dr.evomodel.tree.TreeModel;
import dr.evomodelxml.coalescent.GMRFSkyrideLikelihoodParser;
import dr.inference.model.MatrixParameter;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.SymmTridiagMatrix;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class GMRFMultilocusSkyrideLikelihood extends GMRFSkyrideLikelihood implements MultiLociTreeSet {
    private double cutOff;
    private int numGridPoints;
    protected int oldFieldLength;
    // number of coalescent events which occur in an interval with constant population size
    protected double[] numCoalEvents;
    protected double[] storedNumCoalEvents;
    protected double[] gridPoints;
    protected double theLastTime;
    protected double diagonalValue;
    // sortedPoints[i][0] is the time of the i-th grid point or sampling or coalescent event
    // sortedPoints[i][1] is 0 if the i-th point is a grid point, 1 if it's a sampling point, and 2 if it's a coalescent point
    // sortedPoints[i][2] is the number of lineages present in the interval starting at time sortedPoints[i][0]
    protected Parameter phiParameter;
    protected Parameter ploidyFactors;
    protected double[] ploidySums;
    protected double[] storedPloidySums;
    protected SymmTridiagMatrix precMatrix;
    protected SymmTridiagMatrix storedPrecMatrix;
    public GMRFMultilocusSkyrideLikelihood(List<Tree> treeList,
                                           Parameter popParameter,
                                           Parameter groupParameter,
                                           Parameter precParameter,
                                           Parameter lambda,
                                           Parameter beta,
                                           MatrixParameter dMatrix,
                                           boolean timeAwareSmoothing,
                                           double cutOff,
                                           int numGridPoints,
                                           Parameter phi,
                                           Parameter ploidyFactorsParameter) {
        super(GMRFSkyrideLikelihoodParser.SKYLINE_LIKELIHOOD);
        this.popSizeParameter = popParameter;
        this.groupSizeParameter = groupParameter;
        this.precisionParameter = precParameter;
        this.lambdaParameter = lambda;
        this.betaParameter = beta;
        this.dMatrix = dMatrix;
        this.timeAwareSmoothing = timeAwareSmoothing;
        this.cutOff = cutOff;
        this.numGridPoints = numGridPoints;
        this.phiParameter = phi;
        this.ploidyFactors = ploidyFactorsParameter;
        setupGridPoints();
        addVariable(popSizeParameter);
        addVariable(precisionParameter);
        addVariable(lambdaParameter);
        if (betaParameter != null) {
            addVariable(betaParameter);
        }
        if (phiParameter != null) {
            addVariable(phiParameter);
        }
        addVariable(ploidyFactors);
        setTree(treeList);
        int correctFieldLength = getCorrectFieldLength();
        if (popSizeParameter.getDimension() <= 1) {
            // popSize dimension hasn't been set yet, set it here:
            popSizeParameter.setDimension(correctFieldLength);
        }
        fieldLength = popSizeParameter.getDimension();
        if (correctFieldLength != fieldLength) {
            throw new IllegalArgumentException("Population size parameter should have length " + correctFieldLength);
        }
        oldFieldLength = getCorrectOldFieldLength();
        if (ploidyFactors.getDimension() != treeList.size()) {
            throw new IllegalArgumentException("Ploidy factors parameter should have length " + treeList.size());
        }
        // Field length must be set by this point
        wrapSetupIntervals();
        coalescentIntervals = new double[oldFieldLength];
        storedCoalescentIntervals = new double[oldFieldLength];
        sufficientStatistics = new double[fieldLength];
        storedSufficientStatistics = new double[fieldLength];
        numCoalEvents = new double[fieldLength];
        storedNumCoalEvents = new double[fieldLength];
        ploidySums = new double[fieldLength];
        storedPloidySums = new double[fieldLength];
        setupGMRFWeights();
        setupSufficientStatistics();
        addStatistic(new DeltaStatistic());
        initializationReport();
        if (groupSizeParameter != null) {
            for (int i = 0; i < groupSizeParameter.getDimension(); i++)
                groupSizeParameter.setParameterValue(i, 1.0);
        }
    }
    //rewrite this constructor without duplicating so much code
    public GMRFMultilocusSkyrideLikelihood(List<Tree> treeList,
                                           Parameter popParameter,
                                           Parameter groupParameter,
                                           Parameter precParameter,
                                           Parameter lambda,
                                           Parameter beta,
                                           MatrixParameter dMatrix,
                                           boolean timeAwareSmoothing,
                                           Parameter specGridPoints) {
        super(GMRFSkyrideLikelihoodParser.SKYLINE_LIKELIHOOD);
        gridPoints = specGridPoints.getParameterValues();
        //gridPointsSpecified = true;
        this.numGridPoints = gridPoints.length;
        this.cutOff = gridPoints[numGridPoints - 1];
        this.popSizeParameter = popParameter;
        this.groupSizeParameter = groupParameter;
        this.precisionParameter = precParameter;
        this.lambdaParameter = lambda;
        this.betaParameter = beta;
        this.dMatrix = dMatrix;
        this.timeAwareSmoothing = timeAwareSmoothing;
        addVariable(popSizeParameter);
        addVariable(precisionParameter);
        addVariable(lambdaParameter);
        if (betaParameter != null) {
            addVariable(betaParameter);
        }
        setTree(treeList);
        int correctFieldLength = getCorrectFieldLength();
        if (popSizeParameter.getDimension() <= 1) {
            // popSize dimension hasn't been set yet, set it here:
            popSizeParameter.setDimension(correctFieldLength);
        }
        fieldLength = popSizeParameter.getDimension();
        if (correctFieldLength != fieldLength) {
            throw new IllegalArgumentException("Population size parameter should have length " + correctFieldLength);
        }
        oldFieldLength = getCorrectOldFieldLength();
        // Field length must be set by this point
        wrapSetupIntervals();
        coalescentIntervals = new double[oldFieldLength];
        storedCoalescentIntervals = new double[oldFieldLength];
        sufficientStatistics = new double[fieldLength];
        storedSufficientStatistics = new double[fieldLength];
        numCoalEvents = new double[fieldLength];
        storedNumCoalEvents = new double[fieldLength];
        ploidySums = new double[fieldLength];
        storedPloidySums = new double[fieldLength];
        setupGMRFWeights();
        addStatistic(new DeltaStatistic());
        initializationReport();
    }
    protected void setTree(List<Tree> treeList) {
        treesSet = this;
        this.treeList = treeList;
        makeTreeIntervalList(treeList, true);
        numTrees = treeList.size();
    }
    private void makeTreeIntervalList(List<Tree> treeList, boolean add) {
        if (intervalsList == null) {
            intervalsList = new ArrayList<TreeIntervals>();
        } else {
            intervalsList.clear();
        }
        for (Tree tree : treeList) {
            intervalsList.add(new TreeIntervals(tree));
            if (add && tree instanceof TreeModel) {
                addModel((TreeModel) tree);
            }
        }
    }
    protected int getCorrectFieldLength() {
        return numGridPoints + 1;
    }
    protected int getCorrectOldFieldLength() {
        int tips = 0;
        for (Tree tree : treeList) {
            tips += tree.getExternalNodeCount();
        }
        return tips - treeList.size();
    }
    protected void handleModelChangedEvent(Model model, Object object, int index) {
        if (model instanceof TreeModel) {
            TreeModel treeModel = (TreeModel) model;
            int tn = treeList.indexOf(treeModel);
            if (tn >= 0) {
                //   intervalsList.get(tn).setIntervalsUnknown();  // TODO Why is this slower (?) than remaking whole list?
                makeTreeIntervalList(treeList, false);
                intervalsKnown = false;
                likelihoodKnown = false;
            } else {
                throw new RuntimeException("Unknown tree modified in GMRFMultilocusSkyrideLikelihood");
            }
        } else {
            throw new RuntimeException("Unknown object modified in GMRFMultilocusSkyrideLikelihood");
        }
    }
    public void initializationReport() {
        System.out.println("Creating a GMRF smoothed skyride model for multiple loci:");
        System.out.println("\tPopulation sizes: " + popSizeParameter.getDimension());
        System.out.println("\tIf you publish results using this model, please reference: ");
        System.out.println("\t\tMinin, Bloomquist and Suchard (2008) Molecular Biology and Evolution, 25, 1459-1471, and");
        System.out.println("\t\tGill, Lemey, Faria, Rambaut, Shapiro and Suchard (2013) Molecular Biology and Evolution, 30, 713-724.");
    }
    public void wrapSetupIntervals() {
        // Do nothing
    }
    int numTrees;
    protected void setupGridPoints() {
        if (gridPoints == null) {
            gridPoints = new double[numGridPoints];
        } else {
            Arrays.fill(gridPoints, 0);
        }
        for (int pt = 0; pt < numGridPoints; pt++) {
            gridPoints[pt] = (pt + 1) * (cutOff / numGridPoints);
        }
    }
    protected void setupSufficientStatistics() {
        //numCoalEvents = new double[fieldLength];
        //sufficientStatistics = new double[fieldLength];
        Arrays.fill(numCoalEvents, 0);
        Arrays.fill(sufficientStatistics, 0);
        Arrays.fill(ploidySums, 0);
        //index of smallest grid point greater than at least one sampling/coalescent time in current tree
        int minGridIndex;
        //index of greatest grid point less than at least one sampling/coalescent time in current tree
        int maxGridIndex;
        int numLineages;
        int currentGridIndex;
        int currentTimeIndex;
        double currentTime;
        double nextTime;
        double ploidyFactor;
        //time of last coalescent event in tree
        double lastCoalescentTime;
        for (int i = 0; i < numTrees; i++) {
            ploidyFactor = 1 / getPopulationFactor(i);
            currentTimeIndex = 0;
            currentTime = intervalsList.get(i).getIntervalTime(currentTimeIndex);
            nextTime = intervalsList.get(i).getIntervalTime(currentTimeIndex + 1);
            while (nextTime <= currentTime) {
                currentTimeIndex++;
                currentTime = intervalsList.get(i).getIntervalTime(currentTimeIndex);
                nextTime = intervalsList.get(i).getIntervalTime(currentTimeIndex + 1);
            }
            numLineages = intervalsList.get(i).getLineageCount(currentTimeIndex + 1);
            minGridIndex = 0;
            while (gridPoints[minGridIndex] <= currentTime) {
                minGridIndex++;
            }
            currentGridIndex = minGridIndex;
            lastCoalescentTime = currentTime + intervalsList.get(i).getTotalDuration();
            theLastTime = lastCoalescentTime;
            maxGridIndex = numGridPoints - 1;
            while ((maxGridIndex >= 0) && (gridPoints[maxGridIndex] >= lastCoalescentTime)) {
                maxGridIndex = maxGridIndex - 1;
            }
            if (maxGridIndex >= 0) {
                //from likelihood of interval between first sampling time and gridPoints[minGridIndex]
                while (nextTime < gridPoints[currentGridIndex]) {
                    //check to see if interval ends with coalescent event
                    if (intervalsList.get(i).getCoalescentEvents(currentTimeIndex + 1) > 0) {
                        numCoalEvents[currentGridIndex]++;
                    }
                    sufficientStatistics[currentGridIndex] = sufficientStatistics[currentGridIndex] + (nextTime - currentTime) * numLineages * (numLineages - 1) * 0.5 * ploidyFactor;
                    currentTime = nextTime;
                    currentTimeIndex++;
                    nextTime = intervalsList.get(i).getIntervalTime(currentTimeIndex + 1);
                    while (nextTime <= currentTime) {
                        currentTimeIndex++;
                        currentTime = intervalsList.get(i).getIntervalTime(currentTimeIndex);
                        nextTime = intervalsList.get(i).getIntervalTime(currentTimeIndex + 1);
                    }
                    numLineages = intervalsList.get(i).getLineageCount(currentTimeIndex + 1);
                }
                sufficientStatistics[currentGridIndex] = sufficientStatistics[currentGridIndex] + (gridPoints[currentGridIndex] - currentTime) * numLineages * (numLineages - 1) * 0.5 * ploidyFactor;
                ploidySums[currentGridIndex] = ploidySums[currentGridIndex] + Math.log(ploidyFactor) * numCoalEvents[currentGridIndex];
                currentGridIndex++;
                //from likelihood of intervals between gridPoints[minGridIndex] and gridPoints[maxGridIndex]
                while (currentGridIndex <= maxGridIndex) {
                    if (nextTime >= gridPoints[currentGridIndex]) {
                        sufficientStatistics[currentGridIndex] = sufficientStatistics[currentGridIndex] + (gridPoints[currentGridIndex] - gridPoints[currentGridIndex - 1]) * numLineages * (numLineages - 1) * 0.5 * ploidyFactor;
                        ploidySums[currentGridIndex] = ploidySums[currentGridIndex] + Math.log(ploidyFactor) * numCoalEvents[currentGridIndex];
                        currentGridIndex++;
                    } else {
                        sufficientStatistics[currentGridIndex] = sufficientStatistics[currentGridIndex] + (nextTime - gridPoints[currentGridIndex - 1]) * numLineages * (numLineages - 1) * 0.5 * ploidyFactor;
                        //check to see if interval ends with coalescent event
                        if (intervalsList.get(i).getCoalescentEvents(currentTimeIndex + 1) > 0) {
                            numCoalEvents[currentGridIndex]++;
                        }
                        currentTime = nextTime;
                        currentTimeIndex++;
                        nextTime = intervalsList.get(i).getIntervalTime(currentTimeIndex + 1);
                        while (nextTime <= currentTime) {
                            currentTimeIndex++;
                            currentTime = intervalsList.get(i).getIntervalTime(currentTimeIndex);
                            nextTime = intervalsList.get(i).getIntervalTime(currentTimeIndex + 1);
                        }
                        numLineages = intervalsList.get(i).getLineageCount(currentTimeIndex + 1);
                        while (nextTime < gridPoints[currentGridIndex]) {
                            //check to see if interval is coalescent interval or sampling interval
                            if (intervalsList.get(i).getCoalescentEvents(currentTimeIndex + 1) > 0) {
                                numCoalEvents[currentGridIndex]++;
                            }
                            sufficientStatistics[currentGridIndex] = sufficientStatistics[currentGridIndex] + (nextTime - currentTime) * numLineages * (numLineages - 1) * 0.5 * ploidyFactor;
                            currentTime = nextTime;
                            currentTimeIndex++;
                            nextTime = intervalsList.get(i).getIntervalTime(currentTimeIndex + 1);
                            while (nextTime <= currentTime) {
                                currentTimeIndex++;
                                currentTime = intervalsList.get(i).getIntervalTime(currentTimeIndex);
                                nextTime = intervalsList.get(i).getIntervalTime(currentTimeIndex + 1);
                            }
                            numLineages = intervalsList.get(i).getLineageCount(currentTimeIndex + 1);
                        }
                        sufficientStatistics[currentGridIndex] = sufficientStatistics[currentGridIndex] + (gridPoints[currentGridIndex] - currentTime) * numLineages * (numLineages - 1) * 0.5 * ploidyFactor;
                        ploidySums[currentGridIndex] = ploidySums[currentGridIndex] + Math.log(ploidyFactor) * numCoalEvents[currentGridIndex];
                        currentGridIndex++;
                    }
                }
                //from likelihood of interval between gridPoints[maxGridIndex] and lastCoalescentTime
                sufficientStatistics[currentGridIndex] = sufficientStatistics[currentGridIndex] + (nextTime - gridPoints[currentGridIndex - 1]) * numLineages * (numLineages - 1) * 0.5 * ploidyFactor;
                //check to see if interval ends with coalescent event
                if (intervalsList.get(i).getCoalescentEvents(currentTimeIndex + 1) > 0) {
                    numCoalEvents[currentGridIndex]++;
                }
                currentTime = nextTime;
                currentTimeIndex++;
                while ((currentTimeIndex + 1) < intervalsList.get(i).getIntervalCount()) {
                    // currentTime = nextTime;
                    // currentTimeIndex++;
                    nextTime = intervalsList.get(i).getIntervalTime(currentTimeIndex + 1);
                    while (nextTime <= currentTime) {
                        currentTimeIndex++;
                        currentTime = intervalsList.get(i).getIntervalTime(currentTimeIndex);
                        nextTime = intervalsList.get(i).getIntervalTime(currentTimeIndex + 1);
                    }
                    numLineages = intervalsList.get(i).getLineageCount(currentTimeIndex + 1);
                    //check to see if interval is coalescent interval or sampling interval
                    if (intervalsList.get(i).getCoalescentEvents(currentTimeIndex + 1) > 0) {
                        numCoalEvents[currentGridIndex]++;
                    }
                    sufficientStatistics[currentGridIndex] = sufficientStatistics[currentGridIndex] + (nextTime - currentTime) * numLineages * (numLineages - 1) * 0.5 * ploidyFactor;
                    currentTime = nextTime;
                    currentTimeIndex++;
                }
            } else {
                while ((currentTimeIndex + 1) < intervalsList.get(i).getIntervalCount()) {
                    //check to see if interval is coalescent interval or sampling interval
                    if (intervalsList.get(i).getCoalescentEvents(currentTimeIndex + 1) > 0) {
                        numCoalEvents[currentGridIndex]++;
                    }
                    sufficientStatistics[currentGridIndex] = sufficientStatistics[currentGridIndex] + (nextTime - currentTime) * numLineages * (numLineages - 1) * 0.5 * ploidyFactor;
                    currentTime = nextTime;
                    currentTimeIndex++;
                    if ((currentTimeIndex + 1) < intervalsList.get(i).getIntervalCount()) {
                        nextTime = intervalsList.get(i).getIntervalTime(currentTimeIndex + 1);
                        while (nextTime <= currentTime) {
                            currentTimeIndex++;
                            currentTime = intervalsList.get(i).getIntervalTime(currentTimeIndex);
                            nextTime = intervalsList.get(i).getIntervalTime(currentTimeIndex + 1);
                        }
                        numLineages = intervalsList.get(i).getLineageCount(currentTimeIndex + 1);
                    }
                }
                ploidySums[currentGridIndex] = ploidySums[currentGridIndex] + Math.log(ploidyFactor) * numCoalEvents[currentGridIndex];
            }
        }
    }
    public double[] getNumCoalEvents() {
        return numCoalEvents;
    }
    protected double calculateLogCoalescentLikelihood() {
        if (!intervalsKnown) {
            // intervalsKnown -> false when handleModelChanged event occurs in super.
            wrapSetupIntervals();
            setupSufficientStatistics();
            intervalsKnown = true;
        }
        // Matrix operations taken from block update sampler to calculate data likelihood and field prior
        double currentLike = 0;
        double[] currentGamma = popSizeParameter.getParameterValues();
        for (int i = 0; i < fieldLength; i++) {
            currentLike += -numCoalEvents[i] * currentGamma[i] + ploidySums[i] - sufficientStatistics[i] * Math.exp(-currentGamma[i]);
        }
        return currentLike;
    }
    protected double calculateLogFieldLikelihood() {
        if (!intervalsKnown) {
            //intervalsKnown -> false when handleModelChanged event occurs in super.
            wrapSetupIntervals();
            setupSufficientStatistics();
            intervalsKnown = true;
        }
        double currentLike = 0;
        DenseVector diagonal1 = new DenseVector(fieldLength);
        DenseVector currentGamma = new DenseVector(popSizeParameter.getParameterValues());
        SymmTridiagMatrix currentQ = getScaledWeightMatrix(precisionParameter.getParameterValue(0), lambdaParameter.getParameterValue(0));
        currentQ.mult(currentGamma, diagonal1);
        //        currentLike += 0.5 * logGeneralizedDeterminant(currentQ) - 0.5 * currentGamma.dot(diagonal1);
        currentLike += 0.5 * (fieldLength - 1) * Math.log(precisionParameter.getParameterValue(0)) - 0.5 * currentGamma.dot(diagonal1);
        if (lambdaParameter.getParameterValue(0) == 1) {
            currentLike -= (fieldLength - 1) / 2.0 * LOG_TWO_TIMES_PI;
        } else {
            currentLike -= fieldLength / 2.0 * LOG_TWO_TIMES_PI;
        }
        return currentLike;
    }
    public double getLogLikelihood() {
        if (!likelihoodKnown) {
            logLikelihood = calculateLogCoalescentLikelihood();
            logFieldLikelihood = calculateLogFieldLikelihood();
            likelihoodKnown = true;
        }
        return logLikelihood + logFieldLikelihood;
    }
    protected void setupGMRFWeights() {
        //setupSufficientStatistics();
        //Set up the weight Matrix
        double[] offdiag = new double[fieldLength - 1];
        double[] diag = new double[fieldLength];
        diagonalValue = 2;
        //First set up the offdiagonal entries;
        for (int i = 0; i < fieldLength - 1; i++) {
            offdiag[i] = -1;
        }
        //Then set up the diagonal entries;
        for (int i = 1; i < fieldLength - 1; i++) {
            //	diag[i] = -(offdiag[i] + offdiag[i - 1]);
            diag[i] = diagonalValue;
        }
        //Take care of the endpoints
        //diag[0] = -offdiag[0];
        //diag[fieldLength - 1] = -offdiag[fieldLength - 2];
        diag[0] = diagonalValue - 1.0;
        diag[fieldLength - 1] = diagonalValue - 1.0;
        weightMatrix = new SymmTridiagMatrix(diag, offdiag);
    }
    protected double getFieldScalar() {
        return 1.0;
    }
    private List<Tree> treeList;
    private List<TreeIntervals> intervalsList;
    public int nLoci() {
        return treeList.size();
    }
    public Tree getTree(int nt) {
        return treeList.get(nt);
    }
    public TreeIntervals getTreeIntervals(int nt) {
        return intervalsList.get(nt);
    }
    public double getPopulationFactor(int nt) {
        return ploidyFactors.getParameterValue(nt);
    }
    public void storeTheState() {
        for (TreeIntervals intervals : intervalsList) {
            intervals.storeState();
        }
    }
    public void restoreTheState() {
        for (TreeIntervals intervals : intervalsList) {
            intervals.restoreState();
        }
    }
    protected void storeState() {
        // System.arraycopy(numCoalEvents, 0, storedNumCoalEvents, 0, numCoalEvents.length);
        super.storeState();
        System.arraycopy(numCoalEvents, 0, storedNumCoalEvents, 0, numCoalEvents.length);
        // storedPrecMatrix = precMatrix.copy();
        System.arraycopy(ploidySums, 0, storedPloidySums, 0, ploidySums.length);
    }
    protected void restoreState() {
        super.restoreState();
        // Swap pointers
        double[] tmp = numCoalEvents;
        numCoalEvents = storedNumCoalEvents;
        storedNumCoalEvents = tmp;
        double[] tmp2 = ploidySums;
        ploidySums = storedPloidySums;
        storedPloidySums = tmp2;
    }
    public int getCoalescentIntervalLineageCount(int i) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
    public IntervalType getCoalescentIntervalType(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
