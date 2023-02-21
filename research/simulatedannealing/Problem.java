package research.simulatedannealing;
import java.util.Arrays;
import java.util.Random;

import research.api.java.*;

public class Problem {
    private KusiakLayoutEvaluator evaluator;
    private WindScenario scenario;
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
                    layout[count] = getCoordinates(i, j, minDist);
                    count++;
                }
            }
        }
        return layout;
    }
    

    /**
     * Generates a solution whereby
     * the turbines are evenly spaced
     * in the farm. Got to be careful since
     * our starting position in the search
     * space really matters.
     * @return
     */
    public Solution generateInitialSolution() {
        int[][] solution = new int[row][col];
        int x, y;
        Random r = new Random();
        double count = 0;
        double step = (row * col) / (double) nTurbines;   //by taking a greater step, we can avoid unnecessarily crowing turbines (will get less than nturbines). By doing this, we can then exploit random position to further benefit the cause

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

        return new Solution(solution, this);
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

    
}
