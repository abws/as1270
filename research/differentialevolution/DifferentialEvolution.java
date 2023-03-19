package research.differentialevolution;

import java.util.List;

public class DifferentialEvolution {
    int popSize;      //Size of population
    double crossoverRate;
    int maxIterations;  //Maximum number of iterations
    double scalingFactor; //Scaling factor


    Problem problem;    

    DifferentialEvolution(int popSize, double crossoverRate, int maxIterations, double scalingFactor, Problem problem) {
        this.popSize = popSize;
        this.crossoverRate = crossoverRate;
        this.maxIterations = maxIterations;
        this.scalingFactor = scalingFactor;
        this.problem = problem;
    }

    public void run() {
        int iteration = 0;
        Mutation mutation = new Mutation(scalingFactor);
        List<Vector> population = problem.initialisePopulation(popSize);
        
        while (iteration < maxIterations) {
            List<Vector> mutationVector = mutation.differentialMutation(population);
            List<Vector> trial = crossover.binomialCrossover(population, mutationVector, crossoverRate);

            population = replacement.selectBest(population, trial); //offspring
        }



    }




    /**
     * Calculates the difference
     * between two vectors
     * @param vectorA the vector to point towards
     * @param vectorB from this vector
     */
    public double[] vectorDifferential(double[] vectorA, double[] vectorB) {
        double[] difference = new double[vectorA.length];
        for (int i = 0; i < vectorA.length; i++) {
            difference[i] = vectorA[i] - vectorB[i];
        }
        return difference;
    }
    
}
