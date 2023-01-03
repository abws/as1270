package semester_1.others.simulated_annealing_quadratic;

import java.util.ArrayList;
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
    private double coolingRate;
    private int iterations;
    
    //Default constructor
    public SimulatedAnnealing() { 
        temp = 10000.00;            //higher temperture allows us to explore more
        coolingRate = 0.98;
        iterations = 1000;
    }

    /**
     * Main simulated annealing algorithm method
     * @param problem The (mathematical) formulation of our problem
     * @return maxima The maxima found
     */
    public int simulatedAnnealing(Problem problem) {
        int current = problem.getInitial();

        while (iterations != 0) {
            iterations--;   //use this instead of a threshold to better compare algorithms later

            temp *= coolingRate;
            int next = problem.getRandomNeighbour(current);
            double probability = getProbability(current, next);

            if (probability > 1) current = next;    // always accept the better solution
            else if (probability >= Math.random()) current = next;  //accept the worse solution with probability
            
            //int delta = -(QuadraticEvaluator.quadraticEvaluator(current) - QuadraticEvaluator.quadraticEvaluator(next));
            //System.out.printf("temp: %.2f, current: %d, neighbours: %s, next: %d, delta: %d, random: %.2f, e: %f %n", temp, current, neighbours.toString(), next,delta, Math.random(), Math.exp(delta / temp));
        }
        return current;
    }

    /**
     * Represents e^delta(E)/T: A property of the Boltzmann Distribution
     * @param current The current solution
     * @param next The random neighbor
     * @return The probability of accepting a bad solution
     */
    private double getProbability(int current, int next) {

        //Delta gives the difference in energies. Since this is an ascent (maximisation) we want a negative value (low energy) for 'bad' solutions, hence we minus current
        int delta = (QuadraticEvaluator.quadraticEvaluator(next) - QuadraticEvaluator.quadraticEvaluator(current));
        return Math.exp(delta / temp);  // a positive delta always gives a p higher than 1. A negative one gives a p between 0 and 1
    }
}
