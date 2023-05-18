package research.geneticalgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Also known as Survivor Selection
 */
public class Replacement {
    Problem problem;

    Replacement(Problem problem) { //add so user can change using input
        this.problem = problem;
    }
    
    /**
     * Generational. Removes all parents and replaces them with offspring
     * @param parents
     * @param offSpring
     * @return
     */
    public List<Individual> deleteOldest(List<Individual> parents, List<Individual> offSpring) {
        problem.updateAllFitnesses(offSpring);
        return offSpring;
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
        
        int parentIndex = parents.size() - 1;

        for (int i = 0; i < n; i++) {
            offSpring.set(i, parents.get(parentIndex));
            parentIndex--;
        }

        return offSpring;
    }
    
}
