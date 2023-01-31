package research.algorithms;

import java.util.*;

import research.problems.ProblemSGA;

public class SimpleGeneticAlgorithm {
    private ProblemSGA problem;
    private final int POP_SIZE = 100;
    private final double MUT_RATE = 0.03;    
    private final double CROSSOVER_RATE = 0.7;
    private final int GENERATIONS = 10;
    private int TURBINES;;
    private int INDIV_LENGTH;
    //private static double Math.random() = Math.random();

    public double run(ProblemSGA problem) {
        this.problem = problem;
        INDIV_LENGTH = problem.getStringLength();
        TURBINES = problem.getScenario().nturbines;

        ArrayList<String> population = problem.getRandomPopulation(POP_SIZE, INDIV_LENGTH, TURBINES);

        for (int i = 0; i < GENERATIONS; i++) {
            // for (int j = 0; j < POP_SIZE; j++) {
            //     System.out.println(countTurbines(population.get(j)));
            // }

            //System.out.println(maxFitness(population));

            //System.out.println(countTurbines(population.get(0)));

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
        System.out.println(sum/POP_SIZE);

        return weights;
    }

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

    public ArrayList<String> onePointCrossover(String parent1, String parent2) {
        Random r = new Random();
        ArrayList<String> offSpring = new ArrayList<String>();

        int crossoverPoint = r.nextInt(INDIV_LENGTH - 1) + 1;    //-1 so the random number has 3 positions to take (including 0). +1 as returning 0 would make us cut the string at the very start (so wont cut). The number refers to the position we cut at before 
        String child1 = parent1.substring(0, crossoverPoint) + parent2.substring(crossoverPoint);
        String child2 = parent2.substring(0, crossoverPoint) + parent1.substring(crossoverPoint);
        //System.out.println(Arrays.deepToString(problem.decode(parent1)));

        //System.out.printf("%d %d %d %d%n", countTurbines(parent1), countTurbines(parent2), countTurbines(child1), countTurbines(child2));
        offSpring.addAll(Arrays.asList(child1, child2));

        return offSpring;
    }

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

    private double maxFitness(ArrayList<String> population) {
        double maxFitness = 0.0;
        for (String n : population) {
            double fitness = problem.evaluate(n);
            if (fitness > maxFitness) maxFitness = fitness;
        }

        return maxFitness;

    }

    private ArrayList<String> legalise(ArrayList<String> pop) {
        Random r = new Random();
        ArrayList<String> cleanPop = new ArrayList<>();

        for (int i = 0; i < pop.size(); i++) {
            String indiv = pop.get(i);
            StringBuilder sb = new StringBuilder(indiv);

            int turbineCount = countTurbines(indiv);
            //System.out.println(turbineCount);
            int difference = turbineCount - TURBINES;
            
            while (difference > 0) {    //we have too many turbines
                int position = r.nextInt(INDIV_LENGTH); //position to remove turbine from
                Character c = sb.charAt(position);
                if (c == '1') {
                    sb.setCharAt(position, '0');
                    
                    difference--;
                    //System.out.println(difference);
                    //System.out.println(countTurbines(sb.toString()));

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
            //System.out.printf("new: %d", countTurbines(sb.toString()));
            //System.out.printf("diff1: %d,,,, diff2: %d%n", countTurbines(sb.toString()), difference);
            cleanPop.add(sb.toString());
        }
        return cleanPop;
    }

    private int countTurbines(String ind) {
        int count = 0;
        for (int i = 0; i < ind.length(); i++) {
            if (ind.charAt(i) == '1') count++;
        }
        return count;
    }
}