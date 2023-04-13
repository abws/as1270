package research.anglemodulateddifferentialevolution;

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
    
    
}
