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

    @Override
    public double[][] decode(String individual) {
        int columns = (int) (scenario.width % scenario.minDist);
        double rows = (int) scenario.height % scenario.minDist;

        int[][] gridIndividual = gridify(individual, columns, rows);

        double[][] layout = new double [scenario.nturbines][2];

        for (int i = 0; i < (columns); i++) {
            for (int j = 0; j < rows; j++) {
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
