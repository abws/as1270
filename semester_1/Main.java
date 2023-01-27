package semester_1;
import java.util.ArrayList;

import semester_1.algorithms.SimpleGeneticAlgorithm;
import semester_1.individuals.Individual;
import semester_1.problems.ProblemConstrainedQuadratic;

public class Main {
    public static void main(String[] args) {
   
        ProblemConstrainedQuadratic problem = new ProblemConstrainedQuadratic();
        
        //int localMaxima = HillClimb.hillClimb(problem);
        //System.out.println(localMaxima);

        SimpleGeneticAlgorithm sa = new SimpleGeneticAlgorithm();
        // ArrayList<Individual> pop = problem.getRandomPopulation(4, 4);
        // System.out.println(pop);

        // ArrayList<Double> weights = sa.calculateWeights(pop);
        // System.out.println(weights);

        // ArrayList<Individual> matingPool = sa.rouletteSelection(pop, weights, 4);
        // System.out.println(matingPool);

        // ArrayList<Individual> offspring = sa.onePointCrossover(matingPool, 0.7, 4);
        // System.out.println(offspring);
        //System.out.println(problem.decode("00011010000010010110") * problem.decode("00011010000010010110"));
        sa.run(problem);        




    }
    

    
}
