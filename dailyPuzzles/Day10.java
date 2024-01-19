package dailyPuzzles;

import utils.Grid;

import java.util.ArrayList;
import java.util.Stack;

import utils.Coord;
import utils.Dir;

public class Day10 {
    public static String Solve(Grid input, int part, boolean debug) {
        Coord<String> start = input.findInstance("S");
        System.out.println(start);
        if (part == 1)
            return One(input, start, debug);
        else if (part == 2)
            return Two(input, start, debug);
        else
            return (part + " is not a part");
    }

    public static String One(Grid input, Coord<String> start, boolean debug) {
        long answer = 0;

        Dir prevDir = Dir.NONE;
        Coord<Character> currentTile = new Coord<Character>(start.getRow(), start.getCol(), 1, 1,
                start.getData().charAt(0));
        do {
            answer++;
            Dir toGo = getNextDir(input, currentTile, prevDir);
            long row = currentTile.getRow();
            long col = currentTile.getCol();
            switch (toGo) {
                case NORTH:
                    currentTile = new Coord<Character>(--row, col, 1, 1, input.getData((int) (row), (int) col));
                    prevDir = Dir.SOUTH;
                    break;
                case SOUTH:
                    currentTile = new Coord<Character>(++row, col, 1, 1, input.getData((int) (row), (int) col));
                    prevDir = Dir.NORTH;
                    break;
                case WEST:
                    currentTile = new Coord<Character>(row, --col, 1, 1, input.getData((int) (row), (int) col));
                    prevDir = Dir.EAST;
                    break;
                case EAST:
                    currentTile = new Coord<Character>(row, ++col, 1, 1, input.getData((int) (row), (int) col));
                    prevDir = Dir.WEST;
                    break;
                default:
                    System.out.println("????: " + toGo);
            }
        } while ((!currentTile.getData().equals('S')));
        answer /= 2;
        return answer + "";
    }

    public static String Two(Grid input, Coord<String> start, boolean debug) {
        long answer = 0;

        ArrayList<Coord<Character>> loop = getLoop(input, start);

        int length = input.getLength();
        int height = input.getHeight();

        ArrayList<ArrayList<Boolean>> expLoop = new ArrayList<ArrayList<Boolean>>();
        ArrayList<ArrayList<Boolean>> onlyLoop = new ArrayList<ArrayList<Boolean>>();

        // add 3 arraylists per height
        for (int i = 0; i < height; i++) {
            expLoop.add(new ArrayList<Boolean>());
            expLoop.add(new ArrayList<Boolean>());
            expLoop.add(new ArrayList<Boolean>());

            onlyLoop.add(new ArrayList<Boolean>());
        }

        // add 3 booleans per length
        for (int i = 0; i < expLoop.size(); i++) {
            for (int j = 0; j < length; j++) {
                expLoop.get(i).add(false);
                expLoop.get(i).add(false);
                expLoop.get(i).add(false);

                if (i < onlyLoop.size())
                    onlyLoop.get(i).add(false);
            }
        }

        for (Coord<Character> coord : loop) {
            int row = (int) coord.getRow();
            int col = (int) coord.getCol();

            onlyLoop.get(row).set(col, true);

            // set the middle boolean of each pipe to true
            expLoop.get((row * 3) + 1).set((col * 3 + 1), true);

            // set the north, south, east, and west pipes depending on the shape of the pipe
            switch (coord.getData()) {
                case '|':
                    expLoop.get(row * 3).set((col * 3 + 1), true);
                    expLoop.get(row * 3 + 2).set((col * 3 + 1), true);
                    break;
                case '-':
                    expLoop.get((row * 3) + 1).set((col * 3), true);
                    expLoop.get((row * 3) + 1).set((col * 3 + 2), true);
                    break;
                case 'L':
                    expLoop.get((row * 3)).set((col * 3 + 1), true);
                    expLoop.get((row * 3) + 1).set((col * 3 + 2), true);
                    break;
                case 'J':
                    expLoop.get((row * 3)).set((col * 3 + 1), true);
                    expLoop.get((row * 3) + 1).set((col * 3), true);
                    break;
                case '7':
                    expLoop.get((row * 3) + 1).set((col * 3), true);
                    expLoop.get(row * 3 + 2).set((col * 3 + 1), true);
                    break;
                case 'F':
                    expLoop.get((row * 3) + 1).set((col * 3 + 2), true);
                    expLoop.get(row * 3 + 2).set((col * 3 + 1), true);
                    break;
                case '.':
                    System.out.println("Not a pipe.");
                    break;
                case 'S':
                    // find what kind of pipe S is, and set the surrounding pipes accordingly
                    boolean nB, sB, wB, eB;
                    nB = sB = wB = eB = false;
                    char n = input.getData((int) coord.getRow() - 1, (int) coord.getCol());
                    char s = input.getData((int) coord.getRow() + 1, (int) coord.getCol());
                    char w = input.getData((int) coord.getRow(), (int) coord.getCol() - 1);
                    char e = input.getData((int) coord.getRow(), (int) coord.getCol() + 1);
                    if (n == '|' || n == '7' || n == 'F')
                        nB = true;
                    if (s == '|' || s == 'J' || s == 'L')
                        sB = true;
                    if (w == '-' || w == 'L' || w == 'F')
                        wB = true;
                    if (e == '-' || e == '7' || e == 'J')
                        eB = true;
                    if (nB) {
                        if (eB) {
                            expLoop.get((row * 3)).set((col * 3 + 1), true);
                            expLoop.get((row * 3) + 1).set((col * 3 + 2), true);
                        } else if (wB) {
                            expLoop.get((row * 3)).set((col * 3 + 1), true);
                            expLoop.get((row * 3) + 1).set((col * 3), true);
                        } else if (sB) {
                            expLoop.get(row * 3).set((col * 3 + 1), true);
                            expLoop.get(row * 3 + 2).set((col * 3 + 1), true);
                        }
                    } else if (sB) {
                        if (eB) {
                            expLoop.get((row * 3) + 1).set((col * 3 + 2), true);
                            expLoop.get(row * 3 + 2).set((col * 3 + 1), true);
                        } else if (wB) {
                            expLoop.get((row * 3) + 1).set((col * 3), true);
                            expLoop.get(row * 3 + 2).set((col * 3 + 1), true);
                        }
                    } else if (eB && wB) {
                        expLoop.get((row * 3) + 1).set((col * 3), true);
                        expLoop.get((row * 3) + 1).set((col * 3 + 2), true);
                    }
                    break;
            }
        }

        // starting at the upper outer corner, set all outside values to true
        Stack<Integer[]> toCheck = new Stack<Integer[]>();
        toCheck.add(new Integer[] { 0, 0 });
        expLoop.get(0).set(0, true);
        while (!toCheck.isEmpty()) {
            Integer[] coords = toCheck.pop();

            if (coords[0] > 0) {
                // north
                if (!expLoop.get(coords[0] - 1).get(coords[1])) {
                    toCheck.add(new Integer[] { coords[0] - 1, coords[1] });
                    expLoop.get(coords[0] - 1).set(coords[1], true);
                }
            }

            if (coords[0] < expLoop.size() - 1) {
                // south
                if (!expLoop.get(coords[0] + 1).get(coords[1])) {
                    toCheck.add(new Integer[] { coords[0] + 1, coords[1] });
                    expLoop.get(coords[0] + 1).set(coords[1], true);
                }
            }

            if (coords[1] > 0) {
                // west
                if (!expLoop.get(coords[0]).get(coords[1] - 1)) {
                    toCheck.add(new Integer[] { coords[0], coords[1] - 1 });
                    expLoop.get(coords[0]).set(coords[1] - 1, true);
                }
            }

            if (coords[1] < expLoop.get(0).size() - 1) {
                // east
                if (!expLoop.get(coords[0]).get(coords[1] + 1)) {
                    toCheck.add(new Integer[] { coords[0], coords[1] + 1 });
                    expLoop.get(coords[0]).set(coords[1] + 1, true);
                }
            }
        }

        for (int row = 0; row < expLoop.size(); row++) {
            for (int col = 0; col < expLoop.get(0).size(); col++) {
                if (!expLoop.get(row).get(col)) {
                    if (!onlyLoop.get(row / 3).get(col / 3))
                        answer++;
                }
            }
        }

        answer /= 9;

        return answer + "";
    }

    private static ArrayList<Coord<Character>> getLoop(Grid input, Coord<String> start) {
        ArrayList<Coord<Character>> loop = new ArrayList<Coord<Character>>();
        Dir prevDir = Dir.NONE;
        Coord<Character> currentTile = new Coord<Character>(start.getRow(), start.getCol(), 1, 1,
                start.getData().charAt(0));
        do {
            loop.add(currentTile);
            Dir toGo = getNextDir(input, currentTile, prevDir);
            long row = currentTile.getRow();
            long col = currentTile.getCol();
            switch (toGo) {
                case NORTH:
                    currentTile = new Coord<Character>(--row, col, 1, 1, input.getData((int) (row), (int) col));
                    prevDir = Dir.SOUTH;
                    break;
                case SOUTH:
                    currentTile = new Coord<Character>(++row, col, 1, 1, input.getData((int) (row), (int) col));
                    prevDir = Dir.NORTH;
                    break;
                case WEST:
                    currentTile = new Coord<Character>(row, --col, 1, 1, input.getData((int) (row), (int) col));
                    prevDir = Dir.EAST;
                    break;
                case EAST:
                    currentTile = new Coord<Character>(row, ++col, 1, 1, input.getData((int) (row), (int) col));
                    prevDir = Dir.WEST;
                    break;
                default:
                    System.out.println("????: " + toGo);
            }
        } while ((!currentTile.getData().equals('S')));
        return loop;
    }

    private static Dir getNextDir(Grid input, Coord<Character> tile, Dir origin) {
        switch (tile.getData()) {
            case '|':
                return (origin == Dir.NORTH) ? Dir.SOUTH : Dir.NORTH;
            case '-':
                return (origin == Dir.EAST) ? Dir.WEST : Dir.EAST;
            case 'L':
                return (origin == Dir.NORTH) ? Dir.EAST : Dir.NORTH;
            case 'J':
                return (origin == Dir.NORTH) ? Dir.WEST : Dir.NORTH;
            case '7':
                return (origin == Dir.SOUTH) ? Dir.WEST : Dir.SOUTH;
            case 'F':
                return (origin == Dir.SOUTH) ? Dir.EAST : Dir.SOUTH;
            case '.':
                System.out.println("Not a pipe.");
                return Dir.NONE;
            case 'S':
                char n = input.getData((int) tile.getRow() - 1, (int) tile.getCol());
                char s = input.getData((int) tile.getRow() + 1, (int) tile.getCol());
                char w = input.getData((int) tile.getRow(), (int) tile.getCol() - 1);
                char e = input.getData((int) tile.getRow(), (int) tile.getCol() + 1);
                if (n == '|' || n == '7' || n == 'F')
                    return Dir.NORTH;
                if (s == '|' || s == 'J' || s == 'L')
                    return Dir.SOUTH;
                if (w == '-' || w == 'L' || w == 'F')
                    return Dir.WEST;
                if (e == '-' || e == '7' || e == 'J')
                    return Dir.EAST;
                break;
        }

        return Dir.NONE;
    }
}
