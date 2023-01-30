package research.problems;

import research.api.java.*;

/**
 * Problem formulation for optimizing a wind farm
 * using the evaluation function provided by WindFLO
 * Defines the nature and constraints of the problem
 * Contains any intitialization parameters and any 
 * extra things needed by the Simple Genetic Algorithm
 * @author Abdiwahab Salah 38.5
 * @version 27.01.23
 */
public class ProblemSGA extends Problem{
    private KusiakLayoutEvaluator evaluator;
    private WindScenario scenario;
    private final int BASE = 2;

    public ProblemSGA(KusiakLayoutEvaluator evaluator, WindScenario scenario) throws Exception {
        this.scenario = scenario;
        this.evaluator = evaluator;
    }

    @Override
    public double evaluate(Object individual) {
        double[][] phenotype = decode(individual);
        double fitness = evaluator.evaluate(phenotype);

        return fitness;
    }

    @Override
    public Object encode(double[][] individual) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Decodes a string of size h*w with n 1s
     * into a n by 2 array corresponding to turbine 
     * coordinates
     * @param individual The string to be decoded
     * @return A two-dimensional array representing the grid as turbine coordinates
     */
    @Override
    public double[][] decode(Object individual) {
        int columns = (int) (scenario.width % scenario.minDist);
        int rows = (int) (scenario.height % scenario.minDist);

        int[][] gridIndividual = gridify((String) individual, columns, rows);

        double[][] layout = new double [scenario.nturbines][2];
        int count = 0;

        for (int i = 0; i < (rows); i++) {
            for (int j = 0; j < columns; j++) {
                if (gridIndividual[i][j] == 1) {
                    layout[count] = getCoordinates(i, j, scenario.minDist);
                    count++;
                }
            }
        }
        return layout;
    }

    private double[] getCoordinates(int y, int x, double minDist) {
        double [] coordinates = new double[2];
        coordinates[0] = (x * minDist + (minDist / 2));
        coordinates[1] = (y * minDist + (minDist / 2));

        return coordinates;

    }

    /**
     * Gridifies a string
     * @param ind The string to be gridified
     * @param x The width of the grid
     * @param y The height of the grid
     * @return A two-dimensional array representing the grid
     */
    private int[][] gridify(String ind, int x, int y) {
        int[][] grid = new int[y][x]; //[rows][columns] since rows are 'bigger' and classified by first
        int count = 0;

        for (int i = 0; i < y; i ++) {
            for (int j = 0; j < x; j++) {
                grid[i][j] = Character.getNumericValue(ind.charAt(count));
                System.out.println(grid[i][j]);
                count++;
            }
        }

        return grid;
    }


    /**
     * Encoding operator: Denary -> Signed Binary
     * @param num The denary representaion of the binary number
     * @return binary The binary number to decode 
     */
    public String encode(int num, int bits) {
        StringBuilder sb = new StringBuilder();
        for (int i = bits - 1; i >= 0; i--) {
            int bit = (num >> i) & 1;
            sb.append(bit);
        }

        return sb.toString();
    }
    
}
