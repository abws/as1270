package research.algorithms;

import java.util.*;

import research.problems.ProblemGA;

public class GeneticAlgorithm {
    private ProblemGA problem;
    private final int POP_SIZE = 30;
    private final double MUT_RATE = 0.1;    
    private final double CROSSOVER_RATE = 0.9;
    private final int GENERATIONS = 100;
    private int TURBINES;;
    private int INDIV_LENGTH;
    private double[] lastFitness; //the fitness of the most recent population

    //private static double Math.random() = Math.random();

    public double run(ProblemGA problem) {
        this.problem = problem;
        INDIV_LENGTH = problem.getStringLength();
        TURBINES = problem.getScenario().nturbines;

        ArrayList<String> population = problem.getRandomPopulation(POP_SIZE, INDIV_LENGTH, TURBINES);

        for (int i = 0; i < GENERATIONS; i++) {
            ArrayList<Double> weights = calculateWeights(population);
            population = reproduce(population, weights);
        }

        return maxFitness(population);
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
        ArrayList<Double> weights = new ArrayList<>();
        lastFitness = new double[population.size()];
        //Get total sum and store fitnesses
        for (String individual : population) { 
            double fitness = problem.evaluate(individual);
            sum += fitness;
            weights.add(fitness);
            lastFitness[-1] = fitness;
        }

        //Get individual weights
        for (int i = 0; i < weights.size(); i++) { 
            weights.set(i, weights.get(i) / sum);
        }

        return weights;
    }

    /**
     * Function for managing last
     * three operations:
     * Parent selection, crossover and mutation
     * @param population
     * @param weights
     * @return offSpring
     */
    private ArrayList<String> reproduce(ArrayList<String> population, ArrayList<Double> weights) {
        //parent selection
        ArrayList<String> matingPool = rouletteSelection(population, weights, POP_SIZE);
 

        //crossover
        ArrayList<String> offSpring = new ArrayList<>();
        while (offSpring.size() < POP_SIZE) {

            //Select a random pair of parents
            Random r = new Random();
            String parent1; String parent2;
            parent1 = matingPool.get(r.nextInt(POP_SIZE)); parent2 = matingPool.get(r.nextInt(POP_SIZE));

            //Crossover at rate 0.7
            if (Math.random() < CROSSOVER_RATE) {
                offSpring.addAll(onePointCrossover(parent1, parent2));
            }
        }

        //mutation
        for (int i = 0; i < offSpring.size(); i++) {
            offSpring.set(i, bitMutation(offSpring.get(i)));    //mutate each individual in the offspring array
        }

        offSpring = legalise(offSpring);

        return offSpring;
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
    public ArrayList<String> rouletteSelection(ArrayList<String> population, ArrayList<Double> weights, int populationSize) {
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
     * Recombination operator (2 parents)
     * (2 children)
     * @param parent1
     * @param parent2
     * @return
     */
    public ArrayList<String> onePointCrossover(String parent1, String parent2) {
        Random r = new Random();
        ArrayList<String> offSpring = new ArrayList<String>();

        int crossoverPoint = r.nextInt(INDIV_LENGTH - 1) + 1;    //-1 so the random number has 3 positions to take (including 0). +1 as returning 0 would make us cut the string at the very start (so wont cut). The number refers to the position we cut at before 
        String child1 = parent1.substring(0, crossoverPoint) + parent2.substring(crossoverPoint);
        String child2 = parent2.substring(0, crossoverPoint) + parent1.substring(crossoverPoint);

        offSpring.addAll(Arrays.asList(child1, child2));

        return offSpring;
    }

    /**
     * Recombination operator (2 parents)
     * (2 children)
     * @param parent1
     * @param parent2
     * @return
     */
    public ArrayList<String> nPointCrossover(String parent1, String parent2, int n) {
        Random r = new Random();
        n = Math.max(n, INDIV_LENGTH); //make sure n never overflows
        ArrayList<Integer> crossoverPoints = new ArrayList<>();
        ArrayList<String> offSpring = new ArrayList<String>();

        StringBuilder child1 = new StringBuilder(parent1);
        StringBuilder child2 = new StringBuilder(parent2);

        while (crossoverPoints.size() < n) {
            int cp = r.nextInt(INDIV_LENGTH - 1) + 1;
            if (!crossoverPoints.contains(cp)) crossoverPoints.add(cp);
        }

        Collections.sort(crossoverPoints);
        int x = 0;
        for (int point : crossoverPoints) {
            child1.replace(x, point, child2.substring(x, n));
            child2.replace(x, point, child1.substring(x, n));
            x = point;
        }
        offSpring.addAll(Arrays.asList(child1.toString(), child2.toString()));
        return offSpring;
    }

    /**
     * Mutation operator
     * @param parent1
     * @param parent2
     * @return
     */
    private String bitMutation(String ind) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < INDIV_LENGTH; i++) {
            if (Math.random() < MUT_RATE) {
                char bit = (ind.charAt(i) == '1') ? '0' : '1'; //flips the bit
                sb.append(bit);
            }
            else sb.append(ind.charAt(i));  //else add the bit as it is
        }

        return sb.toString();
    }

    /**
     * Calculautes the maximum fitness of
     * a population. Becareful! It should only
     * be used at the end of the algorithm, and even
     * then we can calulate 
     * @param population
     * @return
     */
    private double maxFitness(ArrayList<String> population) {
        double maxFitness = 0.0;
        for (String n : population) {
            double fitness = problem.evaluate(n);
            if (fitness > maxFitness) maxFitness = fitness;
        }

        return maxFitness;

    }

    /**
     * Repair operator.
     * Shoots randomly at the farm
     * and eliminates as many turbines as specified
     * @param pop
     * @return
     */
    private ArrayList<String> legalise(ArrayList<String> pop) {
        Random r = new Random();
        ArrayList<String> cleanPop = new ArrayList<>();

        for (int i = 0; i < pop.size(); i++) {
            String indiv = pop.get(i);
            StringBuilder sb = new StringBuilder(indiv);

            int turbineCount = countTurbines(indiv);
            int difference = turbineCount - TURBINES;
            
            while (difference > 0) {    //we have too many turbines
                int position = r.nextInt(INDIV_LENGTH); //position to remove turbine from
                Character c = sb.charAt(position);
                if (c == '1') {
                    sb.setCharAt(position, '0');
                    
                    difference--;
                }
            }
            while (difference < 0) {    //we have too few turbines
                int position = r.nextInt(INDIV_LENGTH); //position to add turbine to
                Character c = sb.charAt(position);
                if (c == '0') {
                    sb.setCharAt(position, '1');
                    difference++;
                }
            }
            cleanPop.add(sb.toString());
        }
        return cleanPop;
    }

    /**
     * Counts the number of
     * turbines in a state
     * @param ind
     * @return
     */
    private int countTurbines(String ind) {
        int count = 0;
        for (int i = 0; i < ind.length(); i++) {
            if (ind.charAt(i) == '1') count++;
        }
        return count;
    }

    /**
     * Performs a sigma scale
     * on a given fitness
     * @param fitnesses Array of fitnesses
     * @return
     */
    private double sigmaScale(double[] fitnesses) {
        double mean = calculateMean(fitnesses);
        double sd = calculateStandardDeviation(fitnesses, mean); 
        
        int count = 0;
        for (int i = 0; i < fitnesses.length; i++) {
            if (ind.charAt(i) == '1') count++;
        }
        return count;
    }

    private double calculateMean(double[] fitnesses) {
        double sum = 0.0;
        for (double f : fitnesses) {
            sum+=f;
        }
    return sum / fitnesses.length;
    }

    private double calculateStandardDeviation(double[] fitnesses, double mean) {
        double sd = 0.0;
        for (double f : fitnesses) {
            sd += Math.pow((f - mean), 2);
        }

        return sd / fitnesses.length;
    }
}