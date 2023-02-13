package research.particleswarmoptimisation;

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
    double[] vMax;

    ParticleSwarmOptimisation(int swarmSize, double c1, double c2, int maxIterations, double wMin, double wMax, double[] vMax) {
    }

    public void run() {
        ArrayList<Particle> swarm = problem.initialiseSwarm(swarmSize);
        
        do {
            for (Particle p : swarm) {
                //calculate objective
                //update pbest
                //update gbest
            }
            //update innertia

            for (Particle p : swarm) {
                //update velocity
                //update position
            }
        } 
        while (i < maxIterations);

    } 

    
}
