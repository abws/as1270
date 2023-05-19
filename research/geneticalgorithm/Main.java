package research.geneticalgorithm;

import research.api.java.KusiakLayoutEvaluator;
import research.api.java.WindScenario;


/**
 * Main class. Run this to start the Genetic Algorithm.
 * For more scenarios, check the API package. All obstacles must be removed
 */
public class Main {
    public static void main(String[] args) throws Exception {

        WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/testscenarios/5.xml");

        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        Problem problem = new Problem(evaluator, ws, 10);    
        
        GeneticAlgorithm.run(problem, 500);

    }


    
    
    
}
