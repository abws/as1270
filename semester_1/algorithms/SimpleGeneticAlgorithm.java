package semester_1.algorithms;

import java.util.ArrayList;
import semester_1.individuals.Individual;
import semester_1.problems.ProblemConstrainedQuadratic;

/**
 * Simple Genetic Algorithm
 * Uses roulette selection and one point crossover,
 * and a random mutation rate 0.2.
 * Population size is set at 4, and crossover happens at rate 1
 * @author Abdiwahab Salah
 * @version 05.01.23
 */
public class SimpleGeneticAlgorithm {
    private final int POP_SIZE = 4;

    public static int run(ProblemConstrainedQuadratic problem) {
        ArrayList<Individual> population = problem.getRandomPopulation(4, 4); //fitness is calculated with the birth of an individual
        ArrayList<Integer> weights = calculateWeights(population);

        ArrayList<Individual> matingPool = reproduce(POP_SIZE);
        newPopulation = matingPool.onePointCrossOver(POP_SIZE / 2);

        for (Individual i : population) {

        }

        return 0;
    }

    private int rouletteSelection(){};
    private int reproduce(){};
    private int onePointCrossOver(){};

    private static ArrayList<Integer> calculateWeights(ArrayList<Individual> population) {
        int sum = 0;
        ArrayList<Integer> weights = new ArrayList<>();

        //Get total sum
        for (Individual individual : population) { 
            sum += individual.VALUE;
        }

        //Get individual weights
        for (Individual individual : population) { 
            weights.add(individual.VALUE / sum);
        }
        return weights;
    }
}