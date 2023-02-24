package research.geneticalgorithm;

import java.util.Arrays;

import research.api.java.KusiakLayoutEvaluator;
import research.api.java.WindScenario;

public class Mina {
    
    public static void main(String[] args) throws Exception {
        WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/api/Wind Competition/2014/competition_0.xml");
        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        Problem p = new Problem(evaluator, ws, 2);

        String ind = "100110110";

        int[][] test = p.gridifyAlternate(ind, 3, 3);
        System.out.println(Arrays.deepToString(test));
        
    }
    
}
