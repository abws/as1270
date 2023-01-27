package other.preliminaries.algorithms;

import java.util.ArrayList;
import java.util.Collections;

import other.preliminaries.problems.ProblemConstrainedQuadratic;

/**
 * Hill Climb Algorithm
 * Also known as the greedy local search algorithm
 * where we always select the best neighbour
 * @author Abdiwahab Salah
 * @version 02.01.23
 */
public class HillClimb {

    public static int hillClimb(ProblemConstrainedQuadratic problem) {

        int current = problem.getInitial();
        while (true) {            
            int neighbour = problem.getBestNeighbour(current);

            if (problem.objectiveFunction(neighbour) <= problem.objectiveFunction(current))
                return current;
            current = neighbour;
        }
    }

}
