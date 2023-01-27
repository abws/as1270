package other.preliminaries.others.hill_climb_quadratic;

import java.util.ArrayList;
import java.util.Arrays;
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

    public Problem() {
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
        ArrayList<Integer> neighbours = new ArrayList<Integer>(Arrays.asList(input - this.step, input + this.step));
        return neighbours;
    }

    public boolean isEdge(int input) {
        if ((input <= lowerBound) || (input >= upperBound)) return true;
        else return false;
    }

    private int getRandomInt() {
        Random r = new Random();
        int randomInt = r.nextInt(upperBound - lowerBound + 1) + lowerBound;
        return randomInt;
    }
}
