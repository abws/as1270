package research.geneticalgorithm;

/**
 * Class for representing binary individuals
 * @author Abdiwahab Salah
 * @version 05.02.23
 */
public class Individual {
    public String VALUE;
    public double fitness;
    public Problem problem;


    Individual(String value, Problem problem, boolean wantsFitness) {
        this.VALUE = value;
        fitness = (wantsFitness) ? setFitness() : 0;
    }

    public void updateFitness() {
        this.fitness = problem.evaluate(VALUE);
    }
    




    
}
