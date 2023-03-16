package research.geneticalgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

        for (int i = 0; i < indivArray.length(); i++) {
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


}
