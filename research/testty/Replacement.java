package research.testty;

import java.util.ArrayList;
import java.util.List;

public class Replacement {

    private Problem problem;
    
    Replacement(Problem problem) { //Problem provides the evaluation function
        this.problem = problem;
    }

    public List<double[]> selectBest(List<double[]> parents, List<double[]> trials) {
        List<double[]> offSpring = new ArrayList<>();
        for (int i = 0; i < parents.size(); i++) {
            double[] p = parents.get(i);
            double[] t = trials.get(i);
            double pf = problem.evaluatePenalty(p);
            double tf = problem.evaluatePenalty(t);
            // System.out.println(Math.max(pf, tf));
            if (Math.max(pf, tf) > Problem.max) Problem.max = Math.max(pf, tf);

            if (pf > tf) {offSpring.add(p);}
            else {offSpring.add(t);}
        }

        return offSpring;
    }
    
    
}
