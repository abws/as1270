package research.continuous;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import research.api.java.*;

/**
 * Problem class representing
 * the problem formulation.
 * In charge of enforcing constraints,
 * decoding solutions, and
 * providing access to the 
 * API as well as performing
 * helpful claculations
 * @author Abdiwahab Salah
 * @version 14.02.23
 */
public abstract class ContinuousProblem {
    private KusiakLayoutEvaluator evaluator;
    private WindScenario scenario;


    public int nDimension;
    public int n;
    public int nTurbines;
    public double minDist;
    public double height;
    public double width;
    double penaltyCoefficient1;
    double penaltyCoefficient2;

    public ContinuousProblem(KusiakLayoutEvaluator evaluator, WindScenario scenario, int n, double penaltyCoefficient1, double penaltyCoefficient2) throws Exception {
        this.scenario = scenario;
        this.evaluator = evaluator;

        //Commonly used
        this.nDimension = scenario.nturbines * 2; //Dimensionality of particles. All vectors will be the same size.
        this.nTurbines = scenario.nturbines;
        this.n = n;
        this.height = scenario.height;
        this.width = scenario.width;
        this.minDist = scenario.R * 8;
        this.penaltyCoefficient1 = penaltyCoefficient1;
        this.penaltyCoefficient2 = penaltyCoefficient2;
    }

    /**
     * Evaluates a given particle 
     * position using the Wake Free
     * Ratio evaluation function.
     * Only takes in vectors.
     * @param particle
     * @return
     */
    public double evaluate(double[] particlePosition) {
        double[][] particleCoordinates = decodeDirect(particlePosition);
        double fitness = evaluator.evaluate_2014(particleCoordinates);

        return fitness;
    }

    /**
     * Evaluates a given particle 
     * position using the Wake Free
     * Ratio evaluation function and 
     * applying a penalty function.
     * Only takes in vectors.
     * @param particle
     * @param c penalty coefficient
     * @return
     */
    public double evaluatePenalty(double[] particlePosition) {
        double[][] particleCoordinates = decodeDirect(particlePosition);

        /* Calculate total energy production */
        evaluator.evaluate_2014(particleCoordinates);   //calculates the AEP and sets it in the evaluator
        double energyProduction = evaluator.getEnergyOutput();

        double violationSum1 = 0;
        double violationSum2 = 0;


        for (int i = 0; i < particleCoordinates.length; i++) {     //loop through each edge only once (n(n+1)/n) - ~doubles speed
            for (int j = i+1; j < particleCoordinates.length; j++) {
                violationSum1 += proximityConstraintViolation(particleCoordinates[i], particleCoordinates[j], minDist);
            }
            violationSum2 += boundConstraintViolation(particleCoordinates[i]);
        }
        
        double penalty1 = this.penaltyCoefficient1 * (Math.sqrt(violationSum1));
        // System.out.println("Vio;1111 "+penalty1);

        // double penalty2 = this.penaltyCoefficient2 * (Math.sqrt(violationSum2));
        // System.out.println("Vio;2222 "+penalty2);


        // double fitness = (energyProduction - (penalty1 + penalty2)) / (scenario.wakeFreeEnergy * nTurbines);
        double fitness = (energyProduction - (penalty1 )) / (scenario.wakeFreeEnergy * nTurbines);
        

     

        return fitness;
    }

    /**
     * Decodes a particle 
     * vector assuming the form 
     * (x1, y1, x2, y2, ..., xn, yn)
     * to give a nx2 matrix 
     * representing the coordinates
     * of turbines.
     * @param particleVector 
     * @return
     */
    public double[][] decodeDirect(double[] particlePosition) {
        double[][] layout = new double[nTurbines][2];
        int index = 0;

        for (int i = 0; i < nTurbines; i++) {   //fill row by row
            layout[i][0] = particlePosition[index];
            layout[i][1] = particlePosition[index + 1];
            index += 2;
        }

        return layout;
    }

    /**
     * Decodes a particle 
     * vector assuming the form 
     * (x1, x2,..., xn, y1, y2,..., yn)
     * to give a nx2 matrix 
     * representing the coordinates
     * of turbines.
     * @param particleVector 
     * @return
     */
    public double[][] decodeSeperate(double[] particlePosition) {
        double[][] decodedParticle = new double[nTurbines][2];
        int x = 0;
        int y = nTurbines; /*If theres 400 turbines, the vector will have a dimension of 800. 0- 
                                                399 will be x coordinates & the last 400 will be y coordinates.*/
        for (int i = 0; i < nTurbines; i++) {
            decodedParticle[i][0] = particlePosition[x];
            decodedParticle[i][1] = particlePosition[y];
            x++; y++;
        }

        return decodedParticle;
    }

    /**
     * Encodes a layout 
     * into a vector in the form 
     * (x1, x2,..., xn, y1, y2,..., yn)
     * Mainly used after geomtric reformation
     * @param layout
     * @return
     */
    public double[] encodeDirect(double[][] layout) {
        double[] particlePosition = new double[nDimension];
        int index = 0;

        for (int i = 0; i < nTurbines; i++) {   //fill row by row
            particlePosition[index] = layout[i][0];
            particlePosition[index + 1] = layout[i][1];
            index += 2;
        }

        return particlePosition;
    }



    /**
     * Reforms the points in
     * a planar euclidian space
     * so as to ensure the 
     * solution it presents is 
     * both feasible and geometrically
     * most similar to the original
     * @param layout the layout to potentially modify
     * @param z the minimum distance between two points
     * @return a feasible layout
     */
    public double[][] geometricReformer(double[][] layout, double z) {
        double[] manner;
        double[] repulser;

        for (int m = 0; m < layout.length; m++) {
            manner = layout[m];

            for (int r = 0; r < layout.length; r++) {
                if (r != m) {
                    repulser = layout[r];
                    double distance = calculateEuclideanDistance(repulser, manner);
                    if (distance > z) continue;

                    manner = spacialShift(repulser, manner, distance, z, 1); 
                }
            }

            layout[m] = manner;
        }

        return layout;
    }

    /**
     * Shifts the position
     * of a single coordinate 
     * in the realm of another
     * such that it moves just 
     * outside of the realm
     * of infeasiblity
     * @param repulser the position that has a realm
     * @param manner the position to be shifted 
     * @param distance the euclidian distance between manner
     * @param z the radius of the realm
     * @param c a constant to ensure we move slightly outside realm (and not on the edge)
     * @return
     */
    public double[] spacialShift(double[] repulser, double[] manner, double distance, double z, double c) {
        double x1 = repulser[0];
        double y1 = repulser[1];

        double x2 = manner[0];
        double y2 = manner[1];
        
        
        double shiftedPositionX = ((x1 - x2) * (z + c) / distance) + x1;    //unit vector terminal points towards the one that comes first (x1) in the subtraction
        double shiftedPositionY = ((y1 - y2) * (z + c) / distance) + y1;
        double[] shiftedPosition = new double[]{shiftedPositionX, shiftedPositionY};

        return shiftedPosition;
    }

    public double[] spacialShiftRight(double[] repulser, double[] manner, double distance, double z, double c) {
        double x1 = repulser[0];
        double y1 = repulser[1];

        double x2 = manner[0];
        double y2 = manner[1];
        double sign = (x1 - x2) / Math.abs(x1 - x2); //will be 1 or -1        
        
        double shiftedPositionX = (sign*(x1 - x2) * (z + c) / distance) + x1;    //unit vector terminal points towards the one that comes first (x1) in the subtraction
        double shiftedPositionY = (sign*(y1 - y2) * (z + c) / distance) + y1;

        double[] shiftedPosition = new double[]{shiftedPositionX, shiftedPositionY};
        double d = calculateEuclideanDistance(repulser, shiftedPosition);
        return shiftedPosition;
    }


    /**
     * Calculates the distance
     * between two turbines
     * @param pointA
     * @param pointB
     * @return
     */
    public double calculateEuclideanDistance(double[] pointA, double[] pointB) {
        double x1 = pointA[0];
        double x2 = pointB[0];
        double y1 = pointA[1];
        double y2 = pointB[1];

        double distance = Math.sqrt( Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
        return distance;
    }

    /**
     * Calculates the violation value
     * for a proximity constraint violation
     * Calculates the squared 
     * distance between two 
     * turbines, subtracted by 
     * the minimum distance squared
     * @param pointA
     * @param pointB
     * @return
     */
    public double proximityConstraintViolation(double[] pointA, double[] pointB, double minDist) {
        double x1 = pointA[0];
        double x2 = pointB[0];
        double y1 = pointA[1];
        double y2 = pointB[1];

        double distanceSquared = Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2);
        double constraint = distanceSquared - Math.pow(minDist, 2);     //If violated, distance squared will be less than minDist squared and we'll get a negative value
        constraint = Math.abs(Math.min(0, constraint));    //Get the magnitude of the negative number, or 0 otherwise

        return constraint;
    }

    public double boundConstraintViolation(double[] coordinate) {
        double xDistance, yDistance, distanceSquared;
        xDistance = yDistance = distanceSquared = 0;

        if (coordinate[0] < 0) xDistance+=(coordinate[0]*-1);
        if (coordinate[1] < 0) yDistance+=(coordinate[1]*-1);
        if (coordinate[0] > this.width) xDistance+=(coordinate[0]-this.width);
        if (coordinate[1] > this.height) yDistance+=(coordinate[1]-this.height);

        distanceSquared = Math.pow((xDistance), 2) + Math.pow((yDistance), 2);
        return distanceSquared;
    }


    /**
     * Calculates the weight difference
     * for each iteration step.
     * @return weight step
     */
    public double calculateWeightStep(double wMax, double wMin, int maxIterations) {
        double wStep =  (wMax - wMin) / maxIterations;
        return wStep;
    }
    
    /**
     * Boundary handling mechanism.
     * Moves particles that fly out
     * of boundary to the closest feasible
     * position.
     * @param particlePosition
     * @return
     */
    public double[] absorbBoundHandle(double[] particlePosition) {
        for (int i = 0; i < particlePosition.length; i+=2) {
            particlePosition[i] = Math.max(0, particlePosition[i]);
            particlePosition[i+1] = Math.max(0, particlePosition[i+1]);

            particlePosition[i] = Math.min(particlePosition[i], this.width);
            particlePosition[i+1] = Math.min(particlePosition[i+1], this.height);
        }

        return particlePosition;
    }

    /**
     * Boundary handling mechanism.
     * Moves particles that fly out
     * of boundary to a random
     * position (only for that element)
     * @param particlePosition
     * @return
     */
    public double[] randomBoundHandle(double[] particlePosition) {
        Random r = new Random();

        for (int i = 0; i < particlePosition.length; i+=2) {
            if (particlePosition[i] < 0 || particlePosition[i] > this.width) {
                particlePosition[i] = r.nextDouble(this.width);
            }
            if (particlePosition[i+1] < 0 || particlePosition[i+1] > this.height) {
                particlePosition[i+1] = r.nextDouble(this.height);
            }
        }
        return particlePosition;
    }

    /**
     * Counts the number
     * of turbines breaking 
     * the minimum distance 
     * constraint.
     * @param layout
     * @return
     */
    public int countViolations(double[][] layout) {
        int count = 0;
        for (int i = 0; i < layout.length; i++) {     //loop through each edge only once (n(n+1)/n) - ~doubles speed
            for (int j = i+1; j < layout.length; j++) {
                if (calculateEuclideanDistance(layout[i], layout[j]) < 308 ) count++;
            }
        }
        return count;
    }

    /**
     * Checks if the layout
     * violates the boundary
     * constraint
     * @param position
     * @return
     */
    public boolean boundaryViolated(double[] position) {
        double[][] layout = decodeDirect(position);
        for (double[] l: layout) {     //loop through each edge only once (n(n+1)/n) - ~doubles speed
            if ((l[0] < 0) || (l[1] < 0) || (l[0] > this.width) || (l[1] > this.height)) return true;
        }

        return false;
    }
    
    /**
     * Calculates the max 
     * fitness of an array 
     * of layouts
     * @param fitness
     * @return
     */
    public double maxFitness(double[] fitness) {
        double maxFitness = fitness[0];
        for (int i = 1; i < n; i++) {
            if (fitness[i] > maxFitness) maxFitness = fitness[i];
        }
        return maxFitness; 
    }
    
}