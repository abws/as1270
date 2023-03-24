package research.binaryparticleswarmoptimisation;

/**
 * Class representing 
 * a single particle floating
 * through the search space.
 * Only concerned with book-keeping.
 * No operations should be performed
 * here. Only knows about himself.
 * @author Abdiwahab Salah
 * @version 14.02.23
 */
public class Particle {
    private static int globalcounter=0;
    private int[] currentPosition;
    private double[] velocity;
    private int[] pBest;

    public double fitness;
    private double pBestFitness;

    private Problem problem;
    

    /*
     * A new particle will be created once
     */
    Particle(int[] initialPosition, double[] initialVelocity, Problem problem) { //Problem provides the evaluation function
        this.problem = problem;

        this.currentPosition = initialPosition;
        this.velocity = initialVelocity;
        this.pBestFitness = Double.NEGATIVE_INFINITY;
        this.pBest = initialPosition;

        updateFitness();
    }
    

    /* Getters and setters */
    public int[] getPosition() {
        return this.currentPosition;
    }

    public double[] getVelocity() {
        return this.velocity;
    }

    public int[] getPersonalBest() {
        return this.pBest;
    }
    
    public double getPersonalBestFitness() {
        return this.pBestFitness;
    }

   public void setPosition(int[] newPosition, boolean wantsFitnessUpdated) {    //PSO class does the calculations
       this.currentPosition = newPosition;
       if (wantsFitnessUpdated) {
           updateFitness();
       }
   }

   public void setVelocity(double[] newVelocity) {   //PSO class does the calculations
       this.velocity = newVelocity;
   }

   public void updateFitness() {
       double fitness = problem.evaluate(currentPosition);
       this.fitness = fitness;
    //    System.out.println(globalcounter++%10 +": "+ fitness+": "+problem.countViolations(problem.decodeDirect(this.currentPosition)));

       updatePersonalBest();
   }

   public boolean updatePersonalBest() {
       if ((fitness > pBestFitness)) { //assuming maximisation
            pBest = currentPosition;
            pBestFitness = fitness;
            return true;
       }
       return false;    //feed back to user in case of necessity
   }
}
