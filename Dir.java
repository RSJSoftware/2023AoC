package utils;

public enum Dir {
    NORTH(-1, 0), EAST(0, 1), SOUTH(1, 0), WEST(0, -1), NONE(0, 0);

    public int rowOffset;
    public int colOffset;

    Dir(int rowOffset, int colOffset) {
        this.rowOffset = rowOffset;
        this.colOffset = colOffset;
    }

    public Dir back() {
        return switch (this) {
            case NORTH -> SOUTH;
            case WEST -> EAST;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case NONE -> NONE;
        };
    }

    public Dir left() {
        return switch (this) {
            case NORTH -> WEST;
            case WEST -> SOUTH;
            case SOUTH -> EAST;
            case EAST -> NORTH;
            case NONE -> NONE;
        };
    }

    public Dir right() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
            case NONE -> NONE;
        };
    }
}
