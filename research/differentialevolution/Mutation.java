package research.differentialevolution;

import java.util.ArrayList;
import java.util.Arrays;
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
        List<Vector> mutants = new ArrayList<Vector>();

        for (int i = 0; i < population.size(); i++) {
            List<double[]> randomMembers = getRandomMembers(i, population, 3);

            double[] z = randomMembers.get(0);
            double[] x = randomMembers.get(1);
            double[] y = randomMembers.get(2);

            Vector m = vectorAddition(z, scale(scalingFactor, vectorDifferential(x, y)));
            mutants.add(m);
        }

        return mutants;
    }


    public List<Vector> differentialMutationBest1Z(List<Vector> population) {
        List<Vector> mutants = new ArrayList<Vector>();

        for (int i = 0; i < population.size(); i++) {
            List<double[]> randomMembers = getRandomMembers(i, population, 2);

            double[] z = getBestMember(population);
            double[] x = randomMembers.get(1);
            double[] y = randomMembers.get(2);

            Vector m = vectorAddition(z, scale(scalingFactor, vectorDifferential(x, y)));
            mutants.add(m);
        }

        return mutants;
    }


    /**
     * Gets two random individuals
     * from the population
     * @param d The index of the parent
     * @param pop
     * @param n Number of members to select
     * @return
     */
    public List<double[]> getRandomMembers(int d, List<Vector> pop, int n) {
        Random r = new Random();
        List<Integer> indexes = new ArrayList<>();
        List<double[]> randomMembers = new ArrayList<>();
        indexes.add(d);
        while (indexes.size() != n+1) {
            int randomIndex = r.nextInt(pop.size());    //add elements such that [0] != [1] != [2]
            if (indexes.contains(randomIndex)) continue;
            indexes.add(randomIndex);
        }

        for (int i = 0; i < indexes.size(); i++) {
            randomMembers.add(Arrays.copyOf(pop.get(indexes.get(i)).getVector(), problem.nDimension));
        }

        return randomMembers;
    }

    public double[] getBestMember(List<Vector> pop) {
        double[] bestVector = pop.get(0).getVector();
        double maxFitness = pop.get(0).fitness;

        for (int i = 1; i < pop.size(); i++) {
            if (pop.get(0).fitness > maxFitness) bestVector = pop.get(0).getVector();
        }

        return Arrays.copyOf(bestVector, bestVector.length);
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

    /**
     * Calculates the sum
     * of two vectors
     */
    public Vector vectorAddition(double[] vectorA, double[] vectorB) {
        double[] sum = new double[vectorA.length];
        for (int i = 0; i < vectorA.length; i++) {
            sum[i] = vectorA[i] + vectorB[i];
        }
        return new Vector(sum, false, problem);
    }

}
