package semester_1.simulated_annealing_quadratic;

import java.util.ArrayList;
import java.util.Random;

/**
 * Defines the nature and constraints of the problem
 * Contains any intitialization parameters and any 
 * constraints needed by the algorithm
 * @author Abdiwahab Salah
 * @version 06.12.22
 */
public class Problem {
    private  int initial = getRandomInt();
    private int upperBound;
    private int lowerBound;
    private int step;

    public Problem() {   // default constructor
        upperBound = 25;
        lowerBound = -25;
        step = 1;
    }

    public Problem(int uB, int lB, int s) {
        upperBound = uB;
        lowerBound = lB;
        step = s;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public int getInitial() {
        return initial;
    }

    public ArrayList<Integer> getNeighbours(int input) {
        int leftNeigh = input - this.step;
        int rightNeigh = input + this.step;
        ArrayList<Integer> neighbours = new ArrayList<Integer>();

        //only add neighbours if they are in the bounds of the search space
        if (leftNeigh >= lowerBound) neighbours.add(leftNeigh); 
        if (rightNeigh <= upperBound) neighbours.add(rightNeigh);
        
        return neighbours;
    }

    private int getRandomInt() {
        Random r = new Random();
        int randomInt = r.nextInt(upperBound - lowerBound + 1) + lowerBound;
        return randomInt;
    }
}
