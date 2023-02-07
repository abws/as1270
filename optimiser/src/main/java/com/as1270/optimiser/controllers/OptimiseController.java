package com.as1270.optimiser.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;

@Controller
public class OptimiseController {

    @GetMapping("/")
    public String showOptimisePage(Model model) {
        //run algorithm, model will contain a very large double[nturbines][2] of coordinates
        double[][] coordinates = {{5,2}, {3, 4}, {3, 5}, {6, 7}};

        model.addAttribute("name", Arrays.deepToString(coordinates));
        return "optimise";
    }
}
