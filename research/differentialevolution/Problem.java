package research.differentialevolution;
import java.util.ArrayList;
import java.util.Arrays;
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
public class Problem {
    private KusiakLayoutEvaluator evaluator;
    private WindScenario scenario;
    private static int bound = 0;

    public int nDimension;
    public int popSize;
    public int nTurbines;
    public double minDist;
    public double height;
    public double width;

    //penalty parameters
    double w1;
    double w2;
    double k;

    public Problem(KusiakLayoutEvaluator evaluator, WindScenario scenario, int popSize, double w1, double w2, double k) throws Exception {
        this.scenario = scenario;
        this.evaluator = evaluator;

        //Commonly used
        this.nDimension = scenario.nturbines * 2; //Dimensionality of vectors. All vectors will be the same size.
        this.nTurbines = scenario.nturbines;
        this.popSize = popSize;
        this.height = scenario.height;
        this.width = scenario.width;
        this.minDist = scenario.R * 8;
        this.w1 = w1;
        this.w2 = w2;
        this.k = k;

    }
    /**
     * Evaluates a given vector 
     * position using the Wake Free
     * Ratio evaluation function.
     * Only takes in vectors.
     * @param vector
     * @return
     */
    public double evaluate(double[] position) {
        double[][] vectorCoordinates = decodeDirect(position);
        double fitness = evaluator.evaluate_2014(vectorCoordinates);
        // System.out.println("mindist: " + countViolations(vectorCoordinates));

        return fitness;
    }

    public double evaluatePenalty(double[] particlePosition) {
        double[][] particleCoordinates = decodeDirect(particlePosition);

        /* Calculate total energy production */
        evaluator.evaluate_2014(particleCoordinates);   //calculates the AEP and sets it in the evaluator
        double energyProduction = evaluator.getEnergyOutput();

        double p1 = 0;
        double p2 = 0;


        for (int i = 0; i < particleCoordinates.length; i++) {     //loop through each edge only once (n(n+1)/n) - ~doubles speed
            for (int j = i+1; j < particleCoordinates.length; j++) {
                p1 += staticProximityViolation(particleCoordinates[i], particleCoordinates[j], minDist);
            }
            // violationSum2 += boundConstraintViolation(particleCoordinates[i]);
        }
         p1 = binaryProximityViolation(particleCoordinates);
        // System.out.println("Violations: " + binaryProximityViolation(particleCoordinates));

        bound =0;
        p1 = Math.pow(p1, k);
        p1 = w1 * p1;

        // double fitness = (energyProduction - (penalty1 + penalty2)) / (scenario.wakeFreeEnergy * nTurbines);
        double fitness = (energyProduction - (p1 )) / (scenario.wakeFreeEnergy * nTurbines);
        
        return fitness;

    }


    /**
     * Decodes a vector 
     * vector assuming the form 
     * (x1, y1, x2, y2, ..., xn, yn)
     * to give a nx2 matrix 
     * representing the coordinates
     * of turbines.
     * @param vector 
     * @return
     */
    public double[][] decodeDirect(double[] position) {
        double[][] layout = new double[nTurbines][2];
        int index = 0;

        for (int i = 0; i < nTurbines; i++) {   //fill row by row
            layout[i][0] = position[index];
            layout[i][1] = position[index + 1];
            index += 2;
        }

        return layout;
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
        double[] position = new double[nDimension];
        int index = 0;

        for (int i = 0; i < nTurbines; i++) {   //fill row by row
            position[index] = layout[i][0];
            position[index + 1] = layout[i][1];
            index += 2;
        }

        return position;
    }

    /**
     * Initialises a swarm of vectors
     * with random positions & velocities
     * @param swarmSize
     * @return
     */
    public List<Vector> initialisePopulation(int popSize) {
        List<Vector> pop = new ArrayList<Vector>();
        for (int i = 0; i < popSize; i++) {
            pop.add(createRandomVector());
        }
        return pop;
    }


    /**
     * Initialises a single vector
     * with a random position, & a 
     * random initial velocity.
     * Position will be of the form
     * (x1, y1, x2, y2, ..., xn, yn).
     * All vectors will be uniformly
     * distributed accross the search space
     * @return
     */
    public Vector createRandomVector() {
        Random random = new Random();
        double[] randomPosition = new double[nDimension];
        // double[][] layout = new double[nTurbines][2];

        for (int i = 0; i < nDimension; i+=2) {
            randomPosition[i] = random.nextDouble(width);    //x coordinate
            randomPosition[i + 1] = random.nextDouble(height);  //y coordinate
        }

        // layout = geometricReformer(decodeDirect(randomPosition), minDist);
        // randomPosition = encodeDirect(layout);
        
        // randomPosition = periodicBoundHandle(randomPosition);

        Vector randomvector = new Vector(randomPosition, true, this);
        return randomvector;
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
    public double staticProximityViolation(double[] pointA, double[] pointB, double minDist) {
        double distance = calculateEuclideanDistance(pointA, pointB);

        double constraint = minDist - distance;     // If violated, distance will be less than minDist and we'll get a negative value
        constraint = Math.max(0, constraint);     // 0 if good

        return constraint;
    }


    /**
     * Counts the number
     * of turbines breaking 
     * the minimum distance 
     * constraint.
     * @param layout
     * @return
     */
    public int binaryProximityViolation(double[][] layout) {
        int count = 0;
        for (int i = 0; i < layout.length; i++) {     //loop through each edge only once (n(n+1)/n) - ~doubles speed
            for (int j = i+1; j < layout.length; j++) {
                if (calculateEuclideanDistance(layout[i], layout[j]) < 308 ) count++;
            }
        }
        return count;
    }

        /**
     * For debugging or binary penalties
     * @param position
     * @return
     */
    public int countBoundaryViolations(double[][] layout) {
        int count = 0;
        for (double[] l: layout) { 
            if ((l[0] < 0) || (l[1] < 0) || (l[0] > this.width) || (l[1] > this.height)) count++;
        }

        return count;
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
                        layout = decodeDirect(periodicBoundHandle(encodeDirect(layout)));

            for (int r = layout.length-1; r > 0; r--) {
                if (r != m) {
                    repulser = layout[r];
                    double distance = calculateEuclideanDistance(repulser, manner);
                    if (distance > z) continue;

                    manner = spacialShiftRight(repulser, manner, distance, z, 1); 
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

        return shiftedPosition;
    }
    
    
    

    /**
     * Reforms the points in
     * a planar euclidian space
     * so as to ensure the 
     * solution it presents is 
     * both feasible and geometrically
     * most similar to the original (v2)
     * @param layout the layout to potentially modify
     * @param z the minimum distance between two points
     * @return a feasible layout
     */
    public double[][] geometricReformerV2(double[][] layout, double z) {
        double[] manner;
        double[] repulser;

        for (int m = 0; m < layout.length; m++) {
            manner = layout[m];

            for (int r = 0; r < layout.length; r++) {
                if (r != m) {
                    repulser = layout[r];
                    double distance = calculateEuclideanDistance(repulser, manner);
                    if (distance > z) continue;

                    layout = repulse(layout, repulser, manner, r, m, distance, z); 
                }
            }
            // for (int r = layout.length-1; r > 0; r--) {
            //     if (r != m) {
            //         repulser = layout[r];
            //         double distance = calculateEuclideanDistance(repulser, manner);
            //         if (distance > z) continue;

            //         layout = repulse(layout, repulser, manner, r, m, distance, z); 
            //     }
            // }
        }

        return layout;
    }
    


    public double[][] repulse(double[][] layout, double[] repulser, double[] manner, int r, int m, double distance, double z) {
        distance = distance/2;
        double x1 = repulser[0];
        double y1 = repulser[1];

        double x2 = manner[0];
        double y2 = manner[1];

        double x3 = (x1 + x2) / 2.0;    //mid point
        double y3 = (y1 + y2) / 2.0; 

        double shiftedPositionX1 = (((x3 - x1) * z) / distance) + x3; 
        double shiftedPositionY1 = (((y3 - y1) * z) / distance) + y3; 

        double shiftedPositionX2 = (((x3 - x2) * z) / distance) + x3; 
        double shiftedPositionY2 = (((y3 - y2) * z) / distance) + y3;

        double[] shiftedPosition1 = new double[]{shiftedPositionX1, shiftedPositionY1};
        double[] shiftedPosition2 = new double[]{shiftedPositionX2, shiftedPositionY2};

        layout[r] =  shiftedPosition1;
        layout[m] =  shiftedPosition2;

        return layout;
    }

    /**
     * Latest edition that combines the 
     * fixation of both constraints
     * @param layout the layout to potentially modify
     * @param z the minimum distance between two points
     * @return a feasible layout
     */
    public double[][] geometricReformerV3(double[][] layout, double z) {
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
                    if (manner[0] < 0) {
                        manner[0] = (manner[0] % width) + width; //so we wrap wround 
                        r = 0;
                    }
                    if (manner[1] < 0) {
                        manner[1] = (manner[1] % height) + height;
                        r = 0;
                    }
                    if (manner[0] > this.width) {
                        manner[0] = (manner[0] % width); 
                        r=0;
                    }
                    if (manner[1] > this.height) {
                        manner[1] = (manner[1] % height); 
                        r=0;
                    }
                }
            }

            layout[m] = manner;
        }

        return layout;
    }

    public double[][] geometricReformerHybrid(double[][] layout, double z) {
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
                    if (manner[0] < 0) {
                        manner[0] = (manner[0] % width) + width; 
                        r = 0;
                    }
                    if (manner[1] < 0) {
                        manner[1] = (manner[1] % height) + height;
                        r = 0;
                    }
                    if (manner[0] > this.width) {
                        manner[0] = (manner[0] % width); 
                        r=0;
                    }
                    if (manner[1] > this.height) {
                        manner[1] = (manner[1] % height); 
                        r=0;
                    }
                }
            }

            layout[m] = manner;
        }

        return layout;
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
     * of boundary to a random feasible
     * position.
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
     * Boundary handling mechanism.
     * Moves particles that fly out
     * of boundary to a periodic feasible
     * position.
     * @param particlePosition
     * @return
     */
    public double[] periodicBoundHandle(double[] position) {
        for (int i = 0; i < position.length; i+=2) {
            if (position[i] < 0) {
                position[i] = (position[i] % width) + width; //so we wrap wround 
            }
            if (position[i+1] < 0) {
                position[i+1] = (position[i+1] % height) + height;

            }
            if (position[i] > this.width) {
                position[i] = (position[i] % width); 
            }
            if (position[i+1] > this.height) {
                position[i+1] = (position[i+1] % height); 
            }

        }
        return position;
    }

    /**
     * Counts the number of
     * turbines in a state
     * @param value 
     * @return
     */
    public int countTurbines(int[] particle) {
        int sum = 0;
        for (int i = 0; i < particle.length; i++) {
            if (particle[i] == 1) sum++;
        }
        return sum;
    }

    public double maxFitness(List<Vector> pop) {
        double maxFitness = pop.get(0).fitness;

        for (int i = 1; i < pop.size(); i++) {
            double current = pop.get(i).fitness;
            if (current >= maxFitness) {maxFitness = current;}
        }
        return maxFitness; 
    }

    public double avgFitness(List<Vector> pop) {
        double sum = 0;

        for (int i = 0; i < pop.size(); i++) {
            double current = pop.get(i).fitness;
            sum += current;
        }
        return sum/pop.size(); 
    }    
}