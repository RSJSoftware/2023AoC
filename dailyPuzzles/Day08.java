package dailyPuzzles;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.regex.Pattern;

import utils.AoCMath;

import java.util.regex.Matcher;
import java.util.Hashtable;

public class Day08 {
    public static String Solve(ArrayList<String> input, int part, boolean debug) {
        String directions = input.get(0);

        Pattern p = Pattern.compile("([[a-zA-Z]|\\d]{3}) = \\(([[a-zA-Z]|\\d]{3}), ([[a-zA-Z]|\\d]{3})\\)");

        Hashtable<String, String[]> map = new Hashtable<String, String[]>();
        input.remove(0);
        input.remove(0);
        for (String s : input) {
            Matcher m = p.matcher(s);
            if (m.find()) {
                map.put(m.group(1), new String[] { m.group(2), m.group(3) });
            }
        }

        if (part == 1)
            return One(directions, map, debug);
        else if (part == 2)
            return Two(directions, map, debug);
        else
            return (part + " is not a part");
    }

    public static String One(String directions, Hashtable<String, String[]> map, boolean debug) {
        long answer = 0L;
        String currentPos = "AAA";
        int index = 0;
        while (!currentPos.equals("ZZZ")) {
            if (index >= directions.length())
                index = 0;

            currentPos = (directions.charAt(index) == 'L') ? map.get(currentPos)[0] : map.get(currentPos)[1];
            index++;
            answer++;
        }

        return answer + "";
    }

    public static String Two(String directions, Hashtable<String, String[]> map, boolean debug) {
        long answer = 0L;

        ArrayList<String> currentPos = new ArrayList<String>();
        ArrayList<Long> foundPos = new ArrayList<Long>();

        Pattern startP = Pattern.compile("([[a-zA-Z]|\\d]{2}A)");
        Pattern endP = Pattern.compile("([[a-zA-Z]|\\d]{2}Z)");

        Enumeration<String> keys = map.keys();
        while (keys.hasMoreElements()) {
            Matcher m = startP.matcher(keys.nextElement());
            if (m.find()) {
                currentPos.add(m.group());
                foundPos.add(0L);
                System.out.println("Added " + m.group());
            }
        }

        System.out.println(currentPos);

        boolean allEnd = false;
        int index = 0;
        while (!allEnd) {
            answer++;
            if (index >= directions.length())
                index = 0;

            for (int i = 0; i < currentPos.size(); i++) {
                currentPos.set(i, (directions.charAt(index) == 'L') ? map.get(currentPos.get(i))[0]
                        : map.get(currentPos.get(i))[1]);
                Matcher m = endP.matcher(currentPos.get(i));
                if (m.find()) {
                    if (foundPos.get(i) == 0)
                        foundPos.set(i, answer - foundPos.get(i));
                }

            }

            for (Long l : foundPos) {
                allEnd = true;
                if (l == 0) {
                    allEnd = false;
                    break;
                }
            }

            index++;
        }

        System.out.println(foundPos);

        return AoCMath.lcm(foundPos) + "";
    }
}
