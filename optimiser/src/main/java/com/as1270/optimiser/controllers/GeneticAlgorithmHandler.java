package com.as1270.optimiser.controllers;

import com.as1270.optimiser.models.api.java.*;
import com.as1270.optimiser.models.geneticalgorithm.*;
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
        int generations = Integer.parseInt(data.get("generations"));
        String environmentB64 = data.get("environment");
        byte[] environmentBytes = Base64.getDecoder().decode(environmentB64);
        String environmentXml = new String(environmentBytes, StandardCharsets.UTF_8);

        // Set up preliminaries
        WindScenario ws = new WindScenario(environmentXml);
        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        double[][] coordinates = new double[ws.nturbines][2];
        // Run genetic algorithm
        System.out.println(data.get("algorithm"));
        if (data.get("algorithm") == "geneticAlgorithm") {
            int popSize = Integer.parseInt(data.get("popSize"));
            com.as1270.optimiser.models.geneticalgorithm.Problem problem = new com.as1270.optimiser.models.geneticalgorithm.Problem(evaluator, ws, popSize);
            coordinates = GeneticAlgorithm.run(problem, generations, problem.POP_SIZE, session, objectMapper);
        }

        // Send the results to the client
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(coordinates)));
    }
}