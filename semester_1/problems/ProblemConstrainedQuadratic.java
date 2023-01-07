package semester_1.problems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import semester_1.individuals.Individual;
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
    private final int BASE = 2;

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

    /**
     * Returns one random neighbour
     * Used by Simulated Annealing
     * @param input The current node
     * @return A random neighbour
     */
    public int getRandomNeighbour(int input) {
        Random r = new Random();
        ArrayList<Integer> neighbours = this.getNeighbours(input);

        int index = r.nextInt(neighbours.size());
        return neighbours.get(index);
    }

    /**
     * Returns highest valued neighbour
     * Only used by Hill Climb
     * @param input The current node
     * @return The best neighbour
     */
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

    /**
     * Random population generator
     * @param size The size of the population
     * @param bits How many bits to represent each individual
     * @return population The random population
     */
    public ArrayList<Individual> getRandomPopulation(int size, int bits) {
        ArrayList<Individual> population = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            population.add(new Individual(encode(getRandomInt(), bits)));
        }
        return population;
    }


    /**
     * Encoding operator: Denary -> Signed Binary
     * @param num The denary representaion of the binary number
     * @return binary The binary number to decode 
     */
    public String encode(int num, int bits) {
        StringBuilder sb = new StringBuilder();
        for (int i = bits - 1; i >= 0; i--) {
            int bit = (num >> i) & 1;
            sb.append(bit);
        }

        return sb.toString();
    }


    /**
     * Decoding operator: Signed binary -> Denary
     * @param binary The binary number to decode
     * @return num The denary representaion of the binary number
     */
    public int decode(String binary) {
        int num = 0;

        for (int i = 0; i < binary.length(); i++) {

            //Used && to short circuit - think of a way to reduce conditions assessed
            if (i == 0 &&  binary.charAt(i) == '1') num -= Character.getNumericValue(binary.charAt(i)) * Math.pow(BASE, binary.length() - i-1);
            else num += Character.getNumericValue(binary.charAt(i)) * Math.pow(BASE, binary.length() - i-1);
        }     

        return num;
    }

    //private helper function
    private int getRandomInt() {
        Random r = new Random();
        int randomInt = r.nextInt(upperBound - lowerBound + 1) + lowerBound;
        return randomInt;
    }
}
