package research.problems;

public abstract class Problem {
    public abstract Object encode(double[][] individual);
    
    public abstract double[][] decode(Object individual);

    public abstract long evaluate(Object layout);
    
}
