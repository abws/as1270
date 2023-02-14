package research.particleswarmoptimisation;

/**
 * Class representing 
 * a single particle floating
 * through the search space.
 * Only concerned with book-keeping.
 * No operations should be performed
 * here.
 * @author Abdiwahab Salah
 * @version 14.02.23
 */
public class Particle {
    double[] currentPosition;
    double[] velocity;
    double[] pBest;

    double currentValue;
    double pBestValue;

    Problem problem;
    

    //A new particle will be created once
    Particle(double[] initialPosition, double[] initialVelocity, Problem problem) { //Problem provides the evaluation function
        this.currentPosition = initialPosition;
        this.velocity = initialVelocity;
        updateCurrentValue();

        this.problem = problem;
    }

   public void updateCurrentPosition(double[] newPosition) {    //PSO class does the calculations
       this.currentPosition = newPosition;
   }

   public void updateVelocity(double[] newVelocity) {   //PSO class does the calculations
       this.velocity = newVelocity;
   }

   public void updateCurrentValue() {
       double fitness = problem.evaluate(currentPosition);
       this.currentValue = fitness;
   }

   public void updatePersonalBest() {
       if (currentValue > pBestValue) { //assuming maximisation
            pBest = currentPosition;
           pBestValue = currentValue;
       }
   }
}
