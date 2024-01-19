package dailyPuzzles;

import java.util.ArrayList;

import utils.Grid;

public class Day13 {
    public static String Solve(ArrayList<Grid> input, int part, boolean debug) {
        if (part == 1)
            return One(input, debug);
        else if (part == 2)
            return Two(input, debug);
        else
            return (part + " is not a part");
    }

    public static String One(ArrayList<Grid> input, boolean debug) {
        long answer = 0;

        for (Grid g : input) {
            int[] reflectCoord = findMirror(g, 0);
            if (reflectCoord[2] == 0)
                answer += reflectCoord[0] + 1;
            else
                answer += ((reflectCoord[0] + 1) * 100);
        }

        return answer + "";
    }

    public static String Two(ArrayList<Grid> input, boolean debug) {
        long answer = 0;

        for (Grid g : input) {
            int[] reflectCoord = findMirror(g, 1);
            // System.out.println(Arrays.toString(reflectCoord));
            if (reflectCoord[2] == 0)
                answer += reflectCoord[0] + 1;
            else
                answer += ((reflectCoord[0] + 1) * 100);
        }
        return answer + "";
    }

    private static int[] findMirror(Grid grid, int diff) {
        boolean isFound = false;
        // [0] is the first col/row, [1] is the second col/row, [2] is a flag for
        // vertical(0) or horizontal(1)
        int[] output = new int[] { 0, 0, 0 };

        // start by checking columns
        for (int i = 0; i < grid.getLength() - 1; i++) {
            int remainingDiff = diff;
            int comp = compareString(grid.getColumn(i), grid.getColumn(i + 1));

            // if the comparison returned a single difference, remove remaining differences
            // and change comp to 0
            if (remainingDiff > 0 && comp > 0) {
                remainingDiff--;
                comp--;
            }

            // if columns are the same, check all columns from back to front
            if (comp == 0) {
                output[0] = i;
                output[1] = i + 1;
                if (validateMirror(grid, output, remainingDiff)) {
                    isFound = true;
                    break;
                }
            }
        }

        // if mirror was found, return it
        if (isFound)
            return output;

        // check the rows
        output[2] = 1;
        for (int i = 0; i < grid.getHeight() - 1; i++) {
            int remainingDiff = diff;
            int comp = compareString(grid.getRow(i), grid.getRow(i + 1));
            // if the comparison returned a single difference, remove remaining differences
            // and change comp to 0
            if (remainingDiff > 0 && comp > 0) {
                remainingDiff--;
                comp--;
            }

            // if rows are the same, check all columns from back to front
            if (comp == 0) {
                output[0] = i;
                output[1] = i + 1;
                if (validateMirror(grid, output, remainingDiff)) {
                    isFound = true;
                    break;
                }
            }
        }

        return output;
    }

    private static boolean validateMirror(Grid grid, int[] coord, int diff) {
        int i = coord[0] - 1;
        int j = coord[1] + 1;
        int remainingDiff = diff;

        // check columns or rows based on what was passed in the flag
        if (coord[2] == 0) {
            // check all the way until i or j is that the end of the grid
            while (i >= 0 && j < grid.getLength()) {
                int comp = compareString(grid.getColumn(i), grid.getColumn(j));
                // if the strings are not the same, or only have one difference with no
                // differences remaining, return false
                if (comp == -1 || (comp == 1 && remainingDiff == 0))
                    return false;

                // if there was a difference remaining and the strings had only one difference,
                // remove the remaining difference
                if (comp == 1)
                    remainingDiff--;
                i--;
                j++;
            }
        } else {
            while (i >= 0 && j < grid.getHeight()) {
                // if the strings are not the same, or only have one difference with no
                // differences remaining, return false
                int comp = compareString(grid.getRow(i), grid.getRow(j));
                if (comp == -1 || (comp == 1 && remainingDiff == 0))
                    return false;

                // if there was a difference remaining and the strings had only one difference,
                // remove the remaining difference
                if (comp == 1)
                    remainingDiff--;

                i--;
                j++;
            }
        }

        return remainingDiff == 0;

    }

    private static int compareString(String s1, String s2) {
        // 0 for same string
        if (s1.equals(s2))
            return 0;

        int differences = 0;
        // strings should always be the same length
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i))
                differences++;

            // return -1 if the strings have a greater differnce than 1
            if (differences > 1)
                return -1;
        }

        // System.out.println("Smudge found at:\n" + s1 + "\n" + s2);
        // return 1 if strings only have 1 difference
        return 1;
    }
}
