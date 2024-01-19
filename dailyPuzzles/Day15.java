package dailyPuzzles;

import java.util.ArrayList;
import java.util.Arrays;

public class Day15 {
    private record Lens(String label, int val) {

    }

    public static String Solve(ArrayList<String> input, int part, boolean debug) {
        ArrayList<String> values = new ArrayList<String>(Arrays.asList(input.get(0).split(",")));

        if (part == 1)
            return One(values, debug);
        else if (part == 2)
            return Two(values, debug);
        else
            return (part + " is not a part");
    }

    public static String One(ArrayList<String> input, boolean debug) {
        long answer = 0;

        // iterate through each character in each string in the array
        for (String s : input) {
            char[] chars = s.toCharArray();
            long hashVal = 0L;
            // find the hash value of each character and add to the total
            for (char c : chars)
                hashVal = HashCode(c, hashVal);

            answer += hashVal;
        }

        return answer + "";
    }

    public static String Two(ArrayList<String> input, boolean debug) {
        long answer = 0;

        // create an arraylist of boxes -- arraylists of lenses
        ArrayList<ArrayList<Lens>> boxes = new ArrayList<ArrayList<Lens>>();

        // add 256 boxes to the array list
        for (int i = 0; i < 256; i++)
            boxes.add(new ArrayList<Lens>());

        // iterate through each character in each string in the array
        for (String s : input) {
            char[] chars = s.toCharArray();
            int hashVal = 0;
            String label = "";
            for (char c : chars) {
                // check for special characters: '=' should be ignored
                if (c == '=')
                    continue;
                // digits should update the lens value in the corresponding box and lens if they
                // exist, or added if they don't
                else if (Character.isDigit(c)) {
                    int index = findHash(boxes, hashVal, label);
                    if (index == -1)
                        boxes.get(hashVal).add(new Lens(label, Character.getNumericValue(c)));
                    else
                        boxes.get(hashVal).set(index, new Lens(label, Character.getNumericValue(c)));
                    // '-' should remove the corresponding lens from the box if it exists
                } else if (c == '-') {
                    int index = findHash(boxes, hashVal, label);
                    if (index != -1)
                        boxes.get(hashVal).remove(index);
                } else {
                    // if no special character,update the label string and update the hash value
                    label += c;
                    hashVal = (int) HashCode(c, hashVal);
                }
            }
        }

        // add all lenses in each box and position to the result
        for (int box = 0; box < boxes.size(); box++) {
            for (int slot = 0; slot < boxes.get(box).size(); slot++) {
                answer += ((box + 1) * (slot + 1) * boxes.get(box).get(slot).val);
            }
        }

        return answer + "";
    }

    private static long HashCode(char c, long initVal) {
        // use the ascii value to find the hash code as defined in the puzzle
        long updateVal = initVal;
        int asciiVal = (int) c;
        // System.out.println("init val before: " + c + " " + updateVal);

        updateVal += asciiVal;
        // System.out.println("val after adding: " + c + " as: " + asciiVal + " " +
        // updateVal);
        updateVal *= 17;
        // System.out.println("val after multi: " + c + " " + updateVal);
        updateVal %= 256;

        // System.out.println("final val after adding: " + c + " " + updateVal + "\n");

        return updateVal;
    }

    private static int findHash(ArrayList<ArrayList<Lens>> boxes, int hashVal, String label) {
        // find an existing label in the boxes, returning -1 if it does not exist
        for (int i = 0; i < boxes.get(hashVal).size(); i++) {
            if (boxes.get(hashVal).get(i).label.equals(label)) {
                return i;
            }
        }

        return -1;

    }

}
