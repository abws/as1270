package research.geneticalgorithm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//remember, since the lists contains objects, changing their values in a function will change their true values
public class Tests {

    public static void run(Problem problem, int generations) {
        int popSize = problem.POP_SIZE;
        ParentSelection ps = new ParentSelection(problem);
        Recombination r = new Recombination(problem, 0.8);
        Mutation m = new Mutation(problem, 0.01);
        Replacement rp = new Replacement(problem);
        Repair re = new Repair(problem);


        String outputFilename = "/Users/abdiwahabsalah/Documents/GitLab/as1270/research/geneticalgorithm/lattice/01g.txt";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilename))) {
            for (int n = 0; n < 1; n++) {
                List<Individual> population = problem.getRandomPopulation(popSize, problem.INDIV_LENGTH, problem.N_TURBINES);
                for (int i = 0; i < generations; i++) {
                    double maxFitness = Collections.max(problem.getFitnessesArrayList(population));
                    System.out.println(maxFitness);
                    // bw.write(maxFitness + " ");

                    List<Individual> matingPool = new ArrayList<>();
                    List<Individual> offSpring = new ArrayList<>();

                    matingPool = ps.tournamentSelection(population, popSize, popSize / 2, false); //parent selection
                    offSpring = r.recombineNPoint(matingPool, popSize, problem.N_TURBINES/4); //recombination

                    offSpring = re.repairRandom(m.mutatePopulationSwap(offSpring)); //mutation and repair
                    population = rp.elitism(population, offSpring, 1); //survival selection
                }
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
