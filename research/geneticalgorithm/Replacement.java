package research.geneticalgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<Individual> genitor(List<Individual> parents, List<Individual> offSpring, int popSize) {
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
    public List<Individual> elitism(ArrayList<Individual> parents, ArrayList<Individual> offSpring, int n) {
        List<Double> parentFitnesses = problem.getFitnessesArrayList(parents);
        List<Double> offSpringFitness = problem.getFitnessesArrayList(offSpring);

        Collections.sort(parentFitnesses);
        Collections.sort(offSpringFitness);
        int parentIndex = parentFitnesses.size();

        for (int i = 0; i < n; i++) {
            offSpring.set(i, parents.get(parentIndex));
            parentIndex--;
        }

        return offSpring;
    }
    
}
