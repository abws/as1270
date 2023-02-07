package research.geneticalgorithm;

import java.util.ArrayList;

public class Mutation {
    private Problem problem;
    public double MUT_RATE;
    public int INDIV_LENGTH;


    Mutation(Problem problem, double mutRate) {
        this.problem = problem;
        this.INDIV_LENGTH = problem.INDIV_LENGTH;
        this.MUT_RATE = mutRate;
    }

    public ArrayList<String> mutatePopulation(ArrayList<String> offSpring) {
        //mutation
        for (int i = 0; i < offSpring.size(); i++) {
            offSpring.set(i, bitMutation(offSpring.get(i)));    //mutate each individual in the offspring array
        }
        return offSpring;
    }

    /**
     * Bit flipping Mutation Operator.
     * Considers each gene independently
     * @param parent1
     * @param parent2
     * @return
     */
    private String bitMutation(String ind) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < INDIV_LENGTH; i++) {
            if (Math.random() < MUT_RATE) {
                char bit = (ind.charAt(i) == '1') ? '0' : '1'; //flips the bit, //could use XOR
                sb.append(bit);
            }
            else sb.append(ind.charAt(i));  //else add the bit as it is
        }

        return sb.toString();
    }
}
