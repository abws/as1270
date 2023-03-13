package research.test;

/**
 * Class representing 
 * a single particle floating
 * through the search space.
 * Only concerned with book-keeping.
 * No operations should be performed
 * here. Only knows about him/her-self.
 * @author Abdiwahab Salah
 * @version 14.02.23
 */
public class Particle {
    private double[] currentPosition;
    private double[] velocity;
    private double[] pBest;
    private static int globalcounter = 0;

    private double fitness;
    private double pBestFitness;

    private Problem problem;
    

    /*
     * A new particle will be created once
     */
    Particle(double[] initialPosition, double[] initialVelocity, Problem problem) { //Problem provides the evaluation function
        this.problem = problem;

        this.currentPosition = initialPosition;
        this.velocity = initialVelocity;
        this.pBestFitness = 0;
        this.pBest = initialPosition;

        updateFitness();
    }
    

    /* Getters and setters */
    public double[] getPosition() {
        return this.currentPosition;
    }

    public double[] getVelocity() {
        return this.velocity;
    }

    public double[] getPersonalBest() {
        return this.pBest;
    }
    
    public double getPersonalBestFitness() {
        return this.pBestFitness;
    }

   public void setPosition(double[] newPosition, boolean wantsFitnessUpdated) {    //PSO class does the calculations
       this.currentPosition = newPosition;
       if (wantsFitnessUpdated) {
           updateFitness();
       }
   }

   public void setVelocity(double[] newVelocity) {   //PSO class does the calculations
       this.velocity = newVelocity;
   }

   public void updateFitness() {
    double fitness = problem.evaluatePenalty(currentPosition);
    this.fitness = fitness;
    // System.out.println(globalcounter++%problem.swarmSize +": "+ fitness+": "+problem.countViolations(problem.decodeDirect(this.currentPosition)));

    updatePersonalBest();
}

   public boolean updatePersonalBest() {
       if (fitness > pBestFitness) { //assuming maximisation
            pBest = currentPosition;
            pBestFitness = fitness;
            return true;
       }
       return false;    //feed back to user in case of necessity
   }
}
