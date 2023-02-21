package research.simulatedannealing;

/**
 * Class for representing 
 * a solution from the simulated
 * annealing algorithm
 * @author Abdiwahab Salah
 * @version 20.02.23
 */
public class Solution {
    public int[][] grid;
    public double fitness;

    public Problem problem;


    Solution(int[][] grid, Problem problem) {
        this.grid = grid;
        this.problem = problem;

        fitness = problem.evaluate(grid);
    }

    public int[][] getGrid() {
        return this.grid;
    }

    public double getFitness() {
        return this.fitness;
    }   
}
