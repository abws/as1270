package research.geneticalgorithm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import research.api.java.KusiakLayoutEvaluator;
import research.api.java.WindScenario;

public class Init {
    public static void main(String[] args) throws Exception {
        WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/testscenarios/2.xml");

        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        Problem problem = new Problem(evaluator, ws, 100);    
        List<Individual> population = problem.getRandomPopulation(100, problem.INDIV_LENGTH, problem.N_TURBINES);
        String outputFilename = "/Users/abdiwahabsalah/Documents/GitLab/as1270/research/geneticalgorithm/lattices/s-5.txt";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilename))) {
            for (Individual individual : population) {
                System.out.println(individual.getFitness());
                bw.write(individual.getFitness() + " ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    

    }
    
}
