package com.as1270.optimiser.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OptimiseController {

    @GetMapping("/")
    public String showOptimisePage(Model model) {
        model.addAttribute("name", "Abdiwahab");
        return "optimise";
    }
}
