package research;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import research.algorithms.SimpleGeneticAlgorithm;
import research.api.java.*;


import research.problems.ProblemSGA;

/*
 * Testing suite
 * Used for testing single functions
 * Now leave me and let me get to work in this mini lab
 */
public class Main {
    public static void main(String[] args) throws Exception {

        WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/api/Scenarios/00.xml");
        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);

        ProblemSGA prob = new ProblemSGA(evaluator, ws);
        //String test = prob.getRandomPopulation(10, 10).get(0);
        /*
        double[][] test2 = prob.decode(test);
        System.out.println(Arrays.deepToString(test2));
        System.out.println(Arrays.deepToString(prob.gridify(test, 5, 2)));
        */
        //System.out.println(prob.evaluate(test));

        ArrayList<String> t3 = prob.getRandomPopulation(4, prob.getStringLength(), ws.nturbines);
        //System.out.println(t3);
        SimpleGeneticAlgorithm neww = new SimpleGeneticAlgorithm();
        //System.out.println(Arrays.deepToString(prob.gridify(t3.get(0), (int) (ws.width % (8*ws.R)), (int) (ws.height % (8*ws.R)))));
        System.out.println(t3.get(0));
        //System.out.println(Arrays.deepToString(prob.decode(t3.get(0))));
        System.out.println(prob.evaluate(t3.get(0)));

        //System.out.println(prob.evaluate(t3.get(0)));
        //System.out.println(neww.calculateWeights(t3, prob));
        }
    
}


