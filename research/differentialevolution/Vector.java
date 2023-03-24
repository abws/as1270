package research.differentialevolution;

/**
 * Class representing a
 * vector, with some having 
 * fitnesses
 * Used also for mutation vectors
 * and trial vectors
 * @author Abdiwahab Salah
 * @version 14.02.23
 */
public class Vector {
    private double[] vector;
    public double fitness;

    private Problem problem;
    
    Vector(double[] vector, boolean wantsFitnessUpdated, Problem problem) { //Problem provides the evaluation function
        this.problem = problem;

        this.vector = vector;
        if (wantsFitnessUpdated) updateFitness();
    }
    

    /* Getters and setters */
    public double[] getVector() {
        return this.vector;
    }

    public void setVector(double[] newPosition, boolean wantsFitnessUpdated) {
        this.vector = newPosition;

        if (wantsFitnessUpdated) updateFitness();
    }

   public void updateFitness() {
       double fitness = problem.evaluatePenalty(vector);
       this.fitness = fitness;
   } 
}
