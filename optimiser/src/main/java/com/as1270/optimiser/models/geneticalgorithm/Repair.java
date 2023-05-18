package com.as1270.optimiser.models.geneticalgorithm;

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
     * Informed repair.
     * Removes the worst turbines
     * if excess. Otherwise random.
     * Warning: Calls evaluation function! 
     * @param pop
     * @return
     */
    public List<Individual> repairInformed(List<Individual> pop) {
            for (int i = 0; i < pop.size(); i++) {
                Individual ind = pop.get(i);
                ind = removeWorst(ind);
                pop.set(i, ind);    //mutate each individual in the offspring array
            }
        return pop;
    }

    public Individual removeWorst(Individual ind) {
        Random r = new Random();
        String value = ind.getValue();
        StringBuilder indivArray = new StringBuilder(value);

        int turbineCount = problem.countTurbines(value);
        int difference = turbineCount - problem.N_TURBINES;
        if (difference > 0) problem.evaluate(ind.getValue());
        double[] turbineIndexes = problem.evaluator.getTurbineFitnesses();

        while (difference > 0) {    //we have too many turbines
            int coordinatePosition = problem.lowestIndex(turbineIndexes); //position of turbine in list of turbines
            int position = problem.getPosition(ind.getValue(), coordinatePosition); //position of turbine in grid
            Character c = indivArray.charAt(position);
            if (c == '1') {
                indivArray.setCharAt(position, '0');
                difference--;
            }

        }

        while (difference < 0) {    //we have too few turbines
            int position = r.nextInt(INDIV_LENGTH); //position to add turbine to
            Character c = indivArray.charAt(position);
            if (c == '0') {
                indivArray.setCharAt(position, '1');
                difference++;
            }
        }
        ind.setValue(indivArray.toString());
        return ind; 
    }
    





  /**
   * WARNING.
   * BEWARE OF USING THE FOLLOWING CODE.
   * IT IS FOR EXPERIMENTAL PURPOSES
   * AND CONTAINS AND NUMBER OF SIGNIFICANT
   * BUGS.
   * USER DISCRETION IS ADVISED.
   */


    /**
     * Overview method
     * @param offSpring
     * @return
     */
    public List<Individual> repairSlidingBox(List<Individual> pop) {
        //mutation
        for (int i = 0; i < pop.size(); i++) {
            Individual ind = pop.get(i);
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
        StringBuilder indivArray = new StringBuilder(ind.getValue());
        double[] probs = new double[ind.getValue().length()];

        for (int i = 0; i < INDIV_LENGTH; i++) {
            int y = i / columns;    //grid position of current farm position
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
            // System.out.println(c);
            probs[position] = 1;

            if (c == '1') {   //maybe limit how many times we do this since we could get stuck
                indivArray.setCharAt(position, '0');
                difference--;
            }
        }
        while (difference < 0) {    //we have too few turbines
            int position = leastDense(probs); //position to add turbine to
            Character c = indivArray.charAt(position);

            probs[position] = 0;
            if (c == '0') {
                indivArray.setCharAt(position, '1');
                difference++;
            }
        }
        // System.out.println("Befow: "+problem.countTurbines(ind.getValue()));
        // System.out.println("After: "+problem.countTurbines(indivArray.toString()));
        ind.setValue(indivArray.toString());
        return ind; 
    }

    private int mostDense(double[] probs) {
        int maxIndex = 0;
        for (int i = 0; i < probs.length; i++ ) {
            if (probs[i] >= probs[maxIndex]) maxIndex = i;
        }

        return maxIndex;
    }

    private int leastDense(double[] probs) {
        int minIndex = 0;
        for (int i = 0; i < probs.length; i++ ) {
            if (probs[i] <= probs[minIndex]) minIndex = i;
            if (probs[minIndex] == 0) break; //since we cannot get any lower anyway
        }

        return minIndex;
    }
    

    /**
     * Overview method
     * @param offSpring
     * @return
     */
    public List<Individual> repairSimpleWindow(List<Individual> pop) {
        //mutation
        for (int i = 0; i < pop.size(); i++) {
            Individual ind = pop.get(i);
            double[] windows = simpleWindows(ind.getValue());

            pop.set(i, window(ind, windows));    //mutate each individual in the offspring array
        }
        return pop;
    }

    public double[] simpleWindows(String ind) {
        double[] windows = new double[(rows-2) * (columns-2)];
        int count = 0;

        for (int y = 0; y < rows - 2; y++ ) {
            for (int x = 0; x < columns - 2; x++) {
                int sum = 
                Character.getNumericValue(ind.charAt(y * (x))) +
                Character.getNumericValue(ind.charAt(y * (x+1))) +
                Character.getNumericValue(ind.charAt(y * (x+2))) +
                Character.getNumericValue(ind.charAt(y+1 * (x))) +
                Character.getNumericValue(ind.charAt(y+1 * (x+1))) +
                Character.getNumericValue(ind.charAt(y+1 * (x+2))) +
                Character.getNumericValue(ind.charAt(y+2 * (x))) +
                Character.getNumericValue(ind.charAt(y+2 * (x+1))) +
                Character.getNumericValue(ind.charAt(y+2 * (x+2)));
                windows[count] = sum / 9.0;
                count++;
            }
        }
        return windows;
    }

    public Individual window(Individual ind, double[] windows) {
        StringBuilder indivArray = new StringBuilder(ind.getValue());
        Random r = new Random();

        int turbineCount = problem.countTurbines(ind.getValue());
        int difference = turbineCount - problem.N_TURBINES;
        while (difference > 0) {    //we have too many turbines

            int position = mostDense(windows); 
            int y = (position / (columns - 2));
            int x = (position % (columns - 2)); //position to remove turbine from
            int indivPosition = (y + r.nextInt(3)) * (x + r.nextInt(3));
            Character c = indivArray.charAt(indivPosition); //get one of the turbines in the window

            if (c == '1') {   //maybe limit how many times we do this since we could get stuck
                windows[position] = ((windows[position] * 9) - 1) / 9;
                indivArray.setCharAt(indivPosition, '0');
                difference--;
            }
        }

        while (difference < 0) {    //we have too few turbines
            int position = leastDense(windows); //position to add turbine to
            int y = (position / (columns - 2));
            int x = (position % (columns - 2)); //position to remove turbine from
            int indivPosition = (y + r.nextInt(3)) * (x + r.nextInt(3));
            Character c = indivArray.charAt(indivPosition); //get one of the turbines in the window

            if (c == '0') {
                windows[position] = ((windows[position] * 9) + 1) / 9;

                indivArray.setCharAt(indivPosition, '1');
                difference++;
            }
        }
        ind.setValue(indivArray.toString());
        return ind;
    }
}
