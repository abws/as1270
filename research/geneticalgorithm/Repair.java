package research.geneticalgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Repair {
    private Problem problem;
    public double MUT_RATE;
    public int INDIV_LENGTH;
    public int columns;
    public int rows;


    Repair(Problem problem) {
        this.problem = problem;
        this.INDIV_LENGTH = problem.INDIV_LENGTH;

        columns = problem.columns;
        rows = problem.rows;
    }

    /**
     * Repair operator.
     * Shoots randomly at the farm
     * and eliminates or introduces 
     * as many turbines as needed.
     * Pretty efficient (only loops 
     * whole farm once)
     * @param pop
     * @return
     */
    public List<Individual> repairRandom(List<Individual> pop) {
        Random r = new Random();
        List<Individual> cleanPop = new ArrayList<>();

        for (int i = 0; i < pop.size(); i++) {
            Individual individual = pop.get(i);
            String value = pop.get(i).getValue();
            StringBuilder sb = new StringBuilder(value);

            int turbineCount = problem.countTurbines(value);
            int difference = turbineCount - problem.N_TURBINES;
            
            while (difference > 0) {    //we have too many turbines
                int position = r.nextInt(INDIV_LENGTH); //position to remove turbine from
                Character c = sb.charAt(position);
                if (c == '1') {
                    sb.setCharAt(position, '0');
                    difference--;
                }
            }
            while (difference < 0) {    //we have too few turbines
                int position = r.nextInt(INDIV_LENGTH); //position to add turbine to
                Character c = sb.charAt(position);
                if (c == '0') {
                    sb.setCharAt(position, '1');
                    difference++;
                }
            }
            individual.setValue(sb.toString());
            cleanPop.add(individual);
        }
        return cleanPop;
    }

     /**
     * Overview method
     * @param offSpring
     * @return
     */
    public List<Individual> repairSlidingBox(List<Individual> pop) {
        //mutation
        for (int i = 0; i < pop.size(); i++) {
            Individual ind = pop.get(i);
            // int[][] grid = problem.gridify(o.getValue(), problem.columns, problem.rows);
            double[] boxes = slidingBoxProb(ind.getValue());

            pop.set(i, slidingBox(ind, boxes));    //mutate each individual in the offspring array
        }
        return pop;
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

    public Individual slidingBox(Individual ind, double[] boxes) { //boxes rows are 0 -> (col - 2)
        Random r = new Random();
        StringBuilder indivArray = new StringBuilder(ind.getValue());
        double[] probs = new double[ind.getValue().length()];

        for (int i = 0; i < INDIV_LENGTH; i++) {
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

            double counter, sum, p;
            counter = 1; sum = p = 0 ;

            for (int[] c : bValues) {
                if ((c[0] < 0) || (c[0] > boxCol) || (c[1] < 0) || (c[1] > boxRow)) continue;   //skip infeasible box positions
                
                int a = c[0];
                int b = c[1];

                sum += boxes[(b * boxCol) + a];
                p = sum / counter;
                counter++;
            }
            probs[i] = p;

        }

        int turbineCount = problem.countTurbines(ind.getValue());
        int difference = turbineCount - problem.N_TURBINES;
    
        while (difference > 0) {    //we have too many turbines
            int position = mostDense(probs); //position to remove turbine from
            Character c = indivArray.charAt(position);
            //System.out.println(probs[position]);

            if (c == '1' && probs[position] > 0.4) {   //maybe limit how many times we do this since we could get stuck
                indivArray.setCharAt(position, '0');
                difference--;
            }
        }
        while (difference < 0) {    //we have too few turbines
            int position = r.nextInt(INDIV_LENGTH); //position to add turbine to
            Character c = indivArray.charAt(position);
            if (c == '0' && probs[position] == 0) {
                indivArray.setCharAt(position, '1');
                difference++;
            }
        }
        // System.out.println("Befow: "+problem.countTurbines(ind.getValue()));
        // System.out.println("After: "+problem.countTurbines(indivArray.toString()));
        ind.setValue(indivArray.toString());
        return ind; 
    }

    

    
}
