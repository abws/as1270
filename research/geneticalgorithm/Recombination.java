package research.geneticalgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Responsible for providing
 * crossover operators for the 
 * Genetic Algorithm
 * @author Abdiwahab Salah
 * @version 05/02/23
 */
public class Recombination {
    private Problem problem;
    public int CROSSOVER_RATE;
    public int INDIV_LENGTH;


    Recombination(Problem problem) {
        this.problem = problem;
        this.INDIV_LENGTH = problem.INDIV_LENGTH;
    }

    public ArrayList<String> recombineOnePoint(ArrayList<String> matingPool, int offspringSize) {
        //crossover
        ArrayList<String> offSpring = new ArrayList<>();
        while (offSpring.size() < offspringSize) {

            //Select a random pair of parents
            Random r = new Random();
            String parent1; String parent2;
            parent1 = matingPool.get(r.nextInt( offspringSize)); parent2 = matingPool.get(r.nextInt(offspringSize));

            //Crossover at rate 0.7
            if (Math.random() < CROSSOVER_RATE) {
                offSpring.addAll(onePointCrossover(parent1, parent2));
            }
        }
        return offSpring;

    }

    /**
     * Recombination operator 
     * (2 parents)
     * (2 children)
     * @param parent1
     * @param parent2
     * @return
     */
    private ArrayList<String> onePointCrossover(String parent1, String parent2) {
        Random r = new Random();
        ArrayList<String> offSpring = new ArrayList<String>();

        int crossoverPoint = r.nextInt(INDIV_LENGTH - 1) + 1;    //-1 so the random number has 3 positions to take (including 0). +1 as returning 0 would make us cut the string at the very start (so wont cut). The number refers to the position we cut at before 
        String child1 = parent1.substring(0, crossoverPoint) + parent2.substring(crossoverPoint);
        String child2 = parent2.substring(0, crossoverPoint) + parent1.substring(crossoverPoint);

        offSpring.addAll(Arrays.asList(child1, child2));

        return offSpring;
    }

    /**
     * Recombination operator 
     * (2 parents)
     * (2 children)
     * @param parent1
     * @param parent2
     * @return
     */
    private ArrayList<String> nPointCrossover(String parent1, String parent2, int n) {
        Random r = new Random();
        n = Math.max(n, INDIV_LENGTH); //make sure n never overflows
        ArrayList<Integer> crossoverPoints = new ArrayList<>();
        ArrayList<String> offSpring = new ArrayList<String>();

        StringBuilder child1 = new StringBuilder(parent1);
        StringBuilder child2 = new StringBuilder(parent2);

        while (crossoverPoints.size() < n) {
            int cp = r.nextInt(INDIV_LENGTH - 1) + 1;
            if (!crossoverPoints.contains(cp)) crossoverPoints.add(cp);
        }

        Collections.sort(crossoverPoints);
        int x = 0;
        for (int point : crossoverPoints) {
            child1.replace(x, point, child2.substring(x, n));
            child2.replace(x, point, child1.substring(x, n));
            x = point;
        }
        offSpring.addAll(Arrays.asList(child1.toString(), child2.toString()));
        return offSpring;
    }
    
}
