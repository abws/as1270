package semester_1.simulated_annealing_quadratic;

public class Main {
    public static void main(String[] args) {
        Problem problem = new Problem();

        SimulatedAnnealing sa = new SimulatedAnnealing();

        int localMaxima = sa.simulatedAnnealing(problem);
        System.out.println(localMaxima);
    }
    
}
