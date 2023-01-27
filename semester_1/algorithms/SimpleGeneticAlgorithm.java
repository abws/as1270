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
    private final int POP_SIZE = 4;
    private final double MUT_RATE = 0.03;    
    private final double CROSSOVER_RATE = 0.7;
    private final int GENERATIONS = 50;

    public ArrayList<Individual> run(ProblemConstrainedQuadratic problem) {
        ArrayList<Individual> population = problem.getRandomPopulation(POP_SIZE, 20);   //fitness is calculated with the birth of an individual

        for (int i = 0; i < GENERATIONS; i++) {
            System.out.printf("generation %d: %s", i, population);
            ArrayList<Double> weights = calculateWeights(population);
            System.out.println(weights);
            population = reproduce(population, weights, POP_SIZE);

        }

        return population;
    }

    private ArrayList<Individual> reproduce(ArrayList<Individual> population, ArrayList<Double> weights, int populationSize) {
        ArrayList<Individual> matingPool = rouletteSelection(population, weights, POP_SIZE);
        ArrayList<Individual> offSpring = onePointCrossover(matingPool, CROSSOVER_RATE, POP_SIZE);

        for (int i = 0; i < offSpring.size(); i++) {
            offSpring.set(i, bitMutation(offSpring.get(i)));
        }

        return offSpring;
    }

    public ArrayList<Individual> rouletteSelection(ArrayList<Individual> population, ArrayList<Double> weights, int populationSize) {
        ArrayList<Individual> matingPool = new ArrayList<>();

        for (int n = 0; n < populationSize; n++) {  //Repeat so we select n individuals
            double randomSpin = Math.random();  //This generated a random number from 0-1 (similar to a random spin)
            double cumulativeWeight = 0;        //Represents the starting position of the roulette wheel

            for (int i = 0; i < weights.size(); i++) {  //Stack up each weight. The space it takes up from 0-1 corresponds to how large it is (i.e. its weight)
                cumulativeWeight += weights.get(i);
                if (randomSpin <= cumulativeWeight) {
                    matingPool.add(population.get(i));
                    break;
                }
            }
        }

        return matingPool;
    }

    //modularise this function - should really only be concerned with everything in the if statement (reproduce() is the 'manager function that calls and takes care of 'meta' tasks')
    public ArrayList<Individual> onePointCrossover(ArrayList<Individual> matingPool, double crossoverRate, int popSize) {
        ArrayList<Individual> offSpring = new ArrayList<>();
        while (offSpring.size() < popSize) {

            //Select a random pair of parents
            Random r = new Random();

            Individual parent1; Individual parent2; 
            parent1 = matingPool.get(r.nextInt(popSize)); parent2 = matingPool.get(r.nextInt(popSize));

            //Crossover at rate 0.7
            if (Math.random() < crossoverRate) {
                int crossoverPoint = r.nextInt(parent1.INDIVIDUAL.length() - 1) + 1;    //-1 so the random number has 3 positions to take (including 0). +1 as returning 0 would make us cut the string at the very start (so wont cut). The number refers to the position we cut at before it
                Individual child1 = new Individual(parent1.INDIVIDUAL.substring(0, crossoverPoint) + parent2.INDIVIDUAL.substring(crossoverPoint));
                Individual child2 = new Individual(parent2.INDIVIDUAL.substring(0, crossoverPoint) + parent1.INDIVIDUAL.substring(crossoverPoint));


                //System.out.printf("parent 1: %s, parent 2: %s, child1: %s, child2: %s crossoverpoint:%d%n", parent1, parent2, child1, child2, crossoverPoint);
                //System.out.printf("child1: %s, child1: %d, child2: %s, child2: %d%n", child1.INDIVIDUAL, child1.VALUE, child2.INDIVIDUAL, child2.VALUE);

                offSpring.addAll(Arrays.asList(child1, child2));
            }
        }
        return offSpring;
    }

    private Individual bitMutation(Individual ind) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < ind.INDIVIDUAL.length(); i++) {
            if (Math.random() < MUT_RATE) {
                //sb.append(Integer.toString(~(Character.getNumericValue(ind.INDIVIDUAL.charAt(i)))));
                char bit = (ind.INDIVIDUAL.charAt(i) == '1') ? '0' : '1';
                sb.append(bit);
            }
            else sb.append(ind.INDIVIDUAL.charAt(i));
        }

        Individual i = new Individual(sb.toString());
        return i;
    }

    public ArrayList<Double> calculateWeights(ArrayList<Individual> population) {
        double sum = 0;
        ArrayList<Double> weights = new ArrayList<>();
        //Get total sum
        for (Individual individual : population) { 
            //System.out.printf("code: %s, value: %d%n", individual.INDIVIDUAL, individual.VALUE);
            sum += individual.VALUE;
        }

        //Get individual weights
        for (Individual individual : population) { 
            weights.add(individual.VALUE / sum);
        }
        return weights;
    }
}