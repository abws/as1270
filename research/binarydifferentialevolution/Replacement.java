package research.binarydifferentialevolution;

import java.util.ArrayList;
import java.util.List;

public class Replacement {
    private Problem problem;

    Replacement(Problem problem) {
        this.problem = problem;
    }
    public List<Vector> selectBest(List<Vector> parents, List<Vector> trials) {
        List<Vector> offSpring = new ArrayList<>();
        for (int i = 0; i < parents.size(); i++) {
            Vector p = parents.get(i);
            Vector t = trials.get(i);

            if (p.fitness > t.fitness) {offSpring.add(p);}
            else {offSpring.add(t);}
        }

        return offSpring;
    }

    public List<Vector> selectBestSpecial(List<Vector> parents, List<Vector> trials) {
        List<Vector> offSpring = new ArrayList<>();
        double violationSumP = 0;
        double violationSumT = 0;

        for (int i = 0; i < parents.size(); i++) {
            Vector p = parents.get(i);
            Vector t = trials.get(i);

            double[][] coordinatesP = problem.decodeDirect(p.getVector());
            double[][] coordinatesT = problem.decodeDirect(t.getVector());



            for (int j = 0; j < coordinatesP.length; j++) {     //loop through each edge only once (n(n-1)/n) - ~doubles speed
                for (int k = j+1; k < coordinatesP.length; k++) {
                    violationSumP += problem.staticProximityViolation(coordinatesP[j], coordinatesP[k], problem.minDist);
                    violationSumT += problem.staticProximityViolation(coordinatesT[j], coordinatesT[k], problem.minDist);
                }
            }

            if ((violationSumT == 0) && (p.fitness < t.fitness)) offSpring.add(t);  //it is feasible and has a better fitness
            else if ((violationSumT == 0) && (violationSumP > 0)) offSpring.add(t); //it is feasible while the parent isnt
            else if ((violationSumT > 0) && (violationSumP >= violationSumT)) offSpring.add(t); //provides a lower or equal constraint value
            
            else offSpring.add(p);
        }

        return offSpring;
    }
    
    
}
