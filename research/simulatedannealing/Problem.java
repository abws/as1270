package research.simulatedannealing;
import research.api.java.*;

public class Problem {
    private KusiakLayoutEvaluator evaluator;
    private WindScenario scenario;
    public int nTurbines;
    public int popSize;
    public int col;
    public int row;
    public double minDist;


    public Problem(KusiakLayoutEvaluator evaluator, WindScenario scenario, int populationSize) throws Exception {
        this.scenario = scenario;
        this.evaluator = evaluator;

        //Commonly used
        nTurbines = scenario.nturbines;
        popSize = populationSize;

        double minDist = 8 * scenario.R;
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
     * in the farm
     * @return
     */
    public Solution generateInitialSolution() {
        int[][] solution = new int[row][col];
        int count = 0;
        int step = (row * col) / nTurbines; 

        while (count < row * col) {
            int x = count / row;
            int y = count % col;

            solution[x][y] = 1;
            count += step;
        }

        return solution;
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
