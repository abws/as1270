package research.simulatedannealing;

import java.util.ArrayList;
import java.util.Arrays;
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
        double initial = temperature;

        while (i < iterations) {
            Solution neighbour = pertubate(current);
            System.out.println(current.getFitness());

            double p = maxMetropolisAlgorithm(current.getFitness(), neighbour.getFitness(), temperature);
            // System.out.println("probs: "+ p);

            if (Math.random() <= p) {
                current = neighbour;
            }
            // temperature *= coolingRate;
            temperature = initial / i;
            // System.out.println("temp: " + temperature);
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

        double difference = (neighbour - current) * 100;

        double p = Math.exp(difference / t);
        return p;
    }

    private Solution pertubate(Solution current) {
        List<int[]> turbinePositions = new ArrayList<>();
        int[][] grid = current.getGrid();
        int[][] neighbours;
        int[] point;
        
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] == 1) 
                    turbinePositions.add(new int[]{x, y});
            }      
        }

        for (int[] turbinePos : turbinePositions) {
            int x = turbinePos[0];
            int y = turbinePos[1];
            neighbours = getNeighbours(grid, x, y, grid[y].length - 1, grid.length - 1);

            double partition = 1 / ((double) neighbours.length * 2); //staying still take half of all positions
            // double partition = 1 / (double) neighbours.length; //staying still take half of all positions

            int rowIndex = (int) Math.floor(Math.random() / partition);
            // System.out.println(neighbours.length+"--"+rowIndex);
            grid[y][x] = 0;
            point = neighbours[Math.min(rowIndex, neighbours.length - 1)]; //for when we want to stay still
            grid[point[1]][point[0]] = 1;
        }

        return new Solution(grid, problem);
    }

    private int[][] getNeighbours(int[][] grid, int x1, int y1, int xMax, int yMax) {
        List<int[]> neighboursList = new ArrayList<>();

        int[][] neighbours = new int[][] {
            {x1-1, y1},
            {x1, y1-1},
            {x1+1, y1},
            {x1, y1+1}
        };

        for (int[] n : neighbours) {
            int x = n[0];
            int y = n[1];

            if (!(x < 0 || y < 0 || x > xMax || y > yMax || grid[y][x] == 1)) {
                neighboursList.add(n);
            }
        }
        neighboursList.add(new int[]{x1, y1});

        neighbours = new int[neighboursList.size()][2];

        for (int i = 0; i < neighbours.length; i++) {
            neighbours[i] = neighboursList.get(i);
        }

        return neighbours;
    }
}
