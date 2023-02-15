package research.particleswarmoptimisation;

import java.util.ArrayList;
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
        double[][] decodedParticle = new double[nTurbines][2];
        int index = 0;

        for (int i = 0; i < nTurbines; i++) {
            decodedParticle[i][0] = particlePosition[index];
            decodedParticle[i][1] = particlePosition[index + 1];
            index += 2;
        }

        return decodedParticle;
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
        double[] xCoordinates = new double[nTurbines];
        double[] yCoordinates = new double[nTurbines];
        double[] randomPosition = new double[particleDimension];
        double[] velocity = new double[particleDimension];

        int index = 0;

        for (int i = 0; i < nTurbines; i++) {
            xCoordinates[i] = random.nextDouble(width); //Maybe useless and a waste of space
            yCoordinates[i] = random.nextDouble(height);

            randomPosition[index] = xCoordinates[i];    
            randomPosition[index + 1] = yCoordinates[i];

            velocity[index] = random.nextDouble(width);
            velocity[index + 1] = random.nextDouble(height);

            index += 2;
        }

        Particle randomParticle = new Particle(randomPosition, velocity, this);
        return randomParticle;
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

}
