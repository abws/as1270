package research.miscellaneous;

public class BitStringOperators {

    public static String xor(String bitString1, String bitString2) {
        int length = Math.max(bitString1.length(), bitString2.length());
        int val1 = getIntegerValue(bitString1);
        int val2 = getIntegerValue(bitString2);


        int result = val1 ^ val2;
        String binaryResult = String.format("%" + length + "s", Integer.toBinaryString(result)).replace(' ', '0');

        return binaryResult;
    }

    public static String and(String bitString1, String bitString2) {
        int length = Math.max(bitString1.length(), bitString2.length());
        int val1 = getIntegerValue(bitString1);
        int val2 = getIntegerValue(bitString2);


        int result = val1 & val2;
        String binaryResult = String.format("%" + length + "s", Integer.toBinaryString(result)).replace(' ', '0');

        return binaryResult;
    }

 
    public static String or(String bitString1, String bitString2) {
        int length = Math.max(bitString1.length(), bitString2.length());
        int val1 = getIntegerValue(bitString1);
        int val2 = getIntegerValue(bitString2);


        int result = val1 | val2;
        String binaryResult = String.format("%" + length + "s", Integer.toBinaryString(result)).replace(' ', '0');

        return binaryResult;
    }

    public static String zeroExtend(int i, int length) {
        return String.format("%" + length + "s", Integer.toBinaryString(i)).replace(' ', '0');
    }

    public static String oneExtend(int i, int length) {
        return String.format("%" + length + "s", Integer.toBinaryString(i)).replace(' ', '1');
    }


    private static int getIntegerValue(String bitString) {
        return Integer.parseInt(bitString, 2);
    }
}
