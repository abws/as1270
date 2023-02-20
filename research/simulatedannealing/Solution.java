package research.simulatedannealing;

/**
 * Class for representing 
 * a solution from the simulated
 * annealing algorithm
 * @author Abdiwahab Salah
 * @version 20.02.23
 */
public class Solution {
    public int[][] layout;
    public double fitness;

    public Problem problem;


    Solution(int[][] layout, Problem problem) {
        this.layout = layout;
        this.problem = problem;

        fitness = problem.evaluate(layout);
    }

    public int[][] getLayout() {
        return this.layout;
    }

    public double getFitness() {
        return this.fitness;
    }   
}
