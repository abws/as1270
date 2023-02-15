package research.particleswarmoptimisation;

import java.util.List;
import java.util.Random;

/**
 * Standard Particle Swarm Optimisation
 * Algorithm for the Wind Farm Layout
 * Optimisation problem
 * Runnable class that optimises 
 * a list of particles based on
 * a variable input
 */
public class ParticleSwarmOptimisation {
    int swarmSize;
    double c1;
    double c2; 
    int maxIterations;
    double wMin;
    double wMax; 
    double[] vMin;
    double[] vMax;
    Problem problem;

    ParticleSwarmOptimisation(int swarmSize, double c1, double c2, double wMin, double wMax, double[] vMin, double[] vMax, int maxIterations, Problem problem) {
        this.swarmSize = swarmSize;
        this.c1 = c1;
        this.c2 = c2;
        this.maxIterations = maxIterations;
        this.wMin = wMin;
        this.wMax = wMax;
        this.vMin = vMin;
        this.vMax = vMax;
        this.problem = problem;
    }

    public void run() {
        Random random = new Random();

        /* Initialise weight step size for inertia */
        double weight = wMax;
        double wStep = problem.calculateWeightStep(wMax, wMin, maxIterations);
        int iteration = 0;

        List<Particle> swarm = problem.initialiseSwarm(swarmSize);


        /* Main loop iteratively moving particles along search space 
        * By using the particle class, we avoid having to loop multiple
        * times over the swarm as seen on most implementations of PSO 
        */
        while (iteration < maxIterations) {
            for (Particle p : swarm) {
                /* Calculate Inertia */
                double[] inertia = scalarMultipy(weight, p.getVelocity());

                /* Calculate cognitive component */
                double[] distanceToPBest = vectorDifference(p.pBest, p.getPosition());
                double[] randomisedPDistance = vectorMultiply(distanceToPBest, randomiserArray1);
                double[] cognitive = scalarMultipy(c1, randomisedPDistance);
                
                /* Calculate social component */
                double[] distanceToGBest = vectorDifference(problem.gBest, p.getPosition());
                double[] randomisedGDistance = vectorMultiply(distanceToPBest, randomiserArray2);
                double[] social = scalarMultipy(c1, randomisedGDistance);

                /* Update velocity and position */
                double[] newVelocity = vectorAddition(inertia, cognitive, social);
                double[] newPosition = vectorAddition(p.getPosition(), newVelocity);

                /* Update Particle */
                p.setPosition(newPosition);
                p.setVelocity(newVelocity);

                p.updatePosition(newPosition, true);
                p.updatePersonalBest();
                
                problem.updateGlobalBest(p.getPersonalBest);    //will only update if pBest is better than gBest
            }
        }

        weight -= wStep;
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

    /**
     * Calculates the difference
     * between two vectors
     * @param vectorA
     * @param vectorB
     */
    public double[] vectorDifference(double[] vectorA, double[] vectorB) {
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
     * of three vectors
     */
    public double[] vectorAddition(double[] vectorA, double[] vectorB, double[] vectorC) {
        double[] sum = new double[vectorA.length];
        for (int i = 0; i < vectorA.length; i++) {
            sum[i] = vectorA[i] + vectorB[i] + vectorC[i];
        }
        return sum;
    }

}
