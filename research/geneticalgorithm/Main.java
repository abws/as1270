package research.geneticalgorithm;

import research.api.java.KusiakLayoutEvaluator;
import research.api.java.WindScenario;

public class Main {
    public static void main(String[] args) throws Exception {

        // WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/api/Scenarios/00.xml");
        WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/api/Wind Competition/2014/competition_0.xml");

        //WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/api/Wind Competition/2014/competition_7.xml");
        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        Problem problem = new Problem(evaluator, ws, 10);    
        
        GeneticAlgorithm.run(problem, 100, 10);

    }


    
    
    
}
