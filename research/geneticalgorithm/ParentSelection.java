package research.geneticalgorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.TreeMap;

import javax.print.attribute.HashAttributeSet;

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

    public ArrayList<String> fitnessProportionalSelection(ArrayList<String> population, int populationSize) { //here pop size is for the size of the mating pool
        ArrayList<Double> weights = calculateWeights(population);
        ArrayList<String> matingPool = rouletteWheel(population, weights, populationSize);

        return matingPool;
    }

    public ArrayList<String> fitnessProportionalSigmaSelection(ArrayList<String> population, int populationSize, double sigmaConstant) {
        ArrayList<Double> weights = calculateSigmaWeights(population, sigmaConstant);
        ArrayList<String> matingPool = rouletteWheel(population, weights, populationSize);

        return matingPool;
    }

    public ArrayList<String> linearRankingSelection(ArrayList<String> population, int populationSize, double constant) {
        Object[] pair =  calculateLinearRankedWeights(population, constant);
        ArrayList<Double> weights =  (ArrayList<Double>)  pair[1];
        population =  (ArrayList<String>)  pair[0];
        ArrayList<String> matingPool = rouletteWheel(population, weights, populationSize);

        return matingPool;
    }

    public ArrayList<String> tournamentSelection(ArrayList<String> population, int populationSize, int k) {
        ArrayList<String> matingPool = new ArrayList<>();
        Random r = new Random();
        String[] candidates = new String[k];
        int[] indexes = new int[k];
        //LinkedHashMap<Integer, String> candidates = new LinkedHashMap(k); //specifying a capacitiy increases performance
        while (matingPool.size() < populationSize) {
            for (int i = 0; i < k; i++) { //pick k random individuals
                int index = r.nextInt(population.size());
                indexes[i] = index;
                candidates[i] = population.get(index);
                //candidates.put(index, population.get(index)); //some high level thinking going on here
            }
            matingPool.add(tournament(candidates, indexes));
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

    public ArrayList<Double> calculateWeights(ArrayList<String> population) {
        double sum = 0;
        ArrayList<Double> weights = problem.populationFitness;

        //Get total sum and store fitnesses
        for (double fitness : weights) { 
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
     * @param c
     * @return
     */
    public ArrayList<Double> calculateSigmaWeights(ArrayList<String> population, double c) {
        double sum = 0;
        ArrayList<Double> weights = new ArrayList<>();
        ArrayList<Double> populationFitnesses = problem.populationFitness;
        double[] fitnesses = populationFitnesses.stream().mapToDouble(Double::doubleValue).toArray();

        fitnesses = sigmaScale(fitnesses, c);  //Apply sigma scaling

        for (double f : fitnesses) {
            sum += f;                                     
            weights.add(f);
        }

        //Get individual weights
        for (int i = 0; i < weights.size(); i++) { 
            weights.set(i, weights.get(i) / sum);
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
    public Object[] calculateLinearRankedWeights(ArrayList<String> population, double c) {
        ArrayList<Double> weights = new ArrayList<>();
        TreeMap<Double, String> fitnessMapping = new TreeMap<>();
        int popSize = problem.POP_SIZE;

        ArrayList<Double> populationFitnesses = problem.populationFitness;
        double[] fitnesses = populationFitnesses.stream().mapToDouble(Double::doubleValue).toArray(); //performance, apparently

        //Get total sum and store fitnesses
        for (int i = 0; i < population.size(); i++) { 
            fitnessMapping.put(fitnesses[i], population.get(i));
        }
        Collection<String> values = fitnessMapping.values();
        population = new ArrayList<String>(values); //ordered the same as the weight now

        for (int i = 0; i < fitnessMapping.size(); i++ ) {
            double weight = (((2 * i * (c - 1)) / (double) (popSize * (popSize - 1)))); // formula for calulating selection probability using linear selection
            weights.add(weight);
        }

        return new Object[] {population, weights};
    }

    public String tournament(String[] candidates, int[] indexes) { //integer is the individuals' position in the fitness array
        ArrayList<Double> fitnesses = problem.populationFitness;
        //candidates.values().iterator().next();
        double max = fitnesses.get(indexes[0]).doubleValue();
        String best = candidates[0];

        for (int i : indexes) {
            double current = fitnesses.get(i);
            if (current > max) {
                max = current;
                best = candidates[i];
            }
        }

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
    public ArrayList<String> rouletteWheel(ArrayList<String> population, ArrayList<Double> weights, int n) {
        ArrayList<String> matingPool = new ArrayList<>();

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
     * @param population
     * @param weights
     * @param n
     * @return
     */
    public ArrayList<String> stochasticUniversalSample(ArrayList<String> population, ArrayList<Double> weights, int n) { //n is population size
        ArrayList<String> matingPool = new ArrayList<>();
        Random rand = new Random();
        double randomSpin = rand.nextDouble(1 / (double) n);


        while (matingPool.size() < n) {  //Repeat so we select n individuals
        }

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
