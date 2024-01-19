package dailyPuzzles;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Day05 {
    public static String Solve(ArrayList<ArrayList<String>> input, int part, boolean debug) {

        for (int i = 0; i < input.size(); i++) {
            if (i == 0) {
                // remove all the text from the input
                String update = input.get(i).get(0).substring(input.get(i).get(0).indexOf(":") + 2);
                input.get(i).set(0, update);
                continue;
            }

            input.get(i).remove(0);
        }

        Pattern p = Pattern.compile("\\d+");

        ArrayList<Long> seeds = new ArrayList<Long>();
        ArrayList<ArrayList<Long[]>> maps = new ArrayList<ArrayList<Long[]>>();

        // extract all the numbers, the seeds in an arraylist alone, all mappings in an
        // arraylist that holds arraylists for each map, that hold arrays of longs for
        // each set
        for (int i = 0; i < input.size(); i++) {
            for (int j = 0; j < input.get(i).size(); j++) {
                Matcher m = p.matcher(input.get(i).get(j));

                // special case for seeds
                if (i == 0) {
                    while (m.find())
                        seeds.add(Long.parseLong(m.group()));
                    break;
                }

                if (j == 0)
                    maps.add(new ArrayList<Long[]>());
                maps.get(i - 1).add(new Long[] { 0L, 0L, 0L });

                int index = 0;
                while (m.find())
                    maps.get(i - 1).get(j)[index++] = Long.parseLong(m.group());
            }
        }

        if (part == 1)
            return One(seeds, maps, debug);
        else if (part == 2)
            return Two(seeds, maps, debug);
        else
            return (part + " is not a part");
    }

    public static String One(ArrayList<Long> seeds, ArrayList<ArrayList<Long[]>> maps, boolean debug) {
        long answer = Long.MAX_VALUE;

        // iterate through each seed and find the lowest location number
        for (long seed : seeds) {
            long location = findLoc(seed, maps)[0];
            answer = Long.min(location, answer);
        }

        return answer + "";
    }

    public static String Two(ArrayList<Long> seeds, ArrayList<ArrayList<Long[]>> maps, boolean debug) {
        long answer = Long.MAX_VALUE;

        long seed = -1;
        long toCheck = -1;
        int index = 0;
        // iterate through each seed, connecting it to the next number in the list
        for (long s : seeds) {
            // record the seed
            if (index++ % 2 == 0) {
                seed = s;
                continue;
            }
            // record the range
            toCheck = s;

            // iterate through each seed in the range
            for (long i = seed; i < seed + toCheck; i++) {
                // System.out.print("seed: " + i + " ");

                // update the location if need be, and skip all seeds that will for sure be
                // larger than the current seed
                long[] location = findLoc(i, maps);
                answer = Long.min(location[0], answer);
                if (location[1] != 0)
                    i += location[1] - 1;
            }
        }

        return answer + "";
    }

    private static long[] findLoc(long seed, ArrayList<ArrayList<Long[]>> maps) {
        long location = seed;

        // int i = 0;
        long smallestDif = Long.MAX_VALUE;
        // iterate through each map, keeping track of what the smallest difference to a
        // new set will be for each level, this will be all the seeds that can be
        // skipped
        // because they are always going to be greater that whatever the return value of
        // this method will be
        for (ArrayList<Long[]> map : maps) {
            // String mapdata = "";
            // switch (i) {
            // case 0: mapdata = "soil: ";break;
            // case 1:mapdata = "fert: ";break;
            // case 2:mapdata = "water: ";break;
            // case 3:mapdata = "light: ";break;
            // case 4:mapdata = "temp: ";break;
            // case 5:mapdata = "hum: ";break;
            // case 6:mapdata = "loc: ";break; }
            // i++;

            // iterate through each set in the map
            for (Long[] set : map) {
                // if the last recorded value was within the range set by the set, check the
                // distance to the end of the set, and update the recorded value with what the
                // set says
                if (location >= set[1] && location < set[1] + set[2]) {
                    smallestDif = Long.min(smallestDif, ((set[2] + set[1]) - location));
                    location = set[0] + (location - set[1]);
                    break;
                }

                // if the set wasn't a match, but the beginning of the set it greater than the
                // recorded value, update smallest dif if necessary with distance to the
                // beginning of set
                if (set[1] > location)
                    smallestDif = Long.min(smallestDif, (set[1] - location));
            }
            // System.out.print(mapdata + "" + location + " ");
        }

        // System.out.println("smallest difference: " + smallestDif);

        return new long[] { location, smallestDif };

    }

}
