package other.preliminaries.individuals;

import other.preliminaries.objective_functions.QuadraticEvaluator;
import other.preliminaries.problems.ProblemConstrainedQuadratic;

/**
 * Class for representing binary individuals
 * @author Abdiwahab Salah
 * @version 05.01.23
 */
public class Individual {
    public String INDIVIDUAL;
    public long VALUE;
    
    public Individual(String individual) {
        INDIVIDUAL = individual.trim();
        VALUE = getValue(new ProblemConstrainedQuadratic());
    }

    //Calculates fitness of individual
    public long getValue(ProblemConstrainedQuadratic problem) {
        return QuadraticEvaluator.quadraticEvaluator(problem.decode(INDIVIDUAL));
    }

    public String toString() {
        return INDIVIDUAL;
    }
    
}
