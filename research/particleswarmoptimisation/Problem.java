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
    private static int bound = 0;

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
    double w1;
    double w2;

    public Problem(KusiakLayoutEvaluator evaluator, WindScenario scenario, int swarmSize, double w1, double w2) throws Exception {
        this.scenario = scenario;
        this.evaluator = evaluator;

        //Commonly used
        this.particleDimension = scenario.nturbines * 2; //Dimensionality of particles. All vectors will be the same size.
        this.nTurbines = scenario.nturbines;
        this.swarmSize = swarmSize;
        this.height = scenario.height;
        this.width = scenario.width;
        this.minDist = scenario.R * 8;
        this.w1 = w1;
        this.w2 = w2;
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

        double p1 = 0;
        double p2 = 0;


        for (int i = 0; i < particleCoordinates.length; i++) {     //loop through each edge only once (n(n+1)/n) - ~doubles speed
            for (int j = i+1; j < particleCoordinates.length; j++) {
                p1 += proximityConstraintViolation(particleCoordinates[i], particleCoordinates[j], minDist);
            }
            // violationSum2 += boundConstraintViolation(particleCoordinates[i]);
        }

        bound =0;
        
        // double penalty1 = this.penaltyCoefficient1 * (Math.sqrt(violationSum1));

        // double penalty2 = this.penaltyCoefficient2 * (Math.sqrt(violationSum2));


        // double fitness = (energyProduction - (penalty1 + penalty2)) / (scenario.wakeFreeEnergy * nTurbines);
        double fitness = (energyProduction - (p1 )) / (scenario.wakeFreeEnergy * nTurbines);
        

     

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
        // can be used to test geometric reformer 
        // layout = geometricReformer(decodeDirect(randomPosition), minDist);
        // randomPosition = encodeDirect(layout);
        // randomPosition = absorbBoundHandle(randomPosition);
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
     * RMTRA
     * @param layout
     * @param z
     * @return
     */
    public double[][] geometricReformerRight(double[][] layout, double z) {
        double[] manner;
        double[] repulser;

        for (int m = 0; m < layout.length; m++) {
            manner = layout[m];

            for (int r = 0; r < layout.length; r++) {
                if (r != m) {
                    repulser = layout[r];
                    double distance = calculateEuclideanDistance(repulser, manner);
                    if (distance > z) continue;

                    manner = spacialShiftRight(repulser, manner, distance, z, 1); 
                }
            }

            layout[m] = manner;
        }

        return layout;
    }

    /**
     * Latest edition that combines the 
     * repairment of both constraints. Uses 
     * a periodic boundary repair. The complexity
     * of this may be infinite though. As a result,
     * we may fix a layout and completely reform its
     * spacial structure since we loop so many time. 
     * Furthermore, a valid comparison may not be possible
     * because of this extra functionality.
     * @param layout the layout to potentially modify
     * @param z the minimum distance between two points
     * @return a feasible layout
     */
    public double[][] geometricReformerEmbedded(double[][] layout, double z) {
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
                    if (manner[0] < 0) {
                        manner[0] = (manner[0] % width) + width; //so we wrap wround 
                        r = 0;
                    }
                    if (manner[1] < 0) {
                        manner[1] = (manner[1] % height) + height;
                        r = 0;
                    }
                    if (manner[0] > this.width) {
                        manner[0] = (manner[0] % width); 
                        r=0;
                    }
                    if (manner[1] > this.height) {
                        manner[1] = (manner[1] % height); 
                        r=0;
                    }
                }
            }

            layout[m] = manner;
        }

        return layout;
    }

    /**
     * Both the turbines repulse one another
     * in this scheme
     * @param layout the layout to potentially modify
     * @param z the minimum distance between two points
     * @return a feasible layout
     */
    public double[][] geometricReformerRepulse(double[][] layout, double z) {
        double[] manner;
        double[] repulser;

        for (int m = 0; m < layout.length; m++) {
            manner = layout[m];

            for (int r = 0; r < layout.length; r++) {
                if (r != m) {
                    repulser = layout[r];
                    double distance = calculateEuclideanDistance(repulser, manner);
                    if (distance > z) continue;

                    layout = repulse(layout, repulser, manner, r, m, distance, z); 
                }
            }
        }

        return layout;
    }


    public double[][] repulse(double[][] layout, double[] repulser, double[] manner, int r, int m, double distance, double z) {
        distance = distance/2;
        double x1 = repulser[0];
        double y1 = repulser[1];

        double x2 = manner[0];
        double y2 = manner[1];

        double x3 = (x1 + x2) / 2.0;    //mid point
        double y3 = (y1 + y2) / 2.0; 

        double shiftedPositionX1 = (((x3 - x1) * z) / distance) + x3; 
        double shiftedPositionY1 = (((y3 - y1) * z) / distance) + y3; 

        double shiftedPositionX2 = (((x3 - x2) * z) / distance) + x3; 
        double shiftedPositionY2 = (((y3 - y2) * z) / distance) + y3;

        double[] shiftedPosition1 = new double[]{shiftedPositionX1, shiftedPositionY1};
        double[] shiftedPosition2 = new double[]{shiftedPositionX2, shiftedPositionY2};

        layout[r] =  shiftedPosition1;
        layout[m] =  shiftedPosition2;

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

    public double boundConstraintViolation(double[] coordinate) {
        double xDistance, yDistance, distanceSquared;
        xDistance = yDistance = distanceSquared = 0;

        if (coordinate[0] < 0) {xDistance+=(coordinate[0]*-1);bound++;}
        if (coordinate[1] < 0) {yDistance+=(coordinate[1]*-1); bound++;}
        if (coordinate[0] > this.width) {xDistance+=(coordinate[0]-this.width); bound++;}
        if (coordinate[1] > this.height) {yDistance+=(coordinate[1]-this.height); bound++;}

        distanceSquared = Math.pow((xDistance), 2) + Math.pow((yDistance), 2);
        return distanceSquared;
    }


    /**
     * Calculates the weight difference
     * for each iteration step.
     * @return weight step
     */
    public double calculateWeightStep(double wMax, double wMin, int maxIterations) {
        double wStep =  (wMax - wMin) / (double) maxIterations;
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
     * Boundary handling mechanism.
     * Moves particles that fly out
     * of boundary to a random feasible
     * position.
     * @param particlePosition
     * @return
     */
    public double[] randomBoundHandle(double[] particlePosition) {
        Random r = new Random();

        for (int i = 0; i < particlePosition.length; i+=2) {
            if (particlePosition[i] < 0 || particlePosition[i] > this.width) {
                particlePosition[i] = r.nextDouble(this.width);
            }
            if (particlePosition[i+1] < 0 || particlePosition[i+1] > this.height) {
                particlePosition[i+1] = r.nextDouble(this.height);
            }
        }
        return particlePosition;
    }


    /**
     * Boundary handling mechanism.
     * Moves particles that fly out
     * of boundary to a periodic
     * position.
     * @param particlePosition
     * @return
     */
    public double[] periodicBoundHandle(double[] position) {
        for (int i = 0; i < position.length; i+=2) {
            if (position[i] < 0) {
                position[i] = (position[i] % width) + width; //so we wrap wround 
            }
            if (position[i+1] < 0) {
                position[i+1] = (position[i+1] % height) + height;

            }
            if (position[i] > this.width) {
                position[i] = (position[i] % width); 
            }
            if (position[i+1] > this.height) {
                position[i+1] = (position[i+1] % height); 
            }

        }
        return position;
    }

    /**
     * Counts the number
     * of turbines breaking 
     * the minimum distance 
     * constraint.
     * @param layout
     * @return
     */
    public int countProximityViolations(double[][] layout) {
        int count = 0;
        for (int i = 0; i < layout.length; i++) {     //loop through each edge only once (n(n+1)/n) - ~doubles speed
            for (int j = i+1; j < layout.length; j++) {
                if (calculateEuclideanDistance(layout[i], layout[j]) < 308 ) count++;
            }
        }
        return count;
    }

    public int countBoundaryViolations(double[][] layout) {
        int count = 0;
        for (double[] l: layout) {     //loop through each edge only once (n(n-1)/n) - ~doubles speed
            if ((l[0] < 0) || (l[1] < 0) || (l[0] > this.width) || (l[1] > this.height)) count++;
        }
        return count;
    }

    public boolean boundaryViolated(double[] position) {
        double[][] layout = decodeDirect(position);
        for (double[] l: layout) {     //loop through each edge only once (n(n-1)/n) - ~doubles speed
            if ((l[0] < 0) || (l[1] < 0) || (l[0] > this.width) || (l[1] > this.height)) return true;
        }

        return false;
    }

    /* Getters and Setters */

    public double[] getGlobalBest() {
        return gBest;
    }

    public double getGlobalBestFitness() {
        return gBestFitness;
    }

    public boolean updateGlobalBest(double newFitness, double[] newPosition) {
        if ((newFitness >= gBestFitness)) { //assuming maximisation
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

    /**
     * Updates the local best
     * @param swarm
     * @param index
     */
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

    public double maxFitness(double[] fitness) {
        double maxFitness = fitness[0];
        for (int i = 1; i < swarmSize; i++) {
            if (fitness[i] > maxFitness) maxFitness = fitness[i];
        }
        return maxFitness; 

    }

    public double avgFitness(List<Particle> swarm) {
        double sum = 0;

        for (int i = 1; i < swarm.size(); i++) {
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