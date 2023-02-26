package research.geneticalgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//remember, since the lists contains objects, changing their values in a function will change their true values
public class GeneticAlgorithm {

    public static void run(Problem problem, int generations, int popSize) {
        ParentSelection ps = new ParentSelection(problem);
        Recombination r = new Recombination(problem, 0.4);
        Mutation m = new Mutation(problem, 0.03);
        Replacement rp = new Replacement(problem);


        List<Individual> population = problem.getRandomPopulation(popSize, problem.INDIV_LENGTH, problem.N_TURBINES);
        
        for (int i = 0; i < generations; i++) {
            System.out.println(Collections.max(problem.getFitnessesArrayList(population)));
            List<Individual> matingPool = new ArrayList<>();
            List<Individual> offSpring = new ArrayList<>();

            matingPool = ps.tournamentSelection(population, popSize, 11, true); //parent selection
            offSpring = r.recombineNPoint(matingPool, popSize, 100); //recombination
            offSpring = problem.repairRandom(m.mutatePopulationRandom(offSpring)); //mutation and repair
            population = rp.elitism(population, offSpring, 1); //survival selection
        }
    }
}
