package research.debugginglab;

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

    public double[][] lBest;
    public double[] lBestFitnesses;

    public int particleDimension;
    public int swarmSize;
    public int nTurbines;
    public double minDist;
    public double height;
    public double width;
    double penaltyCoefficient;
    public int neighbourhoodSize;


    public Problem(KusiakLayoutEvaluator evaluator, WindScenario scenario, int swarmSize, double penaltyCoefficient, int neighbourhoodSize) throws Exception {
        this.scenario = scenario;
        this.evaluator = evaluator;

        //Commonly used
        this.particleDimension = scenario.nturbines * 2; //Dimensionality of particles. All vectors will be the same size.
        this.nTurbines = scenario.nturbines;
        this.swarmSize = swarmSize;
        this.height = scenario.height;
        this.width = scenario.width;
        this.minDist = scenario.R * 8;
        this.penaltyCoefficient = penaltyCoefficient;
        this.neighbourhoodSize =  neighbourhoodSize;
        this.lBest = new double[swarmSize][particleDimension];
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
    public double evaluate(double[] particlePosition) {
        double[][] particleCoordinates = decodeDirect(particlePosition);
        double fitness = evaluator.evaluate_2014(particleCoordinates);

        return fitness;
    }

    /**
     * Evaluates a given particle 
     * position using the Wake Free
     * Ratio evaluation function and 
     * applying a penalty function.
     * Only takes in vectors.
     * @param particle
     * @param c penalty coefficient
     * @return
     */
    public double evaluatePenalty(double[] particlePosition) {
        double[][] particleCoordinates = decodeDirect(particlePosition);

        /* Calculate total energy production */
        evaluator.evaluate_2014(particleCoordinates);   //calculates the AEP and sets it in the evaluator
        double energyProduction = evaluator.getEnergyOutput();

        double violationSum = 0;

        for (int i = 0; i < particleCoordinates.length; i++) {     //loop through each edge only once (n(n+1)/n) - ~doubles speed
            for (int j = i+1; j < particleCoordinates.length; j++) {
                violationSum += proximityConstraintViolation(particleCoordinates[i], particleCoordinates[j], minDist);
            }
        }
        double penalty = this.penaltyCoefficient * (Math.sqrt(violationSum));
        double fitness = (energyProduction - penalty) / (scenario.wakeFreeEnergy * nTurbines);
     

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
        double[] randomPosition = new double[particleDimension];
        double[] velocity = new double[particleDimension];  //instantiate with all zeros
        // double[][] layout = new double[nTurbines][2];

        for (int i = 0; i < particleDimension; i+=2) {
            randomPosition[i] = random.nextDouble(width);    //x coordinate
            randomPosition[i + 1] = random.nextDouble(height);  //y coordinate
        }

        // layout = geometricReformer(decodeDirect(randomPosition), minDist);
        // randomPosition = encodeDirect(layout);
        randomPosition = absorbBoundHandle(randomPosition);

        // randomPosition = encodeDirect(decodeDirect(randomPosition));

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
     * Calculates the violation value
     * for a proximity constraint violation
     * Calculates the squared 
     * distance between two 
     * turbines, subtracted by 
     * the minimum distance squared
     * @param pointA
     * @param pointB
     * @return
     */
    public double proximityConstraintViolation(double[] pointA, double[] pointB, double minDist) {
        double x1 = pointA[0];
        double x2 = pointB[0];
        double y1 = pointA[1];
        double y2 = pointB[1];

        double distanceSquared = Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2);
        double constraint = distanceSquared - Math.pow(minDist, 2);     //If violated, distance squared will be less than minDist squared and we'll get a negative value
        constraint = Math.abs(Math.min(0, constraint));    //Get the magnitude of the negative number, or 0 otherwise

        return constraint;
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
    
    /**
     * Boundary handling mechanism.
     * Moves particles that fly out
     * of boundary to the closest feasible
     * position.
     * @param particlePosition
     * @return
     */
    public double[] absorbBoundHandle(double[] particlePosition) {
        for (int i = 0; i < particlePosition.length; i+=2) {
            particlePosition[i] = Math.max(0, particlePosition[i]);
            particlePosition[i+1] = Math.max(0, particlePosition[i+1]);

            particlePosition[i] = Math.min(particlePosition[i], this.width);
            particlePosition[i+1] = Math.min(particlePosition[i+1], this.height);
        }

        return particlePosition;
    }

    /**
     * Counts the number
     * of turbines breaking 
     * the minimum distance 
     * constraint.
     * @param layout
     * @return
     */
    public int countViolations(double[][] layout) {
        int count = 0;
        for (int i = 0; i < layout.length; i++) {     //loop through each edge only once (n(n+1)/n) - ~doubles speed
            for (int j = i+1; j < layout.length; j++) {
                if (calculateEuclideanDistance(layout[i], layout[j]) < 308 ) count++;
            }
        }
        return count;

    }

    /* Getters and Setters */

    public double[] getGlobalBest() {
        return gBest;
    }

    public double getGlobalBestFitness() {
        return gBestFitness;
    }

    public boolean updateGlobalBest(double newFitness, double[] newPosition) {
        if (newFitness >= gBestFitness) { //assuming maximisation
            // System.out.println(countViolations(decodeDirect(newPosition)));
            this.gBest = newPosition;
            this.gBestFitness = newFitness;
            return true;
        }
        return false;
    }

    public double[] getLocalBest(int index) {
        return lBest[index];
    }

    public double getLocalBestFitness(int index) {
        return lBestFitnesses[index];
    }

    public boolean updateLocalBest(List<Particle> swarm, int index) {
        Particle p = swarm.get(index);
        double newFitness = p.getPersonalBestFitness();
        double[] newPosition =  p.getPersonalBest();

        int indexB = Math.floorMod(index + 1, swarmSize);   //indexes wrap around the ends, such that we build a ring topology
        int indexC = Math.floorMod(index - 1, swarmSize);
        lBestFitnesses[index] = getBestNeighbourFitness(swarm, index, indexB, indexC);

        if (newFitness >= lBestFitnesses[index]) { //assuming maximisation
            this.lBest[index] = newPosition;
            this.gBestFitness = maxFitness(lBestFitnesses);
            return true;
        }
        return false;
    }

    public double getBestNeighbourFitness(List<Particle> swarm, int indexA, int indexB, int indexC) {
        double maxFitness = swarm.get(indexA).getPersonalBestFitness();
        double fitnessB = swarm.get(indexB).getPersonalBestFitness();
        double fitnessC = swarm.get(indexC).getPersonalBestFitness();

        if (fitnessB >= maxFitness) maxFitness = fitnessB;
        if (fitnessC >= maxFitness) maxFitness = fitnessC;

        return maxFitness;
    }

    public double maxFitness(double[] fitness) {
        double maxFitness = fitness[0];
        for (int i = 1; i < swarmSize; i++) {
            if (fitness[i] > maxFitness) maxFitness = fitness[i];
        }
        return maxFitness; 

    }
    
}