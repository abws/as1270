package research.binaryparticleswarmoptimisation;

import research.api.java.*;

public class Main {
    public static void main(String[] args) throws Exception {
        WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/testscenarios/1.xml");

        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        Problem problem = new Problem(evaluator, ws, 100);
        ParticleSwarmOptimisation pso = new ParticleSwarmOptimisation(problem.swarmSize, 2, 2, 0.4, 0.9, 50, problem);

        pso.run();
    }

}
