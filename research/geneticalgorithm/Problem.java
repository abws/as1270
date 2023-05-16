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
 * @version 08.02.23
 */
public class Problem {
    public KusiakLayoutEvaluator evaluator;
    public WindScenario scenario;
    public int INDIV_LENGTH;
    public int N_TURBINES;
    public int POP_SIZE;

    public double minDist;
    public int columns;
    public int rows;

    public Problem(KusiakLayoutEvaluator evaluator, WindScenario scenario, int populationSize) throws Exception {
        this.scenario = scenario;
        this.evaluator = evaluator;

        //Commonly used
        N_TURBINES = scenario.nturbines;
        POP_SIZE = populationSize;
        minDist = 8 * scenario.R;
        columns = (int) (scenario.width / minDist);     //one to n
        rows = (int) (scenario.height / minDist);
        INDIV_LENGTH = this.getStringLength();
    }

    public double evaluate(String individual) {
        double[][] phenotype = decode(individual);
        // System.out.println(Arrays.deepToString(phenotype));
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
    public double[][] decode(String individual) {

        int[][] gridIndividual = gridify(individual, columns, rows);

        double[][] layout = new double [countTurbines(individual)][2];
        int count = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (gridIndividual[i][j] == 1) {
                    layout[count] = getMeshCoordinatesLR(i, j, minDist);
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
     * @tested
     */
    private double[] getCoordinates(int y, int x, double minDist) {
        double [] coordinates = new double[2];
        coordinates[0] = (x * minDist);
        coordinates[1] = (y * minDist);

        return coordinates;
    }

    private double[] getCoordinatesCenter(int y, int x, double minDist) {
        double [] coordinates = new double[2];
        coordinates[0] = ((x * minDist) + (minDist / 2));
        coordinates[1] = ((y * minDist) + (minDist / 2));

        return coordinates;
    }

    private double[] getMeshCoordinatesLR(int y, int x, double minDist) {
        int maxX = columns;
        int maxY = rows;
        double [] coordinates = new double[2];
        coordinates[0] = (x * minDist) + (y * (minDist / maxY));
        coordinates[1] = ((maxY - y) * minDist) - (x * (minDist / maxX));

        return coordinates;
    }

    private double[] getMeshCoordinatesRL(int y, int x, double minDist) {
        int maxX = columns;
        int maxY = rows;
        double [] coordinates = new double[2];
        coordinates[0] = ((maxX - x) * minDist) - (y * (minDist / maxY));
        coordinates[1] = ((maxY - y) * minDist) - (x * (minDist / maxX));

        return coordinates;
    }

    private double[] getCoordinatesY(int y, int x, double minDist) {
        double [] coordinates = new double[2];
        coordinates[0] = ((x * minDist));
        if (x % 2 == 0) {
            coordinates[1] = ((y * minDist) + (minDist / 2));
        } else {
            coordinates[1] = ((y * minDist));
        }

        return coordinates;
    }

    private double[] getCoordinatesX(int y, int x, double minDist) {
        double [] coordinates = new double[2];
        if (y % 2 == 0) {
            coordinates[0] = ((x * minDist) + (minDist / 2));
        } else {
            coordinates[0] = ((x * minDist));
        }
        coordinates[1] = y * minDist;

        return coordinates;
    }


    /**
     * Gridifies a string
     * @param ind The string to be gridified
     * @param x The width of the grid
     * @param y The height of the grid
     * @return A two-dimensional array representing the grid
     * @tested
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
     * Gridifies a string but with the knowledge
     * that we're dealing with a wind farm. Hence 
     * interpreting a string of 0s and 1s would be 
     * best if we could keep the spacial structure and
     * characteristics of the farm intact. Hence, by alternating,
     * close crowded 1s in the string are crowded in the grid.
     * @Useful for when we apply heuristics to the strings
     * @param ind The string to be gridified
     * @param x The width of the grid
     * @param y The height of the grid
     * @return A two-dimensional array representing the grid
     * NOT COMPLETE
     */
    public int[][] gridifyAlternate(String ind, int x, int y) {
        int[][] grid = new int[y][x]; //[rows][columns] since rows are 'bigger' and classified by first
        int pos = 0;

        for (int i = 0; i < y; i ++) {
            if (i % 2 == 0) 
                    for (int j = 0; j < x; j++) { //this part must go down for i % 2 == 0
                        grid[i][j] = Character.getNumericValue(ind.charAt(pos));
                        pos++;
                    }
            else
                for (int j = (x - 1); j >= 0; j--) { 
                    grid[i][j] = Character.getNumericValue(ind.charAt(pos)); //we start from the right and fill in from the left now
                    pos++;
                }
        }
        
        return grid;
    }


    /**
     * Generates a random string population
     * @param popSize
     * @param bits
     * @return Random population
     */
    public List<Individual> getRandomPopulation(int popSize, int bits) {
        Random rand = new Random();
        List<Individual> population = new ArrayList<>();

        for (int i = 0; i < popSize; i++) {
            StringBuilder indivArray = new StringBuilder();

            for (int j = 0; j < bits; j++) {
                char bit = (char) ((rand.nextBoolean()) ? '1': '0');   
                indivArray.append(bit);
            }
            Individual individual = new Individual(indivArray.toString(), this, true);

            population.add(individual);
        }

        return population;
    }
    
    /**
     * Generates a random string population
     * with a given number of 1s (turbines)
     * @param popSize
     * @param bits
     * @return Random population
     * @tested
     */
    public List<Individual> getRandomPopulation(int popSize, int bits, int turbines) {
        Random rand = new Random();
        List<Individual> population = new ArrayList<>();

        for (int i = 0; i < popSize; i++) {
            int[] indivArray = new int[bits];
            List<Integer> taken = new ArrayList<>();

            while (taken.size() < turbines) {
                int randPosition = rand.nextInt(0, bits);
                if (!taken.contains(randPosition)) {
                    indivArray[randPosition] = 1;
                    taken.add(randPosition);
                }
                indivArray[randPosition] = 1;
            }

            String value = createString(indivArray);
            Individual individual = new Individual(value, this, true);
            population.add(individual);
        }
        return population;
    }  
    
    public WindScenario getScenario() {
        return this.scenario;
    }

    /**
     * Calculates how many bits 
     * are needed to represent the wind farm
     * @return
     */
    public int getStringLength() {
        return columns * rows;
    }

    private String createString(int[] indivArray) {
        StringBuilder sb = new StringBuilder();
        for(int i : indivArray) {
            sb.append(i); 
        }
        return sb.toString();
    }

    /**
     * Calculates the mean from an array of means
     * @param fitnesses
     * @return
     */
    public double calculateMean(double[] fitnesses) {
        double sum = 0.0;
        for (double f : fitnesses) {
            sum+=f;
        }
        return sum / fitnesses.length;
    }

    /**
     * Calculate the standard deviation 
     * of an array of fitnesses
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
     * and eliminates or introduces 
     * as many turbines as needed.
     * @param pop
     * @return
     */
    public List<Individual> repairRandom(List<Individual> pop) {
        Random r = new Random();
        List<Individual> cleanPop = new ArrayList<>();

        for (int i = 0; i < pop.size(); i++) {
            Individual individual = pop.get(i);
            String value = pop.get(i).getValue();
            StringBuilder sb = new StringBuilder(value);

            int turbineCount = countTurbines(value);
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
            individual.setValue(sb.toString());
            cleanPop.add(individual);
        }
        return cleanPop;
    }

    /**
     * Counts the number of
     * turbines in a state
     * @param value 
     * @return
     */
    public int countTurbines(String value) {
        int count = 0;
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == '1') count++;
        }
        return count;
    }



    /**
     * Generates an array of
     * fitnesses by calling function.
     * Only call this for the offspring
     * list after the last operator and before
     * survivor selection.
     * Calls objective function, use only at start and 
     * in survivor selection
     * @param population
     * @return
     */
    public double[] calculateFitnesses(List<Individual> population) {
        double[] fitnesses = new double[population.size()];

        //Get total sum and store fitnesses
        for (int i = 0; i < fitnesses.length; i++) {
            Individual individual = population.get(i);
            individual.updateFitness();
            double fitness = individual.getFitness();
            fitnesses[i] = fitness;
        }

        return fitnesses;
    }


    /**
     * Generates an array of
     * fitnesses without calling function
     * @param population
     * @return
     */
    public double[] getFitnesses(List<Individual> population) {
        double[] fitnesses = new double[population.size()];

        for (int i = 0; i < population.size(); i++) {
            Individual individual = population.get(i);
            double fitness = individual.getFitness();
            fitnesses[i] = fitness;
        }

        return fitnesses;
    }

    public void updateAllFitnesses(List<Individual> population) {
        for (int i = 0; i < population.size(); i++) {
            population.get(i).updateFitness();
        }
    }

    /**
     * Generates an array of
     * fitnesses without calling function
     * @param population
     * @return
     */
    public List<Double> getFitnessesArrayList(List<Individual> population) {
        List<Double> fitnesses = new ArrayList<Double>(population.size());

        //Get total sum and store fitnesses
        for (int i = 0; i < population.size(); i++) {
            Individual individual = population.get(i);
            double fitness = individual.getFitness();
            fitnesses.add(fitness);
        }

        return fitnesses;
    }

    /**
     * The index of the lowest value in the array
     * @param array
     * @return
     */
    public int lowestIndex(double[] array) {
        int lowest = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] <= array[lowest]) lowest = i;
        }
        array[lowest] = 1;
        return lowest;
    }

    /**
     * The index of the highest value in the array
     * @param array
     * @return
     */
    public int highestIndex(double[] array) {
        int highest = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] >= array[highest]) highest = i;
        }
        array[highest] = 0;
        return highest;
    }

    /**
     * Maps the coordinate array coordinate to a bit in the string
     * @param ind
     * @param coordinatePosition
     * @return
     */
    public int getPosition(String ind, int coordinatePosition) {
        int count, i;
        count = i = 0;
        
        while(i < ind.length() && count!=coordinatePosition ) {
            if (ind.charAt(i) == '1') {
                count++;
            } 
            i++;
        }
        return i;
    }

}
