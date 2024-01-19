package dailyPuzzles;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Day06 {
    public static String Solve(ArrayList<String> input, int part, boolean debug) {
        if (part == 1)
            return One(input, debug);
        else if (part == 2)
            return Two(input, debug);
        else
            return (part + " is not a part");
    }

    public static String One(ArrayList<String> input, boolean debug) {
        // isolate the values for time and distance
        ArrayList<Long> time = new ArrayList<Long>();
        ArrayList<Long> dis = new ArrayList<Long>();

        Pattern p = Pattern.compile("\\d+");

        Matcher m = p.matcher(input.get(0));
        while (m.find())
            time.add(Long.parseLong(m.group()));

        m = p.matcher(input.get(1));
        while (m.find())
            dis.add(Long.parseLong(m.group()));

        long answer = 1;

        // iterate over each value in time and distance (they will be the same size)
        for (int i = 0; i < time.size(); i++) {
            long betterDis = getBetterDistance(dis.get(i), time.get(i));

            answer *= betterDis;
            // System.out.println("Better distance: " + betterDis);
        }

        return answer + "";
    }

    public static String Two(ArrayList<String> input, boolean debug) {
        String timeString = "";
        String disString = "";

        // concat all digits in the string into one long string and parse that into a
        // long value
        Pattern p = Pattern.compile("\\d+");

        Matcher m = p.matcher(input.get(0));
        while (m.find())
            timeString += m.group();

        m = p.matcher(input.get(1));
        while (m.find())
            disString += m.group();

        long time = Long.parseLong(timeString);
        long dis = Long.parseLong(disString);

        long answer = getBetterDistance(dis, time);

        return answer + "";
    }

    private static long getBetterDistance(long dis, long time) {
        long betterDis = 0L;

        // iterate each value from 1 to when time left and time elapsed are equal or
        // time elapsed is greater than time left
        // 0 time elapsed will never be better than the current record, and when time
        // elapsed is greater than or equal to time left, all potential times have been
        // seen
        for (int timeElapsed = 1; timeElapsed <= time - timeElapsed; timeElapsed++) {
            // System.out.println("speed: " + j + " time left: " + (time.get(i) - j) + "
            // distance: " + (j * (time.get(i) - j)));

            // if distance is better than before and time elapsed and time left are the
            // same, add 1 because this value combo
            // will only be seen this once, otherwise add 2 because the value combo reversed
            // will be seen again
            if ((timeElapsed * (time - timeElapsed)) > dis)
                betterDis += ((time - timeElapsed) == timeElapsed) ? 1 : 2;
        }

        return betterDis;

    }

}
