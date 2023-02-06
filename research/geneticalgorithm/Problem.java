package research.geneticalgorithm;

import java.util.*;

import research.api.java.*;

/**
 * Problem formulation for optimizing a wind farm
 * using the evaluation function provided by WindFLO.
 * Defines the nature and constraints of the problem
 * Contains any intitialization parameters and any 
 * extra things needed by the Simple Genetic Algorithm
 * @author Abdiwahab Salah
 * @version 05.02.23
 */
public class Problem {
    private KusiakLayoutEvaluator evaluator;
    private WindScenario scenario;

    public int INDIV_LENGTH;
    public int N_TURBINES;
    public int POP_SIZE;
    public int GENERATIONS;


    public Problem(KusiakLayoutEvaluator evaluator, WindScenario scenario, int populationSize, int generations) throws Exception {
        this.scenario = scenario;
        this.evaluator = evaluator;
        INDIV_LENGTH = this.getStringLength();
        N_TURBINES = scenario.nturbines;
        POP_SIZE = populationSize;
        GENERATIONS = generations;
    }

    public double evaluate(Object individual) {
        double[][] phenotype = decode(individual);
        double fitness = evaluator.evaluate_2014(phenotype);

        return fitness;
    }

    /**
     * Decodes a string of size h*w with n 1s
     * into a n by 2 array corresponding to turbine 
     * coordinates
     * @param individual The string to be decoded
     * @return A two-dimensional array representing the grid as turbine coordinates
     * Tested
     */
    public double[][] decode(Object individual) {
        double minDist = 8 * scenario.R;

        int columns = (int) (scenario.width / minDist);
        int rows = (int) (scenario.height / minDist);

        int[][] gridIndividual = gridify((String) individual, columns, rows);

        double[][] layout = new double [scenario.nturbines][2];
        int count = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (gridIndividual[i][j] == 1) {
                    layout[count] = getCoordinates(i, j, minDist);
                    count++;
                }
            }
        }
        return layout;
    }

    /**
     * Helper method to return
     * turbine coordinates given 
     * cell they're in within grid
     * @param y
     * @param x
     * @param minDist
     * @return Coordinates (x, y)
     * Tested
     */
    private double[] getCoordinates(int y, int x, double minDist) {
        double [] coordinates = new double[2];
        coordinates[0] = ((x * minDist) + (minDist / 2));
        coordinates[1] = ((y * minDist) + (minDist / 2));

        return coordinates;
    }

    /**
     * Gridifies a string
     * @param ind The string to be gridified
     * @param x The width of the grid
     * @param y The height of the grid
     * @return A two-dimensional array representing the grid
     * Tested
     */
    public int[][] gridify(String ind, int x, int y) {
        int[][] grid = new int[y][x]; //[rows][columns] since rows are 'bigger' and classified by first
        int count = 0;

        for (int i = 0; i < y; i ++) {
            for (int j = 0; j < x; j++) {
                grid[i][j] = Character.getNumericValue(ind.charAt(count));
                count++;
            }
        }

        return grid;
    }

    /**
     * Generates a random string population
     * @param popSize
     * @param bits
     * @return Random population
     * Tested
     */
    public ArrayList<String> getRandomPopulation(int popSize, int bits) {
        Random rand = new Random();
        ArrayList<String> population = new ArrayList<String>();

        for (int i = 0; i < popSize; i++) {
            StringBuilder sb = new StringBuilder();

            for (int j = 0; j < bits; j++) {
                char bit = (char) ((rand.nextBoolean()) ? '1': '0');   
                sb.append(bit);
            }
            population.add(sb.toString());
        }

        return population;
    }
    
    /**
     * Generates a random string population
     * with a given number of 1s (turbines)
     * @param popSize
     * @param bits
     * @return Random population
     * Tested
     */
    public ArrayList<String> getRandomPopulation(int popSize, int bits, int turbines) {
        Random rand = new Random();
        ArrayList<String> population = new ArrayList<String>();

        for (int i = 0; i < popSize; i++) {
            int[] indivArray = new int[bits];
            ArrayList<Integer> taken = new ArrayList<>();

            while (taken.size() < turbines) {
                int randPosition = rand.nextInt(0, bits);
                if (!taken.contains(randPosition)) {
                    indivArray[randPosition] = 1;
                    taken.add(randPosition);
                }
                indivArray[randPosition] = 1;
            }

            String individual = createString(indivArray);
            population.add(individual);
        }
        return population;
    }  
    
    public WindScenario getScenario() {
        return this.scenario;
    }

    public int getStringLength() {
        double minDist = 8 * scenario.R;

        int columns = (int) (scenario.width / minDist);
        int rows = (int) (scenario.height / minDist);
        return columns * rows;
    }

    private String createString(int[] indivArray) {
        StringBuilder sb = new StringBuilder();
        for(int i : indivArray) {
            sb.append(i); 
        }
        return sb.toString();
    }

    public double calculateMean(double[] fitnesses) {
        double sum = 0.0;
        for (double f : fitnesses) {
            sum+=f;
        }
    return sum / fitnesses.length;
    }

    /**
     * Calculate the standard deviation 
     * of fitnesses in a population
     * @param fitnesses
     * @param mean
     * @return Standard deviation
     */
    public double calculateStandardDeviation(double[] fitnesses, double mean) {
        double sd = 0.0;
        for (double f : fitnesses) {
            sd += Math.pow((f - mean), 2);
        }
        sd = Math.sqrt((sd / (fitnesses.length - 1)));
        return sd;
    }

        /**
     * Repair operator.
     * Shoots randomly at the farm
     * and eliminates as many turbines as specified
     * @param pop
     * @return
     */
    public ArrayList<String> legalise(ArrayList<String> pop) {
        Random r = new Random();
        ArrayList<String> cleanPop = new ArrayList<>();

        for (int i = 0; i < pop.size(); i++) {
            String indiv = pop.get(i);
            StringBuilder sb = new StringBuilder(indiv);

            int turbineCount = countTurbines(indiv);
            int difference = turbineCount - N_TURBINES;
            
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
    public int countTurbines(String ind) {
        int count = 0;
        for (int i = 0; i < ind.length(); i++) {
            if (ind.charAt(i) == '1') count++;
        }
        return count;
    }
}
