package dailyPuzzles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import utils.Coord;
import utils.Grid;

public class Day14 {
    public static String Solve(Grid input, int part, boolean debug) {

        if (part == 1)
            return One(input, debug);
        else if (part == 2)
            return Two(input, debug);
        else
            return (part + " is not a part");
    }

    public static String One(Grid input, boolean debug) {
        long answer = 0L;

        // find the coords of each round rock
        ArrayList<Coord<String>> roundRock = input.findToken("O");
        Grid tiltedGrid = tiltNorth(input, roundRock);

        // find the new coords for each round rock
        roundRock.clear();
        roundRock = tiltedGrid.findToken("O");

        // add each rock's weighted row to the answer
        for (Coord<String> rock : roundRock)
            answer += input.getHeight() - rock.getRow();

        return answer + "";
    }

    public static String Two(Grid input, boolean debug) {
        long answer = 0L;

        // tilt the grid by the given cycles
        Grid tiltedGrid = cycleTile(input, 1000000000);

        // find the final location of all rocks
        ArrayList<Coord<String>> roundRock = tiltedGrid.findToken("O");

        // add each rock's weighted row to the answer
        for (Coord<String> rock : roundRock)
            answer += input.getHeight() - rock.getRow();

        return answer + "";
    }

    private static Grid cycleTile(Grid input, int cycles) {
        Map<String, Integer> memoMap = new HashMap<>();
        Grid tiltedGrid = new Grid(input);
        memoMap.put(tiltedGrid.toString(), 0);
        int loopLength = -1;
        int remainingCycle = -1;
        // loop for however many cycles exist
        for (int i = 1; i <= cycles; i++) {
            // find round rock locations, and update before every tilt
            ArrayList<Coord<String>> roundRock = tiltedGrid.findToken("O");
            tiltedGrid = tiltNorth(tiltedGrid, roundRock);

            // east and west tilts find round rocks by column to order them correctly
            roundRock.clear();
            roundRock = tiltedGrid.findTokenInCol("O");
            tiltedGrid = tiltWest(tiltedGrid, roundRock);

            roundRock.clear();
            roundRock = tiltedGrid.findToken("O");
            tiltedGrid = tiltSouth(tiltedGrid, roundRock);

            roundRock.clear();
            roundRock = tiltedGrid.findTokenInCol("O");
            tiltedGrid = tiltEast(tiltedGrid, roundRock);

            // if the current grid has been foud before, record the length of the loop and
            // the remaining cycles and break from the loop
            if (memoMap.containsKey(tiltedGrid.toString())) {
                loopLength = i - memoMap.get(tiltedGrid.toString());
                remainingCycle = cycles - i;
                break;
            }

            // cache the grid with the cycle number as its value
            memoMap.put(tiltedGrid.toString(), i);
        }

        // if a loop was found:
        if (loopLength != -1) {
            // find the remainder of the remaining cycles / loop length and cycle by that
            // amount
            int cycleToGo = remainingCycle % loopLength;
            for (int i = 1; i <= cycleToGo; i++) {
                ArrayList<Coord<String>> roundRock = tiltedGrid.findToken("O");
                tiltedGrid = tiltNorth(tiltedGrid, roundRock);

                roundRock.clear();
                roundRock = tiltedGrid.findTokenInCol("O");
                tiltedGrid = tiltWest(tiltedGrid, roundRock);

                roundRock.clear();
                roundRock = tiltedGrid.findToken("O");
                tiltedGrid = tiltSouth(tiltedGrid, roundRock);

                roundRock.clear();
                roundRock = tiltedGrid.findTokenInCol("O");
                tiltedGrid = tiltEast(tiltedGrid, roundRock);
            }
        }

        return tiltedGrid;
    }

    public static Grid tiltNorth(Grid input, ArrayList<Coord<String>> roundRock) {
        Grid tiltedGrid = new Grid(input);

        for (Coord<String> rock : roundRock) {
            char objectAt = '.';
            long row = rock.getRow();
            while (objectAt == '.' && row > 0) {
                objectAt = input.getData(--row, rock.getCol());
            }

            if (objectAt != '.')
                row++;

            input.setData((int) row, (int) rock.getCol(), 'O');
            if (row != rock.getRow())
                input.setData((int) rock.getRow(), (int) rock.getCol(), '.');
        }

        return tiltedGrid;
    }

    public static Grid tiltSouth(Grid input, ArrayList<Coord<String>> roundRock) {
        Grid tiltedGrid = new Grid(input);

        for (int i = roundRock.size() - 1; i >= 0; i--) {
            Coord<String> rock = roundRock.get(i);
            char objectAt = '.';
            long row = rock.getRow();
            while (objectAt == '.' && row < input.getHeight() - 1) {
                objectAt = input.getData(++row, rock.getCol());
            }

            if (objectAt != '.')
                row--;

            input.setData((int) row, (int) rock.getCol(), 'O');
            if (row != rock.getRow())
                input.setData((int) rock.getRow(), (int) rock.getCol(), '.');
        }

        return tiltedGrid;
    }

    public static Grid tiltEast(Grid input, ArrayList<Coord<String>> roundRock) {
        Grid tiltedGrid = new Grid(input);

        for (int i = roundRock.size() - 1; i >= 0; i--) {
            Coord<String> rock = roundRock.get(i);
            char objectAt = '.';
            long col = rock.getCol();
            while (objectAt == '.' && col < input.getLength() - 1) {
                objectAt = input.getData(rock.getRow(), ++col);
            }

            if (objectAt != '.')
                col--;

            input.setData((int) rock.getRow(), (int) col, 'O');
            if (col != rock.getCol())
                input.setData((int) rock.getRow(), (int) rock.getCol(), '.');
        }

        return tiltedGrid;

    }

    public static Grid tiltWest(Grid input, ArrayList<Coord<String>> roundRock) {
        Grid tiltedGrid = new Grid(input);

        for (Coord<String> rock : roundRock) {
            char objectAt = '.';
            long col = rock.getCol();
            while (objectAt == '.' && col > 0) {
                objectAt = input.getData(rock.getRow(), --col);
            }

            if (objectAt != '.')
                col++;

            input.setData((int) rock.getRow(), (int) col, 'O');
            if (col != rock.getCol())
                input.setData((int) rock.getRow(), (int) rock.getCol(), '.');
        }

        return tiltedGrid;

    }
}
