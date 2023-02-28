package research.geneticalgorithm;

import java.util.ArrayList;
import java.util.List;

public class Mutation {
    private Problem problem;
    public double MUT_RATE;
    public int INDIV_LENGTH;
    public int columns;
    public int rows;


    Mutation(Problem problem, double mutRate) {
        this.problem = problem;
        this.INDIV_LENGTH = problem.INDIV_LENGTH;
        this.MUT_RATE = mutRate;

        columns = problem.columns;
        rows = problem.rows;
    }

    /**
     * Overview method.
     * Uses the bitmutation method
     * to randomly flip the bits in 
     * the whole population.
     * @param offSpring
     * @return
     */
    public List<Individual> mutatePopulationRandom(List<Individual> offSpring) {
        //mutation
        for (int i = 0; i < offSpring.size(); i++) {
            offSpring.set(i, bitMutation(offSpring.get(i)));    //mutate each individual in the offspring array
        }
        return offSpring;
    }

    /**
     * Bit flipping Mutation Operator.
     * Considers each gene independently.
     * May introduce or remove turbines from
     * the layout
     * @param parent1
     * @param parent2
     * @return
     */
    private Individual bitMutation(Individual individual) {
        StringBuilder indivArray = new StringBuilder(individual.getValue());

        for (int i = 0; i < indivArray.length(); i++) {
            if (Math.random() < MUT_RATE) {
                char newChar = indivArray.charAt(i) == '1' ? '0' : '1'; //flips the bit, //could use XOR
                indivArray.setCharAt(i, newChar);
            }
        }
        individual.setValue(indivArray.toString());
        return individual;
    }


    /**
     * Overview method
     * @param offSpring
     * @return
     */
    public List<Individual> mutatePopulationSlidingBox(List<Individual> offSpring) {
        //mutation
        for (int i = 0; i < offSpring.size(); i++) {
            Individual ind = offSpring.get(i);
            // int[][] grid = problem.gridify(o.getValue(), problem.columns, problem.rows);
            double[] boxes = slidingBoxProb(ind.getValue());

            offSpring.set(i, bitInformedMutation(ind, boxes));    //mutate each individual in the offspring array
        }
        return offSpring;

    }

    public double[] slidingBoxProb(String ind) {
        double[] boxes = new double[(rows - 1) * (columns - 1)];    //number of sliding boxes
        int count = 0;
        for (int y = 0; y < rows - 1; y+=2) {
            for (int x = 0; x < columns - 1; x+=2) {
                int a = Character.getNumericValue(ind.charAt(((y) * (columns-1)) + x));                  //the 4 coordinates of a box
                int b = Character.getNumericValue(ind.charAt(((y) * (columns-1)) + x + 1));
                int c = Character.getNumericValue(ind.charAt(((y+1) * (columns-1)) + x));
                int d = Character.getNumericValue(ind.charAt(((y+1) * (columns-1)) + x + 1));
                
                boxes[count] = (a + b + c + d) / 4.0;
                count++;
            }
        }

        return boxes;
    }

    public Individual bitInformedMutation(Individual ind, double[] boxes) { //boxes rows are 0 -> (col - 2)
        StringBuilder indivArray = new StringBuilder(ind.getValue());

        for (int i = 0; i < INDIV_LENGTH; i++) {
            double classis;     
            int y = i / columns;    //3rd and 4th arrows
            int x = i % columns;

            int boxRow = rows-2;
            int boxCol = columns-2;

            int bx1 = x, bx2 = x-1;
            int by1 = y, by2 = y-1;

            int[][] bValues = {     //4 boxes and their (starting) positions in the grid
                {bx1, by1},
                {bx1, by2},
                {bx2, by1},
                {bx2, by2}
            };

            double counter, sum, prob;
            counter = 1; sum = prob = 0 ;

            for (int[] c : bValues) {
                if ((c[0] < 0) || (c[0] > boxCol) || (c[1] < 0) || (c[1] > boxRow)) continue;   //skip infeasible box positions
                
                int a = c[0];
                int b = c[1];

                sum += boxes[(b * boxCol) + a];
                prob = sum / counter;
                counter++;
            }

            classis = this.MUT_RATE * (1/prob);
            if (Math.random() < classis) {     //greater p corresponds to more turbine density
                char newChar = indivArray.charAt(i) == '1' ? '0' : '0'; //flips the one
                indivArray.setCharAt(i, newChar);
            }

        }
        // System.out.println("Befow: "+problem.countTurbines(ind.getValue()));
        // System.out.println("After: "+problem.countTurbines(indivArray.toString()));
        ind.setValue(indivArray.toString());
        return ind; 
    }


}
