package research.problems;

public abstract class Problem {
    public abstract double evaluate(Object layout);

    public abstract Object encode(double[][] individual);
    
    public abstract double[][] decode(Object individual);
    
}
