package research;

import research.algorithms.SimpleGeneticAlgorithm;
import research.problems.ProblemSGA;
import research.api.java.*;
import java.util.*;

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

        //ArrayList<String> t3 = prob.getRandomPopulation(4, prob.getStringLength(), ws.nturbines);
        //System.out.println(t3);

        //System.out.println(neww.calculateWeights(t3, prob));

        SimpleGeneticAlgorithm sa = new SimpleGeneticAlgorithm();
        sa.run(prob);
        //System.out.println(onePointCrossover("0010111011", "1111111111"));

        }

        public static ArrayList<String> onePointCrossover(String parent1, String parent2) {
            Random r = new Random();
            ArrayList<String> offSpring = new ArrayList<String>();
    
            int crossoverPoint = r.nextInt(10 - 1) + 1;    //-1 so the random number has 3 positions to take (including 0). +1 as returning 0 would make us cut the string at the very start (so wont cut). The number refers to the position we cut at before
            System.out.println(crossoverPoint);
 
            String child1 = parent1.substring(0, crossoverPoint) + parent2.substring(crossoverPoint);
            String child2 = parent2.substring(0, crossoverPoint) + parent1.substring(crossoverPoint);
            //System.out.println(Arrays.deepToString(problem.decode(parent1)));
    
            //System.out.printf("%d %d %d %d%n", countTurbines(parent1), countTurbines(parent2), countTurbines(child1), countTurbines(child2));
            offSpring.addAll(Arrays.asList(child1, child2));
    
            return offSpring;
        }
    
}


