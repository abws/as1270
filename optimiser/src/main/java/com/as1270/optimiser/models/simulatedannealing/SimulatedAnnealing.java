package com.as1270.optimiser.models.simulatedannealing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * The Simulated Annealing Algorithm
 * Paramters that can be changed
 * include the neighbourhood operator,
 * the intial temperature, the cooling
 * rate and the acceptance formula.
 */
public class SimulatedAnnealing {
    double temperature;
    double coolingRate;
    int iterations;
    int stepConstant;
    Problem problem;


    public SimulatedAnnealing(double temperature, double coolingRate, int iterations, int stepConstant, Problem problem) {
        this.temperature = temperature;
        this.coolingRate = coolingRate;
        this.iterations = iterations;
        this.stepConstant = stepConstant;

        this.problem = problem;
    }

    public double[][] run(WebSocketSession session, ObjectMapper objectMapper) throws IOException {
        State current = problem.generateInitialState();
        int i = 0;

        while (i < iterations) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(current.getFitness())));

             State neighbour = relocateSlidingBoxPersist(current, 2, true);
            double p = maxMetropolisAlgorithm(current.getFitness(), neighbour.getFitness(), temperature);
            if (Math.random() <= p) {
                current = neighbour;
            }
            temperature *= coolingRate;
            i++;
        }

        return problem.decode(current.getGrid());
    
    }

    /**
     * A selection scheme based
     * on the Boltzmann-Gibbs 
     * distribution.
     * Assumes maximisation
     * @param current
     * @param neighbour
     * @param t temperature
     */
    private double maxMetropolisAlgorithm(double current, double neighbour, double t) {
        if (neighbour > current) return 1;

        double difference = -(current - neighbour) * 1000;

        double p = Math.exp(difference / t);
        return p;
    }


    private State relocateSlidingBoxPersist(State current, double g, boolean persist) {
        Random r = new Random();
        String state = problem.stringify(current.getGrid());
        StringBuilder stateArray = new StringBuilder(state);

        for (int i = 0; i < (stateArray.length()); i++) {
            if (stateArray.charAt(i) != '1') continue; //only change if its a one
            int position1 = i;
            int position2 = r.nextInt(stateArray.length());
            if (persist) {
                char newChar = stateArray.charAt(position2) == '1' ? '0' : '1'; //flips the bit, //could use XOR
                position2 = problem.getNearest(stateArray.toString(), position2, newChar);
            }
            //experiment rate with any formula
            double rate = problem.slidingBox(new String(stateArray.toString()), position1);
            if (Math.random() < rate*(1-temperature)) {
                char temp = stateArray.charAt(position1);
                stateArray.setCharAt(position1, stateArray.charAt(position2));
                stateArray.setCharAt(position2, temp);  
            }
        }
        int[][] grid = problem.gridify(stateArray.toString(), problem.col, problem.row);
        return new State(grid, problem);
    }


}
