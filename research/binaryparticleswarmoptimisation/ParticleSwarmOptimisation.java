package research.binaryparticleswarmoptimisation;

import java.util.Arrays;
import java.util.List;

/**
 * Standard Particle Swarm Optimisation
 * Algorithm for the Wind Farm Layout
 * Optimisation problem
 * Runnable class that optimises 
 * a list of particles based on
 * a variable input
 * @author Abdiwahab Salah
 * @version 03/03/23
 */
public class ParticleSwarmOptimisation {
    int swarmSize;      //Size of population
    double c1;          //Acceleration factor for cognitive component  
    double c2;          //Acceleration factor for cognitive component 
    int maxIterations;  //Maximum number of iterations
    double wMin;        //Minimum weight
    double wMax;        //Maximum weight
    Problem problem;    

    ParticleSwarmOptimisation(int swarmSize, double c1, double c2, double wMin, double wMax, int maxIterations, Problem problem) {
        this.swarmSize = swarmSize;
        this.c1 = c1;
        this.c2 = c2;
        this.maxIterations = maxIterations;
        this.wMin = wMin;
        this.wMax = wMax;

        this.problem = problem;
    }


    /**
    * Main loop iteratively moving particles along search space 
    * By using the particle class, we avoid having to loop multiple
    * times over the swarm as seen on most implementations of PSO 
    */
    public void run() {
        /* Initialise weight step size for inertia */
        double weight = wMax;
        double wStep = problem.calculateWeightStep(wMax, wMin, maxIterations);
        int iteration = 0;

        List<Particle> swarm = problem.initialiseSwarm(swarmSize);

        while (iteration < maxIterations) {

            System.out.println(problem.gBestFitness);
            // System.out.println(problem.avgFitness(swarm));
            // c2 = c2 * weight;   //explore less as we go on
            double[] randomiserArray1 = generateRandomiserVector(problem.particleDimension);
            double[] randomiserArray2 = generateRandomiserVector(problem.particleDimension); 
            for (Particle p : swarm) {
                /* Calculate Inertia */
                double[] inertia = scalarMultipy(1, p.getVelocity()); 

                /* Calculate cognitive component */
                double[] distanceToPBest = vectorDifference(p.getPersonalBest(), p.getPosition());
                double[] randomisedPDistance = hadamardProduct(distanceToPBest, randomiserArray1);
                double[] cognitive = scalarMultipy(c1, randomisedPDistance);
                
                // /* Calculate social component */
                double[] distanceToGBest = vectorDifference(problem.gBest, p.getPosition());
                double[] randomisedGDistance = hadamardProduct(distanceToGBest, randomiserArray2);
                double[] social = scalarMultipy(c2, randomisedGDistance);
                //  double[] distanceToLBest = vectorDifference(problem.lBest[swarm.indexOf(p)], p.getPosition());
                // double[] randomisedLDistance = hadamardProduct(distanceToLBest, randomiserArray2);
                // double[] social = scalarMultipy(c2, randomisedLDistance);

                /* Update velocity and position */
                double[] newVelocity = vectorAddition(inertia, cognitive, social);
                double[] normalisedVelocity = sigmoid(newVelocity);
                int[] newPosition = updatePosition(p.getPosition(), normalisedVelocity);    //combine with bottom for efficiency
                newPosition = problem.repairRandom(newPosition);

                /* Update Particle */
                p.setPosition(newPosition, true); //updates pBest
                p.setVelocity(newVelocity);

                /* Update local and global best */
                problem.updateGlobalBest(p.getPersonalBestFitness(), p.getPersonalBest());    //will only update if pBest is better than gBest
                                // problem.updateLocalBest(swarm, swarm.indexOf(p));    //will only update if pBest is better than lBest

            }
            weight -= wStep;
            iteration++;
        }
    }

    /**
     * Calculates the value of a 
     * vector after a scalar multiplication
     * @param scalar
     * @param vector
     */
    public double[] scalarMultipy(double scalar, double[] vector) {
        double[] scalarised = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            scalarised[i] = scalar * vector[i];
        }

        return scalarised;
    }

    public double[] scalarMultipy(double scalar, int[] vector) {
        double[] scalarised = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            scalarised[i] = scalar * vector[i];
        }

        return scalarised;
    }

    /**
     * Calculates the difference
     * between two vectors
     * @param vectorA the vector to point towards
     * @param vectorB from this vector
     */
    public double[] vectorDifference(double[] vectorA, double[] vectorB) {
        double[] difference = new double[vectorA.length];
        for (int i = 0; i < vectorA.length; i++) {
            difference[i] = vectorA[i] - vectorB[i];
        }
        return difference;
    }

    /**
     * Calculates the difference
     * between two vectors
     * @param vectorA the vector to point towards
     * @param vectorB from this vector
     */
    public double[] vectorDifference(int[] vectorA, int[] vectorB) {
        double[] difference = new double[vectorA.length];
        for (int i = 0; i < vectorA.length; i++) {
            difference[i] = vectorA[i] - vectorB[i];
        }
        return difference;
    }

    /**
     * Calculates hadamard product
     * of two vectors (elementwise 
     * product)
     */
    public double[] hadamardProduct(double[] vectorA, double[] vectorB) {
        double[] productVector = new double[vectorA.length];
        for (int i = 0; i < vectorA.length; i++) {
            productVector[i] = vectorA[i] * vectorB[i];
        }
        return productVector;
    }
    
    /**
     * Calculates the sum
     * of two vectors
     */
    public double[] vectorAddition(double[] vectorA, double[] vectorB) {
        double[] sum = new double[vectorA.length];
        for (int i = 0; i < vectorA.length; i++) {
            sum[i] = vectorA[i] + vectorB[i];
        }
        return sum;
    }

    /**
     * Calculates the sum
     * of two vectors
     */
    public double[] vectorAddition(int[] vectorA, double[] vectorB) {
        double[] sum = new double[vectorA.length];
        for (int i = 0; i < vectorA.length; i++) {
            sum[i] = vectorA[i] + vectorB[i];
        }
        return sum;
    }


    /**
     * Calculates the sum
     * of three vectors
     */
    public double[] vectorAddition(double[] vectorA, double[] vectorB, double[] vectorC) {
        double[] sum = new double[vectorA.length];
        for (int i = 0; i < vectorA.length; i++) {
            sum[i] = vectorA[i] + vectorB[i] + vectorC[i];
        }
        return sum;
    }

    /**
     * Generates a vector
     * of random numbers between
     * 0 and 1
     */
    public double[] generateRandomiserVector(int vectorLength) {
        double[] randomiserVector = new double[vectorLength];
        for (int i = 0; i < vectorLength; i++) {
            randomiserVector[i] = Math.random();
        }
        return randomiserVector;
    }

    /**
     * Constriction factor to 
     * added to velocity update
     * rule to iteratively reduce 
     * velocity
     * @param velocity
     * @param c - Constant, set at c1+c2
     * @return
     */
    public double[] constrictionFactor(double[] velocity, double c) {
        double k = 2.0 / Math.abs(2 - c - Math.sqrt(Math.pow(c, 2) - (4*c)));
        velocity = scalarMultipy(k, velocity);
        return velocity;
    }

    public double[] sigmoid(double[] velocity) {
        double[] newVelocity = new double[velocity.length];
        for (int i = 0; i < newVelocity.length; i++) {
            newVelocity[i] = 1 / (1 + Math.exp(-velocity[i]));
        }

        return newVelocity;
    }

    private int[] updatePosition(int[] position, double[] velocity) {
        int[] newPosition = new int[position.length];
        for (int i = 0; i < position.length; i++) {
            if (Math.random() < velocity[i]) newPosition[i] = 1;
            else newPosition[i] = 0;
        }
        return newPosition;
    }
}
