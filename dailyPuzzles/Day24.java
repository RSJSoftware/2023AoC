package dailyPuzzles;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day24 {
    static record Coord(double x, double y, double z) {
    }

    static record hail(Coord pos, Coord vel) {
        public double Slope() {
            return vel.y / vel.x;
        }

        public double yAtZero() {
            return (-Slope() * pos.x) + pos.y;
        }

        public Coord getIntersect(hail h) {
            if (Slope() == h.Slope())
                return null;
            double x = (h.yAtZero() - yAtZero()) / (Slope() - h.Slope());
            double y = (Slope() * x) + yAtZero();

            return new Coord(x, y, 0);
        }

        public boolean isPast(Coord c) {
            if ((vel.x < 0 && pos.x < c.x) || (vel.x > 0 && pos.x > c.x))
                return true;

            if ((vel.y < 0 && pos.y < c.y) || (vel.y > 0 && pos.y > c.y))
                return true;

            return false;
        }
    }

    public static String Solve(ArrayList<String> input, int part, boolean debug) {
        Pattern p = Pattern.compile("(-?\\d+),\\s+(-?\\d+),\\s+(-?\\d+)\\s+@\\s+(-?\\d+),\\s+(-?\\d+),\\s+(-?\\d+)");

        ArrayList<hail> hailStorm = new ArrayList<>();
        for (String s : input) {
            Matcher m = p.matcher(s);

            if (m.find()) {
                Coord pos = new Coord(Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2)),
                        Double.parseDouble(m.group(3)));
                Coord vel = new Coord(Double.parseDouble(m.group(4)), Double.parseDouble(m.group(5)),
                        Double.parseDouble(m.group(6)));
                hailStorm.add(new hail(pos, vel));
            }
        }

        hailStorm.add(new hail(new Coord(24, 13, 10), new Coord(-3, 1, 2)));

        for (hail h : hailStorm) {
            System.out.println(h + " slope: " + h.Slope() + " y: " + h.yAtZero());
        }
        if (part == 1)
            return One(hailStorm, debug);
        else if (part == 2)
            return Two(hailStorm, debug);
        else
            return (part + " is not a part");
    }

    public static String One(ArrayList<hail> hailStorm, boolean debug) {
        long answer = 0;
        long least = 200000000000000L;
        long most = 400000000000000L;

        for (int i = 0; i < hailStorm.size() - 1; i++) {
            for (int j = i + 1; j < hailStorm.size(); j++) {
                Coord intersect = hailStorm.get(i).getIntersect(hailStorm.get(j));

                if (intersect == null || intersect.x < least || intersect.x > most || intersect.y < least
                        || intersect.y > most || hailStorm.get(i).isPast(intersect)
                        || hailStorm.get(j).isPast(intersect))
                    continue;

                answer++;
            }
        }

        return answer + "";
    }

    public static String Two(ArrayList<hail> hailStorm, boolean debug) {
        double answer = 0;

        double c[][] = new double[4][4];
        double r[] = new double[4];

        for (int i = 0; i < 4; i++) {
            hail h1 = hailStorm.get(i);
            hail h2 = hailStorm.get(i + 1);
            c[i][0] = h2.vel.y - h1.vel.y;
            c[i][1] = h1.vel.x - h2.vel.x;
            c[i][2] = h1.pos.y - h2.pos.y;
            c[i][3] = h2.pos.x - h1.pos.x;
            r[i] = -h1.pos.x * h1.vel.y + h1.pos.y * h1.vel.x + h2.pos.x * h2.vel.y - h2.pos.y * h2.vel.x;
        }

        gaussianElimination(c, r);

        double x = r[0];
        double y = r[1];
        long vx = Math.round(r[2]);

        c = new double[2][2];
        r = new double[2];

        for (int i = 0; i < 2; i++) {
            hail h1 = hailStorm.get(i);
            hail h2 = hailStorm.get(i + 1);
            c[i][0] = h1.vel.x - h2.vel.x;
            c[i][1] = h2.pos.x - h1.pos.x;
            r[i] = -h1.pos.x * h1.vel.z + h1.pos.z * h1.vel.x + h2.pos.x * h2.vel.z - h2.pos.z * h2.vel.x
                    - ((h2.vel.z - h1.vel.z) * x) - ((h1.pos.z - h2.pos.z) * vx);
        }

        gaussianElimination(c, r);

        double z = r[0];

        answer = Math.floor(x + y + z);

        return String.format("%14.0f", answer);
    }

    private static void gaussianElimination(double[][] coefficients, double rhs[]) {
        int nrVariables = coefficients.length;
        for (int i = 0; i < nrVariables; i++) {
            // Select pivot
            double pivot = coefficients[i][i];
            // Normalize row i
            for (int j = 0; j < nrVariables; j++) {
                coefficients[i][j] = coefficients[i][j] / pivot;
            }
            rhs[i] = rhs[i] / pivot;
            // Sweep using row i
            for (int k = 0; k < nrVariables; k++) {
                if (k != i) {
                    double factor = coefficients[k][i];
                    for (int j = 0; j < nrVariables; j++) {
                        coefficients[k][j] = coefficients[k][j] - factor * coefficients[i][j];
                    }
                    rhs[k] = rhs[k] - factor * rhs[i];
                }
            }
        }
    }

}
