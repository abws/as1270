package semester_1.individuals;

import semester_1.objective_functions.QuadraticEvaluator;
import semester_1.problems.ProblemConstrainedQuadratic;

/**
 * Class for representing binary individuals
 * @author Abdiwahab Salah
 * @version 05.01.23
 */
public class Individual {
    public String INDIVIDUAL;
    public int VALUE;
    
    public Individual(String individual) {
        INDIVIDUAL = individual.trim();
        VALUE = getValue(new ProblemConstrainedQuadratic());
    }

    //Calculates fitness of individual
    public int getValue(ProblemConstrainedQuadratic problem) {
        return QuadraticEvaluator.quadraticEvaluator(problem.decode(INDIVIDUAL));
    }

    public String toString() {
        return INDIVIDUAL;
    }
    
}
