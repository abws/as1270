package research.simulatedannealing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The Simulated Annealing Algorithm
 * Paramters that can be changed
 * include the neighbourhood operator,
 * the intial temperature, the cooling
 * rate and the acceptance formula.
 */
public class Test {
    double temperature;
    double coolingRate;
    int iterations;
    int stepConstant;
    Problem problem;

    Test(double temperature, double coolingRate, int iterations, int stepConstant, Problem problem) {
        this.temperature = temperature;
        this.coolingRate = coolingRate;
        this.iterations = iterations;
        this.stepConstant = stepConstant;

        this.problem = problem;
    }

    public void run() throws IOException {
        State current = problem.generateInitialState();
        int i = 0;
        String outputFilename = "/Users/abdiwahabsalah/Documents/GitLab/as1270/research/simulatedannealing/results/test1-good.txt";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilename))) {
            for (int n = 0; n < 1; n++) {
                while (i < iterations) {
                    System.out.println(current.getFitness());
                    bw.write(temperature+"");
                    bw.newLine();

                    // State neighbour = perturbate(current);
                    // State neighbour = relocate(current);
                    // State neighbour = relocateSlidingBox(current);
                    State neighbour = relocateSlidingBoxGreedy(current);



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

        double difference = -(current - neighbour) * 1000;

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

    private State relocateSlidingBox(State current) {
        Random r = new Random();
        String state = problem.stringify(current.getGrid());
        StringBuilder stateArray = new StringBuilder(state);

        for (int i = 0; i < (stateArray.length()); i++) {
            if (stateArray.charAt(i) != '1') continue; //only change if its a one
            int position1 = i;

            int position2 = r.nextInt(stateArray.length());

            double rate = problem.slidingBox(new String(stateArray.toString()), position1);
            if (Math.random() < rate*(1-temperature)) {
                char temp = stateArray.charAt(position1);
                stateArray.setCharAt(position1, stateArray.charAt(position2));
                stateArray.setCharAt(position2, temp);  
            }
        }

        int[][] grid = problem.gridify(stateArray.toString(), problem.col, problem.row);
        return new State(grid, problem);
    }

    private State relocateSlidingBoxGreedy(State current) {
        Random r = new Random();
        String state = problem.stringify(current.getGrid());
        StringBuilder stateArray = new StringBuilder(state);

        for (int i = 0; i < (stateArray.length()); i++) {
            if (stateArray.charAt(i) != '1') continue; //only change if its a one
            int position1 = i;


            int position2 = r.nextInt(stateArray.length());
            char newChar = stateArray.charAt(position2) == '1' ? '0' : '1'; //flips the bit, //could use XOR

            position2 = problem.getNearest(stateArray.toString(), position2, newChar);

            // double rate = MUT_RATE;
            double rate = problem.slidingBox(new String(stateArray.toString()), position1);
            if (Math.random() < rate*(1-temperature)) {
                char temp = stateArray.charAt(position1);
                stateArray.setCharAt(position1, stateArray.charAt(position2));
                stateArray.setCharAt(position2, temp);  
            }
        }

        int[][] grid = problem.gridify(stateArray.toString(), problem.col, problem.row);
        return new State(grid, problem);
    }


}
