package com.as1270.optimiser.controllers;

import com.as1270.optimiser.models.api.java.*;
import com.as1270.optimiser.models.geneticalgorithm.*;
import com.as1270.optimiser.models.geneticalgorithm.Problem;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class GeneticAlgorithmController {
    private Problem problem;
    private WindScenario ws;
    private KusiakLayoutEvaluator evaluator;
    private int generations;
    private ExecutorService nonBlockingService = Executors
            .newCachedThreadPool();
    @PostMapping(value = "/optimise", params = "algorithm=geneticAlgorithm")
    public String geneticAlgorithm(@RequestParam Map<String, String> data, Model model) throws Exception {
        //extract form data
        int popSize = Integer.parseInt(data.get("popSize"));
        int generations = Integer.parseInt(data.get("generations"));

        //set up preliminaries
        this.ws = new WindScenario("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/api/Scenarios/00.xml");
        evaluator = new KusiakLayoutEvaluator();
        evaluator.initialize(ws);
        this.evaluator = evaluator;
        this.problem = new Problem(evaluator, ws, popSize);
        this.generations = generations;
        return "optimise";
    }

    @GetMapping("/sse")
    public SseEmitter sse() {
        SseEmitter emitter = new SseEmitter();
        //run genetic algorithm
        double[][] coordinates = GeneticAlgorithm.run(problem, generations, problem.POP_SIZE);
        System.out.println(Arrays.deepToString(coordinates));
        nonBlockingService.execute(() -> {
            try {
                int n = 0;
                for (int i = 0; i < 10; i++) {
                    emitter.send(n++);
                    Thread.sleep(100);
                }
                double[][] x = new double[2][2];
                x[0] = new double[]{2, 1};
                x[1] = new double[]{1, 2};

                emitter.send("x: " + Arrays.deepToString(x));
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }


}
