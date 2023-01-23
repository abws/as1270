package semester_1.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
    private static final double MUT_RATE = 1.0 / POP_SIZE;    
    private static  final double CROSSOVER_RATE = 0.7;

    public static int run(ProblemConstrainedQuadratic problem) {
        ArrayList<Individual> population = problem.getRandomPopulation(4, 4); //fitness is calculated with the birth of an individual
        ArrayList<Integer> weights = calculateWeights(population);
        //ArrayList<Individual> offsprings = new ArrayList<>();
        ArrayList<Individual> offSpring = reproduce(population, weights, POP_SIZE);

        newPopulation = matingPool.onePointCrossOver(POP_SIZE / 2);

        for (Individual i : population) {

        }

        return 0;
    }

    private int reproduce(ArrayList<Individual> population, ArrayList<Integer> weights, int populationSize) {
        ArrayList<Individual> matingPool = rouletteSelection(population, weights, POP_SIZE);
        ArrayList<Individual> offSpring = onePointCrossover(matingPool, CROSSOVER_RATE, POP_SIZE);

        for (int i = 0; i < offSpring.size(); i++) {
            offSpring.set(i, bitMutation(offSpring.get(i));
        }


        return 

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

    //modularise this function - should really only be concerned with everything in the if statement (reproduce() is the 'manager function that calls and takes care of 'meta' tasks')
    private ArrayList<Individual> onePointCrossover(ArrayList<Individual> matingPool, double crossoverRate, int popSize) {
        ArrayList<Individual> offSprings = new ArrayList<>();
        while (offSprings.size() < popSize) {

            //Select a random pair of parents
            Random r = new Random();

            Individual parent1; Individual parent2; 
            parent1 = matingPool.get(r.nextInt(popSize)); parent2 = matingPool.get(r.nextInt(popSize));

            //Crossover at rate 0.7
            if (Math.random() < crossoverRate) {
                int crossoverPoint = r.nextInt(parent1.INDIVIDUAL.length() - 1) + 1;    //-1 so the random number has 5 positions to take (including 0). +1 as returning 0 would make us cut the string at the very start (so wont cut)
                Individual child1 = new Individual(parent1.INDIVIDUAL.substring(0, crossoverPoint) + parent2.INDIVIDUAL.substring(crossoverPoint + 1));
                Individual child2 = new Individual(parent2.INDIVIDUAL.substring(0, crossoverPoint) + parent1.INDIVIDUAL.substring(crossoverPoint + 1));
                offSprings.addAll(Arrays.asList(child1, child2));
            }
        }
        return offSprings;
    }

    public Individual bitMutation(Individual ind) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < ind.INDIVIDUAL.length(); i++) {
            if (Math.random() < MUT_RATE) {
                //sb.append(Integer.toString(~(Character.getNumericValue(ind.INDIVIDUAL.charAt(i)))));
                char bit = (ind.INDIVIDUAL.charAt(i) == '1') ? '0' : '1';
                sb.append(bit);
            }
            else sb.append(ind.INDIVIDUAL.charAt(i));
        }
        ind.INDIVIDUAL  = sb.toString();
        return ind;
    }

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