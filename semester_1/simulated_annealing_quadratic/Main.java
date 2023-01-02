package semester_1.simulated_annealing_quadratic;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
   
        Problem problem = new Problem();
        SimulatedAnnealing sa = new SimulatedAnnealing();

        int localMaxima = sa.simulatedAnnealing(problem);
        System.out.println(localMaxima);
    }
    
}
