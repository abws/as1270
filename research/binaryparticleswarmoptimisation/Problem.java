package research.binaryparticleswarmoptimisation;

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

    public int[] gBest;
    public double gBestFitness;

    public int[][] lBest;
    public double[] lBestFitnesses;

    public int particleDimension;
    public int swarmSize;
    public int nTurbines;
    public double minDist;
    public double height;
    public double width;
    public int columns;
    public int rows;
    double penaltyCoefficient1;
    double penaltyCoefficient2;

    public Problem(KusiakLayoutEvaluator evaluator, WindScenario scenario, int swarmSize) throws Exception {
        this.scenario = scenario;
        this.evaluator = evaluator;

        //Commonly used
        this.nTurbines = scenario.nturbines;
        this.swarmSize = swarmSize;
        this.height = scenario.height;
        this.width = scenario.width;
        this.minDist = scenario.R * 8;
        this.columns = (int) (scenario.width / minDist);     //one to n
        this.rows = (int) (scenario.height / minDist);

        this.particleDimension = columns * rows; //Dimensionality of particles. All vectors will be the same size.


        this.lBest = new int[swarmSize][particleDimension];
        this.lBestFitnesses = new double[swarmSize];

    }

    /**
     * Evaluates a given particle 
     * position using the Wake Free
     * Ratio evaluation function.
     * Only takes in vectors.
     * @param particle
     * @return
     */
    public double evaluate(int[] particlePosition) {
        double[][] particleCoordinates = decode(particlePosition);
        // System.out.println(particleCoordinates.length);
        double fitness = evaluator.evaluate_2014(particleCoordinates);

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
    public double[][] decode(int[] particlePosition) {

        int[][] gridIndividual = gridify(particlePosition, this.columns, this.rows);

        double[][] layout = new double [countTurbines(particlePosition)][2];
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
     * Initialises a swarm of particles
     * with random positions & velocities
     * @param swarmSize
     * @return
     */
    public List<Particle> initialiseSwarm(int swarmSize) {
        List<Particle> swarm = new ArrayList<Particle>();
        int maxIndex = 0;

        for (int i = 0; i < swarmSize; i++) {
            swarm.add(createRandomParticle());
            if (swarm.get(i).getPersonalBestFitness() >= swarm.get(maxIndex).getPersonalBestFitness()) maxIndex = i;
        }

        this.gBest = swarm.get(maxIndex).getPosition();
        this.gBestFitness = swarm.get(maxIndex).getPersonalBestFitness();

        return swarm;
    }

    /**
     * Initialises a swarm of particles
     * with random positions & velocities
     * and uses a neighbourhood based on
     * the ring topology 
     * @param swarmSize
     * @return
     */
    public List<Particle> initialiseSwarmRing(int swarmSize) {
        List<Particle> swarm = new ArrayList<Particle>();

        for (int i = 0; i < swarmSize; i++) {
            swarm.add(createRandomParticle());
        }
        for (int i = 0; i < swarmSize; i++) {
            updateLocalBest(swarm, i);
        }
        return swarm;
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

    public Particle createRandomParticleSpecial() {
        Random random = new Random();
        int counter = 0;
        int[] randomPosition = new int[particleDimension];
        double[] velocity = new double[particleDimension];  //instantiate with all zeros

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

        int n = countTurbines(position);
        int difference = n - nTurbines; 

        if (difference > 0) evaluate(position);
        double[] turbineIndexes = evaluator.getTurbineFitnesses();

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
     * Calculates the weight difference
     * for each iteration step.
     * @return weight step
     */
    public double calculateWeightStep(double wMax, double wMin, int maxIterations) {
        double wStep =  (wMax - wMin) / maxIterations;
        return wStep;
    }

    /* Getters and Setters */

    public int[] getGlobalBest() {
        return gBest;
    }

    public double getGlobalBestFitness() {
        return gBestFitness;
    }

    public boolean updateGlobalBest(double newFitness, int[] newPosition) {
        if ((newFitness >= gBestFitness)) { //assuming maximisation
            // System.out.println(countViolations(decodeDirect(newPosition)));
            this.gBest = newPosition;
            this.gBestFitness = newFitness;
            return true;
        }
        return false;
    }

    public int[] getLocalBest(int index) {
        return lBest[index];
    }

    public double getLocalBestFitness(int index) {
        return lBestFitnesses[index];
    }

    public void updateLocalBest(List<Particle> swarm, int index) {
        int indexB = Math.floorMod(index + 1, swarmSize);   //indexes wrap around the ends, such that we build a ring topology
        int indexC = Math.floorMod(index - 1, swarmSize);
        int maxIndex = getBestNeighbour(swarm, index, indexB, indexC);
        Particle best = swarm.get(maxIndex);
        this.lBestFitnesses[index] = best.fitness; 
        this.lBest[index] = best.getPosition();

        double gBestTemp = maxFitness(lBestFitnesses);
        if (this.gBestFitness < gBestTemp)this.gBestFitness = maxFitness(lBestFitnesses); //debugging purposes
    }

    public int getBestNeighbour(List<Particle> swarm, int indexA, int indexB, int indexC) {
        int maxIndex = indexA;
        double maxFitness = swarm.get(indexA).fitness;


        for (int i: new int[]{indexA, indexB, indexC}) {
            double fitness = swarm.get(i).fitness;
            if (fitness >= maxFitness) {
                maxFitness = fitness;
                maxIndex = i;
            }
        }

        return maxIndex;
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

    public double maxFitness(double[] fitness) {
        double maxFitness = fitness[0];
        for (int i = 1; i < swarmSize; i++) {
            if (fitness[i] > maxFitness) maxFitness = fitness[i];
        }
        return maxFitness; 

    }

    public double avgFitness(List<Particle> swarm) {
        double sum = 0;

        for (int i = 0; i < swarm.size(); i++) {
            double current = swarm.get(i).fitness;
            sum+=current;
        }
        return sum/swarm.size(); 
    }

    public double maxFitness(List<Particle> pop) {
        double maxFitness = pop.get(0).fitness;

        for (int i = 1; i < pop.size(); i++) {
            double current = pop.get(i).fitness;
            if (current >= maxFitness) {maxFitness = current;}
        }
        return maxFitness; 
    }
    
}