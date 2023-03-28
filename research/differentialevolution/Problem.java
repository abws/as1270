package research.differentialevolution;
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
    double penaltyCoefficient1;
    double penaltyCoefficient2;

    public Problem(KusiakLayoutEvaluator evaluator, WindScenario scenario, int popSize, double penaltyCoefficient1, double penaltyCoefficient2) throws Exception {
        this.scenario = scenario;
        this.evaluator = evaluator;

        //Commonly used
        this.nDimension = scenario.nturbines * 2; //Dimensionality of vectors. All vectors will be the same size.
        this.nTurbines = scenario.nturbines;
        this.popSize = popSize;
        this.height = scenario.height;
        this.width = scenario.width;
        this.minDist = scenario.R * 8;
        this.penaltyCoefficient1 = penaltyCoefficient1;
        this.penaltyCoefficient2 = penaltyCoefficient2;

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

    /**
     * Evaluates a given vector 
     * position using the Wake Free
     * Ratio evaluation function and 
     * applying a penalty function.
     * Only takes in vectors.
     * @param vector
     * @param c penalty coefficient
     * @return
     */
    public double evaluatePenalty(double[] position) {
        double[][] coordinates = decodeDirect(position);

        /* Calculate total energy production */
        evaluator.evaluate_2014(coordinates);   //calculates the AEP and sets it in the evaluator
        double energyProduction = evaluator.getEnergyOutput();

        double violationSum1 = 0.0;
        double violationSum2 = 0.0;

        for (int i = 0; i < coordinates.length; i++) {     //loop through each edge only once (n(n-1)/n) - ~doubles speed
            for (int j = i+1; j < coordinates.length; j++) {
                violationSum1 += proximityConstraintViolation(coordinates[i], coordinates[j], minDist);
            }
            violationSum2 += boundConstraintViolation(coordinates[i]);
        }

        //debugging
        System.out.printf("boundary :%d, num:%f\n",bound/2, violationSum1);
        System.out.println("mindist: " + countProxmityViolations(coordinates)+ ", num: "+ violationSum2);
        bound = 0;


        double penalty1 = this.penaltyCoefficient1 * Math.pow(violationSum1, 1);
        double penalty2 = this.penaltyCoefficient2 * Math.pow(violationSum2, 1);

        double penalty = penalty1 + penalty2;

        // double fitness = (energyProduction - (penalty)) / (scenario.wakeFreeEnergy * nTurbines);
        double fitness = energyProduction - penalty ;
        return fitness;
    }

        /**
     * Evaluates a given vector 
     * position using the Wake Free
     * Ratio evaluation function and 
     * applying a penalty function.
     * Only takes in vectors.
     * @param vector
     * @param c penalty coefficient
     * @return
     */
    public double evaluatePenaltyBinary(double[] position) {
        double[][] coordinates = decodeDirect(position);

        /* Calculate total energy production */
        evaluator.evaluate_2014(coordinates);   //calculates the AEP and sets it in the evaluator
        double energyProduction = evaluator.getEnergyOutput();

        double violationSum1 = 0.0;
        double violationSum2 = 0.0;

        violationSum1 = countProxmityViolations(coordinates);
        violationSum2 = countBoundaryViolations(coordinates);

        //debugging
        System.out.printf("mindist:%.0f\n",violationSum1);
        System.out.printf("boundary:%.0f\n",violationSum2);
        double penalty1 = this.penaltyCoefficient1 * Math.pow(violationSum1, 2);
        double penalty2 = this.penaltyCoefficient2 * Math.pow(violationSum2, 2);

        double penalty = penalty1 + penalty2;

        // double fitness = (energyProduction - (penalty)) / (scenario.wakeFreeEnergy * nTurbines);
        double fitness = energyProduction - penalty ;
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
     * Decodes a vector 
     * vector assuming the form 
     * (x1, x2,..., xn, y1, y2,..., yn)
     * to give a nx2 matrix 
     * representing the coordinates
     * of turbines.
     * @param vector 
     * @return
     */
    public double[][] decodeSeperate(double[] position) {
        double[][] decodedVector = new double[nTurbines][2];
        int x = 0;
        int y = nTurbines; /*If theres 400 turbines, the vector will have a dimension of 800. 0- 
                                                399 will be x coordinates & the last 400 will be y coordinates.*/
        for (int i = 0; i < nTurbines; i++) {
            decodedVector[i][0] = position[x];
            decodedVector[i][1] = position[y];
            x++; y++;
        }

        return decodedVector;
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
        double[][] layout = new double[nTurbines][2];

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
        // double d = calculateEuclideanDistance(repulser, shiftedPosition);
        // System.out.println(d);
        return shiftedPosition;
    }

    public double[] spacialShiftRightGP(double[] repulser, double[] manner, double distance, double z, double c) {
        double x1 = repulser[0];
        double y1 = repulser[1];
    
        double x2 = manner[0];
        double y2 = manner[1];
        
        double dx = x1 - x2;
        double dy = y1 - y2;
        
        double shiftedPositionX = (dy * (z + c) / distance) + x1; // Swapped and negated the difference in the X coordinate
        double shiftedPositionY = (-dx * (z + c) / distance) + y1; // Swapped and negated the difference in the Y coordinate
    
        double[] shiftedPosition = new double[]{shiftedPositionX, shiftedPositionY};
        return shiftedPosition;
    }
    public double[] spacialShiftGpp(double[] repulser, double[] manner, double distance, double z, double c) {
        double x1 = repulser[0];
        double y1 = repulser[1];
    
        double x2 = manner[0];
        double y2 = manner[1];
    
        // Calculate the vector from manner to repulser
        double vectorX = x1 - x2;
        double vectorY = y1 - y2;
    
        // Calculate the perpendicular vector to the right
        double perpVectorX = -vectorY;
        double perpVectorY = vectorX;
    
        // Normalize the perpendicular vector
        double perpMagnitude = Math.sqrt(perpVectorX * perpVectorX + perpVectorY * perpVectorY);
        double perpUnitVectorX = perpVectorX / perpMagnitude;
        double perpUnitVectorY = perpVectorY / perpMagnitude;
    
        // Calculate the shifted position by moving manner along the perpendicular direction
        double shiftedPositionX = x2 + perpUnitVectorX * (z + c);
        double shiftedPositionY = y2 + perpUnitVectorY * (z + c);
    
        double[] shiftedPosition = new double[]{shiftedPositionX, shiftedPositionY};
    
        return shiftedPosition;
    }

    public double[] spacialShiftGppp(double[] repulser, double[] manner, double distance, double z, double c) {
        double x1 = repulser[0];
        double y1 = repulser[1];
    
        double x2 = manner[0];
        double y2 = manner[1];
    
        // Calculate the vector from repulser to manner
        double vectorX = x2 - x1;
        double vectorY = y2 - y1;
    
        // Calculate the angle of the vector
        double angle = Math.atan2(vectorY, vectorX);
    
        // Add 90 degrees (pi / 2 radians) to the angle to get the angle to the right
        double rightAngle = angle + Math.PI / 2;
    
        // Calculate the shifted position in the direction of the right angle
        double shiftedPositionX = x1 + (z + c) * Math.cos(rightAngle);
        double shiftedPositionY = y1 + (z + c) * Math.sin(rightAngle);
    
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
                    // layout = decodeDirect(periodicBoundHandle(encodeDirect(layout)));
                }
            }
            for (int r = layout.length-1; r > 0; r--) {
                if (r != m) {
                    repulser = layout[r];
                    double distance = calculateEuclideanDistance(repulser, manner);
                    if (distance > z) continue;

                    layout = repulse(layout, repulser, manner, r, m, distance, z); 
                    // layout = decodeDirect(periodicBoundHandle(encodeDirect(layout)));
                }
            }
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
        double neww = calculateEuclideanDistance(shiftedPosition1, shiftedPosition2);

        layout[r] =  shiftedPosition1;
        layout[m] =  shiftedPosition2;

        return layout;
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
        double distance = calculateEuclideanDistance(pointA, pointB);
        double constraint = minDist - distance;
        constraint = Math.max(0, constraint);    //0 if good

        return constraint;
    }

    public double boundConstraintViolation(double[] coordinate) {
        double xDistance, yDistance, distance;
        xDistance = yDistance = distance = 0;
        
        if (coordinate[0] < 0) {xDistance+=(Math.abs(coordinate[0])); bound++;}        
        else if (coordinate[0] > this.width) {xDistance+=(coordinate[0]-this.width); bound++;}

        if (coordinate[1] < 0) {yDistance+=(Math.abs(coordinate[1])); bound++;}
        else if (coordinate[1] > this.height) {yDistance+=(coordinate[1]-this.height); bound++;}

        distance = Math.sqrt(Math.pow((xDistance), 2) + Math.pow((yDistance), 2));
        return distance;
    }


    /**
     * Boundary handling mechanism.
     * Moves vectors that fly out
     * of boundary to the closest feasible
     * position.
     * @param position
     * @return
     */
    public double[] absorbBoundHandle(double[] position) {
        for (int i = 0; i < position.length; i+=2) {
            position[i] = Math.max(0, position[i]);
            position[i+1] = Math.max(0, position[i+1]);

            position[i] = Math.min(position[i], this.width);
            position[i+1] = Math.min(position[i+1], this.height);
        }

        return position;
    }

    public double[] randomBoundHandle(double[] position) {
        Random r = new Random();

        for (int i = 0; i < position.length; i+=2) {
            if (position[i] < 0 || position[i] > this.width) {
                position[i] = r.nextDouble(this.width);
            }
            if (position[i+1] < 0 || position[i+1] > this.height) {
                position[i+1] = r.nextDouble(this.height);
            }
        }
        return position;
    }

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
     * Counts the number
     * of turbines breaking 
     * the minimum distance 
     * constraint.
     * For debugging or binary penalties
     * @param layout
     * @return
     */
    public int countProxmityViolations(double[][] layout) {
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