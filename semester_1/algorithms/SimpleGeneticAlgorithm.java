package semester_1.algorithms;

import java.util.ArrayList;
import semester_1.individuals.Individual;
import semester_1.problems.ProblemConstrainedQuadratic;

/**
 * Simple Genetic Algorithm
 * Uses roulette selection and one point crossover,
 * and a random mutation rate 0.25.
 * Population size is set at 4, and crossover happens at rate 0.7
 * @author Abdiwahab Salah
 * @version 05.01.23
 */
public class SimpleGeneticAlgorithm {
    private static final int POP_SIZE = 4;
    private static final double MUT_RATE = 1 / POP_SIZE;
    privatestatic  final double CROSSOVER_RATE = 0.7;

    public static int run(ProblemConstrainedQuadratic problem) {
        ArrayList<Individual> population = problem.getRandomPopulation(4, 4); //fitness is calculated with the birth of an individual
        ArrayList<Integer> weights = calculateWeights(population);
        ArrayList<Individual> offsprings = new ArrayList<>();
        ArrayList<Individual> matingPool = reproduce(population, weights, POP_SIZE);

        newPopulation = matingPool.onePointCrossOver(POP_SIZE / 2);

        for (Individual i : population) {

        }

        return 0;
    }

    private int reproduce(ArrayList<Individual> population, ArrayList<Integer> weights, int populationSize) {
        ArrayList<Individual> matingPool = rouletteSelection(population, weights, POP_SIZE);
        onePointCrossover(matingPool, CROSSOVER_RATE);


    }


    private ArrayList<Individual> rouletteSelection(ArrayList<Individual> population, ArrayList<Integer> weights, int populationSize) {
        double randomSpin = Math.random();  //This generated a random number from 0-1 (similar to a random spin)
        double cumulativeWeight = 0;        //Represents the starting position of the roulette wheel

        ArrayList<Individual> matingPool = new ArrayList<>();

        for (int n = 0; n < populationSize; n++) {
            for (int i = 0; i < weights.size(); i++) {  //Stack up each weight. The space it takes up from 0-1 corresponds to how large it is (i.e. its weight)
                cumulativeWeight += weights.get(i);

                if (randomSpin <= cumulativeWeight) {
                    matingPool.add(population.get(i));
                }
            }
        }

        return matingPool;
    }

    private int onePointCrossOver(){}

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