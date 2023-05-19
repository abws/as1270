package com.as1270.optimiser.controllers;

import com.as1270.optimiser.models.api.java.*;
import com.as1270.optimiser.models.geneticalgorithm.*;
import com.as1270.optimiser.models.binaryparticleswarmoptimisation.*;
import com.as1270.optimiser.models.simulatedannealing.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.Map;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class GeneticAlgorithmHandler extends TextWebSocketHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Deserialize input parameters from the received message 
        Map<String, String> data = objectMapper.readValue(message.getPayload(), new TypeReference<>() {});
        String environmentB64 = data.get("environment");
        byte[] environmentBytes = Base64.getDecoder().decode(environmentB64);
        String environmentXml = new String(environmentBytes, StandardCharsets.UTF_8);

        // Set up preliminaries
        WindScenario ws = new WindScenario(environmentXml);
        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        double[][] coordinates = new double[ws.nturbines][2];


        if (data.get("geneticAlgorithm").equals("1")) {
            System.out.println("Running genetic algorithm");
            int generations = Integer.parseInt(data.get("generations"));
            int popSize = Integer.parseInt(data.get("popSize"));
            com.as1270.optimiser.models.geneticalgorithm.Problem problem = new com.as1270.optimiser.models.geneticalgorithm.Problem(evaluator, ws, popSize);
            coordinates = GeneticAlgorithm.run(problem, generations, session, objectMapper);
        }

        else if (data.get("particleSwarmOptimisation").equals("1")) {
            System.out.println("Running pso");
            int swarmSize = Integer.parseInt(data.get("swarmSize"));
            double c1 = Double.parseDouble(data.get("c1"));
            double c2 = Double.parseDouble(data.get("c2"));
            int k = Integer.parseInt(data.get("k"));
            int iterations = Integer.parseInt(data.get("iterations"));

            com.as1270.optimiser.models.binaryparticleswarmoptimisation.Problem problem = new com.as1270.optimiser.models.binaryparticleswarmoptimisation.Problem(evaluator, ws, swarmSize);
            ParticleSwarmOptimisation pso = new ParticleSwarmOptimisation(problem.swarmSize, c1, c2, k, iterations, false, problem);

            coordinates = pso.run(session, objectMapper);
        }

        else if (data.get("simulatedAnnealing").equals("1")) {
            double temperature = Double.parseDouble(data.get("initialTemperature"));
            double coolingRate = Double.parseDouble(data.get("coolingRate"));
            int iterations = Integer.parseInt(data.get("iterationSA"));
            int stepConstant = Integer.parseInt(data.get("stepConstant"));
            com.as1270.optimiser.models.simulatedannealing.Problem problem = new com.as1270.optimiser.models.simulatedannealing.Problem(evaluator, ws);

            SimulatedAnnealing sa = new SimulatedAnnealing(temperature, coolingRate, iterations, stepConstant, problem);
            coordinates = sa.run(session, objectMapper);

        }

        // Send the results to the client
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(coordinates)));
    }
}