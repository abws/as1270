package research;

import research.algorithms.GeneticAlgorithm;
import research.problems.ProblemGA;
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

        ProblemGA prob = new ProblemGA(evaluator, ws);

        GeneticAlgorithm sa = new GeneticAlgorithm();
        sa.run(prob);
    

        }



    }


