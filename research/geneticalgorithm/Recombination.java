package research.geneticalgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Responsible for providing
 * crossover operators for the 
 * Genetic Algorithm.
 * Made up of two type of methods.
 * One method simply combines n parents.
 * The other uses these methods to create n offspring.
 * GET RID OF ONE POINT CROSSOVER AS ITS A SPECIAL FORM OF N POINT CROSSOVER
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

    public ArrayList<String> recombineNPoint(ArrayList<String> matingPool, int offspringSize, int n) {
        //crossover
        ArrayList<String> offSpring = new ArrayList<>();
        while (offSpring.size() < offspringSize) {

            //Select a random pair of parents
            Random r = new Random();
            String parent1; String parent2;
            parent1 = matingPool.get(r.nextInt( offspringSize)); parent2 = matingPool.get(r.nextInt(offspringSize));

            //Crossover at rate 0.7
            if (Math.random() < CROSSOVER_RATE) {
                offSpring.addAll(nPointCrossover(parent1, parent2, n));
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

        int crossoverPoint = r.nextInt(INDIV_LENGTH - 1) + 1;    //INDIV_LENGTH may give any position from 0 to n-1. we minus one as we also dont want 0. bus since zero may be returned, we add one afterwards. The number refers to the position we cut at before 
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
    public ArrayList<String> nPointCrossover(String parent1, String parent2, int n) {
        Random r = new Random();
        n = Math.min(n, INDIV_LENGTH); //make sure n never overflows
        ArrayList<Integer> crossoverPoints = new ArrayList<>();
        ArrayList<String> offSpring = new ArrayList<String>();

        StringBuilder child1 = new StringBuilder(parent1);
        StringBuilder child2 = new StringBuilder(parent2);

        while (crossoverPoints.size() < n) {
            int cp = r.nextInt(INDIV_LENGTH - 1) + 1;
            if (!crossoverPoints.contains(cp)) crossoverPoints.add(cp);
        }

        System.out.println(crossoverPoints);

        Collections.sort(crossoverPoints);
        int x = 0;
        for (int point : crossoverPoints) {
            child1.replace(x, point, child2.substring(x, point));
            child2.replace(x, point, child1.substring(x, point));
            x = point;                                              //shift to the last point. note, point will contain something else in the next run
        }
        offSpring.addAll(Arrays.asList(child1.toString(), child2.toString()));
        return offSpring;
    }

    public ArrayList<String> uniformCrossover(String parent1, String parent2, int p) {
        double[] randomVariables = new double[INDIV_LENGTH];
        IntStream.range(0, randomVariables.length).forEach(i -> randomVariables[i] = Math.random());

        ArrayList<String> offSpring = new ArrayList<String>();
        StringBuilder child1 = new StringBuilder();


        for (int i = 0; i < INDIV_LENGTH; i++) {
            if (randomVariables[i] < p) child1.append(parent1.charAt(i));
            else child1.append(parent2.charAt(i));
        }
        String child2 = child1.toString().replace('0', 'a').replace('1', '0').replace('a', '1');

        offSpring.addAll(Arrays.asList(child1.toString(), child2.toString()));
        return offSpring;
        }

    
}
