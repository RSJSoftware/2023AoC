package utils;

//Depreciated since I learned about java 'records'
public class Coord<T> {
    long row;
    long col;
    T data;
    long height;
    long length;

    public Coord(long r, long c, long h, long l, T d) {
        row = r;
        col = c;
        height = h;
        length = l;
        data = d;
    }

    public Coord(long r, long c) {
        row = r;
        col = c;
        height = 1;
        length = 1;
        data = null;
    }

    public void setHeight(long h) {
        height = h;
    }

    public long getHeight() {
        return height;
    }

    public void setLength(int l) {
        length = l;
    }

    public long getLength() {
        return length;
    }

    public void setData(T d) {
        data = d;
    }

    public T getData() {
        return data;
    }

    public void setRow(long r) {
        row = r;
    }

    public long getRow() {
        return row;
    }

    public void setCol(long c) {
        col = c;
    }

    public long getCol() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Coord))
            return false;

        return ((Coord) o).row == row && ((Coord) o).col == col;
    }

    public String toString() {
        return "x: " + col + " y: " + row + " val: " + data + " length " + length + " height " + height;
    }
}
