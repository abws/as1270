package semester_1.algorithms;

import java.util.ArrayList;

import semester_1.individuals.Individual;
import semester_1.problems.ProblemConstrainedQuadratic;

/**
 * Hill Climb Algorithm
 * Also known as the greedy local search algorithm
 * where we always select the best neighbour
 * @author Abdiwahab Salah
 * @version 02.01.23
 */
public class SimpleGeneticAlgorithm {

    public static int run(ProblemConstrainedQuadratic problem) {
        ArrayList<Individual> population = problem.getRandomPopulation(4, 4);
        int sum = 0;
        ArrayList<Integer> weights;

        //Get total sum
        for (Individual individual : population) { 
            sum += individual.VALUE;
        }

        //Get individual weights
        for (Individual individual : population) { 
            weights.add(individual.VALUE / sum);
        }
        

        ArrayList<Integer> weights;

        for (Individual i : population) {

        }

        return 0;
    }
}