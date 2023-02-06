package research.geneticalgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.stream.IntStream;

import research.api.java.KusiakLayoutEvaluator;
import research.api.java.WindScenario;

public class Main {
    public static void main(String[] args) throws Exception {

        WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/api/Scenarios/00.xml");
        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);

        Problem p = new Problem(evaluator, ws, 10, 10);        
        Recombination r = new Recombination(p);

        //ArrayList<String> test = nPointCrossover("1011110011", "1010110011", 1);

        double[] randomVariables = new double[5];
        IntStream.range(0, randomVariables.length).forEach(i -> randomVariables[i] = Math.random());

        System.out.println(Arrays.toString(randomVariables));
        //System.out.println(test);
        
    }

        /**
     * Recombination operator 
     * (2 parents)
     * (2 children)
     * @param parent1
     * @param parent2
     * @return
     */
    public static ArrayList<String> nPointCrossover(String parent1, String parent2, int n) {
        Random r = new Random();
        n = Math.min(n, 10); //make sure n never overflows
        ArrayList<Integer> crossoverPoints = new ArrayList<>(Arrays.asList(0));
        ArrayList<String> offSpring = new ArrayList<String>();

        StringBuilder child1 = new StringBuilder(parent1);
        StringBuilder child2 = new StringBuilder(parent2);

        while (crossoverPoints.size() < n+1) { //since 0 is already contained, we want to 'ignore' it
            int cp = r.nextInt(10 - 1) + 1;
            if (!crossoverPoints.contains(cp)) crossoverPoints.add(cp);
        }

        System.out.println(crossoverPoints);

        Collections.sort(crossoverPoints);
        int lower = 0;
        int upper = crossoverPoints.get(0);

        for (int i = 3; i < crossoverPoints.size(); i+=2) {
            String temp = child1.substring(lower, upper); //save here since child1 is about to change for good

            child1.replace(lower, upper, child2.substring(lower, upper));
            child2.replace(lower, upper, temp);
            lower = crossoverPoints.get(i - 1);
            upper = crossoverPoints.get(i); //account for i and i+1 here so no need to loop n times 
        }
        
        offSpring.addAll(Arrays.asList(child1.toString(), child2.toString()));
        return offSpring;
    }

    

    public void uniformCrossover(String parent1, String parent2, int n) {
        double[] randomVariables = new double[5];
        Arrays.fill(randomVariables, Math.random());
        System.out.println(Arrays.toString(randomVariables));
    }
    
    
}
