package semester_1.individuals;

import java.util.ArrayList;
import java.util.Arrays;

import semester_1.objective_functions.QuadraticEvaluator;
import semester_1.problems.ProblemConstrainedQuadratic;

public class Individual {
    public static String INDIVIDUAL;
    public int VALUE;
    
    public Individual(String individual) {
        INDIVIDUAL = individual.trim();
        VALUE = getValue(new ProblemConstrainedQuadratic());
    }

    private int getValue(ProblemConstrainedQuadratic problem) {
        return QuadraticEvaluator.quadraticEvaluator(problem.decode(INDIVIDUAL));
    }

    public String toString() {
        return INDIVIDUAL;
    }
    
}
