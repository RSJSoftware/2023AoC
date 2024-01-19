package dailyPuzzles;

import java.util.ArrayList;

import utils.Dir;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Day18 {
    public record Instruction(Dir d, int amount) {

    }

    public record Vertex(int row, int col) {
    }

    public static String Solve(ArrayList<String> input, int part, boolean debug) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        ArrayList<Instruction> trueInstructions = new ArrayList<>();

        // extract instructions
        Pattern p = Pattern.compile("(\\d+)");
        for (String s : input) {
            Dir dir = Dir.NONE;

            switch (s.charAt(0)) {
                case 'R':
                    dir = Dir.EAST;
                    break;
                case 'U':
                    dir = Dir.NORTH;
                    break;
                case 'L':
                    dir = Dir.WEST;
                    break;
                case 'D':
                    dir = Dir.SOUTH;
                    break;
                default:
                    System.out.println(s.charAt(0) + " invalid direction");
            }

            Matcher m = p.matcher(s.substring(2));
            int a = 0;
            int amountEnd = 0;
            if (m.find()) {
                a = Integer.parseInt(m.group());
                amountEnd = m.end();
            }

            instructions.add(new Instruction(dir, a));

            String col = s.substring(amountEnd + 5, s.length() - 1);
            a = Integer.parseInt(col.substring(0, 5), 16);
            switch (col.charAt(5)) {
                case '0':
                    dir = Dir.EAST;
                    break;
                case '1':
                    dir = Dir.SOUTH;
                    break;
                case '2':
                    dir = Dir.WEST;
                    break;
                case '3':
                    dir = Dir.NORTH;
                    break;
                default:
                    System.out.println(s.charAt(0) + " invalid direction");
            }
            trueInstructions.add(new Instruction(dir, a));

        }

        if (part == 1)
            return One(instructions, debug);
        else if (part == 2)
            return Two(trueInstructions, debug);
        else
            return (part + " is not a part");
    }

    public static String One(ArrayList<Instruction> input, boolean debug) {
        double answer = FindFilledTiles(input);

        return answer + "";
    }

    public static String Two(ArrayList<Instruction> input, boolean debug) {
        double answer = FindFilledTiles(input);

        return String.format("%14.0f", answer);
    }

    private static double FindFilledTiles(ArrayList<Instruction> input) {
        double answer = 1;

        ArrayList<Vertex> verticies = new ArrayList<Vertex>();
        int row = 0;
        int col = 0;

        // find all verticies based on how far the instructions say to move in a
        // specific direction
        // add the distances up in each move as well
        for (Instruction in : input) {
            answer += in.amount;
            verticies.add(new Vertex(row, col));
            row += in.d.rowOffset * in.amount;
            col += in.d.colOffset * in.amount;
        }

        // use the shoelace formula to find the area of the inner loop
        int j = verticies.size() - 1;
        for (int i = 0; i < verticies.size(); i++) {
            answer += (((double) verticies.get(i).col + (double) verticies.get(j).col)
                    * ((double) verticies.get(i).row - (double) verticies.get(j).row));

            j = i;
        }

        // divide the added total by 2 and round up
        return Math.ceil(Math.abs(answer / 2));
    }
}
