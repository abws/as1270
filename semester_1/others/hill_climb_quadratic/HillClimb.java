package semester_1.others.hill_climb_quadratic;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Hill Climb Algorithm
 * Also known as the greedy local search algorithm
 * where we always select the best neighbour
 * @author Abdiwahab Salah
 * @version 06.12.22
 */
public class HillClimb {

    public static int hillClimb(Problem problem) {
        int current = problem.getInitial();
        while (true) {
            if (problem.isEdge(current)) return current;
            ArrayList<Integer> neighbours = problem.getNeighbours(current);
            int neighbour = Collections.max(neighbours);

            if (QuadraticEvaluator.quadraticEvaluator(neighbour) <= QuadraticEvaluator.quadraticEvaluator(current)) 
                return current;
            current = neighbour;
        }
    }
}
