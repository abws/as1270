package semester_1.algorithms;

import java.util.ArrayList;
import java.util.Collections;

import semester_1.problems.ProblemConstrainedQuadratic;

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
