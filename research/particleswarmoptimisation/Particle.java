package research.particleswarmoptimisation;

/**
 * Class representing 
 * a single particle.
 */
public class Particle {
    double[] currentPosition;
    double[] velocity;

    double pBest;
    

    //A new particle will be created once
    Particle(double[] currentPosition, Problem problem) { //Problem provides the evaluation function
        this.currentPosition = currentPosition;
    }

   public void updateCurrentPosition(double[] newPosition) {

   }

   




    
}
