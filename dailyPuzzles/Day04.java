package dailyPuzzles;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Day04 {
    public static String Solve(ArrayList<String> input, int part, boolean debug) {
        for (int i = 0; i < input.size(); i++) {
            String update = input.get(i).substring(input.get(i).indexOf(":") + 2);
            input.set(i, update);
        }

        if (part == 1)
            return One(input, debug);
        else if (part == 2)
            return Two(input, debug);
        else
            return (part + " is not a part");
    }

    public static String One(ArrayList<String> input, boolean debug) {
        long answer = 0;

        Pattern p = Pattern.compile("\\d+");
        for (String s : input) {
            long score = 0;
            String win = s.substring(0, s.indexOf("|"));
            String draw = s.substring(s.indexOf("|"));

            ArrayList<Integer> winNums = new ArrayList<Integer>();
            ArrayList<Integer> drawNums = new ArrayList<Integer>();

            Matcher m = p.matcher(win);
            while (m.find())
                winNums.add(Integer.parseInt(m.group()));

            m = p.matcher(draw);
            while (m.find())
                drawNums.add(Integer.parseInt(m.group()));

            for (Integer w : winNums) {
                for (Integer d : drawNums) {
                    if (w == d) {
                        if (score == 0)
                            score = 1;
                        else
                            score *= 2;
                    }
                }
            }

            answer += score;
        }

        return answer + "";
    }

    public static String Two(ArrayList<String> input, boolean debug) {
        long answer = 0;

        ArrayList<Long> cardAmount = new ArrayList<Long>();

        for (int i = 0; i < input.size(); i++)
            cardAmount.add(1L);

        System.out.println(cardAmount);
        Pattern p = Pattern.compile("\\d+");
        for (int i = 0; i < input.size(); i++) {
            String s = input.get(i);
            long score = 0;
            String win = s.substring(0, s.indexOf("|"));
            String draw = s.substring(s.indexOf("|"));

            ArrayList<Integer> winNums = new ArrayList<Integer>();
            ArrayList<Integer> drawNums = new ArrayList<Integer>();

            Matcher m = p.matcher(win);
            while (m.find())
                winNums.add(Integer.parseInt(m.group()));

            m = p.matcher(draw);
            while (m.find())
                drawNums.add(Integer.parseInt(m.group()));

            for (Integer w : winNums)
                for (Integer d : drawNums)
                    if (w == d)
                        score++;

            for (int j = i + 1; j < i + score + 1; j++) {
                if (j >= cardAmount.size())
                    break;
                cardAmount.set(j, cardAmount.get(j) + cardAmount.get(i));
            }

        }

        for (Long l : cardAmount)
            answer += l;

        return answer + "";

    }

}
