package research.simulatedannealing;

import research.api.java.*;

public class Main {

    public static void main(String[] args) throws Exception {

        WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/api/Wind Competition/2014/competition_0.xml");
        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        Problem problem = new Problem(evaluator, ws);

        SimulatedAnnealing sa = new SimulatedAnnealing(100, 0.01, 1000, problem);
        sa.run();
    }
    
}
