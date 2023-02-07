package research.geneticalgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.stream.IntStream;

import research.api.java.KusiakLayoutEvaluator;
import research.api.java.WindScenario;

public class Main {
    public static void main(String[] args) throws Exception {

        WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/api/Scenarios/00.xml");
        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        Problem problem = new Problem(evaluator, ws, 10);

        


        System.out.println(GeneticAlgorithm.run(problem, 50, 15));

    }


    
    
    
}
