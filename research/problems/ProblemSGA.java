package research.problems;

/**
 * Problem formulation for optimizing a wind farm
 * using the evaluation function provided by WindFLO
 * Defines the nature and constraints of the problem
 * Contains any intitialization parameters and any 
 * extra things needed by the Simple Genetic Algorithm
 * @author Abdiwahab Salah 38.5
 * @version 27.01.23
 */
public class ProblemSGA extends Problem{

    @Override
    public Object encode(double[][] individual) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double[][] decode(Object individual) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long evaluate(Object layout) {
        // TODO Auto-generated method stub
        return 0;
    } 


    /**
     * Encoding operator: Denary -> Signed Binary
     * @param num The denary representaion of the binary number
     * @return binary The binary number to decode 
     */
    public String encode(int num, int bits) {
        StringBuilder sb = new StringBuilder();
        for (int i = bits - 1; i >= 0; i--) {
            int bit = (num >> i) & 1;
            sb.append(bit);
        }

        return sb.toString();
    }
    
}
