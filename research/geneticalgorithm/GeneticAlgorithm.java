package research.geneticalgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class GeneticAlgorithm {

    public static double run(Problem problem, int generations, int popSize) {
        ParentSelection ps = new ParentSelection(problem);
        Recombination r = new Recombination(problem, 0.7);
        Mutation m = new Mutation(problem, 0.03);


        ArrayList<String> population = problem.getRandomPopulation(popSize, problem.INDIV_LENGTH, problem.N_TURBINES);
        
        for (int i = 0; i < generations; i++) {
            problem.calulateFitnesses(population); //an aspect of it should return average and max

            System.out.println(Collections.max(problem.populationFitness)); //change to problem.lastFitness in future
            System.out.println(problem.populationFitness);
            ArrayList<String> matingPool = ps.linearRankingSelection(population, popSize, 1.25);
            ArrayList<String> offSpring = r.recombineNPoint(matingPool, popSize, 250);
            population = problem.legalise(m.mutatePopulation(offSpring));
        }

        return problem.avgFitness(population); //calls the evalution a further popsize times as its the last generation
    }
    
}
