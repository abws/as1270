package research.testty;

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

    public List<double[]> differentialMutation(List<double[]> population) {
        List<double[]> mutants = new ArrayList<double[]>();

        for (int i = 0; i < population.size(); i++) {
            List<double[]> randomMembers = getRandomMembers(i, population);

            double[] x = randomMembers.get(0);
            double[] y = randomMembers.get(1);

            double[] m = scale(scalingFactor, vectorDifferential(x, y));
            mutants.add(m);
        }

        return mutants;
    }


    /**
     * Gets two random individuals
     * from the population
     * @param a
     * @param pop
     * @return
     */
    public List<double[]> getRandomMembers(int a, List<double[]> pop) {
        Random r = new Random();
        List<Integer> indexes = new ArrayList<>();
        List<double[]> randomMembers = new ArrayList<>();

        indexes.add(a);
        while (indexes.size() != 3) {
            int randomIndex = r.nextInt(pop.size());    //add elements such that [0] != [1] != [2]
            if (indexes.contains(randomIndex)) continue;
            indexes.add(randomIndex);
        }

        double[] b = pop.get(indexes.get(1)); double[] c = pop.get(indexes.get(2)); //get the random individuals (their index is given in indexes)
        randomMembers.add(b); randomMembers.add(c);
        //I CANNOT WAIT FOR THE SUMMER. I REALLY DO WONDER WHERE LIFE WILL TAKE ME!!!
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
    public double[] scale(double scalar, double[] vector) {
        double[] scalarised = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            scalarised[i] = scalar * vector[i];
        }

        return scalarised;
    }

}
