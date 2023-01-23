package semester_1;
import semester_1.algorithms.SimpleGeneticAlgorithm;
import semester_1.problems.ProblemConstrainedQuadratic;

public class Main {
    public static void main(String[] args) {
   
        ProblemConstrainedQuadratic problem = new ProblemConstrainedQuadratic();
        
        //int localMaxima = HillClimb.hillClimb(problem);
        //System.out.println(localMaxima);

        SimpleGeneticAlgorithm sa = new SimpleGeneticAlgorithm();

        System.out.println(sa.run(problem));
        //Individual i = new Individual("10111");
        //System.out.println( sa.bitMutation(i));




    }
    

    
}
