package com.as1270.optimiser.models.geneticalgorithm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//remember, since the lists contains objects, changing their values in a function will change their true values
public class GeneticAlgorithm {

    public static double[][] run(Problem problem, int generations, int popSize, WebSocketSession session, ObjectMapper objectMapper) throws Exception {
        ParentSelection ps = new ParentSelection(problem);
        Recombination r = new Recombination(problem, 0.7);
        Mutation m = new Mutation(problem, 0.1);
        Replacement rp = new Replacement(problem);


        List<Individual> population = problem.getRandomPopulation(popSize, problem.INDIV_LENGTH, problem.N_TURBINES);
        
        for (int i = 0; i < generations; i++) {
            System.out.println(Collections.max(problem.getFitnessesArrayList(population)));
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(Collections.max(problem.getFitnessesArrayList(population)))));
            List<Individual> matingPool = new ArrayList<>();
            List<Individual> offSpring = new ArrayList<>();

            matingPool = ps.tournamentSelection(population, popSize, 10, false); //parent selection
            offSpring = r.recombineNPoint(matingPool, popSize, 20); //recombination
            offSpring = problem.legalise(m.mutatePopulation(offSpring)); //mutation and repair
            population = rp.elitism(population, offSpring, 1); //survival selection
        }

        return problem.getFittest(population);

    }
}
