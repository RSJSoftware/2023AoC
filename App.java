import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.*;

import dailyPuzzles.*;
import utils.Grid;

import java.util.ArrayList;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner userInput = new Scanner(System.in);
        JSONParser parser = new JSONParser();
        JSONArray puzzleInputs = new JSONArray();
        ArrayList<PuzzleInput> inputs = new ArrayList<PuzzleInput>();

        try (FileReader reader = new FileReader("src/Inputs.json")) {
            puzzleInputs = (JSONArray) parser.parse(reader);
        } catch (FileNotFoundException e) {
            System.out.println("File: Inputs.json not found");
            e.printStackTrace();
            userInput.close();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            userInput.close();
            return;
        }

        puzzleInputs.forEach(puzzle -> inputs.add(parsePuzzleInputs((JSONObject) puzzle)));

        while (true) {

            System.out.println("Enter day:");
            int day = userInput.nextInt();

            if (day > inputs.size()) {
                while (day > inputs.size()) {
                    System.out.println(day + " has not been added yet.");
                    System.out.println("Enter day:");
                    day = userInput.nextInt();
                }
            }

            System.out.println("Enter part:");
            int part = userInput.nextInt();

            if (part > 2 || part < 1) {
                while (part > 2 || part < 1) {
                    System.out.println("please enter 1 or 2");
                    System.out.println("Enter part:");
                    part = userInput.nextInt();
                }
            }

            /*
             * System.out.println("Debug? (0 for no, 1 for yes):");
             * int debug = userInput.nextInt();
             * 
             * if (debug > 1 || debug < 0) {
             * while (debug > 1 || debug < 0) {
             * System.out.println("please enter 1 or 0");
             * System.out.println("Debug? (0 for no, 1 for yes):");
             * debug = userInput.nextInt();
             * }
             * }
             */

            System.out.println("Sample data? (0 for no, 1 for yes):");
            int sample = userInput.nextInt();

            if (sample > 1 || sample < 0) {
                while (sample > 1 || sample < 0) {
                    System.out.println("please enter 1 or 0");
                    System.out.println("Sample data? (0 for no, 1 for yes):");
                    sample = userInput.nextInt();
                }
            }
            loadPuzzle(inputs.get(day - 1), part, false, sample == 1);

            System.out.println("\nRun another puzzle? (0 for no, 1 for yes):");
            int answer = userInput.nextInt();

            if (answer > 1 || answer < 0) {
                while (answer > 1 || answer < 0) {
                    System.out.println("please enter 1 or 0");
                    System.out.println("Run another puzzle? (0 for no, 1 for yes):");
                    answer = userInput.nextInt();
                }
            }

            if (answer == 0)
                break;
        }

        userInput.close();

    }

    public static void loadPuzzle(PuzzleInput puzzle, int part, boolean debug, boolean sample) {
        ArrayList<String> listInput = new ArrayList<String>();
        Grid gridInput = new Grid();
        ArrayList<ArrayList<String>> batchInput = new ArrayList<ArrayList<String>>();
        ArrayList<Grid> gridListInput = new ArrayList<Grid>();

        switch (puzzle.parseType) {
            case "Line":
                listInput = sample ? AoCParser.lineParse(puzzle.fileNameSample)
                        : AoCParser.lineParse(puzzle.fileName);
                break;
            case "Grid":
                gridInput = sample ? AoCParser.gridParse(puzzle.fileNameSample)
                        : AoCParser.gridParse(puzzle.fileName);
                break;
            case "Batch":
                batchInput = sample ? AoCParser.batchParse(puzzle.fileNameSample, ":")
                        : AoCParser.batchParse(puzzle.fileName, ":");
                break;
            case "GridList":
                gridListInput = sample ? AoCParser.gridListParse(puzzle.fileNameSample)
                        : AoCParser.gridListParse(puzzle.fileName);
                break;
            default:
                System.out.println(puzzle.parseType + " has not been defined.");
        }

        String answer = "";
        long startTime = System.nanoTime();
        switch ((int) puzzle.day) {
            case 1:
                answer = Day01.Solve(listInput, part, debug);
                break;
            case 2:
                answer = Day02.Solve(listInput, part, debug);
                break;
            case 3:
                answer = Day03.Solve(gridInput, part, debug);
                break;
            case 4:
                answer = Day04.Solve(listInput, part, debug);
                break;
            case 5:
                answer = Day05.Solve(batchInput, part, debug);
                break;
            case 6:
                answer = Day06.Solve(listInput, part, debug);
                break;
            case 7:
                answer = Day07.Solve(listInput, part, debug);
                break;
            case 8:
                answer = Day08.Solve(listInput, part, debug);
                break;
            case 9:
                answer = Day09.Solve(listInput, part, debug);
                break;
            case 10:
                answer = Day10.Solve(gridInput, part, debug);
                break;
            case 11:
                answer = Day11.Solve(gridInput, part, debug);
                break;
            case 12:
                answer = Day12.Solve(listInput, part, debug);
                break;
            case 13:
                answer = Day13.Solve(gridListInput, part, debug);
                break;
            case 14:
                answer = Day14.Solve(gridInput, part, debug);
                break;
            case 15:
                answer = Day15.Solve(listInput, part, debug);
                break;
            case 16:
                answer = Day16.Solve(gridInput, part, debug);
                break;
            case 17:
                answer = Day17.Solve(gridInput, part, debug);
                break;
            case 18:
                answer = Day18.Solve(listInput, part, debug);
                break;
            case 19:
                answer = Day19.Solve(listInput, part, debug);
                break;
            case 20:
                answer = Day20.Solve(listInput, part, debug);
                break;
            case 21:
                answer = Day21.Solve(gridInput, part, debug);
                break;
            case 22:
                answer = Day22.Solve(listInput, part, debug);
                break;
            case 23:
                answer = Day23.Solve(gridInput, part, debug);
                break;
            case 24:
                answer = Day24.Solve(listInput, part, debug);
                break;
            case 25:
                answer = Day25.Solve(listInput, part, debug);
                break;
            default:
                answer = (puzzle.day + " has not been solved yet.");
        }
        long endTime = System.nanoTime();

        System.out.println("Answer: " + answer);
        System.out.println("Time to complete: " + ((endTime - startTime) / 1000000) + "ms");
    }

    public static PuzzleInput parsePuzzleInputs(JSONObject puzzle) {
        JSONObject input = (JSONObject) puzzle.get("puzzleInput");
        System.out.println(input);
        return new PuzzleInput((long) input.get("day"), (String) input.get("parseType"),
                (String) input.get("fileName"), (String) input.get("fileNameSample"));
    }
}
