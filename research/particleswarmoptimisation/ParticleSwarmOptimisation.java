package research.particleswarmoptimisation;

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
    double[] vMin;      //Minimum velocity
    double[] vMax;      //Maximum velocity
    Problem problem;    

    ParticleSwarmOptimisation(int swarmSize, double c1, double c2, double wMin, double wMax, int maxIterations, double clampConstant, Problem problem) {
        this.swarmSize = swarmSize;
        this.c1 = c1;
        this.c2 = c2;
        this.maxIterations = maxIterations;
        this.wMin = wMin;
        this.wMax = wMax;

        this.problem = problem;

        /* Velocity Clamping */
        this.vMin = minVelocityClamp(problem.width, problem.height, clampConstant);
        this.vMax = maxVelocityClamp(problem.width, problem.height, clampConstant);
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

        List<Particle> swarm = problem.initialiseSwarmRing(swarmSize);
    
        double[] randomiserArray1 = generateRandomiserVector(problem.particleDimension);
        double[] randomiserArray2 = generateRandomiserVector(problem.particleDimension);

        while (iteration < maxIterations) {
            System.out.println(problem.gBestFitness);
            System.out.println(Arrays.toString(problem.lBestFitnesses));

            for (Particle p : swarm) {
                /* Calculate Inertia */
                double[] inertia = scalarMultipy(1, p.getVelocity()); 

                /* Calculate cognitive component */
                double[] distanceToPBest = vectorDifference(p.getPersonalBest(), p.getPosition());
                double[] randomisedPDistance = hadamardProduct(distanceToPBest, randomiserArray1);
                // double[] randomisedPDistance = scalarMultipy(Math.random(), distanceToPBest);

                double[] cognitive = scalarMultipy(c1, randomisedPDistance);
                
                // /* Calculate social component */
                // double[] distanceToGBest = vectorDifference(problem.gBest, p.getPosition());
                // double[] randomisedGDistance = hadamardProduct(distanceToGBest, randomiserArray2);
                // // double[] randomisedGDistance = scalarMultipy(Math.random(), distanceToGBest);

                // double[] social = scalarMultipy(c2, randomisedGDistance);

                /* Calculate social component */
                double[] distanceToLBest = vectorDifference(problem.lBest[swarm.indexOf(p)], p.getPosition());
                double[] randomisedLDistance = hadamardProduct(distanceToLBest, randomiserArray2);
                double[] social = scalarMultipy(c2, randomisedLDistance);

                /* Update velocity and position */
                // double[] newVelocity = vectorAddition(inertia, cognitive, social);
                // double[] newVelocity = velocityClamp(vectorAddition(inertia, cognitive, social));
                // double[] newVelocity = constrictionFactor(velocityClamp(vectorAddition(inertia, cognitive, social)), c1+c2);
                double[] newVelocity = constrictionFactor(vectorAddition(inertia, cognitive, social), c1+c2);

                double[] newPosition = vectorAddition(p.getPosition(), newVelocity);    //combine with bottom for efficiency
                // double[][] layout = problem.geometricReformer(problem.decodeDirect(newPosition), problem.minDist);
                // newPosition = problem.encodeDirect(layout);

                newPosition = absorbBoundHandle(newPosition);

                /* Update Particle */
                p.setPosition(newPosition, true);
                // System.out.println(Arrays.toString(newPosition));
                // System.out.println("HiL "+problem.evaluate(newPosition));
                p.setVelocity(newVelocity);

                /* Update local and global best */
                p.updatePersonalBest();
                // problem.updateGlobalBest(p.getPersonalBestFitness(), p.getPersonalBest());    //will only update if pBest is better than gBest
                problem.updateLocalBest(swarm, swarm.indexOf(p));    //will only update if pBest is better than lBest

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

    public double[] velocityClamp(double[] currentVector) {        

        for (int i = 0; i < currentVector.length; i++) {
            currentVector[i] = Math.min(currentVector[i], this.vMax[i]);
            currentVector[i] = Math.max(currentVector[i], this.vMin[i]);
        }

        return currentVector;
    }

    /**
     * Puts a lower bound
     * on each dimension of
     * the vector
     * @param maxX
     * @param maxY
     * @param clampConstant
     * @return
     */
    public double[] minVelocityClamp(double maxX, double maxY, double clampConstant) {
        double[] minVelocity = new double[problem.particleDimension];
        for (int i = 0; i < minVelocity.length; i+=2) {
            minVelocity[i] = clampConstant * (0 - maxX);
            minVelocity[i + 1] = clampConstant * (0 - maxY);
        }

        return minVelocity;
    }

    /**
     * Puts an upper bound
     * on each dimension of
     * the vector
     * @param maxX - The maximum for the y coordinates
     * @param maxY  The maximum for the x coordinates
     * @param clampConstant
     * @return
     */
    public double[] maxVelocityClamp(double maxX, double maxY, double clampConstant) {
        double[] maxVelocity = new double[problem.particleDimension];
        for (int i = 0; i < maxVelocity.length; i+=2) {
            maxVelocity[i] = clampConstant * (maxX - 0);
            maxVelocity[i + 1] = clampConstant * (maxY - 0);
        }

        return maxVelocity;
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

            particlePosition[i] = Math.min(particlePosition[i], problem.width);
            particlePosition[i+1] = Math.min(particlePosition[i+1], problem.height);
        }

        return particlePosition;
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


}
