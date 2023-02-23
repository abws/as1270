package research;

public class BitStringOperators {

    public String xor(String bitString1, String bitString2) {
        int length = Math.max(bitString1.length(), bitString1.length());
        int val1 = getIntegerValue(bitString1);
        int val2 = getIntegerValue(bitString2);


        int result = val1 ^ val2;
        String binaryResult = String.format("%" + length + "s", Integer.toBinaryString(result)).replace(' ', '0');

        return binaryResult;
    }

 
    public String and(String bitString1, String bitString2) {
        int length = Math.max(bitString1.length(), bitString1.length());
        int val1 = getIntegerValue(bitString1);
        int val2 = getIntegerValue(bitString2);


        int result = val1 & val2;
        String binaryResult = String.format("%" + length + "s", Integer.toBinaryString(result)).replace(' ', '0');

        return binaryResult;
    }

 
    public String or(String bitString1, String bitString2) {
        int length = Math.max(bitString1.length(), bitString1.length());
        int val1 = getIntegerValue(bitString1);
        int val2 = getIntegerValue(bitString2);


        int result = val1 | val2;
        String binaryResult = String.format("%" + length + "s", Integer.toBinaryString(result)).replace(' ', '0');

        return binaryResult;
    }


    public String zeroExtend(int i, int length) {
        return String.format("%" + length + "s", Integer.toBinaryString(i)).replace(' ', '0');
    }

    public String oneExtend(int i, int length) {
        return String.format("%" + length + "s", Integer.toBinaryString(i)).replace(' ', '1');
    }


    private int getIntegerValue(String bitString) {
        return Integer.parseInt(bitString, 2);
    }
}
