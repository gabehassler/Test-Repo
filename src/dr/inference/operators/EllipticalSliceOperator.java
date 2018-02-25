package dr.inference.operators;
import dr.inference.distribution.DistributionLikelihood;
import dr.inference.distribution.MultivariateDistributionLikelihood;
import dr.inference.distribution.NormalDistributionModel;
import dr.inference.distribution.ParametricDistributionModel;
import dr.inference.model.*;
import dr.inference.prior.Prior;
import dr.inferencexml.operators.EllipticalSliceOperatorParser;
import dr.math.MathUtils;
import dr.math.distributions.GaussianProcessRandomGenerator;
import dr.math.distributions.MultivariateNormalDistribution;
import dr.util.Attribute;
import dr.util.Transform;
import java.util.ArrayList;
import java.util.List;
public class EllipticalSliceOperator extends SimpleMetropolizedGibbsOperator implements GibbsOperator {
private final GaussianProcessRandomGenerator gaussianProcess;
public EllipticalSliceOperator(Parameter variable, GaussianProcessRandomGenerator gaussianProcess,
boolean drawByRow, boolean signal) {
this.variable = variable;
this.gaussianProcess = gaussianProcess;
this.drawByRow = drawByRow; // TODO Fix!
this.signalConstituentParameters = signal;
// TODO Must set priorMean if guassianProcess does not have a 0-mean.
}
public Variable<Double> getVariable() {
return variable;
}
private double getLogGaussianPrior() {
return (gaussianProcess.getLikelihood() == null) ?
gaussianProcess.logPdf(variable.getParameterValues()) :
gaussianProcess.getLikelihood().getLogLikelihood();
}
public double doOperation(Prior prior, Likelihood likelihood) throws OperatorFailedException {
double logPosterior = evaluate(likelihood, prior, pathParameter);
double logGaussianPrior = getLogGaussianPrior();
// Cut-off depends only on non-GP contribution to posterior
double cutoffDensity = logPosterior - logGaussianPrior + MathUtils.randomLogDouble();
drawFromSlice(prior, likelihood, cutoffDensity);
// No need to set variable, as SliceInterval has already done this (and recomputed posterior)
return 0;
}
private double[] pointOnEllipse(double[] x, double[] y, double phi, double[] priorMean) {
final int dim = x.length;
final double cos = Math.cos(phi);
final double sin = Math.sin(phi);
double[] r = new double[dim];
if (priorMean == null) {
for (int i = 0; i < dim; ++i) {
r[i] = x[i] * cos + y[i] * sin;
}
} else {  // Non-0 prior mean
for (int i = 0; i < dim; ++i) {
r[i] = (x[i] - priorMean[i]) * cos + (y[i] - priorMean[i]) * sin + priorMean[i];
}
}
return r;
}
private void setVariable(double[] x) {
for (int i = 0; i < x.length; ++i) {
variable.setParameterValueQuietly(i, x[i]);
}
if (signalConstituentParameters) {
variable.fireParameterChangedEvent();
} else {
((CompoundParameter)variable).fireParameterChangedEvent(-1, Variable.ChangeType.ALL_VALUES_CHANGED);
}
}
//
//        if(!(variable instanceof CompoundParameter))
//        {variable.setParameterValueNotifyChangedAll(0, x[0]);
//        for (int i = 1; i < x.length; ++i) {
//            variable.setParameterValueQuietly(i, x[i]);
//        }}
//        else{
//            if(!drawByRow) {
//                ((CompoundParameter) variable).setParameterValueNotifyChangedAll(0, current, x[0]);
//                for (int i = 1; i < x.length; ++i) {
//                    ((CompoundParameter) variable).setParameterValueQuietly(i, current, x[i]);
//                }
//            }
//            else{
//                for (int i = 0; i < x.length; i++) {
//                    ((CompoundParameter) variable).setParameterValue(current, i, x[i]);
//                }
//            }
//        }
private void drawFromSlice(Prior prior, Likelihood likelihood, double cutoffDensity) {
// Do nothing
double[] x = variable.getParameterValues();
double[] nu = (double[]) gaussianProcess.nextRandom();
//        double[] x;
//        if(!(variable instanceof CompoundParameter))
//            x = variable.getParameterValues();
//        else{
//            if(drawByRow){
//                current=MathUtils.nextInt(((CompoundParameter) variable).getParameter(0).getDimension());
//                x=new double[((CompoundParameter) variable).getParameterCount()];
//                for (int i = 0; i <x.length ; i++) {
//                    x[i]=((CompoundParameter) variable).getParameter(i).getParameterValue(current);
//                }
//            }
//            else {
//                current = MathUtils.nextInt(((CompoundParameter) variable).getParameterCount());
//                x=((CompoundParameter) variable).getParameter(current).getParameterValues();
//            }
//
//        }
//        double[] nu;
//        if(normalPrior!=null)
//            nu = normalPrior.nextMultivariateNormal();
//        else
//            nu = treePrior.nextRandomFast(0);
double phi = MathUtils.nextDouble() * 2.0 * Math.PI;
Interval phiInterval = new Interval(phi - 2.0 * Math.PI, phi);
boolean done = false;
while (!done) {
double[] xx = pointOnEllipse(x, nu, phi, priorMean);
setVariable(xx);
double density = evaluate(likelihood, prior, pathParameter);
density -= getLogGaussianPrior(); // Depends only on non-GP contribution to posterior
if (density > cutoffDensity) {
done = true;
} else {
phiInterval.adjust(phi);
phi = phiInterval.draw();
}
}
}
private class Interval {
double lower;
double upper;
Interval(double lower, double upper) {
this.lower = lower;
this.upper = upper;
}
void adjust(double phi) {
if (phi > 0) {
upper = phi;
} else if (phi < 0) {
lower = phi;
} else {
throw new RuntimeException("Shrunk to current position; bad.");
}
}
double draw() {
return MathUtils.nextDouble() * (upper - lower) + lower;
}
}
public int getStepCount() {
return 1;
}
@Override
public void setPathParameter(double beta) {
pathParameter=beta;
}
public String getOperatorName() {
return EllipticalSliceOperatorParser.ELLIPTICAL_SLICE_SAMPLER;
}
public static void main(String[] arg) {
// Define normal model
Parameter thetaParameter = new Parameter.Default(new double[]{1.0, 0.0});  // Starting values
MaskedParameter meanParameter = new MaskedParameter(thetaParameter,
new Parameter.Default(new double[]{1.0, 0.0}), true);
TransformedParameter precParameter = new TransformedParameter(
new MaskedParameter(thetaParameter,
new Parameter.Default(new double[]{0.0, 1.0}), true),
new Transform.LogTransform(),
true
);
//        System.err.println(thetaParameter);
//        System.err.println(meanParameter);
//        System.err.println(precParameter);
ParametricDistributionModel densityModel = new NormalDistributionModel(meanParameter, precParameter, true);
DistributionLikelihood likelihood = new DistributionLikelihood(densityModel);
// Define prior
MultivariateNormalDistribution priorDistribution = new MultivariateNormalDistribution(
new double[]{0.0, 0.0},
new double[][]{{0.001, 0.0}, {0.0, 0.001}}
);
MultivariateDistributionLikelihood prior = new MultivariateDistributionLikelihood(priorDistribution);
prior.addData(thetaParameter);
// Define data
//        likelihood.addData(new Attribute.Default<double[]>("Data", new double[] {0.0, 2.0, 4.0}));
likelihood.addData(new Attribute.Default<double[]>("Data", new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9}));
List<Likelihood> list = new ArrayList<Likelihood>();
list.add(likelihood);
list.add(prior);
CompoundLikelihood posterior = new CompoundLikelihood(0, list);
EllipticalSliceOperator sliceSampler = new EllipticalSliceOperator(thetaParameter, priorDistribution,
false, true);
final int dim = thetaParameter.getDimension();
final int length = 100000;
double[] mean = new double[dim];
double[] variance = new double[dim];
Parameter[] log = new Parameter[dim];
log[0] = meanParameter;
log[1] = precParameter;
for (int i = 0; i < length; i++) {
try {
sliceSampler.doOperation(null, posterior);
} catch (OperatorFailedException e) {
System.err.println(e.getMessage());
}
for (int j = 0; j < dim; ++j) {
double x = log[j].getValue(0);
mean[j] += x;
variance[j] += x * x;
}
}
for (int j = 0; j < dim; ++j) {
mean[j] /= length;
variance[j] /= length;
variance[j] -= mean[j] * mean[j];
}
System.out.println("E(x)\tStErr(x)");
for (int j = 0; j < dim; ++j) {
System.out.println(mean[j] + " " + Math.sqrt(variance[j]));
}
}
private double pathParameter=1.0;
private final Parameter variable;
private int current;
private boolean drawByRow;
private boolean signalConstituentParameters;
private double[] priorMean = null;
function [xx, cur_log_like] = elliptical_slice(xx, prior, log_like_fn, cur_log_like, angle_range, varargin)
%ELLIPTICAL_SLICE Markov chain update for a distribution with a Gaussian "prior" factored out
%
%     [xx, cur_log_like] = elliptical_slice(xx, chol_Sigma, log_like_fn);
% OR
%     [xx, cur_log_like] = elliptical_slice(xx, prior_sample, log_like_fn);
%
% Optional additional arguments: cur_log_like, angle_range, varargin (see below).
%
% A Markov chain update is applied to the D-element array xx leaving a
% "posterior" distribution
%     P(xx) \propto N(xx;0,Sigma) L(xx)
% invariant. Where N(0,Sigma) is a zero-mean Gaussian distribution with
% covariance Sigma. Often L is a likelihood function in an inference problem.
%
% Inputs:
%              xx Dx1 initial vector (can be any array with D elements)
%
%      chol_Sigma DxD chol(Sigma). Sigma is the prior covariance of xx
%  or:
%    prior_sample Dx1 single sample from N(0, Sigma)
%
%     log_like_fn @fn log_like_fn(xx, varargin{:}) returns 1x1 log likelihood
%
% Optional inputs:
%    cur_log_like 1x1 log_like_fn(xx, varargin{:}) of initial vector.
%                     You can omit this argument or pass [].
%     angle_range 1x1 Default 0: explore whole ellipse with break point at
%                     first rejection. Set in (0,2*pi] to explore a bracket of
%                     the specified width centred uniformly at random.
%                     You can omit this argument or pass [].
%        varargin  -  any additional arguments are passed to log_like_fn
%
% Outputs:
%              xx Dx1 (size matches input) perturbed vector
%    cur_log_like 1x1 log_like_fn(xx, varargin{:}) of final vector
% Iain Murray, September 2009
% Tweak to interface and documentation, September 2010
% Reference:
% Elliptical slice sampling
% Iain Murray, Ryan Prescott Adams and David J.C. MacKay.
% The Proceedings of the 13th International Conference on Artificial
% Intelligence and Statistics (AISTATS), JMLR W&CP 9:541-548, 2010.
D = numel(xx);
if (nargin < 4) || isempty(cur_log_like)
cur_log_like = log_like_fn(xx, varargin{:});
end
if (nargin < 5) || isempty(angle_range)
angle_range = 0;
end
% Set up the ellipse and the slice threshold
if numel(prior) == D
% User provided a prior sample:
nu = reshape(prior, size(xx));
else
% User specified Cholesky of prior covariance:
if ~isequal(size(prior), [D D])
error('Prior must be given by a D-element sample or DxD chol(Sigma)');
end
nu = reshape(prior'*randn(D, 1), size(xx));
end
hh = log(rand) + cur_log_like;
% Set up a bracket of angles and pick a first proposal.
% "phi = (theta'-theta)" is a change in angle.
if angle_range <= 0
% Bracket whole ellipse with both edges at first proposed point
phi = rand*2*pi;
phi_min = phi - 2*pi;
phi_max = phi;
else
% Randomly center bracket on current point
phi_min = -angle_range*rand;
phi_max = phi_min + angle_range;
phi = rand*(phi_max - phi_min) + phi_min;
end
% Slice sampling loop
while true
% Compute xx for proposed angle difference and check if it's on the slice
xx_prop = xx*cos(phi) + nu*sin(phi);
cur_log_like = log_like_fn(xx_prop, varargin{:});
if cur_log_like > hh
% New point is on slice, ** EXIT LOOP **
break;
end
% Shrink slice to rejected point
if phi > 0
phi_max = phi;
elseif phi < 0
phi_min = phi;
else
error('BUG DETECTED: Shrunk to current position and still not acceptable.');
end
% Propose new angle difference
phi = rand*(phi_max - phi_min) + phi_min;
end
xx = xx_prop;
}
