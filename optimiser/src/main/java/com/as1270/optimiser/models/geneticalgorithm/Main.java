package com.as1270.optimiser.models.geneticalgorithm;
import com.as1270.optimiser.models.api.java.*;


public class Main {
    public static void main(String[] args) throws Exception {

        WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/api/Scenarios/00.xml");
        //WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/api/Wind Competition/2014/competition_7.xml");
        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        Problem problem = new Problem(evaluator, ws, 10);
        //List<String> values = output.stream().map(Individual::getValue).collect(Collectors.toList());
    

//        GeneticAlgorithm.run(problem, 100, 10);

    }


    
    
    
}
