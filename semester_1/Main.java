package semester_1;

import java.util.ArrayList;

import semester_1.algorithms.SimpleGeneticAlgorithm;
import semester_1.individuals.Individual;
import semester_1.problems.ProblemConstrainedQuadratic;

public class Main {
    public static void main(String[] args) {
   
        ProblemConstrainedQuadratic problem = new ProblemConstrainedQuadratic();
        
        //int localMaxima = HillClimb.hillClimb(problem);
        //System.out.println(localMaxima);

        SimpleGeneticAlgorithm sa = new SimpleGeneticAlgorithm();
        sa.run(problem);



    }
    

    
}
