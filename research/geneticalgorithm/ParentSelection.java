package research.geneticalgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Responsible for providing parent selection
 * mechanisms for the genetic algorithm.
 * Contains several 'high level' methods
 * that utlise other lower level, general 
 * purpose algorithms (e.g. roulette selection)
 * @author Abdiwahab Salah
 * @version 07/02/22
 */
public class ParentSelection {    
    private final Problem problem;

    ParentSelection(Problem problem) { //add so user can change using input
        this.problem = problem;
    }

    public List<Individual> fitnessProportionalSelection(List<Individual> population, int populationSize) {
        List<Double> weights = calculateWeights(population);
        List<Individual> matingPool = rouletteWheel(population, weights, populationSize);

        return matingPool;
    }

    public List<Individual> fitnessProportionalSigmaSelection(List<Individual> population, int populationSize, double sigmaConstant) {
        List<Double> weights = calculateSigmaWeights(population, sigmaConstant);
        List<Individual> matingPool = rouletteWheel(population, weights, populationSize);

        return matingPool;
    }

    public List<Individual> linearRankingSelection(List<Individual> population, int populationSize, double constant) {
        Object[] pair =  calculateLinearRankedWeights(population, constant);
        List<Double> weights =  (ArrayList<Double>)  pair[1];
        population =  (ArrayList<Individual>)  pair[0];

        List<Individual> matingPool = rouletteWheel(population, weights, populationSize);

        return matingPool;
    }

    /**
     * 
     * Without replacement has less vaiance
     * @param population
     * @param popSize
     * @param k
     * @param withoutReplacement
     * @return
     */
    public List<Individual> tournamentSelection(List<Individual> population, int popSize, int k, boolean withoutReplacement) {
        List<Individual> matingPool = new ArrayList<>();
        List<Integer> indexes;
        List<Individual> candidates;
        Random r = new Random();
        if (withoutReplacement) Math.min(k, popSize); //limit k value or we'll get stuck in foreverness

        while (matingPool.size() < popSize) {
            candidates = new ArrayList<>(); //thanks for garbage collection mr Jaavapius
            indexes = new ArrayList<>();
            while (candidates.size() < k) { //pick k random individuals
                int index = r.nextInt(population.size());
                if (withoutReplacement && indexes.contains(index)) {continue;}    //skip index if its already contained. here k cannot be greater than popsize

                indexes.add(index);
                candidates.add(population.get(index));
            }
            //after we pick candidates, start tournament
            matingPool.add(tournament(candidates));
        }

        return matingPool;
    }



        
 
    /**
     * Calculates the weight of 
     * the given population.
     * Individual and weight have the same index
     * For the WFLOP, this provides
     * very little selection pressure
     * as the fitnesses are so close to one another,
     * thus we explore a lot, and exploit very little
     * as selection becomes uniform and random
     * @param population
     * @returns An array of weights
     * Tested
     */

    public List<Double> calculateWeights(List<Individual> population) {
        double sum = 0;
        double fitness;
        List<Double> weights = new ArrayList<>(population.size());

        //Get total sum and store fitnesses
        for (Individual i : population) { 
            fitness = i.getFitness();

            weights.add(fitness);
            sum += fitness;
        }

        //Get individual weights
        for (int i = 0; i < weights.size(); i++) { 
            weights.set(i, weights.get(i) / sum);
        }

        return weights;
    }

    /**
     * Calculates the weights after
     * applying sigma scaling
     * @param population
     * @param c Sigma constant
     * @return
     */
    public List<Double> calculateSigmaWeights(List<Individual> population, double c) {
        double sum = 0;
        List<Double> weights = new ArrayList<>();
        double[] fitnesses = problem.getFitnesses(population);

        fitnesses = sigmaScale(fitnesses, c);  //Apply sigma scaling

        for (double f : fitnesses) {    //get sum
            sum += f;                                     
            weights.add(f);
        }

        //Get individual weights
        for (int i = 0; i < weights.size(); i++) { 
            weights.set(i, weights.get(i) / sum); //set that position with whatever thats in it over the sum
        }
        return weights;
    }

    /**
     * Calculates weights based
     * on population fitness ranking
     * @param population
     * @param c
     * @return
     */
    public Object[] calculateLinearRankedWeights(List<Individual> population, double c) {
        List<Double> weights = new ArrayList<>();
        int popSize = population.size();

        population = population.stream().sorted(Comparator.comparingDouble(individual -> individual.getFitness())).collect(Collectors.toList());    //sort arraylist by fitness

        for (int i = 0; i < population.size(); i++ ) {
            double weight = (((2 * i * (c - 1)) / (double) (popSize * (popSize - 1)))); // formula for calulating selection probability using linear selection. note: weights will never change. theyre based on ranking and C
            weights.add(weight);
        }
        return new Object[] {population, weights};
    }

    private Individual tournament(List<Individual> candidates) {
        candidates = candidates.stream().sorted(Comparator.comparingDouble(individual -> individual.getFitness())).collect(Collectors.toList());    //sort candidates by fitness
        // double[] p = problem.getFitnesses(candidates);
        // System.out.println(Arrays.toString(p));
        Individual best = candidates.get(candidates.size() - 1);
        // System.out.println(best.getFitness());

        return best;
    }

    /**
     * Parent selection mechanism
     * that represents a one arm wheel
     * being spun n times. The paritions of 
     * the wheel represent the individuals' selection
     * probability
     * @param population
     * @param weights
     * @param populationSize
     * @return
     */
    public List<Individual> rouletteWheel(List<Individual> population, List<Double> weights, int n) {
        List<Individual> matingPool = new ArrayList<>();

        while (matingPool.size() < n) {  //Repeat so we select n individuals
            double randomSpin = Math.random();  //This generated a random number from 0-1 (similar to a random spin)
            double cumulativeWeight = 0;   //Represents the starting position of the roulette wheel

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

    /**
     * A better alternative to
     * the roulette selection algorithm.
     * Represents spinning n equally spaced
     * arms at once, once.
     * Computationally more efficient
     * and has less variance than roulette
     * @param population
     * @param weights
     * @param n
     * @return
     */
    public List<Individual> stochasticUniversalSample(List<Individual> population, List<Double> weights, int n) { //n is population size
        List<Individual> matingPool = new ArrayList<>();
        double cumulativeWeight = weights.get(0);   //Represents the starting position of the roulette wheel
        int index = 0;

        Random rand = new Random();
        double randomSpin = rand.nextDouble(1 / (double) n); //uniformly random number between 0 and 1/n, once
        double[] pointers = new double[n];
        IntStream.range(0, pointers.length).forEach(i -> pointers[i] = (i * 1/n) + randomSpin); //fil array with arrow/pointer positions, and (mini)spin them randomly

        while (matingPool.size() < n) {  //Repeat so we select n individuals
            for (int i = 0; i < pointers.length; i++) {  //Stack up each weight. The space it takes up from 0-1 corresponds to how large it is (i.e. its weight)
                if (pointers[i] <= cumulativeWeight) matingPool.add(population.get(index));

                else {
                    index++;
                    cumulativeWeight += weights.get(index);
                }
            }
        }

        return matingPool;
    }


    /**
     * Performs a sigma scale
     * on an array of fitnesses
     * @param fitnesses Array of fitnesses
     * @return
     */
    private double[] sigmaScale(double[] fitnesses, double c) {
        double mean = problem.calculateMean(fitnesses);
        double sd = problem.calculateStandardDeviation(fitnesses, mean); 
        
        for (int i = 0; i < fitnesses.length; i++) {
            double sigma = fitnesses[i] - (mean - (c*sd)); //sigma scaling formula
            fitnesses[i] = Math.max(sigma, 0);
        }
        return fitnesses;
    }

}
