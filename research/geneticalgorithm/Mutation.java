package research.geneticalgorithm;

import java.util.ArrayList;
import java.util.List;

public class Mutation {
    private Problem problem;
    public double MUT_RATE;
    public int INDIV_LENGTH;


    Mutation(Problem problem, double mutRate) {
        this.problem = problem;
        this.INDIV_LENGTH = problem.INDIV_LENGTH;
        this.MUT_RATE = mutRate;
    }

    public List<Individual> mutatePopulation(List<Individual> offSpring) {
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
}
