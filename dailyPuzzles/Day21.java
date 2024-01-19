package dailyPuzzles;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import utils.Coord;
import utils.Dir;
import utils.Grid;

public class Day21 {
    public record Loc(int row, int col) {
    }

    public static String Solve(Grid input, int part, boolean debug) {
        Coord<String> sCoord = input.findInstance("S");
        Loc start = new Loc((int) sCoord.getRow(), (int) sCoord.getCol());

        if (part == 1)
            return One(input, start, debug);
        else if (part == 2)
            return Two(input, start, debug);
        else
            return (part + " is not a part");
    }

    public static String One(Grid input, Loc start, boolean debug) {
        double answer = 0;

        answer = visitedInSteps(input, start, 64);

        return answer + "";
    }

    public static String Two(Grid input, Loc start, boolean debug) {
        double answer = 0;

        int stepsLeft = 26501365;
        // n is the amount of squares in the empty diamonds in the input file
        double n = (((stepsLeft - (input.getLength() / 2)) / input.getLength()));

        // get all open even and odd tiles in the input
        double evens = visitedInSteps(input, start, 250);
        double odds = visitedInSteps(input, start, 251);

        // get outer corners (beyond the diamond) for evens and odds
        double evenCorner = evens - visitedInSteps(input, start, 64);
        double oddCorner = odds - visitedInSteps(input, start, 65);

        // geometric solution thanks to villuna:
        // (https://github.com/villuna/aoc23/wiki/A-Geometric-solution-to-advent-of-code-2023,-day-21)
        answer = ((n + 1) * (n + 1)) * odds + (n * n) * evens - (n + 1) * oddCorner + n * evenCorner;

        return String.format("%14.0f", answer);
    }

    private static double visitedInSteps(Grid input, Loc start, int stepsLeft) {
        double answer = 0;

        Stack<State> steps = new Stack<>();
        HashMap<Loc, Integer> checked = new HashMap<>();
        HashSet<Loc> destinations = new HashSet<>();

        steps.add(new State(start, 0));
        // step through all locations one at a time until there are no steps left
        while (!steps.isEmpty()) {
            State currentState = steps.pop();

            // if location is further than steps left to take or the caches location has
            // less or equal steps taken, continue
            if (currentState.count > stepsLeft
                    || (checked.containsKey(currentState.loc) && checked.get(currentState.loc) <= currentState.count))
                continue;

            // cache checked location
            checked.put(currentState.loc, currentState.count);
            // if the location is even or odd (whichever is same as steps left), add it to
            // destination
            if (currentState.count % 2 == stepsLeft % 2)
                destinations.add(currentState.loc);

            // add next steps
            currentState.AddNext(steps, input, stepsLeft);
        }

        // answer is all the destinations recorded
        answer = destinations.size();

        return answer;

    }

    static class State {
        Loc loc;
        int count;

        public State(Loc l, int c) {
            loc = l;
            count = c;
        }

        public void AddNext(Collection<State> steps, Grid input, int stepsLeft) {
            // if there are more steps taken than left, return
            if (count + 1 > stepsLeft)
                return;
            int row = loc.row;
            int col = loc.col;

            // add each direction to the steps
            AddNextInbounds(steps, input, new Loc(row + Dir.EAST.rowOffset, col + Dir.EAST.colOffset));
            AddNextInbounds(steps, input, new Loc(row + Dir.NORTH.rowOffset, col + Dir.NORTH.colOffset));
            AddNextInbounds(steps, input, new Loc(row + Dir.SOUTH.rowOffset, col + Dir.SOUTH.colOffset));
            AddNextInbounds(steps, input, new Loc(row + Dir.WEST.rowOffset, col + Dir.WEST.colOffset));

        }

        public void AddNextInbounds(Collection<State> steps, Grid input, Loc l) {
            // if steps are out of bounds, return
            if (l.row < 0 || l.row >= input.getHeight() || l.col < 0 || l.col >= input.getLength()
                    || input.getData(l.row, l.col) == '#')
                return;

            steps.add(new State(l, count + 1));
        }
    }
}