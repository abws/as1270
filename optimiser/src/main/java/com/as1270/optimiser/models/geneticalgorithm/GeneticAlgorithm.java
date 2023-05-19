package com.as1270.optimiser.models.geneticalgorithm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Instantiate the 4 components below
 * Follow the template below to change the different schemes
 */
public class GeneticAlgorithm {

    public static double[][] run(Problem problem, int generations, WebSocketSession session, ObjectMapper objectMapper) throws IOException {
        int popSize = problem.POP_SIZE;
        ParentSelection ps = new ParentSelection(problem);
        Recombination r = new Recombination(problem, 0.8);
        Mutation m = new Mutation(problem, 0.01);
        Replacement rp = new Replacement(problem);
        Repair re = new Repair(problem);

        List<Individual> population = problem.getRandomPopulation(popSize, problem.INDIV_LENGTH, problem.N_TURBINES);
        for (int i = 0; i < generations; i++) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(Collections.max(problem.getFitnessesArrayList(population)))));
            List<Individual> matingPool = new ArrayList<>();
            List<Individual> offSpring = new ArrayList<>();

            matingPool = ps.tournamentSelection(population, popSize, popSize/2, false); //parent selection
            offSpring = r.recombineNPoint(matingPool, popSize, problem.N_TURBINES/4); //recombination
            offSpring = re.repairRandom(m.mutatePopulationSwapSlidingBox(offSpring, 2)); //mutation and repair
            population = rp.elitism(population, offSpring, 1); //survival selection
        }
        return problem.getFittest(population);


    }
    
}
