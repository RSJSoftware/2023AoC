package dailyPuzzles;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class Day07 {
    public static String Solve(ArrayList<String> input, int part, boolean debug) {
        ArrayList<String[]> parsedInput = new ArrayList<String[]>();
        for (String s : input)
            parsedInput.add(new String[] { s.substring(0, s.indexOf(" ")), s.substring(s.indexOf(" ") + 1) });

        if (part == 1)
            return One(parsedInput, debug);
        else if (part == 2)
            return Two(parsedInput, debug);
        else
            return (part + " is not a part");
    }

    public static String One(ArrayList<String[]> input, boolean debug) {
        long answer = 0L;

        // arraylist that holds arraylists of each hand type from strongest to weakest
        ArrayList<ArrayList<String[]>> sortedLists = new ArrayList<ArrayList<String[]>>();
        for (int i = 0; i < 7; i++) {
            sortedLists.add(new ArrayList<String[]>());
        }

        int rankings = 0;
        for (String[] s : input) {
            rankings++;
            // find all unique and duplicated characters in the hand
            SortedSet<Character> uniqueChars = new TreeSet<Character>();
            SortedSet<Character> dupeChars = new TreeSet<Character>();

            for (char c : s[0].toCharArray()) {
                if (!uniqueChars.contains(c))
                    uniqueChars.add(c);
                else
                    dupeChars.add(c);
            }

            // based on the number of unique and duplicate characters, a hand can be sorted
            switch (uniqueChars.size()) {
                // 1 unique character is a 5 of a kind
                case 1:
                    sortedLists.get(0).add(s);
                    break;
                // 2 unique characters with 1 dupe is a 4 of a kind, and with 2 dupes is a full
                // house
                case 2:
                    if (dupeChars.size() == 1)
                        sortedLists.get(1).add(s);
                    else if (dupeChars.size() == 2)
                        sortedLists.get(2).add(s);
                    else
                        System.out.println(dupeChars.size() + " number of dupe characters in: " + s[0] + " unique: 2");
                    break;
                // 3 unique characters with 1 dupe is a 3 of a kind, and with 2 dupes is a 2
                // pair
                case 3:
                    if (dupeChars.size() == 1)
                        sortedLists.get(3).add(s);
                    else if (dupeChars.size() == 2)
                        sortedLists.get(4).add(s);
                    else
                        System.out.println(dupeChars.size() + " number of dupe characters in: " + s[0] + " unique: 3");
                    break;
                // 4 unique characters is a 1 pair
                case 4:
                    sortedLists.get(5).add(s);
                    break;
                // 5 unique characters is a high card
                case 5:
                    sortedLists.get(6).add(s);
                    break;
                default:
                    System.out.println(uniqueChars.size() + " number of unique characters in: " + s[0]);
            }
        }

        // sort the hands by the first unique card
        for (int i = 0; i < sortedLists.size(); i++) {

            boolean swap = true;
            while (swap) {
                swap = false;
                for (int j = 0; j < sortedLists.get(i).size() - 1; j++) {

                    if (!compareHand(sortedLists.get(i).get(j)[0], sortedLists.get(i).get(j + 1)[0])) {
                        swap = true;
                        String[] temp = sortedLists.get(i).get(j);
                        sortedLists.get(i).set(j, sortedLists.get(i).get(j + 1));
                        sortedLists.get(i).set(j + 1, temp);

                    }

                }

            }
        }

        // add all bids multiplied by the ranking to the answer
        for (ArrayList<String[]> arrStrings : sortedLists) {
            for (String[] s : arrStrings) {
                System.out.println(s[0] + " " + s[1] + " " + rankings);
                answer += Long.parseLong(s[1]) * rankings--;
            }
        }

        return answer + "";
    }

    public static String Two(ArrayList<String[]> input, boolean debug) {
        long answer = 0L;

        // arraylist that holds arraylists of each hand type from strongest to weakest
        ArrayList<ArrayList<String[]>> sortedLists = new ArrayList<ArrayList<String[]>>();
        for (int i = 0; i < 7; i++) {
            sortedLists.add(new ArrayList<String[]>());
        }

        int rankings = 0;
        for (String[] s : input) {
            rankings++;
            // find all unique and duplicated characters in the hand
            SortedSet<Character> uniqueChars = new TreeSet<Character>();
            SortedSet<Character> dupeChars = new TreeSet<Character>();
            for (char c : s[0].toCharArray()) {
                if (c == 'J')
                    continue;
                if (!uniqueChars.contains(c))
                    uniqueChars.add(c);
                else
                    dupeChars.add(c);
            }

            // based on the number of unique and duplicate characters, a hand can be sorted
            switch (uniqueChars.size()) {
                // 1 unique character (0 if all wilds) is a 5 of a kind
                case 0:
                case 1:
                    sortedLists.get(0).add(s);
                    break;
                // 2 unique characters with 1 dupe (0 dupes with wilds) is a 4 of a kind, and
                // with 2 dupes is a full house
                case 2:
                    if (dupeChars.size() == 1 | dupeChars.size() == 0) {
                        sortedLists.get(1).add(s);
                    } else if (dupeChars.size() == 2)
                        sortedLists.get(2).add(s);
                    else
                        System.out.println(dupeChars.size() + " number of dupe characters in: " + s[0] + " unique: 2");
                    break;
                // 3 unique characters with 1 dupe (0 dupes with wilds) is a 3 of a kind, and
                // with 2 dupes is a 2 pair
                case 3:
                    if (dupeChars.size() == 1 || dupeChars.size() == 0)
                        sortedLists.get(3).add(s);
                    else if (dupeChars.size() == 2)
                        sortedLists.get(4).add(s);
                    else
                        System.out.println(dupeChars.size() + " number of dupe characters in: " + s[0] + " unique: 3");
                    break;
                // 4 unique characters is a 1 pair
                case 4:
                    sortedLists.get(5).add(s);
                    break;
                // 5 unique characters is a high card
                case 5:
                    sortedLists.get(6).add(s);
                    break;
                default:
                    System.out.println(uniqueChars.size() + " number of unique characters in: " + s[0]);
            }
        }

        // sort the hands by the first unique card
        for (int i = 0; i < sortedLists.size(); i++) {

            boolean swap = true;
            while (swap) {
                swap = false;
                for (int j = 0; j < sortedLists.get(i).size() - 1; j++) {

                    if (!compareHand2(sortedLists.get(i).get(j)[0], sortedLists.get(i).get(j + 1)[0])) {
                        swap = true;
                        String[] temp = sortedLists.get(i).get(j);
                        sortedLists.get(i).set(j, sortedLists.get(i).get(j + 1));
                        sortedLists.get(i).set(j + 1, temp);

                    }

                }

            }
        }

        // add all bids multiplied by the ranking to the answer
        for (ArrayList<String[]> arrStrings : sortedLists) {
            for (String[] s : arrStrings) {
                // System.out.println(s[0] + " " + s[1] + " " + rankings);
                answer += Long.parseLong(s[1]) * rankings--;
            }
        }

        return answer + "";
    }

    private static boolean compareHand(String hand1, String hand2) {
        for (int i = 0; i < hand1.length(); i++) {
            if (hand1.charAt(i) != hand2.charAt(i))
                return getVal(hand1.charAt(i)) > getVal(hand2.charAt(i));
        }
        return true;
    }

    private static boolean compareHand2(String hand1, String hand2) {
        for (int i = 0; i < hand1.length(); i++) {
            if (hand1.charAt(i) != hand2.charAt(i))
                return getVal2(hand1.charAt(i)) > getVal2(hand2.charAt(i));
        }
        return true;
    }

    private static int getVal(char c) {
        switch (c) {
            case '2':
                return 1;
            case '3':
                return 2;
            case '4':
                return 3;
            case '5':
                return 4;
            case '6':
                return 5;
            case '7':
                return 6;
            case '8':
                return 7;
            case '9':
                return 8;
            case 'T':
                return 9;
            case 'J':
                return 10;
            case 'Q':
                return 11;
            case 'K':
                return 12;
            case 'A':
                return 13;
            default:
                System.out.println("error: char " + c);
                return -1;
        }
    }

    private static int getVal2(char c) {
        switch (c) {
            case '2':
                return 1;
            case '3':
                return 2;
            case '4':
                return 3;
            case '5':
                return 4;
            case '6':
                return 5;
            case '7':
                return 6;
            case '8':
                return 7;
            case '9':
                return 8;
            case 'T':
                return 9;
            case 'J':
                return 0;
            case 'Q':
                return 11;
            case 'K':
                return 12;
            case 'A':
                return 13;
            default:
                System.out.println("error: char " + c);
                return -1;
        }
    }
}
