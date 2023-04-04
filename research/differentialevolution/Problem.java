package research.differentialevolution;
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
    double penaltyCoefficient1;
    double penaltyCoefficient2;

    public Problem(KusiakLayoutEvaluator evaluator, WindScenario scenario, int popSize, double penaltyCoefficient1, double penaltyCoefficient2) throws Exception {
        this.scenario = scenario;
        this.evaluator = evaluator;

        //Commonly used
        this.nDimension = scenario.nturbines * 2; //Dimensionality of vectors. All vectors will be the same size.
        this.nTurbines = scenario.nturbines;
        this.popSize = popSize;
        this.height = scenario.height;
        this.width = scenario.width;
        this.minDist = scenario.R * 8;
        this.penaltyCoefficient1 = penaltyCoefficient1;
        this.penaltyCoefficient2 = penaltyCoefficient2;

    }
    /**
     * Evaluates a given vector 
     * position using the Wake Free
     * Ratio evaluation function.
     * Only takes in vectors.
     * @param vector
     * @return
     */
    public double evaluate(double[] position) {
        double[][] vectorCoordinates = decodeDirect(position);
        double fitness = evaluator.evaluate_2014(vectorCoordinates);
        // System.out.println("mindist: " + countViolations(vectorCoordinates));


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
    public double[][] decodeDirect(double[] position) {
        double[][] layout = new double[nTurbines][2];
        int index = 0;

        for (int i = 0; i < nTurbines; i++) {   //fill row by row
            layout[i][0] = position[index];
            layout[i][1] = position[index + 1];
            index += 2;
        }

        return layout;
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
            randomPosition[i] = random.nextDouble(1);    //a, b, c, d
        }

        Vector randomvector = new Vector(randomPosition, true, this);
        return randomvector;
    }

        /**
     * Initialises a single particle
     * with a random position, & a 
     * random initial velocity.
     * Position will be of the form
     * (x1, y1, x2, y2, ..., xn, yn).
     * All particles will be uniformly
     * distributed accross the search space
     * @return
     */
    public Particle createRandomParticle() {
        Random random = new Random();
        int counter = 0;
        int[] randomPosition = new int[particleDimension];
        double[] velocity = new double[particleDimension];  //instantiate with all zeros
        // double[][] layout = new double[nTurbines][2];

        while (counter != nTurbines) {
            int randomIndex = random.nextInt(particleDimension);
            if (randomPosition[randomIndex] != 1) {
                randomPosition[randomIndex] = 1;
                counter++;
            }
        }

        // for (int i = 0; i < particleDimension; i++) {
        //     randomPosition[i] = random.nextInt(2);    //x coordinate
        // }

        Particle randomParticle = new Particle(randomPosition, velocity, this);
        return randomParticle;
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