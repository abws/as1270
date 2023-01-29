package research.api.java;
public class main {

  public static void main(String argv[]) throws Exception {
	//String debugger = System.getProperty("user.dir");
	//System.out.println(debugger);

	WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Desktop/evaluation/WindFLO/Scenarios/obs_00.xml");
	KusiakLayoutEvaluator wfle = new KusiakLayoutEvaluator();
	wfle.initialize(ws);
	GA algorithm = new GA(wfle);
	algorithm.run(); // optional, name of method 'run' provided on submission
	// algorithm can also just use constructor
  }
}
