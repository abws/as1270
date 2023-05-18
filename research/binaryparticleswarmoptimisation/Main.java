package research.binaryparticleswarmoptimisation;

import research.api.java.*;

/**
 * Main class for the binary particle swarm optimisation.
 * Run the algorithm from here. Enter into the ParticleSwarmOptimisation class for more control
 */
public class Main {
    public static void main(String[] args) throws Exception {
        WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/testscenarios/1.xml");

        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        Problem problem = new Problem(evaluator, ws, 100);
        ParticleSwarmOptimisation pso = new ParticleSwarmOptimisation(problem.swarmSize, 2, 1, 0.9, 0.4, 250, false, problem);

        pso.run();
    }

}
