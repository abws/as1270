package research.differentialevolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Mutation {
    double scalingFactor;
    Problem problem;

    public Mutation(double scalingFactor, Problem problem) {
        this.scalingFactor = scalingFactor;
        this.problem = problem;
    }

    public List<Vector> differentialMutation(List<Vector> population) {
        List<Vector> mutantVector = new ArrayList<Vector>();

        for (int i = 0; i < population.size(); i++) {
            List<double[]> randomMembers = getRandomMembers(i, population);

            double[] x = randomMembers.get(0);
            double[] y = randomMembers.get(1);

            Vector mutant = scale(scalingFactor, vectorDifferential(x, y));
            mutantVector.add(mutant);
        }

        return mutantVector;
    }


    /**
     * Gets two random individuals
     * from the population
     * @param a
     * @param pop
     * @return
     */
    public List<double[]> getRandomMembers(int a, List<Vector> pop) {
        Random r = new Random();
        List<Integer> indexes = new ArrayList<>();
        List<double[]> randomMembers = new ArrayList<>();

        indexes.add(a);
        while (indexes.size() != 3) {
            int randomIndex = r.nextInt(pop.size());    //add elements such that [0] != [1] != [2]
            if (indexes.contains(randomIndex)) continue;
            indexes.add(randomIndex);
        }

        double[] b = pop.get(1).getVector(); double[] c = pop.get(2).getVector();
        randomMembers.add(b); randomMembers.add(c);

        return randomMembers;
    }



    /**
     * Calculates the difference
     * between two vectors
     * @param vectorA the vector to point towards
     * @param vectorB from this vector
     */
    public double[] vectorDifferential(double[] vectorA, double[] vectorB) {
        double[] differential = new double[vectorA.length];
        for (int i = 0; i < vectorA.length; i++) {
            differential[i] = vectorA[i] - vectorB[i];
        }
        return differential;
    }

        /**
     * Calculates the value of a 
     * vector after a scalar multiplication
     * @param scalar
     * @param vector
     */
    public Vector scale(double scalar, double[] vector) {
        double[] scalarised = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            scalarised[i] = scalar * vector[i];
        }

        Vector vectorC = new Vector(scalarised, false, problem);
        return vectorC;
    }


    



    
    
}
