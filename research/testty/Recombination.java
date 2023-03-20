package research.testty;

import java.util.ArrayList;
import java.util.List;

public class Recombination {
    double crossoverRate;
    Problem problem;

    public Recombination(double crossoverRate, Problem problem) {
        this.crossoverRate = crossoverRate;
        this.problem = problem;
    }

    public List<double[]> binomialCrossover(List<double[]> pop, List<double[]> mutantVector) {
        List<double[]> trialVector = new ArrayList<>();
        for (int i = 0; i < pop.size(); i++) {
            double[] parent = pop.get(i);
            double[] mutant = mutantVector.get(i);
            double[] trial = uniform(parent, mutant);

            trialVector.add(trial);
        }
        return trialVector;
    }

    public double[] uniform(double[] parent, double[] mutant) {
        double[] trial = new double[parent.length];
        for (int i = 0; i < parent.length; i++) {
            if (Math.random() < crossoverRate) trial[i] = mutant[i];
            else trial[i] = parent[i]; 
        }
        // double[][] layout = new double[problem.nTurbines][2];
        // layout = problem.geometricReformer(problem.decodeDirect(trial), problem.minDist);
        // trial = problem.encodeDirect(layout);
        trial = problem.randomBoundHandle(trial);

        return trial;
    }
    
}
