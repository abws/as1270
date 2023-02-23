package research;

public class BitString {
    private String BitString;
    private int value;
    private int length;

    BitString(String BitString) {
        this.BitString = BitString;
        this.length = BitString.length();
        this.value = Integer.parseInt(BitString, 2);
    }

    public String getBitString() {
        return BitString;
    }
    public int getValue() {
        return value;
    }
    public int getLength() {
        return length;
    }
    public void setBitString(String BitString) {
        this.BitString = BitString;
        this.length = BitString.length();
        this.value = Integer.parseInt(BitString, 2);
    }

    public BitString xor(BitString other) {
        int length = Math.max(this.length, other.getLength());

        int result = this.value ^ other.getValue();
        String binaryResult = String.format("%" + length + "s", Integer.toBinaryString(result)).replace(' ', '0');

        return new BitString(binaryResult);
    }

 
    public BitString and(BitString other) {
        int length = Math.max(this.length, other.getLength());

        int result = this.value & other.getValue();
        String binaryResult = String.format("%" + length + "s", Integer.toBinaryString(result)).replace(' ', '0');

        return new BitString(binaryResult);
    }


    public BitString or(BitString other) {
        int length = Math.max(this.length, other.getLength());

        int result = this.value | other.getValue();
        String binaryResult = zeroExtend(result, length);

        return new BitString(binaryResult);
    }


    public BitString negate() {
       int negated = ~this.value;

        int result = Math.max(negated, -1*negated);
        String binaryResult = oneExtend(result, length);

        return new BitString(binaryResult);
    }

    private String zeroExtend(int i, int length) {
        return String.format("%" + length + "s", Integer.toBinaryString(i)).replace(' ', '0');
    }

    private String oneExtend(int i, int length) {
        return String.format("%" + length + "s", Integer.toBinaryString(i)).replace(' ', '1');
    }


    @Override
    public String toString() {
        return this.BitString;
    }
}
