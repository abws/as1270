package research;

/*
 * Testing suite
 * Used for testing single functions
 * Now leave me and let me get to work in this mini lab
 */
public class Main {
    public static void main(String[] args) {
        String test = "101101101";
        int x = 3; int y = 3;
        int[][] grid = gridify(test, x, y);
        int[][] t = {{2, 3}, {3, 4}, {4, 6}};

        //System.out.println(twoDArrayToString(t));
        System.out.println(twoDArrayToString(grid));
        
    }
    private static int[][] gridify(String ind, int x, int y) {
        int[][] grid = new int[y][x]; //[rows][columns] since rows are 'bigger' and classified by first
        int count = 0;

        for (int i = 0; i < y; i ++) {
            for (int j = 0; j < x; j++) {
                grid[i][j] = Character.getNumericValue(ind.charAt(count));
                System.out.println(grid[i][j]);
                count++;
            }
        }

        return grid;
    }

    public static String twoDArrayToString(int[][] array) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : array) {
            sb.append("[");
            for (int val : row) {
                sb.append(val);
                sb.append(",");
            }
            sb.deleteCharAt(sb.length()-1);
            sb.append("]");
        }
        return sb.toString();
    }
    
    

    
}


