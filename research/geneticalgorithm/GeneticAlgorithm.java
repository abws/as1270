package research.geneticalgorithm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
//remember, since the lists contains objects, changing their values in a function will change their true values
public class GeneticAlgorithm {

    public static void run(Problem problem, int generations, int popSize) {
        ParentSelection ps = new ParentSelection(problem);
        Recombination r = new Recombination(problem, 0.7);
        Mutation m = new Mutation(problem, 0.03);


        List<Individual> population = problem.getRandomPopulation(popSize, problem.INDIV_LENGTH, problem.N_TURBINES);
        
        for (int i = 0; i < generations; i++) {
            System.out.println(Collections.max(problem.getFitnessesArrayList(population)));
            System.out.println(Arrays.toString(problem.getFitnesses(population)));

            List<Individual> matingPool = ps.linearRankingUniform(population, popSize, 2); //fix when true

            List<Individual> offSpring = r.recombineNPoint(matingPool, popSize, 100);

            population = problem.legalise(m.mutatePopulation(offSpring));
            problem.updateAllFitnesses(population);
        }
    }
}
