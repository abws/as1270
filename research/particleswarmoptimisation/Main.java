package research.particleswarmoptimisation;

import java.util.Arrays;

import research.api.java.*;

public class Main {
    public static void main(String[] args) throws Exception {
        WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/api/Wind Competition/2014/competition_0.xml");

        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        Problem problem = new Problem(evaluator, ws, 10, 100, 20);
        ParticleSwarmOptimisation pso = new ParticleSwarmOptimisation(problem.swarmSize, 1.2, 3, 0.4, 0.9, 200, 1, problem);

        pso.run();
    }

    private static void t(String t) {
        System.out.println(t);
    }
    private static void t(double t) {
        System.out.println(t);
    }
    private static void t(double[][] t) {
        System.out.println(Arrays.deepToString(t));
    }
    private static void t(double[] t) {
        System.out.println(Arrays.toString(t));
    }
}
