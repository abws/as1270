package research.particleswarmoptimisation;

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

    public int particleDimension;
    public int swarmSize;
    public int N_TURBINES;


    public Problem(KusiakLayoutEvaluator evaluator, WindScenario scenario, int swarmSize) throws Exception {
        this.scenario = scenario;
        this.evaluator = evaluator;

        //Commonly used
        this.particleDimension = scenario.nturbines * 2; //All vectors will be this size 
        this.N_TURBINES = scenario.nturbines;
        this.swarmSize = swarmSize;
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
        double[][] particleCoordinates = decode(particlePosition);
        double fitness = evaluator.evaluate_2014(particleCoordinates);

        return fitness;
    }

    /**
     * Decodes a given particle 
     * vector to give a nx2 matrix 
     * representing the coordinates
     * of turbines
     * @param particleVector 
     * @return
     */
    public double[][] decode(double[] particlePosition) {
        double[][] decodedParticle = new double[N_TURBINES][2];
        int index = 0;

        for (int i = 0; i < N_TURBINES; i++) {
            decodedParticle[i][0] = particlePosition[index];
            decodedParticle[i][1] = particlePosition[index + 1];
            index += 2;
        }

        return decodedParticle;
    }
}
