package research.algorithms;

import java.util.ArrayList;

import research.problems.ProblemSGA;

public class SimpleGeneticAlgorithm {
    private ProblemSGA problem;
    private final int POP_SIZE = 4;
    private final double MUT_RATE = 0.03;    
    private final double CROSSOVER_RATE = 0.7;
    private final int GENERATIONS = 50;
    private int INDIV_LENGTH;

    public double run(ProblemSGA problem) {
        this.problem = problem;

        INDIV_LENGTH = problem.getStringLength();
        ArrayList<String> population = problem.getRandomPopulation(POP_SIZE, INDIV_LENGTH);

        for (int i = 0; i < GENERATIONS; i++) {
            ArrayList<Double> weights = calculateWeights(population);
            population = reproduce(population, weights, POP_SIZE);
        }

        return population;
    }

    /**
     * Calculates the weight of 
     * the given population.
     * Individual and weight have the same index
     * @param population
     * @return
     */

    public ArrayList<Double> calculateWeights(ArrayList<String> population, ProblemSGA problem) {
        double sum = 0;
        ArrayList<Double> weights = new ArrayList<>();

        //Get total sum and store fitnesses
        for (String individual : population) { 
            double fitness = problem.evaluate(individual);
            //System.out.println(fitness);
            sum += fitness;
            weights.add(fitness);
        }

        //Get individual weights
        for (int i = 0; i < weights.size(); i++) { 
            weights.set(i, weights.get(i) / sum);
        }

        return weights;
    }
}