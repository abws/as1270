package semester_1.problems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import semester_1.objective_functions.QuadraticEvaluator;

/**
 * Problem formulation for optimizing a quadratic function with inputs constrained to -25 and 25
 * Defines the nature and constraints of the problem
 * Contains any intitialization parameters and any 
 * constraints needed by the algorithm
 * @author Abdiwahab Salah
 * @version 03.01.23
 */
public class ProblemConstrainedQuadratic {
    private int initial;
    private int upperBound;
    private int lowerBound;
    private int step;

    public ProblemConstrainedQuadratic() {   // default constructor
        upperBound = 25;
        lowerBound = -25;
        step = 1;
        initial = getRandomInt();
    }

    public ProblemConstrainedQuadratic(int uB, int lB, int s) {
        initial = getRandomInt();
        upperBound = uB;
        lowerBound = lB;
        step = s;
    }

    public int objectiveFunction(int input) {
        return QuadraticEvaluator.quadraticEvaluator(input);
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

    public int getRandomNeighbour(int input) {
        Random r = new Random();
        ArrayList<Integer> neighbours = this.getNeighbours(input);

        int index = r.nextInt(neighbours.size());
        return neighbours.get(index);
    }

    public int getBestNeighbour(int input) {
        ArrayList<Integer> neighbours = getNeighbours(input);
        HashMap<Integer, Integer> neighbourValue = new HashMap<>();

        //Make a map of neighbour and value
        for (int n : neighbours) {
            int val = objectiveFunction(n);
            neighbourValue.put(n, val);
        }

        //Get highest valued neighbour
        Optional<Integer> bestNeighbour = neighbourValue.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey);                                    
        
        return bestNeighbour.isPresent() ? bestNeighbour.get() : null;
    }

    //private helper function
    private int getRandomInt() {
        Random r = new Random();
        int randomInt = r.nextInt(upperBound - lowerBound + 1) + lowerBound;
        return randomInt;
    }
}
