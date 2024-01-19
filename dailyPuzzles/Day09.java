package dailyPuzzles;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Day09 {
    public static String Solve(ArrayList<String> input, int part, boolean debug) {
        // extract all numbers from input
        ArrayList<ArrayList<Long>> parsedInput = new ArrayList<ArrayList<Long>>();
        Pattern p = Pattern.compile("(-?\\d+)");

        for (int i = 0; i < input.size(); i++) {
            parsedInput.add(new ArrayList<Long>());

            Matcher m = p.matcher(input.get(i));
            while (m.find()) {
                parsedInput.get(i).add(Long.parseLong(m.group()));
            }
        }

        if (part == 1)
            return One(parsedInput, debug);
        else if (part == 2)
            return Two(parsedInput, debug);
        else
            return (part + " is not a part");
    }

    public static String One(ArrayList<ArrayList<Long>> input, boolean debug) {
        long answer = 0L;

        // iterate through each sequence of numbers
        for (ArrayList<Long> l : input) {
            Long num = getNextNum(l);
            answer += num;
        }

        return answer + "";
    }

    public static String Two(ArrayList<ArrayList<Long>> input, boolean debug) {
        long answer = 0L;

        // iterate through each sequence of numbers
        for (ArrayList<Long> l : input) {
            Long num = getFirstNumEntry(l);
            answer += num;
        }

        return answer + "";
    }

    private static Long getNextNum(ArrayList<Long> numbers) {
        boolean allZero = true;

        // check to see if all numbers are zero, if so, return 0
        for (Long l : numbers)
            if (l != 0) {
                allZero = false;
                break;
            }

        if (allZero)
            return 0L;

        // return the last number of the given sequence added to the next number of the
        // sequence of differences
        return numbers.get(numbers.size() - 1) + getNextNum(findDifferences(numbers));
    }

    private static Long getFirstNumEntry(ArrayList<Long> numbers) {
        // return the first number minus the next number in the sequence of differences
        // reversed
        return numbers.get(0) - getNextNum(findDifferencesBackward(numbers));
    }

    private static ArrayList<Long> findDifferences(ArrayList<Long> numbers) {
        ArrayList<Long> differences = new ArrayList<Long>();

        // find the difference between each number
        for (int i = 0; i < numbers.size() - 1; i++)
            differences.add(numbers.get(i + 1) - numbers.get(i));

        return differences;
    }

    private static ArrayList<Long> findDifferencesBackward(ArrayList<Long> numbers) {
        ArrayList<Long> differences = new ArrayList<Long>();

        // find the difference between each number backwards
        for (int i = numbers.size() - 2; i >= 0; i--)
            differences.add(numbers.get(i + 1) - numbers.get(i));

        return differences;
    }

}
