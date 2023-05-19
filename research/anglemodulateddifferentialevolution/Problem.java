package research.anglemodulateddifferentialevolution;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import research.api.java.*;

/**
 * Problem class representing
 * the problem formulation.
 * In charge of enforcing constraints,
 * decoding solutions, and
 * providing access to the 
 * API as well as performing
 * helpful claculations
 * @author Abdiwahab Salah
 * @version 14.02.23
 */
public class Problem {
    private KusiakLayoutEvaluator evaluator;
    private WindScenario scenario;
    private static int bound = 0;

    public int nDimension;
    public int popSize;
    public int nTurbines;
    public double minDist;
    public double height;
    public double width;
    public int columns;
    public int rows;

    public Problem(KusiakLayoutEvaluator evaluator, WindScenario scenario, int popSize) throws Exception {
        this.scenario = scenario;
        this.evaluator = evaluator;

        //Commonly used
        this.nDimension = scenario.nturbines * 2; //Dimensionality of vectors. All vectors will be the same size.
        this.nTurbines = scenario.nturbines;
        this.popSize = popSize;
        this.height = scenario.height;
        this.width = scenario.width;
        this.minDist = scenario.R * 8;
        this.columns = (int) (scenario.width / minDist);     //one to n
        this.rows = (int) (scenario.height / minDist);


    }

    /**
     * Evaluates a given vector 
     * position using the Wake Free
     * Ratio evaluation function.
     * Only takes in vectors.
     * @param particle
     * @return
     */
    public double evaluate(int[] position) {
        double[][] particleCoordinates = decode(position);
        double fitness = evaluator.evaluate_2014(particleCoordinates);

        return fitness;
    }

    /**
     * Decodes a vector 
     * vector assuming the form 
     * (x1, y1, x2, y2, ..., xn, yn)
     * to give a nx2 matrix 
     * representing the coordinates
     * of turbines.
     * @param vector 
     * @return
     */
    public int[] decodeAM(double[] angleModulation) {
        int dimension = rows*columns;
        int[] position = new int[dimension];
        double a = angleModulation[0];
        double b = angleModulation[1];
        double c = angleModulation[2];
        double d = angleModulation[3];

        for (int i = 0; i < dimension; i++) { //intervals of 1
            double z = Math.sin( (2*Math.PI) * b * Math.cos( (2*Math.PI) * (c*(i-a)) ) * c) + d;
            if (z > 0) {
                position[i] = 1;
            } else {
                position[i] = 0;
            }

        }

        return position;
    }

    /**
     * Decodes a string of size h*w with n 1s
     * into a n by 2 array corresponding to turbine 
     * coordinates
     * @param individual The string to be decoded
     * @return A two-dimensional array representing the grid as turbine coordinates
     * Tested
     */
    public double[][] decode(int[] vectorPosition) {

        int[][] gridIndividual = gridify(vectorPosition, this.columns, this.rows);

        double[][] layout = new double [countTurbines(vectorPosition)][2];
        int count = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (gridIndividual[i][j] == 1) {
                    layout[count] = getMeshCoordinates(i, j, minDist);
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
        coordinates[0] = ((x * minDist) + (minDist / 2));
        coordinates[1] = ((y * minDist) + (minDist / 2));

        return coordinates;
    }

    private double[] getMeshCoordinates(int y, int x, double minDist) {
        int maxX = columns;
        int maxY = rows;

        double [] coordinates = new double[2];
        coordinates[0] = (x * minDist) + (y * (minDist / maxY));

        coordinates[1] = ((maxY - y) * minDist) - (x * (minDist / maxX));

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
    public int[][] gridify(int[] particle, int x, int y) {
        int[][] grid = new int[y][x]; //[rows][columns] since rows are 'bigger' and classified by first
        int count = 0;

        for (int i = 0; i < y; i ++) {
            for (int j = 0; j < x; j++) {
                grid[i][j] = particle[count];
                count++;
            }
        }

        return grid;
    }

    /**
     * Encodes a layout 
     * into a vector in the form 
     * (x1, x2,..., xn, y1, y2,..., yn)
     * Mainly used after geomtric reformation
     * @param layout
     * @return
     */
    public double[] encodeDirect(double[][] layout) {
        double[] position = new double[nDimension];
        int index = 0;

        for (int i = 0; i < nTurbines; i++) {   //fill row by row
            position[index] = layout[i][0];
            position[index + 1] = layout[i][1];
            index += 2;
        }

        return position;
    }

    /**
     * Initialises a swarm of vectors
     * with random positions & velocities
     * @param swarmSize
     * @return
     */
    public List<Vector> initialiseAMDEPopulation(int popSize) {
        List<Vector> pop = new ArrayList<Vector>();
        for (int i = 0; i < popSize; i++) {
            pop.add(createRandomVector());
        }
        return pop;
    }


    /**
     * Initialises a single vector
     * with a random position, & a 
     * random initial velocity.
     * Position will be of the form
     * (x1, y1, x2, y2, ..., xn, yn).
     * All vectors will be uniformly
     * distributed accross the search space
     * @return
     */
    public Vector createRandomVector() {
        Random random = new Random();
        double[] randomPosition = new double[4];

        for (int i = 0; i < 4; i++) {
            randomPosition[i] = random.nextInt(2000)- 1000;    //a, b, c, d
        }

        Vector randomvector = new Vector(randomPosition, true, this);
        System.out.println(Arrays.toString(randomvector.getLayout()));
        return randomvector;
    }

    public int[] repairRandom(int[] position) {
        Random r = new Random();
        int n = countTurbines(position);
        int difference = n - nTurbines; 

        while (difference > 0) {    //too many
            int i = r.nextInt(position.length);
            if (position[i] == 1) {
                position[i] = 0;
                difference--;
            }
        }

        while (difference < 0) {
            int i = r.nextInt(position.length);
            if (position[i] == 0) {
                position[i] = 1;
                difference++;
            }
        }

        return position;
    }

    public int[] repairWorst(int[] position) {
        int[] tempPosition = Arrays.copyOf(position, position.length);
        evaluate(position);
        double[] turbineIndexes = evaluator.getTurbineFitnesses();

        int n = countTurbines(position);
        int difference = n - nTurbines; 

        while (difference > 0) {    //we have too many turbines
            int coordinatePosition = lowestIndex(turbineIndexes); //position of turbine in list of turbines
            int i = getPosition(position, coordinatePosition); //position of turbine in grid
            
            int t = tempPosition[i];
            if (t == 1) {
                tempPosition[i] = 0;
                difference--;
            }
        }

        while (difference < 0) {    //we have too few turbines
            int coordinatePosition = lowestIndex(turbineIndexes); //position of turbine in list of turbines
            int i = getPosition(position, coordinatePosition); //position of turbine in grid
            
            int t = tempPosition[i];
            if (t == 0) {
                tempPosition[i] = 1;
                difference++;
            }
        }

        return tempPosition; 
    }
    
    public int lowestIndex(double[] array) {
        int lowest = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] <= array[lowest]) lowest = i;
        }
        array[lowest] = 1;
        return lowest;
    }

    public int highestIndex(double[] array) {
        int highest = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] >= array[highest]) highest = i;
        }
        array[highest] = 0;
        return highest;
    }

    public int getPosition(int[] position, int coordinatePosition) {
        int count, i;
        count = i = 0;

         while(i < position.length && count!=coordinatePosition ) {
            if (position[i] == 1) {
                count++;
            } 
            i++;
        }
        return i;
    }
    
    /**
     * Counts the number of
     * turbines in a state
     * @param value 
     * @return
     */
    public int countTurbines(int[] particle) {
        int sum = 0;
        for (int i = 0; i < particle.length; i++) {
            if (particle[i] == 1) sum++;
        }
        return sum;
    }

    public double maxFitness(List<Vector> pop) {
        double maxFitness = pop.get(0).fitness;

        for (int i = 1; i < pop.size(); i++) {
            double current = pop.get(i).fitness;
            if (current >= maxFitness) {maxFitness = current;}
        }
        return maxFitness; 
    }

    public double avgFitness(List<Vector> pop) {
        double sum = 0;

        for (int i = 0; i < pop.size(); i++) {
            double current = pop.get(i).fitness;
            sum += current;
        }
        return sum/pop.size(); 
    }    
}
