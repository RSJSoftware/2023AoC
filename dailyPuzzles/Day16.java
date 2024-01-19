package dailyPuzzles;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Stack;

import utils.Dir;
import utils.Grid;

public class Day16 {
    private record LightBeam(int row, int col, Dir dir) {

    }

    public static String Solve(Grid input, int part, boolean debug) {

        if (part == 1)
            return One(input, debug);
        else if (part == 2)
            return Two(input, debug);
        else
            return (part + " is not a part");
    }

    public static String One(Grid input, boolean debug) {
        // find and return energized tiles from the top left corner going east
        long answer = GetEnergizedTiles(input, new LightBeam(0, 0, Dir.EAST));

        return answer + "";
    }

    public static String Two(Grid input, boolean debug) {
        long answer = 0L;

        // find energized tiles from all edges, go south or north on each edge row, and
        // east or west on each edge col
        for (int row = 0; row < input.getHeight(); row++) {
            for (int col = 0; col < input.getLength(); col++) {
                if (row == 0)
                    answer = Math.max(answer, GetEnergizedTiles(input, new LightBeam(row, col, Dir.SOUTH)));
                if (col == 0)
                    answer = Math.max(answer, GetEnergizedTiles(input, new LightBeam(row, col, Dir.EAST)));
                if (row == input.getHeight() - 1)
                    answer = Math.max(answer, GetEnergizedTiles(input, new LightBeam(row, col, Dir.NORTH)));
                if (col == input.getLength() - 1)
                    answer = Math.max(answer, GetEnergizedTiles(input, new LightBeam(row, col, Dir.WEST)));
            }
        }

        return answer + "";
    }

    private static long GetEnergizedTiles(Grid input, LightBeam start) {
        long answer = 0L;
        // create a boolean map to denote energized tiles filled with false
        Boolean[][] lightMap = new Boolean[input.getHeight()][input.getLength()];

        for (Boolean[] b : lightMap)
            Arrays.fill(b, false);

        // create a stack to keep track of beams to check and energize, and a hashset to
        // avoid loops
        LightBeam currentBeam = start;
        Stack<LightBeam> movingLight = new Stack<LightBeam>();
        HashSet<LightBeam> light = new HashSet<LightBeam>();

        // add the starting tile to the stack
        movingLight.add(currentBeam);

        while (!movingLight.isEmpty()) {
            // get the top values from the stack
            currentBeam = movingLight.pop();
            int row = currentBeam.row;
            int col = currentBeam.col;
            Dir dir = currentBeam.dir;

            // if its out of bounds or exists in the hashset going the same direction,
            // continue to the next stack value
            if (row < 0 || row >= input.getHeight() || col < 0 || col >= input.getLength()
                    || (lightMap[row][col] && light.contains(currentBeam)))
                continue;

            // if the map value is still false, it's a new energized tile, add it to the
            // total
            if (!lightMap[row][col])
                answer++;

            // set the tile to energized and cache the tile and direction
            lightMap[row][col] = true;
            light.add(currentBeam);

            // get the data on the tile
            char currentData = input.getData(row, col);
            switch (currentData) {
                // keep moving forward if it's a '.'
                case '.':
                    movingLight.add(GetNextLight(row, col, dir));
                    break;
                case '-':
                    // split into east and west for '-' if it came from north or south, otherwise go
                    // forward
                    if (dir == Dir.NORTH || dir == Dir.SOUTH) {
                        movingLight.add(GetNextLight(row, col, Dir.EAST));
                        movingLight.add(GetNextLight(row, col, Dir.WEST));
                    } else {
                        movingLight.add(GetNextLight(row, col, dir));
                    }
                    break;
                case '|':
                    // split into north and south for '|' if it came from east or west, otherwise go
                    // forward
                    if (dir == Dir.EAST || dir == Dir.WEST) {
                        movingLight.add(GetNextLight(row, col, Dir.NORTH));
                        movingLight.add(GetNextLight(row, col, Dir.SOUTH));
                    } else {
                        movingLight.add(GetNextLight(row, col, dir));
                    }
                    break;
                case '\\':
                    // bounce 90 degrees in the corresponding direction based on '\'
                    switch (dir) {
                        case NORTH:
                            movingLight.add(GetNextLight(row, col, Dir.WEST));
                            break;
                        case SOUTH:
                            movingLight.add(GetNextLight(row, col, Dir.EAST));
                            break;
                        case EAST:
                            movingLight.add(GetNextLight(row, col, Dir.SOUTH));
                            break;
                        case WEST:
                            movingLight.add(GetNextLight(row, col, Dir.NORTH));
                            break;
                        default:
                            break;
                    }
                    break;
                case '/':
                    // bounce 90 degrees in the corresponding direction based on '/'
                    switch (dir) {
                        case NORTH:
                            movingLight.add(GetNextLight(row, col, Dir.EAST));
                            break;
                        case SOUTH:
                            movingLight.add(GetNextLight(row, col, Dir.WEST));
                            break;
                        case EAST:
                            movingLight.add(GetNextLight(row, col, Dir.NORTH));
                            break;
                        case WEST:
                            movingLight.add(GetNextLight(row, col, Dir.SOUTH));
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    System.out.println("Unexpected val: " + currentData);
            }
        }

        // return counted tiles
        return answer;
    }

    private static LightBeam GetNextLight(int row, int col, Dir dir) {
        // get the next tile based on the direction
        switch (dir) {
            case NORTH:
                return new LightBeam(--row, col, dir);
            case SOUTH:
                return new LightBeam(++row, col, dir);
            case EAST:
                return new LightBeam(row, ++col, dir);
            case WEST:
                return new LightBeam(row, --col, dir);
            default:
                return new LightBeam(row, col, dir);
        }
    }
}
