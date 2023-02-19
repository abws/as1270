package research.geneticalgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * updateAllFitnesses must be called at the start of every function since children's fitness hasnt been tested just yet
 */
public class Replacement {
    Problem problem;

    Replacement(Problem problem) { //add so user can change using input
        this.problem = problem;
    }
    
    public List<Individual> deleteOldest(List<Individual> parents, List<Individual> offSpring) {
        problem.updateAllFitnesses(offSpring);
        return offSpring;
    }

    /**
     * Replace worst n from initial population
     * @param parents
     * @param offSpring
     * @param popSize The population size of offspring
     * @return
     */
    public List<Individual> genitor(List<Individual> parents, List<Individual> offSpring, int popSize) {//??????about this method - not sure it is genitorius
        offSpring = offSpring.stream().sorted(Comparator.comparingDouble(individual -> individual.getFitness())).collect(Collectors.toList());    //sort arraylist by fitness
        List<Individual> newOffSpring = new ArrayList<>();

        for (int i = 0; i < popSize; i++) {
            newOffSpring.add(offSpring.get(i));
        }
        return newOffSpring;
    }

    /**
     * Elitism 
     * @param parents
     * @param offSpring
     * @param n
     * @return
     */
    public List<Individual> elitism(List<Individual> parents, List<Individual> offSpring, int n) {
        problem.updateAllFitnesses(offSpring);
        n = Math.min(n, offSpring.size());
        n = Math.min(n, parents.size()); //get the smaller of all three, we first want to satisfy the smallest

        parents = parents.stream().sorted(Comparator.comparingDouble(individual -> individual.getFitness())).collect(Collectors.toList());    //sort arraylist by fitness
        offSpring = offSpring.stream().sorted(Comparator.comparingDouble(individual -> individual.getFitness())).collect(Collectors.toList());    
        // System.out.println("Before Parents" + Arrays.toString(problem.getFitnesses(parents)));
        // System.out.println("Before Children" + Arrays.toString(problem.getFitnesses(offSpring)));
        
        int parentIndex = parents.size() - 1;

        for (int i = 0; i < n; i++) {
            offSpring.set(i, parents.get(parentIndex));

            // System.out.println("Best from previous: " + parents.get(parentIndex).getFitness());
            // System.out.println("After Children " + Arrays.toString(problem.getFitnesses(offSpring)));

            parentIndex--;
        }

        return offSpring;
    }
    
}
