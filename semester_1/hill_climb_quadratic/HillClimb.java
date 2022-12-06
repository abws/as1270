package semester_1.hill_climb_quadratic;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Hill Climb Algorithm
 * Also known as the greedy local search algorithm
 * we always select the best neighbour
 * @author Abdiwahab Salah
 * @version 06.12.22
 */
public class HillClimb {

    public static int hillClimb() {
        int current = Problem.initial;
        while (true) {
            ArrayList<Integer> neighbours = Problem.getNeighbours(current);
            int neighbour = Collections.max(neighbours);
            if (QuadraticEvaluator(current) <= QuadraticEvaluator(neighbour)) return current;
            current = neighbour;

        }
    }
}
