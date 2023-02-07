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
    public double CROSSOVER_RATE;
    public int INDIV_LENGTH;


    Recombination(Problem problem, double crossoverRate) {
        this.problem = problem;
        this.INDIV_LENGTH = problem.INDIV_LENGTH;
        this.CROSSOVER_RATE = crossoverRate;
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

    public ArrayList<String> recombineUniform(ArrayList<String> matingPool, int offspringSize, double p) {
        //crossover
        ArrayList<String> offSpring = new ArrayList<>();
        while (offSpring.size() < offspringSize) {

            //Select a random pair of parents
            Random r = new Random();
            String parent1; String parent2;
            parent1 = matingPool.get(r.nextInt( offspringSize)); parent2 = matingPool.get(r.nextInt(offspringSize));

            //Crossover at rate 0.7
            if (Math.random() < CROSSOVER_RATE) {
                offSpring.addAll(uniformCrossover(parent1, parent2, p));
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
     * N-point crossover.
     * Crosses over two parents
     * at n points and creates
     * two children
     * @param parent1
     * @param parent2
     * @param n
     * @return
     */
    public ArrayList<String> nPointCrossover(String parent1, String parent2, int n) {
        Random r = new Random();
        n = Math.min(n, INDIV_LENGTH - 1); //make sure n never overflows
        ArrayList<Integer> crossoverPoints = new ArrayList<>(Arrays.asList(0));
        ArrayList<String> offSpring = new ArrayList<String>();

        StringBuilder child1 = new StringBuilder(parent1);
        StringBuilder child2 = new StringBuilder(parent2);

        while (crossoverPoints.size() < n+1) { //since 0 is already contained, we want to 'ignore' it
            int cp = r.nextInt(INDIV_LENGTH - 1) + 1;
            if (!crossoverPoints.contains(cp)) crossoverPoints.add(cp);
        }

        Collections.sort(crossoverPoints);

        if (n % 2 == 0) crossoverPoints.add(INDIV_LENGTH); //so we can wrap to the end

        int lower, upper;

        for (int i = 1; i < crossoverPoints.size(); i+=2) {
            lower = crossoverPoints.get(i - 1);
            upper = crossoverPoints.get(i); //account for i and i+1 here so no need to loop n times 

            String temp = child1.substring(lower, upper); //save here since child1 is about to change for good

            child1.replace(lower, upper, child2.substring(lower, upper));
            child2.replace(lower, upper, temp);
        }
        
        offSpring.addAll(Arrays.asList(child1.toString(), child2.toString()));
        return offSpring;
    }

    /**
     * Uniform crossover.
     * Crosses over two parents
     * bitwise, where the child takes
     * a bit from either either parent
     * with probability 0.5
     * @param parent1
     * @param parent2
     * @param p
     * @return
     */
    public ArrayList<String> uniformCrossover(String parent1, String parent2, double p) { //probably better for wflop, since we want to decrease positional bias (due to crowding)
        double[] randomVariables = new double[INDIV_LENGTH];
        IntStream.range(0, randomVariables.length).forEach(i -> randomVariables[i] = Math.random());

        ArrayList<String> offSpring = new ArrayList<String>();
        StringBuilder child1 = new StringBuilder();


        for (int i = 0; i < INDIV_LENGTH; i++) {
            if (randomVariables[i] < p) child1.append(parent1.charAt(i));
            else child1.append(parent2.charAt(i));
        }
        String child2 = child1.toString().replace('0', 'a').replace('1', '0').replace('a', '1'); //find a more mathematical way of doing this 

        offSpring.addAll(Arrays.asList(child1.toString(), child2.toString()));
        return offSpring;
    }

    
}
