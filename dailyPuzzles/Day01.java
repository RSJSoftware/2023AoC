package dailyPuzzles;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Day01 {
    public static String Solve(ArrayList<String> input, int part, boolean debug) {
        if (part == 1)
            return One(input, debug);
        else if (part == 2)
            return Two(input, debug);
        else
            return (part + " is not a part");
    }

    public static String One(ArrayList<String> input, boolean debug) {
        long answer = 0;

        for (String s : input) {
            // remove all non-digits in each string
            String number = s.replaceAll("\\D+", "");
            // extract the first and last digit in the remaining string
            String finalNumber = number.charAt(0) + "" + number.charAt(number.length() - 1);
            // parse the string into a number and add it to the total
            long parsedNum = Long.parseLong(finalNumber);
            answer += parsedNum;
        }

        return answer + "";
    }

    public static String Two(ArrayList<String> input, boolean debug) {
        long answer = 0;
        // regex to find digits and spelled out digits
        Pattern p = Pattern.compile("one|two|three|four|five|six|seven|eight|nine|[0-9]");
        for (String s : input) {
            String firstDigitString = "";
            String lastDigitString = "";

            // iterate over each character in the array to see if any regex matches occur
            // (this helps find overlapping matches)
            for (int i = 0; i < s.length(); i++) {
                Matcher m = p.matcher(s.substring(i));
                // if no matches are found, break
                if (!m.find())
                    break;

                // if this is the full string, record the first match
                if (i == 0)
                    firstDigitString = m.group();

                // update the record of the last match every time
                lastDigitString = m.group();
            }

            // add the matches together finding the digits if they were spelled out
            String finalNumber = FindDigit(firstDigitString) + "" + FindDigit(lastDigitString);

            // parse the number and add it to the total
            long parsedNum = Long.parseLong(finalNumber);
            answer += parsedNum;
        }

        return answer + "";

    }

    public static int FindDigit(String number) {
        // if the string length is 1, it will be a digit
        if (number.length() == 1)
            return Integer.parseInt(number);

        switch (number) {
            case "one":
                return 1;
            case "two":
                return 2;
            case "three":
                return 3;
            case "four":
                return 4;
            case "five":
                return 5;
            case "six":
                return 6;
            case "seven":
                return 7;
            case "eight":
                return 8;
            case "nine":
                return 9;
        }

        System.out.println(number + " is not a digit");
        return -1;
    }

    public static void Debug(String out, boolean debug) {
        if (debug)
            System.out.println(out);
    }
}
