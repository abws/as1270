package research.testty;

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
        Mutation mutation = new Mutation(scalingFactor, problem);
        Recombination recombination = new Recombination(crossoverRate, problem);
        Replacement replacement = new Replacement(problem);


        List<double[]> population = problem.initialisePopulation(popSize);
        
        while (iteration < maxIterations) {
            System.out.println(Problem.max);
            List<double[]> mutants= mutation.differentialMutation(population);
            List<double[]> trials = recombination.binomialCrossover(population, mutants);

            population = replacement.selectBest(population, trials); //offspring
        }

    }



    
}
