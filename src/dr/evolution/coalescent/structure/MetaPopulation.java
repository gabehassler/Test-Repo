package dr.evolution.coalescent.structure;
public interface MetaPopulation {
int getPopulationCount();
double[] getPopulationSizes(double time);/* returns value of demographic function at time t  (population size; one entry of double[] getPopulationSizes)
double getDemographic(double time, int population);/* calculates the integral 1/N(x) dx from start to finish, for one of the populations
double getIntegral(double start, double finish, int population);
}
