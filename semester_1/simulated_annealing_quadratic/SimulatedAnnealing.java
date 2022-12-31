package semester_1.simulated_annealing_quadratic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Simulated Annealing
 * Also known as the greedy local search algorithm
 * where we always select the best neighbour
 * @author Abdiwahab Salah
 * @version 06.12.22
 */
public class SimulatedAnnealing {
    private double temp;
    private double scheduleRate;
    private int timer;
    
    //Default constructor
    public SimulatedAnnealing() { 
        temp = 1.00;
        scheduleRate = 0.97;
        timer = 100;
    }

    /**
     * Main simulated annealing algorithm method
     * @param problem The (mathematical) formulation of our problem
     * @return maxima The maxima found
     */
    public int SimulatedAnnealing(Problem problem) {
        int current = problem.getInitial();
        while (true) {
            timer--;
            if (timer == 0) return current;

            temp *= scheduleRate;
            Random r = new Random();
            int index = r.nextInt(2);
            int next = problem.getNeighbours(current).get(index);

            int delta = QuadraticEvaluator.quadraticEvaluator(next) - QuadraticEvaluator.quadraticEvaluator(current);
            if (delta > 0) current = next;
            else if (Math.exp(delta / temp) > Math.random()) current = next;
        }
    }
}
