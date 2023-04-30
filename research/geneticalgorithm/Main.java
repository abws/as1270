package research.geneticalgorithm;

import research.api.java.KusiakLayoutEvaluator;
import research.api.java.WindScenario;

public class Main {
    public static void main(String[] args) throws Exception {

        WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/testscenarios/0.xml");

        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        Problem problem = new Problem(evaluator, ws, 15);    
        
        Tests.run(problem, 333);

        

    }


    
    
    
}
