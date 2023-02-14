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
                double[] newVelocity = vectorAdd(inertia, cognitive, social);
                double[] newPosition = vectorAdd(p.getPosition(), newVelocity);

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

}
