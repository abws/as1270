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
public class OptimiseController {
    @GetMapping("/optimise")
    public String showOptimisePage() {
        return "optimise";
    }

//    @GetMapping("/optimise")
//    public String showOptimisePage(Model model) {
//        //run algorithm, model will contain a very large double[nturbines][2] of coordinates
//        double[][] coordinates = {{5,2}, {3, 4}, {3, 5}, {6, 7}};
//
//        model.addAttribute("name", Arrays.deepToString(coordinates));
//        return "optimise";
//    }
//
//    @PostMapping("/optimise")
//    public String optimise(@RequestParam Map<String, String> data, Model model) {
//        for (String key : data.keySet()) {
//            System.out.println(key + " " + data.get(key));
//        }
//        model.addAttribute("algorithm", data.get("algorithm"));
//        return "optimise";
//    }
}
