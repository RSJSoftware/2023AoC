package dailyPuzzles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Pattern;

import utils.AoCMath;

import java.util.regex.Matcher;

public class Day20 {
    static record PulseRec(String m, Pulse p, String ori) {
    }

    public static String Solve(ArrayList<String> input, int part, boolean debug) {
        Pattern p = Pattern.compile("([a-z]+)");

        // get inputs using regex
        HashMap<String, Module> modules = new HashMap<>();
        for (String s : input) {
            Matcher m = p.matcher(s);

            String name = "";
            if (m.find())
                name = m.group();

            ArrayList<String> outputs = new ArrayList<String>();

            while (m.find())
                outputs.add(m.group());

            switch (s.charAt(0)) {
                case '%':
                    modules.put(name, new FlipFlop(name, outputs));
                    break;
                case '&':
                    modules.put(name, new Conj(name, outputs));
                    break;
                case 'b':
                    modules.put(name, new Module(name, outputs));
                    break;
                default:
                    System.out.println("unexpected first char: " + s.charAt(0));

            }
        }

        // connect all inputs
        for (Map.Entry<String, Module> mod : modules.entrySet()) {
            Module m = mod.getValue();

            for (String s : m.outMods) {
                if (modules.containsKey(s)) {
                    modules.get(s).AddInput(m.name);
                }
            }
        }

        // initialize all connections to conjunction modules
        for (Map.Entry<String, Module> mod : modules.entrySet()) {
            if (!(mod.getValue() instanceof Conj))
                continue;

            Conj m = (Conj) mod.getValue();
            m.InitializeConnections();
        }

        if (part == 1)
            return One(modules, debug);
        else if (part == 2)
            return Two(modules, debug);
        else
            return (part + " is not a part");
    }

    public static String One(HashMap<String, Module> modules, boolean debug) {
        long answer = 0;

        long lowPulses = 0;
        long highPulses = 0;

        int loopPress = -1;

        Queue<PulseRec> queue = new LinkedList<>();

        for (int i = 0; i < 1000; i++) {
            queue.add(new PulseRec("broadcaster", Pulse.LOW, "Button"));
            lowPulses++;

            while (!queue.isEmpty()) {
                PulseRec check = queue.remove();

                if (check.p == Pulse.NONE || !modules.containsKey(check.m))
                    continue;

                Module mod = modules.get(check.m);
                Pulse p = mod.ReceivePulse(check.p, check.ori);

                if (p == Pulse.HIGH)
                    highPulses += mod.outMods.size();
                else if (p == Pulse.LOW)
                    lowPulses += mod.outMods.size();
                else if (p == Pulse.NONE)
                    continue;
                mod.AddPulse(queue, p);
            }

            boolean baseState = true;
            for (Map.Entry<String, Module> mod : modules.entrySet()) {
                if (!mod.getValue().IsBaseState()) {
                    baseState = false;
                    break;
                }
            }

            if (baseState) {
                System.out.println("Loop Found! " + (i + 1));
                loopPress = i + 1;
                break;
            }
        }
        System.out.println(lowPulses + " " + highPulses);

        if (loopPress > -1) {
            int remainingLoops = 1000 % loopPress;
            int moreLoops = 1000 / loopPress;

            highPulses *= moreLoops;
            lowPulses *= moreLoops;

            for (int i = 0; i < remainingLoops; i++) {
                queue.add(new PulseRec("broadcaster", Pulse.LOW, "Button"));
                lowPulses++;

                while (!queue.isEmpty()) {
                    PulseRec check = queue.remove();

                    if (check.p == Pulse.NONE || !modules.containsKey(check.m))
                        continue;

                    Module mod = modules.get(check.m);
                    Pulse p = mod.ReceivePulse(check.p, check.ori);

                    // System.out.println("check: " + check + " generated pulse: " + p);

                    if (p == Pulse.HIGH)
                        highPulses += mod.outMods.size();
                    else if (p == Pulse.LOW)
                        lowPulses += mod.outMods.size();
                    else if (p == Pulse.NONE)
                        continue;
                    mod.AddPulse(queue, p);
                }
            }
        }

        System.out.println(lowPulses + " " + highPulses);

        answer = lowPulses * highPulses;

        return answer + "";
    }

    public static String Two(HashMap<String, Module> modules, boolean debug) {
        double answer = 0;

        Queue<PulseRec> queue = new LinkedList<>();

        boolean isFound = false;

        long DSLoop = -1;
        long CSLoop = -1;
        long BDLoop = -1;
        long DTLoop = -1;

        while (!isFound) {
            answer++;
            queue.add(new PulseRec("broadcaster", Pulse.LOW, "Button"));

            while (!queue.isEmpty()) {
                PulseRec check = queue.remove();
                if (check.m.equals("rx") && check.p == Pulse.LOW)
                    return String.format("%14.0f", answer);

                if (check.p == Pulse.NONE || !modules.containsKey(check.m))
                    continue;

                Module mod = modules.get(check.m);
                Pulse p = mod.ReceivePulse(check.p, check.ori);

                if (p == Pulse.NONE)
                    continue;
                mod.AddPulse(queue, p);
            }

            if (DSLoop == -1 && DSBase(modules)) {
                DSLoop = (int) (answer);
                System.out.println("DS loop: " + answer);
            }
            if (CSLoop == -1 && CSBase(modules)) {
                CSLoop = (int) (answer);
                System.out.println("CS loop: " + answer);
            }
            if (BDLoop == -1 && BDBase(modules)) {
                BDLoop = (int) (answer);
                System.out.println("BD loop: " + answer);
            }
            if (DTLoop == -1 && DTBase(modules)) {
                DTLoop = (int) (answer);
                System.out.println("DT loop: " + answer);
            }

            if (DSLoop > -1 && CSLoop > -1 && BDLoop > -1 && DTLoop > -1)
                break;
        }

        ArrayList<Long> loops = new ArrayList<Long>();
        loops.add(DSLoop);
        loops.add(CSLoop);
        loops.add(BDLoop);
        loops.add(DTLoop);

        answer = AoCMath.lcm(loops);

        return String.format("%14.0f", answer);
    }

    private static boolean DSBase(HashMap<String, Module> modules) {
        return modules.get("jk").IsBaseState() &&
                modules.get("qs").IsBaseState() &&
                modules.get("db").IsBaseState() &&
                modules.get("qg").IsBaseState() &&
                modules.get("ls").IsBaseState() &&
                modules.get("ng").IsBaseState() &&
                modules.get("ft").IsBaseState() &&
                modules.get("dz").IsBaseState() &&
                modules.get("fg").IsBaseState() &&
                modules.get("xz").IsBaseState() &&
                modules.get("sx").IsBaseState() &&
                modules.get("lm").IsBaseState() &&
                modules.get("ds").IsBaseState();
    }

    private static boolean CSBase(HashMap<String, Module> modules) {
        return modules.get("sb").IsBaseState() &&
                modules.get("lp").IsBaseState() &&
                modules.get("sh").IsBaseState() &&
                modules.get("kn").IsBaseState() &&
                modules.get("jc").IsBaseState() &&
                modules.get("zf").IsBaseState() &&
                modules.get("lh").IsBaseState() &&
                modules.get("kd").IsBaseState() &&
                modules.get("jg").IsBaseState() &&
                modules.get("bj").IsBaseState() &&
                modules.get("fp").IsBaseState() &&
                modules.get("bk").IsBaseState() &&
                modules.get("cs").IsBaseState();
    }

    private static boolean BDBase(HashMap<String, Module> modules) {
        return modules.get("dc").IsBaseState() &&
                modules.get("nx").IsBaseState() &&
                modules.get("qr").IsBaseState() &&
                modules.get("pz").IsBaseState() &&
                modules.get("rv").IsBaseState() &&
                modules.get("mp").IsBaseState() &&
                modules.get("cj").IsBaseState() &&
                modules.get("pg").IsBaseState() &&
                modules.get("df").IsBaseState() &&
                modules.get("rs").IsBaseState() &&
                modules.get("gq").IsBaseState() &&
                modules.get("vx").IsBaseState() &&
                modules.get("bd").IsBaseState();
    }

    private static boolean DTBase(HashMap<String, Module> modules) {
        return modules.get("mg").IsBaseState() &&
                modules.get("fj").IsBaseState() &&
                modules.get("tr").IsBaseState() &&
                modules.get("bx").IsBaseState() &&
                modules.get("qb").IsBaseState() &&
                modules.get("qm").IsBaseState() &&
                modules.get("ll").IsBaseState() &&
                modules.get("zb").IsBaseState() &&
                modules.get("gz").IsBaseState() &&
                modules.get("dx").IsBaseState() &&
                modules.get("bv").IsBaseState() &&
                modules.get("bs").IsBaseState() &&
                modules.get("dt").IsBaseState();
    }

    static class Module {
        String name;
        ArrayList<String> outMods;
        ArrayList<String> inMods;

        public Module(String n, ArrayList<String> o) {
            name = n;
            outMods = o;
            inMods = new ArrayList<>();
        }

        public void AddInput(String n) {
            inMods.add(n);
        }

        public void AddPulse(Collection<PulseRec> queue, Pulse p) {
            if (p == Pulse.NONE)
                return;
            for (String m : outMods) {
                // System.out.println(name + " -" + p + "-> " + m);
                queue.add(new PulseRec(m, p, name));
            }
        }

        public Pulse ReceivePulse(Pulse p, String n) {
            // System.out.println("Receive pulse not defined");
            return p;
        }

        public boolean IsBaseState() {
            // System.out.println("Is base state not defined");
            return true;
        }

        @Override
        public String toString() {
            return name + " out: " + outMods;
        }
    }

    static class FlipFlop extends Module {
        boolean power;

        public FlipFlop(String n, ArrayList<String> o) {
            super(n, o);
            power = false;
        }

        @Override
        public Pulse ReceivePulse(Pulse p, String n) {
            if (p == Pulse.HIGH || p == Pulse.NONE)
                return Pulse.NONE;

            power = !power;
            if (power)
                return Pulse.HIGH;
            else
                return Pulse.LOW;
        }

        @Override
        public boolean IsBaseState() {
            return !power;
        }

        @Override
        public String toString() {
            return "Flipflop " + super.toString() + " state: " + power;
        }
    }

    static class Conj extends Module {
        HashMap<String, Pulse> connect;

        public Conj(String n, ArrayList<String> o) {
            super(n, o);
            connect = new HashMap<>();
        }

        public void InitializeConnections() {
            for (String n : inMods) {
                connect.put(n, Pulse.LOW);
            }
        }

        @Override
        public Pulse ReceivePulse(Pulse p, String n) {
            if (p == Pulse.NONE)
                return Pulse.NONE;
            connect.put(n, p);

            for (Map.Entry<String, Pulse> connection : connect.entrySet()) {
                if (connection.getValue() == Pulse.LOW)
                    return Pulse.HIGH;
            }

            return Pulse.LOW;
        }

        @Override
        public boolean IsBaseState() {
            for (Map.Entry<String, Pulse> connection : connect.entrySet()) {
                if (connection.getValue() == Pulse.HIGH)
                    return false;
            }

            return true;
        }

        @Override
        public String toString() {
            return "Conj " + super.toString() + " connections: " + connect;
        }
    }

    enum Pulse {
        HIGH,
        LOW,
        NONE;
    }
}
