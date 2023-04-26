package research.simulatedannealing;
import java.util.Arrays;
import java.util.Random;

import research.api.java.*;

public class Problem {
    public KusiakLayoutEvaluator evaluator;
    public WindScenario scenario;
    public int nTurbines;
    public int popSize;
    public int col;
    public int row;
    public double minDist;


    public Problem(KusiakLayoutEvaluator evaluator, WindScenario scenario) throws Exception {
        this.scenario = scenario;
        this.evaluator = evaluator;

        //Commonly used
        nTurbines = scenario.nturbines;

        this.minDist = 8 * scenario.R;
        this.col = (int) (scenario.width / minDist);
        this.row = (int) (scenario.height / minDist);
    }

    public double evaluate(int[][] solution) {
        double[][] layout = decode(solution);
        double fitness = evaluator.evaluate_2014(layout);

        return fitness;
    }

    public double[][] decode(int[][] solution) {
        double[][] layout = new double [nTurbines][2];
        int count = 0;

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (solution[i][j] == 1) {
                    layout[count] = getMeshCoordinates(i, j, minDist);
                    count++;
                }
            }
        }
        return layout;
    }
    
    /**
     * Gridifies a string
     * @param ind The string to be gridified
     * @param x The width of the grid
     * @param y The height of the grid
     * @return A two-dimensional array representing the grid
     * @tested
     */
    public int[][] gridify(String state, int x, int y) {
        int[][] grid = new int[y][x]; //[rows][columns] since rows are 'bigger' and classified by first
        int count = 0;

        for (int i = 0; i < y; i ++) {
            for (int j = 0; j < x; j++) {
                grid[i][j] = Character.getNumericValue(state.charAt(count));
                count++;
            }
        }

        return grid;
    }

    public String stringify(int[][] grid) {
        String state = "";

        for (int i = 0; i < grid.length; i ++) {
            for (int j = 0; j < grid[0].length; j++) {
                state += grid[i][j];
            }
        }

        return state;
    }

    /**
     * Generates a solution whereby
     * the turbines are evenly spaced
     * in the farm. Got to be careful since
     * our starting position in the search
     * space really matters.
     * @return
     */
    public State generateInitialState() {
        int[][] solution = new int[row][col];
        int x, y;
        Random r = new Random();
        double count = 0;
        double step = (row * col) / (double) nTurbines;   //by taking a greater step, we can avoid unnecessarily crowding turbines (will get less than nturbines). By doing this, we can then exploit random position to further benefit the cause

        while (count < row * col) {
            y = ((int) count / col);
            x = ((int) count % col);
            
            solution[y][x] = 1;
            count += step;
        }

        int current = (int) (count / step); //should be as close to nturbines as possible here

        while (current != nTurbines) {    //randomly fill with remaining turbines||||will no longer need to run this!!
            x = r.nextInt(col);
            y = r.nextInt(row);

            if (solution[y][x] != 1) {
                solution[y][x] = 1;
                current++;
            }
        }

        return new State(solution, this);
    }

    /**
     * Helper method to return
     * turbine coordinates given 
     * cell they're in within grid
     * @param y
     * @param x
     * @param minDist
     * @return Coordinates (x, y)
     * Tested
     */
    private double[] getCoordinates(int y, int x, double minDist) {
        double [] coordinates = new double[2];
        coordinates[0] = ((x * minDist) + (minDist / 2));
        coordinates[1] = ((y * minDist) + (minDist / 2));

        return coordinates;
    }

    private double[] getMeshCoordinates(int y, int x, double minDist) {
        int maxX = (int) (scenario.width / minDist);
        int maxY = (int) (scenario.height / minDist);

        double [] coordinates = new double[2];
        coordinates[0] = (x * minDist) + (y * (minDist / maxY));

        coordinates[1] = ((maxY - y) * minDist) - (x * (minDist / maxX));

        return coordinates;
    }

    public int getNearest(String indiv, int position, char value) {//value is either '1' or '0'
    Random rand = new Random();
    int r, l;
    r = l = position;
    while (indiv.charAt(position) != value) { //basically while true if we ever enter the loop
        boolean b = rand.nextBoolean();
        if (b) {
            r = Math.min(r+2, indiv.length()-1);   //to move as far as possible from 'bad' regions (regions that are rich with the thing we want to add or remove) - experiment with how this value changes optimisation, if it does
            if (indiv.charAt(r) == value) return r;

        } else {
            l = Math.max(l-2, 0);
            if (indiv.charAt(l) == value) return l;

        }


    }
    return position;
}

    /**
     * Builds a 3x3 box around the turbine of focus
     * @param individual
     * @param position
     */
    public double slidingBox(String individual, int position) {
        //bits' position in farm
        int y = (position / col) + 1;
        int x = (position % col) + 1;
        int[][] grid = gridifyZeroPad(individual, col, row);

        double sum = 
        grid[y-1][x-1] +
        grid[y-1][x] +
        grid[y-1][x+1] +
        grid[y][x-1] +
        grid[y][x] +
        grid[y][x+1] +
        grid[y+1][x-1] +
        grid[y+1][x] +
        grid[y+1][x+1];

        sum /= 9;
        return sum;
    }

        
    /**
     * Gridifies a string
     * @param ind The string to be gridified
     * @param x The width of the grid
     * @param y The height of the grid
     * @return A two-dimensional array representing the grid
     * @tested
     */
    public int[][] gridifyZeroPad(String ind, int x, int y) {
        int[][] grid = new int[y+2][x+2]; //[rows][columns] since rows are 'bigger' and classified by first
        int count = 0;

        for (int i = 1; i < y-1; i ++) {
            for (int j = 1; j < x-1; j++) {
                grid[i][j] = Character.getNumericValue(ind.charAt(count));
                count++;
            }
        }

        return grid;
    }


    
}
