package semester_1.hill_climb_quadratic;

public class Main {
    public static void main(String[] args) {
        Problem problem = new Problem();

        int localMaxima = HillClimb.hillClimb(problem);
        System.out.println(localMaxima);
    }
    
}
