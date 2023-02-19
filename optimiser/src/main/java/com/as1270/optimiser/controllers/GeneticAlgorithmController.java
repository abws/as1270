package com.as1270.optimiser.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.Map;

@Controller
public class GeneticAlgorithmController {
//
//    /**
//     * Called when the
//     * algorithm in the request
//     * paramater is "genetic algorithm"
//     */
//    @PostMapping("/optimise?algorithm=geneticAlgorithm")
//    public String geneticAlgorithm(@RequestBody Map<String, String> data, Model model) {
//
//        //Process global parameters
//        int popSize = Integer.parseInt(data.get("popSize"));
//        int generations = Integer.parseInt(data.get("generations"));
//
//        //Process Parent Selection Mechanism
//        processParentSelection(data, model);
//
//        String parentSelection = data.get("parentSelection");
//
//
//
//
//        // Return the optimise page
//        return "optimise";
    }
}
