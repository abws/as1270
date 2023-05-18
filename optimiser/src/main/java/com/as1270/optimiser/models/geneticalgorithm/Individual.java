package com.as1270.optimiser.models.geneticalgorithm;

/**
 * Class for representing binary individuals
 * @author Abdiwahab Salah
 * @version 05.02.23
 */
public class Individual {
    public String value;
    public double fitness;

    public Problem problem;


    Individual(String value, Problem problem, boolean wantsFitness) {
        this.value = value;
        this.problem = problem;
        fitness = (wantsFitness) ? problem.evaluate(value) : 0.0; //called
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value){
        this.value = value;
    }
    
    public void updateFitness() { //called
        this.fitness = problem.evaluate(value);
    }

    public double getFitness() {
        return this.fitness;
    }   
    
}
