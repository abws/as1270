package research.differentialevolution;
import research.api.java.*;

public class Main {
    public static void main(String[] args) throws Exception {
        WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/testscenarios/1.xml");

        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        Problem problem = new Problem(evaluator, ws, 15, 100, 2, 1);
        DifferentialEvolution de = new DifferentialEvolution(15, 0.6, 1000, 0.55, problem);
        de.run();
    }

}
