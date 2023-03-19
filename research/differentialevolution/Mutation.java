package research.differentialevolution;

import java.util.List;

public class Mutation {
    double scalingFactor;

    public Mutation(double scalingFactor) {
        this.scalingFactor = scalingFactor;
    }
    
    public double[] differentialMutation(List<Vector> population) {
        List<Vector>
        double[] mutationVector = new double[population.];


    }



    /**
     * Calculates the difference
     * between two vectors
     * @param vectorA the vector to point towards
     * @param vectorB from this vector
     */
    public double[] vectorDifferential(double[] vectorA, double[] vectorB) {
        double[] difference = new double[vectorA.length];
        for (int i = 0; i < vectorA.length; i++) {
            difference[i] = vectorA[i] - vectorB[i];
        }
        return difference;
    }


    



    
    
}
