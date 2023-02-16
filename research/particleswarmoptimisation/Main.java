package research.particleswarmoptimisation;

import java.util.Arrays;

import research.api.java.*;

public class Main {
    public static void main(String[] args) throws Exception {
        WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/api/Wind Competition/2014/competition_0.xml");

        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        Problem problem = new Problem(evaluator, ws, 1);
        ParticleSwarmOptimisation pso = new ParticleSwarmOptimisation(problem.swarmSize, 2, 2, 0.4, 0.9, 10, 0.1, problem);

        //System.out.println(problem.calculateWeightStep(0.9, 0.4, 10));
        //System.out.println(Arrays.toString(pso.generateRandomiserVector(problem.particleDimension)));
        //System.out.println(Arrays.toString(pso.scalarMultipy(2.0, new double[]{2.0, 3.0, 4.0})));
        //System.out.println(Arrays.toString(pso.vectorDifference(new double[]{4.0, 3.5, 5.2}, new double[]{2.0, 3.0, 4.0})));
        //System.out.println(Arrays.toString(pso.vectorAddition(new double[]{4.0, 3.5, 5.2}, new double[]{2.0, 3.0, 4.0})));
        //System.out.println(Arrays.toString(pso.hadamardProduct(new double[]{4.0, 3.5, 5.2}, new double[]{2.0, 3.0, 4.0})));

        pso.run();



    }
}
