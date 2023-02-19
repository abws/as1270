package research.geneticalgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Responsible for providing
 * crossover operators for the 
 * Genetic Algorithm.
 * Made up of two type of methods.
 * One method simply combines n parents.
 * The other uses these methods to create n offspring.
 * ONE POINT CROSSOVER is A SPECIAL FORM OF N POINT CROSSOVER
 * @author Abdiwahab Salah
 * @version 07/02/23
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

    public List<Individual> recombineOnePoint(ArrayList<Individual> matingPool, int offspringSize) {
        //crossover
        List<Individual> offSpring = new ArrayList<>();
        while (offSpring.size() < offspringSize) {

            //Select a random pair of parents
            Random r = new Random();
            Individual parent1; Individual parent2;
            parent1 = matingPool.get(r.nextInt(offspringSize)); parent2 = matingPool.get(r.nextInt(offspringSize));

            //Crossover at rate 0.7
            if (Math.random() < CROSSOVER_RATE) {
                offSpring.addAll(onePointCrossover(parent1, parent2));
            }
            else {
                Individual child1; Individual child2;
                child1 = new Individual(parent1.getValue(), problem, false); child2 = new Individual(parent2.getValue(), problem, false); 

                offSpring.addAll(Arrays.asList(child1, child2)); //by adding this line, we make the crossover rate meaningful
            }        
        }
        return offSpring;
    }

    public List<Individual> recombineNPoint(List<Individual> matingPool, int offspringSize, int n) {
        //crossover
        List<Individual> offSpring = new ArrayList<>();
        while (offSpring.size() < offspringSize) {

            //Select a random pair of parents
            Random r = new Random();
            Individual parent1; Individual parent2;
            parent1 = matingPool.get(r.nextInt(offspringSize)); parent2 = matingPool.get(r.nextInt(offspringSize));

            //Crossover at rate 0.7
            if (Math.random() < CROSSOVER_RATE) {
                offSpring.addAll(nPointCrossover(parent1, parent2, n));
            }
            else {
                Individual child1; Individual child2;
                child1 = new Individual(parent1.getValue(), problem, false); child2 = new Individual(parent2.getValue(), problem, false); 

                offSpring.addAll(Arrays.asList(child1, child2)); //by adding this line, we make the crossover rate meaningful
            }
        }
        return offSpring;
    }

    public List<Individual> recombineUniform(List<Individual> matingPool, int offspringSize, double p) {
        //crossover
        List<Individual> offSpring = new ArrayList<>();
        while (offSpring.size() < offspringSize) {

            //Select a random pair of parents
            Random r = new Random();
            Individual parent1; Individual parent2;
            parent1 = matingPool.get(r.nextInt(offspringSize)); parent2 = matingPool.get(r.nextInt(offspringSize));

            //Crossover at rate 0.7
            if (Math.random() < CROSSOVER_RATE) {
                offSpring.addAll(uniformCrossover(parent1, parent2, p));
            }
            else {
                Individual child1; Individual child2;
                child1 = new Individual(parent1.getValue(), problem, false); child2 = new Individual(parent2.getValue(), problem, false); 

                offSpring.addAll(Arrays.asList(child1, child2)); //by adding this line, we make the crossover rate meaningful
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
    private List<Individual> onePointCrossover(Individual parent1, Individual parent2) {
        Random r = new Random();
        List<Individual> children = new ArrayList<>();

        int crossoverPoint = r.nextInt(INDIV_LENGTH - 1) + 1;    //INDIV_LENGTH may give any position from 0 to n-1. we minus one as we also dont want 0. bus since zero may be returned, we add one afterwards. The number refers to the position we cut at before 
        String value1 =  parent1.getValue().substring(0, crossoverPoint) + parent2.getValue().substring(crossoverPoint);
        String value2 = parent2.getValue().substring(0, crossoverPoint) + parent1.getValue().substring(crossoverPoint);

        Individual child1, child2;
        child1 = new Individual(value1, problem, false); child2 = new Individual(value2, problem, false); //dont want to waste away any evaluations right now

        children.addAll(Arrays.asList(child1, child2));

        return children;
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
    public List<Individual> nPointCrossover(Individual parent1, Individual parent2, int n) {
        Random r = new Random();
        n = Math.min(n, INDIV_LENGTH - 1); //make sure n never overflows
        List<Integer> crossoverPoints = new ArrayList<>(Arrays.asList(0));
        List<Individual> children = new ArrayList<>();

        StringBuilder v1 = new StringBuilder(parent1.getValue());
        StringBuilder v2 = new StringBuilder(parent2.getValue());

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

            String temp = v1.substring(lower, upper); //save here since child1 is about to change for good

            v1.replace(lower, upper, v2.substring(lower, upper));
            v2.replace(lower, upper, temp);
        }
        
        String value1, value2; 
        value1 = v1.toString(); value2 = v2.toString();

        Individual child1, child2;
        child1 = new Individual(value1, problem, false); child2 = new Individual(value2, problem, false); //dont want to waste away any evaluations right now

        children.addAll(Arrays.asList(child1, child2));
        return children;    
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
    public List<Individual> uniformCrossover(Individual parent1, Individual parent2, double p) { //probably better for wflop, since we want to decrease positional bias (due to crowding)
        double[] randomVariables = new double[INDIV_LENGTH];
        IntStream.range(0, randomVariables.length).forEach(i -> randomVariables[i] = Math.random()); //fil array with random values

        List<Individual> children = new ArrayList<>();
        StringBuilder v1 = new StringBuilder();

        for (int i = 0; i < INDIV_LENGTH; i++) {
            if (randomVariables[i] < p) v1.append(parent1.getValue().charAt(i));
            else v1.append(parent2.getValue().charAt(i));
        }
        String value1 = v1.toString();
        String value2 =value1.replace('0', 'a').replace('1', '0').replace('a', '1'); //find a more mathematical way of doing this. does the inverse of given string

        Individual child1, child2;
        child1 = new Individual(value1, problem, false); child2 = new Individual(value2, problem, false); //dont want to waste away any evaluations right now

        children.addAll(Arrays.asList(child1, child2));
        return children;
    }

    
}
