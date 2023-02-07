package research.geneticalgorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

/**
 * Responsible for providing parent selection
 * mechanisms for the genetic algorithm.
 * Contains several 'high level' methods
 * that utlise other lower level, general 
 * purpose algorithms (e.g. roulette selection)
 * @author Abdiwahab Salah
 * @version 05/02/22
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


        
 
    /**
     * Calculates the weight of 
     * the given population.
     * Individual and weight have the same index
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

    /**
     * Parent selection mechanism
     * For the WFLOP, this provides
     * very little selection pressure
     * as the fitnesses are so close to one another,
     * thus we explore a lot, and exploit very little
     * as selection becomes uniform and random
     * @param population
     * @param weights
     * @param populationSize
     * @return
     */
    public ArrayList<String> rouletteWheel(ArrayList<String> population, ArrayList<Double> weights, int populationSize) {
        ArrayList<String> matingPool = new ArrayList<>();

        while (matingPool.size() < populationSize) {  //Repeat so we select n individuals
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
