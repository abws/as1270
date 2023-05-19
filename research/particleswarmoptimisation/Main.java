package research.particleswarmoptimisation;

import java.util.Arrays;

import research.api.java.*;

public class Main {
    public static void main(String[] args) throws Exception {
        WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/testscenarios/4.xml");

        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        Problem problem = new Problem(evaluator, ws, 29, 0, 0);
        ParticleSwarmOptimisation pso = new ParticleSwarmOptimisation(problem.swarmSize, 2, 2.5, 0.4, 0.9, 20, 0.1, problem);

        pso.run();
    }

}
