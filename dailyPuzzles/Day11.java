package dailyPuzzles;

import java.util.ArrayList;
import utils.Coord;
import utils.Grid;

public class Day11 {
    public static String Solve(Grid input, int part, boolean debug) {

        // find the coords of each galaxy
        ArrayList<Coord<String>> galaxies = input.findToken("#");

        if (part == 1)
            return One(input, galaxies, debug);
        else if (part == 2)
            return Two(input, galaxies, debug);
        else
            return (part + " is not a part");
    }

    public static String One(Grid input, ArrayList<Coord<String>> galaxies, boolean debug) {
        long answer = 0L;

        // expand the galaxies
        increaseSize(galaxies, input, 2);

        // find and add the difference between each galaxy
        for (int i = 0; i < galaxies.size() - 1; i++) {
            for (int j = i + 1; j < galaxies.size(); j++) {
                answer += distance(galaxies.get(i), galaxies.get(j));
            }
        }

        return answer + "";
    }

    public static String Two(Grid input, ArrayList<Coord<String>> galaxies, boolean debug) {
        long answer = 0L;

        // expand the galaxies
        increaseSize(galaxies, input, 1000000);

        // find and add the difference between each galaxy
        for (int i = 0; i < galaxies.size() - 1; i++) {
            for (int j = i + 1; j < galaxies.size(); j++) {
                answer += distance(galaxies.get(i), galaxies.get(j));
            }
        }

        return answer + "";
    }

    private static long distance(Coord<String> point1, Coord<String> point2) {
        long horizontalDis = Math.abs(point1.getCol() - point2.getCol());
        long verticalDis = Math.abs(point1.getRow() - point2.getRow());

        // return the sum of the absolute value of differences between point 1
        // and point 2's x and y coordinates
        return horizontalDis + verticalDis;
    }

    public static ArrayList<Coord<String>> increaseSize(ArrayList<Coord<String>> coords, Grid input, int size) {
        int height = input.getHeight();
        int length = input.getLength();

        ArrayList<Integer> clearRow = new ArrayList<Integer>();
        ArrayList<Integer> clearCol = new ArrayList<Integer>();

        // find each clear row
        for (int row = 0; row < height; row++) {
            if (!input.existsInRow(row, "#"))
                clearRow.add(row);
        }

        // find each clear column
        for (int col = 0; col < length; col++) {
            if (!input.existsInColumn(col, "#"))
                clearCol.add(col);
        }

        System.out.println("rows to add: " + clearRow);
        System.out.println("cols to add: " + clearCol);
        // for each clear row starting from the largest
        for (int row = clearRow.size() - 1; row >= 0; row--) {
            // increase the y value of each galaxy that is greater than the original clear
            // row by the given amount
            for (Coord<String> coord : coords) {
                if (coord.getRow() > clearRow.get(row))
                    coord.setRow(coord.getRow() + size - 1);
            }
        }

        // for each clear column starting from the largest
        for (int col = clearCol.size() - 1; col >= 0; col--) {
            // increase the x value of each galaxy that is greater than the original clear
            // column by the given amount
            for (Coord<String> coord : coords) {
                if (coord.getCol() > clearCol.get(col))
                    coord.setCol(coord.getCol() + size - 1);
            }
        }

        return coords;
    }

}
