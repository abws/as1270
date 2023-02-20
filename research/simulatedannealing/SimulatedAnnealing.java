package research.simulatedannealing;

/**
 * The Simulated Annealing Algorithm
 * Paramters that can be changed
 * include the neighbourhood operator,
 * the intial temperature, the cooling
 * rate and the acceptance formula.
 */
public class SimulatedAnnealing {
    double temperature;
    double coolingRate;
    int iterations;
    Problem problem;

    SimulatedAnnealing(double temperature, double coolingRate, int iterations, Problem problem) {
        this.temperature = temperature;
        this.coolingRate = coolingRate;
        this.iterations = iterations;
        this.problem = problem;
    }

    public void run() {
        Solution current = problem.generateInitialSolution();
        int i = 0;

        while (i < iterations) {
            Solution neightbour = pertubate(current);

            double p = maxMetropolisAlgorithm(current.getFitness(), neightbour.getFitness(), temperature);

            if (Math.random() <= p) {
                current = neightbour;
            }
            i++;
        }
    
    }

    /**
     * A selection scheme based
     * on the Boltzmann-Gibbs 
     * distribution.
     * Assumes maximisation
     * @param current
     * @param neighbour
     * @param t temperature
     */
    private double maxMetropolisAlgorithm(double current, double neighbour, double t) {
        if (neighbour > current) return 1;

        double difference = neighbour - current;

        double p = Math.exp(difference / t);
        return p;
    }
 
}
