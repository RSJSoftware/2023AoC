package dailyPuzzles;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.Map;

public class Day12 {
    private record Input(String cond, ArrayList<Integer> group) {

    }

    private static final Map<Input, Long> memoizationMap = new HashMap<>();

    public static String Solve(ArrayList<String> input, int part, boolean debug) {
        ArrayList<String> springs = new ArrayList<String>();
        ArrayList<ArrayList<Integer>> amount = new ArrayList<ArrayList<Integer>>();

        Pattern p = Pattern.compile("(\\d+)");

        for (String s : input) {
            Matcher m = p.matcher(s);

            springs.add(s.substring(0, s.indexOf(" ")));
            amount.add(new ArrayList<Integer>());
            while (m.find())
                amount.get(amount.size() - 1).add(Integer.parseInt(m.group()));
        }

        if (part == 1)
            return One(springs, amount, debug);
        else if (part == 2)
            return Two(springs, amount, debug);
        else
            return (part + " is not a part");
    }

    public static String One(ArrayList<String> springs, ArrayList<ArrayList<Integer>> amount, boolean debug) {
        long answer = 0;

        for (int i = 0; i < springs.size(); i++) {
            answer += findValidations(springs.get(i), amount.get(i));
        }
        return answer + "";
    }

    public static String Two(ArrayList<String> springs, ArrayList<ArrayList<Integer>> amount, boolean debug) {
        long answer = 0;

        for (int i = 0; i < springs.size(); i++) {
            // expand the strings
            String update = springs.get(i) + "?" + springs.get(i) + "?" + springs.get(i) + "?" + springs.get(i) + "?"
                    + springs.get(i);
            springs.set(i, update);

            // expand the amounts
            int size = amount.get(i).size();
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < size; k++)
                    amount.get(i).add(amount.get(i).get(k));
            }
        }

        for (int i = 0; i < springs.size(); i++) {
            answer += findValidations(springs.get(i), amount.get(i));
        }

        return answer + "";
    }

    private static long findValidations(String input, ArrayList<Integer> amount) {
        // record this specific input
        Input in = new Input(input, amount);
        // if it exists in the memoization cache, return the value it holds
        if (memoizationMap.containsKey(in))
            return memoizationMap.get(in);

        // if there is no string, return 1 if there are no amounts left either, 0 if
        // there needed to be another amount
        if (input.isBlank())
            return amount.isEmpty() ? 1 : 0;

        long validations = 0;

        // check the front of the string
        switch (input.charAt(0)) {
            // ignore any '.', remove it and recursively check the remainder of the string
            case '.':
                validations = findValidations(input.substring(1), amount);
                break;
            // change the '?' to a '.' and a '#' and check both strings again
            case '?':
                validations = findValidations("." + input.substring(1), amount)
                        + findValidations("#" + input.substring(1), amount);
                break;
            case '#':
                // if there are no amounts left, set validations to 0 because there shouldn't be
                // any more '#'
                if (amount.size() == 0) {
                    validations = 0;
                    break;
                }

                // find how long this sequence of '#' needs to be
                int amountToFind = amount.get(0);
                // if the remainder of the string is less than the required sequence length, or
                // there is insufficent distance to the next '.' set validations to 0
                if (amountToFind > input.length()
                        || !input.chars().limit(amountToFind).allMatch(c -> c == '#' || c == '?')) {
                    validations = 0;
                    break;
                }

                // make a copy of the amount arraylist without the first value that was just
                // found
                ArrayList<Integer> newAmount = new ArrayList<Integer>(amount.subList(1, amount.size()));
                // if the remainder of the string is the same as the amount that needs to be
                // found, return 1 or 0 based on if any more amounts need to be found
                if (amountToFind == input.length())
                    validations = newAmount.isEmpty() ? 1 : 0;
                // if there is a '.' or '?' just after the amount to be found, continue parsing
                // from
                // just after that ('?' needs to be a '.' for a valid sequence)
                else if (input.charAt(amountToFind) == '.' || input.charAt(amountToFind) == '?')
                    validations = findValidations(input.substring(amountToFind + 1), newAmount);
                // if there is a '#' next, set validations to 0 as the sequence will be too long
                else
                    validations = 0;
                break;
            default:
                System.out.println("Rogue character: " + input);
        }

        // cache the found value with the input and return
        memoizationMap.put(in, validations);
        return validations;
    }

}
