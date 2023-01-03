package semester_1;

import semester_1.algorithms.HillClimb;
import semester_1.problems.ProblemConstrainedQuadratic;

public class Main {
    public static void main(String[] args) {
   
        ProblemConstrainedQuadratic problem = new ProblemConstrainedQuadratic();
        
        int localMaxima = HillClimb.hillClimb(problem);
        System.out.println(localMaxima);
    }
    

    
}
