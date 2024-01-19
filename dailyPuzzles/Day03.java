package dailyPuzzles;

import java.util.ArrayList;
import utils.Grid;
import utils.Coord;

public class Day03 {
    public static String Solve(Grid input, int part, boolean debug) {
        if (part == 1)
            return One(input, debug);
        else if (part == 2)
            return Two(input, debug);
        else
            return (part + " is not a part");
    }

    public static String One(Grid input, boolean debug) {
        long answer = 0;

        // find all digets and tokens in the grid
        ArrayList<Coord<String>> numbers = input.findToken("\\d+");
        ArrayList<Coord<String>> tokens = input.findToken("[^\\d+^.]|\\+");

        // iterate through each number and token, if any are adjacent, add to the total
        for (Coord<String> n : numbers) {
            for (Coord<String> t : tokens) {
                if (input.areAdjacent(n, t, true)) {
                    answer += Long.parseLong(n.getData());
                    break;
                }
            }
        }

        return answer + "";

    }

    public static String Two(Grid input, boolean debug) {
        long answer = 0;

        // find all digets and tokens in the grid
        ArrayList<Coord<String>> numbers = input.findToken("\\d+");
        ArrayList<Coord<String>> tokens = input.findToken("[^\\d+^.]|\\+");

        // iterate through all tokens, looking specifically for *
        for (Coord<String> t : tokens) {
            if (!t.getData().equals("*"))
                continue;

            long first = -1;
            long second = -1;
            // look for the 2 numbers adjacent to the *
            for (Coord<String> n : numbers) {

                if (input.areAdjacent(n, t, true)) {
                    if (first == -1) {
                        first = Long.parseLong(n.getData());
                        continue;
                    }

                    // only multiply and add to the total answer if both numbers are found
                    second = Long.parseLong(n.getData());
                    answer += first * second;
                    break;
                }
            }
        }

        return answer + "";

    }

}
