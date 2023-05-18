package research.particleswarmoptimisation;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import research.api.java.*;

public class Tester {
    public static void main(String[] args) throws Exception {
        WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/testscenarios/2.xml");

        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        Problem problem = new Problem(evaluator, ws, 5, 0, 0); 

        List<Particle> swarm = problem.initialiseSwarm(5);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("outputFile.txt"))) {
            for (int i = 0; i < problem.swarmSize; i++) {
                double[][] coordinates = Arrays.copyOf(problem.decodeDirect(swarm.get(i).getPosition()), problem.nTurbines);
                writer.write("---------------------------------------------\n");
                writer.write("---------------------------------------------\n\n");


                writer.write("Case " + (i + 1) + ":\n");
                writer.write("Coordinates: " + Arrays.deepToString(coordinates) + "\n\n");
                writer.write("Before geometricReformer:\n");
                writer.write("Proximity Violations: " + problem.countProximityViolations(coordinates) + "\n");
                writer.write("---------------------------------------------"+"\n\n");

                coordinates = Arrays.copyOf(problem.decodeDirect(swarm.get(i).getPosition()), problem.nTurbines);
                coordinates = problem.geometricReformer(coordinates, problem.minDist);
                writer.write("After geometricReformer:\n");
                writer.write("Proximity Violations: " + problem.countProximityViolations(coordinates) + "\n");
                writer.write("Boundary Violations: " + problem.countBoundaryViolations(coordinates) + "\n");
                writer.write("Coordinates: " + Arrays.deepToString(coordinates) + "\n\n");
                System.out.println(problem.countProximityViolations(coordinates));
                coordinates = problem.geometricReformer(coordinates, problem.minDist);
                writer.write("After geometricReformerx2:\n");
                writer.write("Proximity Violations: " + problem.countProximityViolations(coordinates) + "\n");
                writer.write("Boundary Violations: " + problem.countBoundaryViolations(coordinates) + "\n");
                writer.write("Coordinates: " + Arrays.deepToString(coordinates) + "\n\n");

                writer.write("---------------------------------------------\n\n");

                coordinates = Arrays.copyOf(problem.decodeDirect(swarm.get(i).getPosition()), problem.nTurbines);
                coordinates = problem.geometricReformerRight(coordinates, problem.minDist);
                writer.write("After geometricReformerRight:\n");
                writer.write("Proximity Violations: " + problem.countProximityViolations(coordinates) + "\n");
                writer.write("Boundary Violations: " + problem.countBoundaryViolations(coordinates) + "\n");
                writer.write("Coordinates: " + Arrays.deepToString(coordinates) + "\n\n");
                coordinates = problem.geometricReformerRight(coordinates, problem.minDist);
                writer.write("After geometricReformerRightx2:\n");
                writer.write("Proximity Violations: " + problem.countProximityViolations(coordinates) + "\n");
                writer.write("Boundary Violations: " + problem.countBoundaryViolations(coordinates) + "\n");
                writer.write("Coordinates: " + Arrays.deepToString(coordinates) + "\n\n");

                writer.write("---------------------------------------------\n\n");
        
                coordinates = Arrays.copyOf(problem.decodeDirect(swarm.get(i).getPosition()), problem.nTurbines);
                coordinates = problem.geometricReformerRepulse(coordinates, problem.minDist);
                writer.write("After geometricReformerRepulse:\n");
                writer.write("Proximity Violations: " + problem.countProximityViolations(coordinates) + "\n");
                writer.write("Boundary Violations: " + problem.countBoundaryViolations(coordinates) + "\n");
                writer.write("Coordinates: " + Arrays.deepToString(coordinates) + "\n\n");
                coordinates = problem.geometricReformerRepulse(coordinates, problem.minDist);
                writer.write("After geometricReformerRepulsex2:\n");
                writer.write("Proximity Violations: " + problem.countProximityViolations(coordinates) + "\n");
                writer.write("Boundary Violations: " + problem.countBoundaryViolations(coordinates) + "\n");
                writer.write("Coordinates: " + Arrays.deepToString(coordinates) + "\n\n");

                writer.write("---------------------------------------------\n\n");
        
                coordinates = problem.decodeDirect(swarm.get(i).getPosition());
                coordinates = problem.geometricReformerEmbedded(coordinates, problem.minDist);
                writer.write("After geometricReformerEmbedded:\n");
                writer.write("Proximity Violations: " + problem.countProximityViolations(coordinates) + "\n");
                writer.write("Boundary Violations: " + problem.countBoundaryViolations(coordinates) + "\n");
                writer.write("Coordinates: " + Arrays.deepToString(coordinates) + "\n\n");
                coordinates = problem.geometricReformerEmbedded(coordinates, problem.minDist);
                writer.write("After geometricReformerEmbeddedx2:\n");
                writer.write("Proximity Violations: " + problem.countProximityViolations(coordinates) + "\n");
                writer.write("Boundary Violations: " + problem.countBoundaryViolations(coordinates) + "\n");
                writer.write("Coordinates: " + Arrays.deepToString(coordinates) + "\n\n");
                
                writer.write("---------------------------------------------\n\n");

            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
                
          
    }
    
}
