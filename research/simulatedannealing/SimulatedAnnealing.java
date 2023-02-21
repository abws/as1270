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

            double partition = 1 / (double) neighbours.length;
            int rowIndex = (int) Math.floor(Math.random() / partition);
    
            grid[y][x] = 0;
            point = neighbours[rowIndex];
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

    // private int[][] repairNeighbours(int[][] grid, int[][] neighbours, int xMax, int yMax) {
        
    //     for (int row = 0; row < neighbours.length; row++) {
    //         int x = neighbours[row][1];
    //         int y = row;

    //         if (x < 0 || y < 0 || x > xMax || y > yMax || grid[y][x] == 1) 
    //             neighbours = removeRow(neighbours, row);
    //     }

    //     return neighbours;
    // }

    // private int[][] removeRow(int[][] neighbours, int removeIndex) {
    //     int[][] updatedList = new int[neighbours.length - 1][2];
    //     int rowUpdated = 0;

    //     for (int row = 0; row < neighbours.length; row++) {

    //         if (row == removeIndex) continue;
    //         updatedList[rowUpdated] = neighbours[row];
    //         rowUpdated++;
    //     }

    //     return updatedList;
    // }

    // private int[][] addRow(int[][] neighbours, int[] coordinate) {
    //     int[][] updatedList = new int[neighbours.length + 1][2];
    //     updatedList[0] = coordinate;

    //     for (int row = 0; row < neighbours.length; row++) {   //updatedList[1:] = neighbours[0:];
    //         updatedList[row + 1] = neighbours[row];
    //     }

    //     return updatedList;
    // }
}
