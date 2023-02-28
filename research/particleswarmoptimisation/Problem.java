package research.particleswarmoptimisation;

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

    public double[] gBest;
    public double gBestFitness;

    public int particleDimension;
    public int swarmSize;
    public int nTurbines;
    public double minDist;
    public double height;
    public double width;


    public Problem(KusiakLayoutEvaluator evaluator, WindScenario scenario, int swarmSize) throws Exception {
        this.scenario = scenario;
        this.evaluator = evaluator;

        //Commonly used
        this.particleDimension = scenario.nturbines * 2; //Dimensionality of particles. All vectors will be the same size.
        this.nTurbines = scenario.nturbines;
        this.swarmSize = swarmSize;
        this.height = scenario.height;
        this.width = scenario.width;
        this.minDist = scenario.R * 8;
    }

    /**
     * Evaluates a given particle 
     * position using the Wake Free
     * Ratio evaluation function.
     * Only takes in vectors.
     * @param particle
     * @return
     */
    public double evaluate(double[] particlePosition) {
        double[][] particleCoordinates = decodeDirect(particlePosition);
        double fitness = evaluator.evaluate_2014(particleCoordinates);

        return fitness;
    }

    /**
     * Decodes a particle 
     * vector assuming the form 
     * (x1, y1, x2, y2, ..., xn, yn)
     * to give a nx2 matrix 
     * representing the coordinates
     * of turbines.
     * @param particleVector 
     * @return
     */
    public double[][] decodeDirect(double[] particlePosition) {
        double[][] layout = new double[nTurbines][2];
        int index = 0;

        for (int i = 0; i < nTurbines; i++) {   //fill row by row
            layout[i][0] = particlePosition[index];
            layout[i][1] = particlePosition[index + 1];
            index += 2;
        }

        return layout;
    }

    /**
     * Decodes a particle 
     * vector assuming the form 
     * (x1, x2,..., xn, y1, y2,..., yn)
     * to give a nx2 matrix 
     * representing the coordinates
     * of turbines.
     * @param particleVector 
     * @return
     */
    public double[][] decodeSeperate(double[] particlePosition) {
        double[][] decodedParticle = new double[nTurbines][2];
        int x = 0;
        int y = nTurbines; /*If theres 400 turbines, the vector will have a dimension of 800. 0- 
                                                399 will be x coordinates & the last 400 will be y coordinates.*/
        for (int i = 0; i < nTurbines; i++) {
            decodedParticle[i][0] = particlePosition[x];
            decodedParticle[i][1] = particlePosition[y];
            x++; y++;
        }

        return decodedParticle;
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
        double[] particlePosition = new double[particleDimension];
        int index = 0;

        for (int i = 0; i < nTurbines; i++) {   //fill row by row
            particlePosition[index] = layout[i][0];
            particlePosition[index + 1] = layout[i][1];
            index += 2;
        }

        return particlePosition;
    }

    /**
     * Initialises a swarm of particles
     * with random positions & velocities
     * @param swarmSize
     * @return
     */
    public List<Particle> initialiseSwarm(int swarmSize) {
        List<Particle> swarm = new ArrayList<Particle>();
        for (int i = 0; i < swarmSize; i++) {
            swarm.add(createRandomParticle());
        }

        return swarm;
    }

    /**
     * Initialises a single particle
     * with a random position, & a 
     * random initial velocity.
     * Position will be of the form
     * (x1, y1, x2, y2, ..., xn, yn)
     * @return
     */
    public Particle createRandomParticle() {
        Random random = new Random();
        double[] randomPosition = new double[particleDimension];
        double[] velocity = new double[particleDimension];  //instantiate with all zeros
        double[][] layout = new double[nTurbines][2];

        for (int i = 0; i < particleDimension; i+=2) {
            randomPosition[i] = random.nextDouble(width);    //x coordinate
            randomPosition[i + 1] = random.nextDouble(height);  //y coordinate
        }
        // System.out.println(Arrays.toString(randomPosition));
        layout = geometricReformer(decodeDirect(randomPosition), minDist);
        System.out.println(Arrays.deepToString(layout));
        System.out.println(Arrays.deepToString(decodeDirect(randomPosition)));
        randomPosition = encodeDirect(layout);
        //randomPosition = encodeDirect(decodeDirect(randomPosition));


        Particle randomParticle = new Particle(randomPosition, velocity, this);
        return randomParticle;
    }

    /**
     * Reforms the points in
     * a planar euclidian space
     * so as to ensure the 
     * solution it presents is 
     * both feasible and geometrically
     * most similar to the original
     * @param layout the layout to potentially modify
     * @param z the minimum distance between two points
     * @return a feasible layout
     */
    public double[][] geometricReformer(double[][] layout, double z) {
        double[] manner;
        double[] repulser;

        for (int m = 0; m < layout.length; m++) {
            manner = layout[m];

            for (int r = 0; r < layout.length; r++) {
                if (r != m) {
                    repulser = layout[r];
                    double distance = calculateEuclideanDistance(repulser, manner);
                    if (distance > z) continue;

                    manner = spacialShift(repulser, manner, distance, z, 1); 
                }
            }

            layout[m] = manner;
        }

        return layout;
    }

    /**
     * Shifts the position
     * of a single coordinate 
     * in the realm of another
     * such that it moves just 
     * outside of the realm
     * of infeasiblity
     * @param repulser the position that has a realm
     * @param manner the position to be shifted 
     * @param distance the euclidian distance between manner
     * @param z the radius of the realm
     * @param c a constant to ensure we move slightly outside realm (and not on the edge)
     * @return
     */
    public double[] spacialShift(double[] repulser, double[] manner, double distance, double z, double c) {
        double x1 = repulser[0];
        double y1 = repulser[1];

        double x2 = manner[0];
        double y2 = manner[1];
        
        
        double shiftedPositionX = ((x1 - x2) * (z + c) / distance) + x1;    //unit vector terminal points towards the one that comes first (x1) in the subtraction
        double shiftedPositionY = ((y1 - y2) * (z + c) / distance) + y1;
        double[] shiftedPosition = new double[]{shiftedPositionX, shiftedPositionY};

        return shiftedPosition;
    }

    public double[] spacialShiftRight(double[] repulser, double[] manner, double distance, double z, double c) {
        double x1 = repulser[0];
        double y1 = repulser[1];

        double x2 = manner[0];
        double y2 = manner[1];
        double sign = (x1 - x2) / Math.abs(x1 - x2); //will be 1 or -1        
        
        double shiftedPositionX = (sign*(x1 - x2) * (z + c) / distance) + x1;    //unit vector terminal points towards the one that comes first (x1) in the subtraction
        double shiftedPositionY = (sign*(y1 - y2) * (z + c) / distance) + y1;

        double[] shiftedPosition = new double[]{shiftedPositionX, shiftedPositionY};
        double d = calculateEuclideanDistance(repulser, shiftedPosition);
        return shiftedPosition;
    }


    /**
     * Calculates the distance
     * between two turbines
     * @param pointA
     * @param pointB
     * @return
     */
    public double calculateEuclideanDistance(double[] pointA, double[] pointB) {
        double x1 = pointA[0];
        double x2 = pointB[0];
        double y1 = pointA[1];
        double y2 = pointB[1];

        double distance = Math.sqrt( Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
        return distance;
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

    public double[] getGlobalBest() {
        return gBest;
    }

    public double getGlobalBestFitness() {
        return gBestFitness;
    }

    public boolean updateGlobalBest(double newFitness, double[] newPosition) {
        if (newFitness > gBestFitness) { //assuming maximisation
            this.gBest = newPosition;
            gBestFitness = newFitness;
            return true;
        }
        return false;
    }


}