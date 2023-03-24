package research.binaryparticleswarmoptimisation;

import research.api.java.*;

public class Main {
    public static void main(String[] args) throws Exception {
        WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/api/Wind Competition/2014/competition_0.xml");

        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        Problem problem = new Problem(evaluator, ws, 10);
        ParticleSwarmOptimisation pso = new ParticleSwarmOptimisation(problem.swarmSize, 0, 1, 0.4, 0.9, 20, problem);

        pso.run();
    }

}
