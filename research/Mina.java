package research;

public class Mina {
    
    public static void main(String[] args) {

        int a = 0b000010101;

        String hi = String.format("%9s", Integer.toBinaryString(a)).replace(' ', '0');
        
        System.out.println(hi);

        int b = Integer.parseInt(hi, 2);

        System.out.println(b);

        //System.out.println(Integer.bitCount(a));
    }
    
}
