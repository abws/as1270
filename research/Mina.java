package research;

import java.util.BitSet;

public class Mina {
    
    public static void main(String[] args) {

        // int a = 0b000010101;

        // String hi = String.format("%9s", Integer.toBinaryString(a)).replace(' ', '0');
        
        // System.out.println(hi);

        // int b = Integer.parseInt(hi, 2);

        // System.out.println(b);

        // String d = "10001010"; // binary representation of 10 with four bits
        // String e = "00001111"; // binary representation of 15 with four bits

        // int intA = Integer.parseInt(d, 2); // parse the binary string as an integer
        // int intB = Integer.parseInt(e, 2);

        // int result = intA ^ intB; // perform XOR operation

        // String binaryResult = Integer.toBinaryString(result); // convert the result back to binary string

        // System.out.println(binaryResult); // output: "00000101", which is the binary representation of 5
        // BitSet lol = new BitSet(10);
        // lol.set(7);
        // lol.set(5);
        
        // System.out.println(lol);
        //System.out.println(Integer.bitCount(a));

        BitString a = new BitString("10001010");
        BitString b = new BitString("1111");

        System.out.println(a.or(b));
    }
    
}
