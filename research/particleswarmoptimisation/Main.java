package research.particleswarmoptimisation;

import java.util.Arrays;

import research.api.java.*;

public class Main {
    public static void main(String[] args) throws Exception {
        WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/api/Wind Competition/2014/competition_0.xml");

        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        Problem problem = new Problem(evaluator, ws, 100, 200);
        ParticleSwarmOptimisation pso = new ParticleSwarmOptimisation(problem.swarmSize, 2.8, 1.3, 0.4, 0.9, 200, 0.1, problem);

        pso.run();



    }
}
