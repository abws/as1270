package research.anglemodulateddifferentialevolution;
import research.api.java.*;

public class Main {
    public static void main(String[] args) throws Exception {
        WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/testscenarios/1.xml");

        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        Problem problem = new Problem(evaluator, ws, 15);
        AngleModulatedDE de = new AngleModulatedDE(0.7, 1000, 0.55, problem);
        de.run();
    }

}
