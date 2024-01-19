package dailyPuzzles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Day22 {
    static record Coord(int x, int y, int z) {

    }

    static HashMap<Integer, Integer> toFallCache = new HashMap<>();

    public static String Solve(ArrayList<String> input, int part, boolean debug) {
        Pattern p = Pattern.compile("(\\d+),(\\d+),(\\d+)~(\\d+),(\\d+),(\\d+)");
        ArrayList<Brick> bricks = new ArrayList<>();
        for (String s : input) {
            Matcher m = p.matcher(s);

            if (m.find()) {
                bricks.add(new Brick(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)),
                        Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)),
                        Integer.parseInt(m.group(5)), Integer.parseInt(m.group(6))));
            }
        }

        Collections.sort(bricks);
        DropBricks(bricks);

        if (part == 1)
            return One(bricks, debug);
        else if (part == 2)
            return Two(bricks, debug);
        else
            return (part + " is not a part");
    }

    public static String One(ArrayList<Brick> bricks, boolean debug) {
        long answer = 0;
        InitSoleSupport(bricks);

        for (Brick b : bricks) {
            if (b.supportBricks.isEmpty() || !b.isSoleSupport)
                answer++;
        }

        return answer + "";
    }

    public static String Two(ArrayList<Brick> bricks, boolean debug) {
        long answer = 0;
        InitSoleSupport(bricks);

        Stack<Integer> toPull = new Stack<>();

        for (int i = 0; i < bricks.size(); i++) {
            Brick b = bricks.get(i);
            if (!b.supportBricks.isEmpty() && b.isSoleSupport)
                toPull.add(i);
        }

        while (!toPull.isEmpty()) {
            int index = toPull.pop();

            answer += SimBrickDrop(bricks, index);
        }

        return answer + "";

    }

    private static int SimBrickDrop(ArrayList<Brick> bricks, int index) {
        int ret = 0;

        ArrayList<Brick> bricksClone = new ArrayList<>();

        for (Brick b : bricks) {
            bricksClone.add(new Brick(b));
        }

        bricksClone.remove(index);

        for (int i = 0; i < bricksClone.size(); i++) {
            bricksClone.get(i).findBaseBricks(bricksClone, i);
            if (bricksClone.get(i).Drop(bricks, i))
                ret++;
        }

        return ret;
    }

    private static void DropBricks(ArrayList<Brick> bricks) {
        for (int i = 0; i < bricks.size(); i++) {
            Brick b = bricks.get(i);
            b.findBaseBricks(bricks, i);
            while (b.baseBricks.isEmpty() && Math.min(b.z, b.z2) > 1) {
                b.Drop(bricks, i);
            }
        }
    }

    private static void InitSoleSupport(ArrayList<Brick> bricks) {
        for (Brick b : bricks) {
            if (b.supportBricks.isEmpty()) {
                continue;
            }

            for (Integer i : b.supportBricks) {
                if (bricks.get(i).baseBricks.size() == 1) {
                    b.isSoleSupport = true;
                    break;
                }
            }
        }
    }

    static class Brick implements Comparable<Brick> {
        int x, y, z, x2, y2, z2;
        ArrayList<Coord> flatCoords;
        ArrayList<Integer> baseBricks, supportBricks;
        boolean isSoleSupport;

        Brick(int x, int y, int z, int x2, int y2, int z2) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;

            flatCoords = new ArrayList<>();
            baseBricks = new ArrayList<>();
            supportBricks = new ArrayList<>();

            isSoleSupport = false;

            for (int i = Math.min(x, x2); i <= Math.max(x, x2); i++) {
                for (int j = Math.min(y, y2); j <= Math.max(y, y2); j++) {
                    flatCoords.add(new Coord(i, j, 0));
                }
            }
        }

        Brick(Brick b) {
            x = b.x;
            y = b.y;
            z = b.z;
            x2 = b.x2;
            y2 = b.y2;
            z2 = b.z2;

            flatCoords = b.flatCoords;
            baseBricks = new ArrayList<>();
            supportBricks = new ArrayList<>();

            isSoleSupport = false;
        }

        boolean Drop(ArrayList<Brick> bricks, int index) {
            if (Math.min(z, z2) <= 1 || !baseBricks.isEmpty())
                return false;

            z--;
            z2--;

            findBaseBricks(bricks, index);

            return true;
        }

        void findBaseBricks(ArrayList<Brick> bricks, int index) {
            for (int i = 0; i < bricks.size(); i++) {
                if (i == index)
                    continue;
                Brick b = bricks.get(i);
                if (b.HighestPoint() >= LowestPoint())
                    continue;
                if (b.HighestPoint() == (Math.min(z, z2) - 1)) {
                    if (ArePointsShared(b.flatCoords)) {
                        baseBricks.add(i);
                        b.supportBricks.add(index);
                    }
                }
            }
        }

        int HighestPoint() {
            return Math.max(z, z2);
        }

        int LowestPoint() {
            return Math.min(z, z2);
        }

        boolean ArePointsShared(ArrayList<Coord> otherFlat) {
            for (int i = 0; i < flatCoords.size(); i++) {
                for (int j = 0; j < otherFlat.size(); j++) {
                    Coord f = flatCoords.get(i);
                    Coord o = otherFlat.get(j);

                    if (f.x == o.x && f.y == o.y)
                        return true;
                }
            }

            return false;
        }

        @Override
        public int compareTo(Brick o) {
            if (!(o instanceof Brick))
                return -1;

            int ret = Integer.compare(Math.min(z, z2), Math.min(o.z, o.z2));

            if (ret == 0) {
                ret = Integer.compare(Math.max(z, z2), Math.max(o.z, o.z2));
            }

            if (ret == 0) {
                ret = Integer.compare(Math.min(y, y2), Math.min(o.y, o.y2));
            }

            if (ret == 0) {
                ret = Integer.compare(Math.max(y, y2), Math.max(o.y, o.y2));
            }
            if (ret == 0) {
                ret = Integer.compare(Math.min(x, x2), Math.min(o.x, o.x2));
            }
            if (ret == 0) {
                ret = Integer.compare(Math.max(x, x2), Math.max(o.x, o.x2));
            }
            if (ret == 0) {
                ret = Integer.compare(Math.min(z, z2), Math.min(o.z, o.z2));
            }
            if (ret == 0) {
                ret = Integer.compare(Math.max(z, z2), Math.max(o.z, o.z2));
            }
            return ret;
        }

        @Override
        public String toString() {
            return x + "," + y + "," + z + "~" + x2 + "," + y2 + "," + z2;
        }
    }
}
