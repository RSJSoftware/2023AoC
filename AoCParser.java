import java.io.FileNotFoundException;
import java.io.File;
import java.util.Scanner;

import utils.Grid;

import java.util.ArrayList;

public class AoCParser {
    public static ArrayList<String> lineParse(String fileName) {
        ArrayList<String> parsedInput = new ArrayList<String>();
        try {
            File inputFile = new File(fileName);
            Scanner reader = new Scanner(inputFile);
            while (reader.hasNextLine()) {
                parsedInput.add(reader.nextLine());
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File: " + fileName + " not found.");
            e.printStackTrace();
        }

        return parsedInput;
    }

    public static Grid gridParse(String fileName) {
        Grid parsedInput = new Grid();

        try {
            File inputFile = new File(fileName);
            Scanner reader = new Scanner(inputFile);
            while (reader.hasNextLine()) {
                parsedInput.addRow(reader.nextLine());
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File: " + fileName + " not found.");
            e.printStackTrace();
        }

        return parsedInput;
    }

    public static ArrayList<ArrayList<String>> batchParse(String fileName, String delimiter) {
        ArrayList<ArrayList<String>> parsedInput = new ArrayList<ArrayList<String>>();

        int batch = -1;

        try {
            File inputFile = new File(fileName);
            Scanner reader = new Scanner(inputFile);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.contains(delimiter)) {
                    batch++;
                    parsedInput.add(new ArrayList<String>());
                } else if (line.equals(""))
                    continue;
                parsedInput.get(batch).add(line);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File: " + fileName + " not found.");
            e.printStackTrace();
        }

        return parsedInput;
    }

    public static ArrayList<Grid> gridListParse(String fileName) {
        ArrayList<Grid> parsedInput = new ArrayList<Grid>();
        Grid grid = new Grid();

        try {
            File inputFile = new File(fileName);
            Scanner reader = new Scanner(inputFile);
            while (reader.hasNextLine()) {
                String nextLine = reader.nextLine();
                if (nextLine.isBlank()) {
                    parsedInput.add(grid);
                    grid = new Grid();
                } else
                    grid.addRow(nextLine);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File: " + fileName + " not found.");
            e.printStackTrace();
        }
        parsedInput.add(grid);

        return parsedInput;

    }
}
