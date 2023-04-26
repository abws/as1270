package research.simulatedannealing;

/**
 * Class for representing 
 * a state from the simulated
 * annealing algorithm
 * @author Abdiwahab Salah
 * @version 20.02.23
 */
public class State {
    public int[][] grid;
    public double fitness;

    public Problem problem;


    State(int[][] grid, Problem problem) {
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
