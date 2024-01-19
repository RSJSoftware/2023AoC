package utils;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Grid {
    private ArrayList<String> data;

    public Grid() {
        data = new ArrayList<String>();
    }

    public Grid(Grid g) {
        data = g.data;
    }

    public void addRow() {
        data.add("");
    }

    public void addRow(String s) {
        data.add(s);
    }

    public void addRow(int index, String s) {
        if (index < 0 || index > data.size()) {
            System.out.println("Out of index adding a row");
            return;
        }
        data.add(index, s);
    }

    public void addColumn(char nullChar) {
        for (int i = 0; i < data.size(); i++) {
            String update = data.get(i);
            update += nullChar;
            data.set(i, update);
        }
    }

    public void addColumn(int index, char nullChar) {
        if (index < 0 || index > data.get(0).length()) {
            System.out.println("Out of index adding a col");
            return;
        }
        for (int i = 0; i < data.size(); i++) {
            String update = data.get(i);
            update = update.substring(0, index) + nullChar + update.substring(index);
            data.set(i, update);
        }
    }

    public int getLength() {
        return data.get(0).length();
    }

    public int getHeight() {
        return data.size();
    }

    public void setData(int row, int column, char c) {
        String update = data.get(row);
        update = update.substring(0, column) + c + update.substring(column + 1);
        data.set(row, update);
    }

    public char getData(int row, int column) {
        if (row < 0 || row >= getHeight() || column < 0 || column >= getLength())
            return '.';
        return data.get(row).charAt(column);
    }

    public char getNextData(int row, int column, Dir d) {
        switch (d) {
            case NORTH:
                row--;
                break;
            case SOUTH:
                row++;
                break;
            case EAST:
                column++;
                break;
            case WEST:
                column--;
                break;
            case NONE:
                return getData(row, column);
            default:
                System.out.println(d + " is not a direction (getNextData, Grid.java)");
        }
        if (row < 0 || row >= getHeight() || column < 0 || column >= getLength())
            return '`';
        return data.get(row).charAt(column);
    }

    public char getData(long row, long column) {
        return getData((int) row, (int) column);
    }

    public char getNextData(long row, long column, Dir d) {
        return getNextData((int) row, (int) column, d);
    }

    public Coord<Integer> getCoord(int point) {
        long row = point / data.get(0).length();
        long col = point % data.get(0).length();

        return new Coord<Integer>(row, col);
    }

    public String getRow(int row) {
        if (row < 0 || row >= data.size())
            return "Out of index";
        return data.get(row);
    }

    public String getColumn(int col) {
        if (col < 0 || col >= data.get(0).length())
            return "Out of index";
        String column = "";

        for (String s : data) {
            column += s.charAt(col);
        }

        return column;
    }

    public ArrayList<Coord<String>> findTokenFrom(String token, int startingIndex) {
        Pattern p = Pattern.compile(token);
        Coord<Integer> startingCoord = getCoord(startingIndex);

        ArrayList<Coord<String>> output = new ArrayList<Coord<String>>();

        for (int i = (int) startingCoord.row; i < data.size(); i++) {
            Matcher m = p.matcher(data.get(i));
            if (i == startingCoord.row) {
                if (m.find((int) startingCoord.col)) {
                    output.add(new Coord<String>(i, m.start(), 1, m.group().length(), m.group()));
                }
            }

            while (m.find())
                output.add(new Coord<String>(i, m.start(), 1, m.group().length(), m.group()));
        }

        return output;
    }

    public ArrayList<Coord<String>> findToken(String token) {
        Pattern p = Pattern.compile(token);

        ArrayList<Coord<String>> output = new ArrayList<Coord<String>>();

        for (int i = 0; i < data.size(); i++) {
            Matcher m = p.matcher(data.get(i));

            while (m.find())
                output.add(new Coord<String>(i, m.start(), 1, m.group().length(), m.group()));
        }

        return output;
    }

    public ArrayList<Coord<String>> findTokenInCol(String token) {
        Pattern p = Pattern.compile(token);

        ArrayList<Coord<String>> output = new ArrayList<Coord<String>>();

        for (int i = 0; i < data.get(0).length(); i++) {
            Matcher m = p.matcher(getColumn(i));

            while (m.find())
                output.add(new Coord<String>(m.start(), i, 1, m.group().length(), m.group()));
        }

        return output;
    }

    public Coord<String> findInstance(String token) {
        Pattern p = Pattern.compile(token);

        for (int i = 0; i < data.size(); i++) {
            Matcher m = p.matcher(data.get(i));

            while (m.find())
                return new Coord<String>(i, m.start(), 1, m.group().length(), m.group());
        }

        return new Coord<String>(-1, -1, -1, -1, "not found");
    }

    public boolean existsInRow(int row, String token) {
        Pattern p = Pattern.compile(token);
        Matcher m = p.matcher(data.get(row));
        return m.find();
    }

    public boolean existsInColumn(int col, String token) {
        Pattern p = Pattern.compile(token);
        Matcher m = p.matcher(getColumn(col));
        return m.find();
    }

    public boolean areAdjacent(Coord<String> token1, Coord<String> token2, boolean includeCorner) {
        int t1length = token1.getData().length();
        int t2length = token2.getData().length();

        for (int i = (int) token1.col; i < token1.col + t1length; i++) {
            for (int j = (int) token2.col; j < token2.col + t2length; j++) {
                if (includeCorner && (Math.abs(i - j) <= 1) && (Math.abs(token1.getRow() - token2.getRow()) <= 1)) {
                    return true;
                }
                if (token1.getRow() == token2.getRow() && (Math.abs(i - j) == 1))
                    return true;
                if (i == j && (Math.abs(token1.getRow() - token2.getRow()) == 1))
                    return true;
            }
        }

        return false;
    }

    public String toString() {
        String out = "";
        for (String s : data) {
            if (out.equals("")) {
                out += s;
                continue;
            }

            out += "\n" + s;
        }

        return out;
    }
}
