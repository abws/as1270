package research.geneticalgorithm;
import java.util.List;
import java.util.Random;


/**
 * Mutation class.
 * Each mutation strategy has a main method that 
 * iterates through the individuals. Each also has a 
 * specific operator. 
 */
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
     * Bit flipping Mutation Operator.
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
     * Overview method.
     * Uses the bitmutation method
     * to randomly flip the bits in 
     * the whole population.
     * @param offSpring
     * @return
     */
    public List<Individual> mutatePopulationReflectRandom(List<Individual> offSpring) {
        //mutation
        for (int i = 0; i < offSpring.size(); i++) {
            offSpring.set(i, bitMutationReflect(offSpring.get(i)));    //mutate each individual in the offspring array
        }
        return offSpring;
    }

    private Individual bitMutationReflect(Individual individual) {
        StringBuilder indivArray = new StringBuilder(individual.getValue());

        for (int i = 0; i < (indivArray.length()/2); i++) {
            if (Math.random() < MUT_RATE) {
                char newChar = indivArray.charAt(i) == '1' ? '0' : '1'; //flips the bit, //could use XOR
                indivArray.setCharAt(i, newChar);

                int relection = getNearest(indivArray.toString(), indivArray.length()- 1 - i, newChar);    //replicate whatever we do on the other side of the string; assumes a string represents a farm well, which isnt really the case
                char oppositeChar = newChar == '1' ? '0' : '1'; //flips the bit, //could use XOR

                indivArray.setCharAt(relection, oppositeChar);
            }
        }
        individual.setValue(indivArray.toString());
        return individual;
    }

    /**
     * Moves a turbine to
     * a random feasible location
     * @param offSpring
     * @return
     */
    public List<Individual> mutatePopulationMoveRandom(List<Individual> offSpring) {
        //mutation
        for (int i = 0; i < offSpring.size(); i++) {
            offSpring.set(i, bitMutationMove(offSpring.get(i)));    //mutate each individual in the offspring array
        }
        return offSpring;
    }

    private Individual bitMutationMove(Individual individual) {
        StringBuilder indivArray = new StringBuilder(individual.getValue());
        Random rand = new Random();

        for (int i = 0; i < indivArray.length(); i++) {
            if (Math.random() < MUT_RATE) {
                char newChar = indivArray.charAt(i) == '1' ? '0' : '1'; //flips the bit, //could use XOR
                indivArray.setCharAt(i, newChar);
                int position = rand.nextInt(indivArray.length());
                position = getNearest(indivArray.toString(), position, newChar);   
                char oppositeChar = newChar == '1' ? '0' : '1'; //flips the bit, //could use XOR
                indivArray.setCharAt(position, oppositeChar);
            }
        }
        individual.setValue(indivArray.toString());
        return individual;
    }

    /**
     * Basic Swap Mutation
     * @param offSpring
     * @return
     */
    public List<Individual> mutatePopulationSwap(List<Individual> offSpring) {
        //mutation
        for (int i = 0; i < offSpring.size(); i++) {
            offSpring.set(i, mutationSwap(offSpring.get(i)));    //mutate each individual in the offspring array
        }
        return offSpring;
    }

    private Individual mutationSwap(Individual individual) {
        StringBuilder indivArray = new StringBuilder(individual.getValue());
        Random rand = new Random();


        for (int i = 0; i < (indivArray.length()/2); i++) {
            if (Math.random() < MUT_RATE) {
                //pick two random positions, and swap them
                int position1 = rand.nextInt(indivArray.length());
                int position2 = rand.nextInt(indivArray.length());
                char newChar = indivArray.charAt(position1) == '1' ? '0' : '1'; //flips the bit, //could use XOR

                position2 = getNearest(indivArray.toString(), position2, newChar);
                char temp = indivArray.charAt(position1);
                indivArray.setCharAt(position1, indivArray.charAt(position2));
                indivArray.setCharAt(position2, temp);
            }
        }
        individual.setValue(indivArray.toString());
        return individual;
    }
    
    /**
     * Reflective Swap Mutation
     * @param offSpring
     * @return
     */
    public List<Individual> mutatePopulationSwapReflect(List<Individual> offSpring) {
        //mutation
        for (int i = 0; i < offSpring.size(); i++) {
            offSpring.set(i, bitMutationSwapReflect(offSpring.get(i)));    //mutate each individual in the offspring array
        }
        return offSpring;
    }

    private Individual bitMutationSwapReflect(Individual individual) {
        StringBuilder indivArray = new StringBuilder(individual.getValue());

        for (int i = 0; i < (indivArray.length()/2); i++) {
            if (Math.random() < MUT_RATE) {
                char c = indivArray.charAt(i); //current char
                int j = indivArray.length()- 1 - i;   //reflective position
                char r = indivArray.charAt(j); //reflective char

                indivArray.setCharAt(i, r); //swap their positions
                indivArray.setCharAt(j, c);
            }
        }
        individual.setValue(indivArray.toString());
        return individual;
    }

    /**
     * Informed Deterministic Swap Mutation
     * @param offSpring
     * @return
     */
    public List<Individual> mutatePopulationDeterministicSwap(List<Individual> offSpring) {
        //mutation
        for (int i = 0; i < offSpring.size(); i++) {
            offSpring.set(i, bitMutationDeterministicSwap(offSpring.get(i)));    //mutate each individual in the offspring array
        }
        return offSpring;
    }

    private Individual bitMutationDeterministicSwap(Individual individual) {
        StringBuilder indivArray = new StringBuilder(individual.getValue());
        Random rand = new Random();

        problem.evaluate(individual.getValue());
        double[] turbineIndexes = problem.evaluator.getTurbineFitnesses();
        for (int i = 0; i < (indivArray.length()/2); i++) {
            if (Math.random() < MUT_RATE) {
                //deterministic step - USES FITNESS EVALUATION

                int coordinatePosition = problem.lowestIndex(turbineIndexes); //position of turbine in list of turbines
                int position1 = problem.getPosition(individual.getValue(), coordinatePosition); //position of turbine in grid

                //pick 1 random position, and swap with 
                int position2 = rand.nextInt(indivArray.length());
                char temp = indivArray.charAt(position1);
                indivArray.setCharAt(position1, indivArray.charAt(position2));
                indivArray.setCharAt(position2, temp);
            }
        }
        individual.setValue(indivArray.toString());
        return individual;
    }

    /**
     * Overview method
     * Obsolete. Please refer to chapter 4.1 for more details
     * @param ind
     * @return
     */
    public List<Individual> mutatePopulationStationaryBox(List<Individual> offSpring) {
        //mutation
        for (int i = 0; i < offSpring.size(); i++) {
            Individual ind = offSpring.get(i);
            // int[][] grid = problem.gridify(o.getValue(), problem.columns, problem.rows);
            double[] boxes = stationaryBoxes(ind.getValue());

            offSpring.set(i, stationaryBoxMutation(ind, boxes));    //mutate each individual in the offspring array
        }
        return offSpring;

    }

    public Individual stationaryBoxMutation(Individual ind, double[] boxes) { //boxes rows are 0 -> (col - 2)
        StringBuilder indivArray = new StringBuilder(ind.getValue());

        for (int i = 0; i < INDIV_LENGTH; i++) {
            double classis;
            int y = i / columns;
            int x = i % columns;

            int boxRow = rows-2;
            int boxCol = columns-2;

            int bx1 = x, bx2 = x-1;
            int by1 = y, by2 = y-1;

            int[][] bValues = {
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
        ind.setValue(indivArray.toString());
        return ind; 
    }


    /**
     * Overview method.
     * Uses the bitmutation method
     * to randomly flip the bits in 
     * the whole population using a sliding
     * box heuristic.
     * Stopped being updated as of March 2023. 
     * Please refer to the swap sliding box method
     * @param offSpring
     * @return
     */
    public List<Individual> mutatePopulationSlidingBox(List<Individual> offSpring) {
        //mutation
        for (int i = 0; i < offSpring.size(); i++) {
            offSpring.set(i, bitMutationSlidingBox(offSpring.get(i)));    //mutate each individual in the offspring array
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
    private Individual bitMutationSlidingBox(Individual individual) {
        StringBuilder indivArray = new StringBuilder(individual.getValue());

        for (int i = 0; i < indivArray.length(); i++) {
            double newRate = slidingBox(new String(indivArray.toString()), i);
            if (indivArray.charAt(i) != 1) { //only change if its a one
                newRate = MUT_RATE;
            }

            newRate = MUT_RATE * (1 + (newRate));
            if (Math.random() <newRate) {
                char newChar = indivArray.charAt(i) == '1' ? '0' : '1'; //flips the bit, //could use XOR
                indivArray.setCharAt(i, newChar);
            }
        }
        individual.setValue(indivArray.toString());
        return individual;
    }

    /**
     * Swap Mutation with Sliding Box heuristic
     * @param offSpring
     * @return
     */
    public List<Individual> mutatePopulationSwapSlidingBox(List<Individual> offSpring, double g) {
        //mutation
        for (int i = 0; i < offSpring.size(); i++) {
            offSpring.set(i, bitMutationSwapSlidingBox(offSpring.get(i), g));    //mutate each individual in the offspring array
        }
        return offSpring;
    }

    private Individual bitMutationSwapSlidingBox(Individual individual, double g) {
        StringBuilder indivArray = new StringBuilder(individual.getValue());
        Random rand = new Random();


        for (int i = 0; i < (indivArray.length()); i++) {
            if (indivArray.charAt(i) != '1') continue; //only change if its a one
            int position1 = i;
            int position2 = rand.nextInt(indivArray.length());
            char newChar = indivArray.charAt(position2) == '1' ? '0' : '1'; //flips the bit, //could use XOR

            position2 = getNearest(indivArray.toString(), position2, newChar);

            double rate = slidingBox(new String(indivArray.toString()), position1);
            rate = MUT_RATE * (1 + (g*(rate)));

            if (Math.random() < rate) {
                char temp = indivArray.charAt(position1);
                indivArray.setCharAt(position1, indivArray.charAt(position2));
                indivArray.setCharAt(position2, temp);
            }
        }
        individual.setValue(indivArray.toString());
        return individual;
    }

    /**
     * Helper functions
     */

    /**
     * Builds a 3x3 box around the turbine of focus
     * @param individual
     * @param position
     */
    public double slidingBox(String individual, int position) {
        //bits' position in farm
        int y = (position / columns) + 1;
        int x = (position % columns) + 1;
        int[][] grid = gridifyZeroPad(individual, columns, rows);

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
        int[][] grid = new int[y+2][x+2];   //by default cells are zero
        int count = 0;

        for (int i = 1; i < y-1; i ++) {
            for (int j = 1; j < x-1; j++) {
                grid[i][j] = Character.getNumericValue(ind.charAt(count));
                count++;
            }
        }

        return grid;
    }

    /**
     * Persists the two locations being swapped
     * to be different
     * @param indiv
     * @param position
     * @param value
     * @return
     */
    private int getNearest(String indiv, int position, char value) {//value is either '1' or '0'
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
     * Obsolete. Please refer to chapter 4.1 for more details
     * @param ind
     * @return
     */
    public double[] stationaryBoxes(String ind) {
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
}
