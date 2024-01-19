package dailyPuzzles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Day02 {
    public static String Solve(ArrayList<String> input, int part, boolean debug) {
        if (part == 1)
            return One(input, debug);
        else if (part == 2)
            return Two(input, debug);
        else
            return part + " is not a part";
    }

    public static String One(ArrayList<String> input, boolean debug) {
        long output = 0;
        for (int i = 0; i < input.size(); i++) {
            // find where the values for the game start
            if (IsPossible(input.get(i), 12, 13, 14)) {
                System.out.println("Game " + (i + 1) + " is possible!");
                output += (i + 1);
            }
        }

        return output + "";
    }

    private static boolean IsPossible(String game, int red, int green, int blue) {
        // find where the game begins (after the colon)
        String values = game.substring(game.indexOf(":") + 2);

        Pattern g = Pattern.compile("green");
        Pattern r = Pattern.compile("red");
        Pattern b = Pattern.compile("blue");

        // split the string up into arrays based on the semicolon
        ArrayList<String> roundList = new ArrayList<String>(Arrays.asList(values.split("; ")));
        for (String round : roundList) {
            // split further by color based on the comma
            ArrayList<String> colorList = new ArrayList<String>(Arrays.asList(round.split(", ")));
            for (String color : colorList) {
                // find which color the string is listing and how much of it was pulled
                Matcher colorMatcher = g.matcher(color);
                if (colorMatcher.find()) {
                    if (green < FindValue(color))
                        return false;
                    continue;
                }

                colorMatcher = r.matcher(color);
                if (colorMatcher.find()) {
                    if (red < FindValue(color))
                        return false;
                    continue;
                }

                colorMatcher = b.matcher(color);
                if (colorMatcher.find()) {
                    if (blue < FindValue(color))
                        return false;
                    continue;
                }
            }
        }
        return true;
    }

    private static long FindValue(String color) {
        return Long.parseLong(color.substring(0, color.indexOf(" ")));
    }

    public static String Two(ArrayList<String> input, boolean debug) {
        long output = 0;
        for (String s : input) {
            output += GetPower(s);
            System.out.println(output);
        }

        return output + "";
    }

    private static long GetPower(String game) {
        // find where the game begins (after the colon)
        String values = game.substring(game.indexOf(":") + 2);

        Pattern g = Pattern.compile("green");
        Pattern r = Pattern.compile("red");
        Pattern b = Pattern.compile("blue");

        long maxGreen = Integer.MIN_VALUE;
        long maxRed = Integer.MIN_VALUE;
        long maxBlue = Integer.MIN_VALUE;

        // split the string up into arrays based on the semicolon
        ArrayList<String> roundList = new ArrayList<String>(Arrays.asList(values.split("; ")));
        for (String round : roundList) {
            // split further by color based on the comma
            ArrayList<String> colorList = new ArrayList<String>(Arrays.asList(round.split(", ")));
            for (String color : colorList) {
                // find which color the string is listing and how much of it was pulled
                Matcher colorMatcher = g.matcher(color);
                if (colorMatcher.find()) {
                    long amount = FindValue(color);
                    if (amount > maxGreen)
                        maxGreen = amount;
                    continue;
                }

                colorMatcher = r.matcher(color);
                if (colorMatcher.find()) {
                    long amount = FindValue(color);
                    if (amount > maxRed)
                        maxRed = amount;
                    continue;
                }

                colorMatcher = b.matcher(color);
                if (colorMatcher.find()) {
                    long amount = FindValue(color);
                    if (amount > maxBlue)
                        maxBlue = amount;
                    continue;
                }
            }
        }

        System.out.println(maxGreen + " " + maxRed + " " + maxBlue);

        return maxGreen * maxRed * maxBlue;

    }
}
