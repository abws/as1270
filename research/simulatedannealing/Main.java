package research.simulatedannealing;

import research.api.java.*;

public class Main {

    public static void main(String[] args) throws Exception {

        WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/testscenarios/1.xml");
        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        Problem problem = new Problem(evaluator, ws);

        SimulatedAnnealing sa = new SimulatedAnnealing(100, 0.95, 5000, 1, problem);
        sa.run();
    }
    
}
