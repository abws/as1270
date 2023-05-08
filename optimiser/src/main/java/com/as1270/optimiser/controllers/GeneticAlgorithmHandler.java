package com.as1270.optimiser.controllers;

import com.as1270.optimiser.models.api.java.*;
import com.as1270.optimiser.models.geneticalgorithm.*;
import com.as1270.optimiser.models.geneticalgorithm.Problem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.Map;

public class GeneticAlgorithmHandler extends TextWebSocketHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Deserialize input parameters from the received message 
        Map<String, String> data = objectMapper.readValue(message.getPayload(), new TypeReference<>() {});
        int popSize = Integer.parseInt(data.get("popSize"));
        int generations = Integer.parseInt(data.get("generations"));

        // Set up preliminaries
        WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/api/Scenarios/00.xml");
        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        Problem problem = new Problem(evaluator, ws, popSize);

        // Run genetic algorithm
        double[][] coordinates = GeneticAlgorithm.run(problem, generations, problem.POP_SIZE, session, objectMapper);

        // Send the results to the client
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(coordinates)));
    }
}