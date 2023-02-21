package research.simulatedannealing;

import java.util.ArrayList;
import java.util.List;

/**
 * The Simulated Annealing Algorithm
 * Paramters that can be changed
 * include the neighbourhood operator,
 * the intial temperature, the cooling
 * rate and the acceptance formula.
 */
public class SimulatedAnnealing {
    double temperature;
    double coolingRate;
    int iterations;
    Problem problem;

    SimulatedAnnealing(double temperature, double coolingRate, int iterations, Problem problem) {
        this.temperature = temperature;
        this.coolingRate = coolingRate;
        this.iterations = iterations;

        this.problem = problem;
    }

    public void run() {
        Solution current = problem.generateInitialSolution();
        int i = 0;

        while (i < iterations) {
            Solution neighbour = pertubate(current);
            System.out.println(current.getFitness());

            double p = maxMetropolisAlgorithm(current.getFitness(), neighbour.getFitness(), temperature);

            if (Math.random() <= p) {
                current = neighbour;
            }
            i++;
        }
    
    }

    /**
     * A selection scheme based
     * on the Boltzmann-Gibbs 
     * distribution.
     * Assumes maximisation
     * @param current
     * @param neighbour
     * @param t temperature
     */
    private double maxMetropolisAlgorithm(double current, double neighbour, double t) {
        if (neighbour > current) return 1;

        double difference = neighbour - current;

        double p = Math.exp(difference / t);
        return p;
    }

    private Solution pertubate(Solution current) {
        int[][] grid = current.getGrid();
        int[][] neighbours;
        
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] == 1) {
                    neighbours = getNeighbours(grid, x, y, grid[x].length - 1, grid.length - 1);
                    double partition = 1 / (neighbours.length + 1); //include current position as a possible move
                    int rowIndex = (int) Math.floor(Math.random() / partition);

                    grid[y][x] = 0;
                    grid[neighbours[rowIndex][0]][neighbours[rowIndex][1]] = 1;
                }
            }
        }

        return new Solution(grid, problem);
    }

    private int[][] getNeighbours(int[][] grid, int x, int y, int xMax, int yMax) {
        int[][] neighbours = new int[][] {
            {x-1, y},
            {x, y-1},
            {x+1, y},
            {x, y+1}
        };
        
        neighbours = legalise(grid, neighbours, xMax, yMax);
        return neighbours;

    }

    private int[][] legalise(int[][] grid, int[][] neighbours, int xMax, int yMax) {

        for (int row = 0; row < neighbours.length; row++) {
            int x = neighbours[row][1];
            int y = row;

            if (x < 0 || y < 0 || x > xMax || y > yMax || grid[y][x] == 1) 
                neighbours = removePosition(neighbours, row);
        } 
        return neighbours;
    }

    private int[][] removePosition(int[][] neighbours, int removeIndex) {
        int[][] updatedList = new int[neighbours.length - 1][2];
        for (int row = 0; row < neighbours.length; row++) {
            if (row == removeIndex) continue;
            updatedList[row] = neighbours[row];
        }

        return updatedList;
    }
 
}
