package research.geneticalgorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Replacement {
    Problem problem;
    
    public List<Individual> deleteOldest(List<Individual> parents, List<Individual> offSpring) {
        return offSpring;
    }

    /**
     * 
     * @param parents
     * @param offSpring
     * @param popSize The population size of offspring
     * @return
     */
    public List<Individual> deleteOldest(List<Individual> parents, List<Individual> offSpring, int popSize) {
        offSpring = offSpring.stream().sorted(Comparator.comparingDouble(individual -> individual.getFitness())).collect(Collectors.toList());    //sort arraylist by fitness
        List<Individual> newOffSpring = new ArrayList<>();

        for (int i = 0; i < popSize; i++) {
            newOffSpring.add(offSpring.get(i));
        }
        return newOffSpring;
    }

    public elitism(ArrayList<Individual> parents, ArrayList<Individual> offSpring, int n, boolean replaceWorst) {
        if (replaceWorst) 
        //sort parents 
        //take n best
        //replace n worst from offspring, otherwise randomly replace from 
    }
    
}
