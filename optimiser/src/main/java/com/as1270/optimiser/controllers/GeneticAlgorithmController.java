package com.as1270.optimiser.controllers;

import com.as1270.optimiser.models.api.java.*;
import com.as1270.optimiser.models.geneticalgorithm.*;
import com.as1270.optimiser.models.geneticalgorithm.Problem;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.Map;

@Controller
public class GeneticAlgorithmController {
    @PostMapping(value = "/optimise", params = "algorithm=geneticAlgorithm")
    public String geneticAlgorithm(@RequestParam Map<String, String> data, Model model) throws Exception {
        //extract form data
        int popSize = Integer.parseInt(data.get("popSize"));
        int generations = Integer.parseInt(data.get("generations"));

        //set up preliminaries
        WindScenario ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/api/Scenarios/00.xml");
        KusiakLayoutEvaluator evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        Problem problem = new Problem(evaluator, ws, popSize);

        //run genetic algorithm
        double[][] coordinates = GeneticAlgorithm.run(problem, generations, popSize);
        model.addAttribute("coordinates", Arrays.deepToString(coordinates)); //add coordinates as a string
        System.out.println(Arrays.deepToString(coordinates));
        return "optimise";
    }
}
