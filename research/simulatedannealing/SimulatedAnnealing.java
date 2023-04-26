package research.simulatedannealing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
    int stepConstant;
    Problem problem;

    SimulatedAnnealing(double temperature, double coolingRate, int iterations, int stepConstant, Problem problem) {
        this.temperature = temperature;
        this.coolingRate = coolingRate;
        this.iterations = iterations;
        this.stepConstant = stepConstant;

        this.problem = problem;
    }

    public void run() {
        State current = problem.generateInitialState();
        int i = 0;

        while (i < iterations) {
            System.out.println(current.getFitness());

            // State neighbour = perturbate(current);
            State neighbour = relocate(current);

            double p = maxMetropolisAlgorithm(current.getFitness(), neighbour.getFitness(), temperature);
            // System.out.println("probs: "+ p);
            if (Math.random() <= p) {
                current = neighbour;
            }
            temperature *= coolingRate;
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

        double difference = -(current - neighbour) * 100;

        double p = Math.exp(difference / t);
        return p;
    }

    /**
     * Neighbourhood function.
     * Treats each turbine position
     * independently, and moves it
     * with some probability to neighbouring
     * regions (up, down, left, right)
     * @param current
     * @return
     */
    private State perturbate(State current) {
        List<int[]> turbinePositions = new ArrayList<>();
        int[][] grid = current.getGrid();
        int[][] neighbours;
        int[] point;
        
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] == 1) 
                    turbinePositions.add(new int[]{x, y});  //grid coordinates of turbines
            }      
        }

        for (int[] turbinePos : turbinePositions) {
            int x = turbinePos[0];
            int y = turbinePos[1];
            neighbours = getNeighbours(grid, x, y);

            double partition = 1 / ((double) neighbours.length * stepConstant); //staying still takes n number of times the probability than other

            int rowIndex = (int) Math.floor(Math.random() / partition); //1 gives the end of the last partition index. All others are somewhere inbetween

            grid[y][x] = 0;
            point = neighbours[Math.min(rowIndex, neighbours.length - 1)]; //the end (staying still) takes everytime rowIndex is higher than it (half the times)
            grid[point[1]][point[0]] = 1;
        }

        return new State(grid, problem);
    }

    /**
     * Neighbourhood function helper.
     * Calculates the neighbouring cells
     * of each turbines position and only
     * returns those that are not occupied 
     * and within the bounds of the grid.
     * @param grid
     * @param x1
     * @param y1
     * @param xMax
     * @param yMax
     * @return
     */
    private int[][] getNeighbours(int[][] grid, int x1, int y1) {
        List<int[]> neighboursList = new ArrayList<>();
        int xMax = grid[y1].length - 1;
        int yMax = grid.length - 1;

        int[][] neighbours = new int[][] {
            {x1-1, y1},
            {x1, y1-1},
            {x1+1, y1},
            {x1, y1+1}
        };

        for (int[] n : neighbours) {
            int x = n[0];
            int y = n[1];

            if (!(x < 0 || y < 0 || x > xMax || y > yMax || grid[y][x] == 1)) { //remove infeasible positions
                neighboursList.add(n);
            }
        }
        neighboursList.add(new int[]{x1, y1});  //current position + all other neighbours

        neighbours = new int[neighboursList.size()][2];

        for (int i = 0; i < neighbours.length; i++) {
            neighbours[i] = neighboursList.get(i);  //copy
        }

        return neighbours;
    }
    /**
     * Neighbourhood function.
     * Relocates n positions using
     * a swap operator.
     * @param current
     * @return
     */
    private State relocate(State current) {
        Random r = new Random();
        int[][] grid = current.getGrid();
        for (int i = 0; i < stepConstant; i++) {
            int x1 = r.nextInt(problem.col); int x2 = r.nextInt(problem.col);
            int y1 = r.nextInt(problem.row); int y2 = r.nextInt(problem.row);

            int temp = grid[y1][x1];
            grid[y1][x1] = grid[y2][x2];
            grid[y2][x2] = temp;
        }

        return new State(grid, problem);
    }


}
