package dailyPuzzles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Day19 {
    static record Parts(int x, int m, int a, int s) {
    }

    static record Instruction(char var, char comparitor, int num, String res) {
    }

    public static String Solve(ArrayList<String> input, int part, boolean debug) {
        Pattern namePattern = Pattern.compile("([a-z]+)\\{");
        Pattern instrPattern = Pattern.compile("([x|m|a|s])([<|>])(\\d+):([a-z]+|[R|A])");
        Pattern defPattern = Pattern.compile("([a-z]+|[R|A])\\}");
        Pattern partPattern = Pattern.compile("\\{x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)\\}");

        HashMap<String, ArrayList<Instruction>> rules = new HashMap<>();
        ArrayList<Parts> parts = new ArrayList<>();

        // use regex to extract instructions
        boolean checkParts = false;
        for (String s : input) {
            if (s.isBlank())
                checkParts = true;

            if (checkParts) {
                Matcher m = partPattern.matcher(s);

                if (m.find()) {
                    parts.add(new Parts(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4))));
                }
            } else {
                Matcher nM = namePattern.matcher(s);
                Matcher iM = instrPattern.matcher(s);
                Matcher dM = defPattern.matcher(s);

                String name = "";
                ArrayList<Instruction> instructions = new ArrayList<>();

                if (nM.find())
                    name = nM.group(1);

                while (iM.find()) {
                    instructions.add(new Instruction(iM.group(1).charAt(0), iM.group(2).charAt(0),
                            Integer.parseInt(iM.group(3)), iM.group(4)));
                }

                if (dM.find())
                    instructions.add(new Instruction('D', '.', -1, dM.group(1)));

                rules.put(name, instructions);
            }
        }

        if (part == 1)
            return One(parts, rules, debug);
        else if (part == 2)
            return Two(rules, debug);
        else
            return (part + " is not a part");
    }

    public static String One(ArrayList<Parts> parts, HashMap<String, ArrayList<Instruction>> rules, boolean debug) {
        long answer = 0;

        System.out.println(rules.get("in"));

        // if a part is accepted, add all parts to the answer
        for (Parts p : parts) {
            if (IsAccepted(p, rules, rules.get("in"))) {
                answer += p.x + p.m + p.a + p.s;
            }
        }

        return answer + "";
    }

    public static String Two(HashMap<String, ArrayList<Instruction>> rules, boolean debug) {
        double answer = 0;

        Stack<State> toCheck = new Stack<>();
        HashSet<State> verified = new HashSet<>();

        toCheck.add(new State(rules.get("in"), 0, false, false, null));

        // find all possible paths to Accepted value
        while (!toCheck.isEmpty()) {
            State currentState = toCheck.pop();

            if (!currentState.isAccepted && verified.contains(currentState))
                continue;

            // if an accepted value is found, trace the path back to the beginning
            if (currentState.isAccepted) {
                State check = currentState;

                int number = -1;

                // while tracing back find all the minimum and maximum possible part values
                // 0-3 == MaxX-MaxS; 4-7 == MinX-MinS
                double[] minsAndMax = new double[] { 4000, 4000, 4000, 4000, 1, 1, 1, 1 };
                while (check.prev != null) {
                    // find what the part value being checked was and whether or not it needs to
                    // succeed the check
                    boolean succeed = check.succeed;
                    check = check.prev;
                    Instruction i = check.ins.get(check.index);
                    switch (i.var) {
                        case 'x':
                            number = 0;
                            break;
                        case 'm':
                            number = 1;
                            break;
                        case 'a':
                            number = 2;
                            break;
                        case 's':
                            number = 3;
                            break;
                        case 'D':
                            continue;
                    }

                    // update the number based on the success of the check, the comparitor, and part
                    // being checked
                    if (i.comparitor == '<') {
                        if (succeed)
                            minsAndMax[number] = Math.min(minsAndMax[number], i.num - 1);
                        else
                            minsAndMax[number + 4] = Math.max(minsAndMax[number + 4], i.num);
                    } else {
                        if (succeed)
                            minsAndMax[number + 4] = Math.max(minsAndMax[number + 4], i.num + 1);
                        else
                            minsAndMax[number] = Math.min(minsAndMax[number], i.num);
                    }
                }

                // add the total number of unique values to the answer
                double Xdif = (minsAndMax[0] - minsAndMax[4]) + 1;
                double Mdif = (minsAndMax[1] - minsAndMax[5]) + 1;
                double Adif = (minsAndMax[2] - minsAndMax[6]) + 1;
                double Sdif = (minsAndMax[3] - minsAndMax[7]) + 1;
                answer += (Xdif * Mdif * Adif * Sdif);
                continue;
            }
            // update varified hash list
            verified.add(currentState);

            // get next states
            currentState.GetNextState(rules, toCheck);
        }

        return String.format("%14.0f", answer);
    }

    private static boolean IsAccepted(Parts p, HashMap<String, ArrayList<Instruction>> rules,
            ArrayList<Instruction> start) {
        ArrayList<Instruction> currentRules = start;
        int ruleIndex = 0;
        Instruction rule = currentRules.get(ruleIndex);

        boolean resolved = false;

        while (!resolved) {
            rule = currentRules.get(ruleIndex);
            int checkingPart = 0;
            boolean isDef = false;

            switch (rule.var) {
                case 'x':
                    checkingPart = p.x;
                    break;
                case 'm':
                    checkingPart = p.m;
                    break;
                case 'a':
                    checkingPart = p.a;
                    break;
                case 's':
                    checkingPart = p.s;
                    break;
                case 'D':
                    isDef = true;
                    if (rule.res.equals("A") || rule.res.equals("R"))
                        resolved = true;
                    else {
                        currentRules = rules.get(rule.res);
                        ruleIndex = 0;
                    }
                    break;
            }

            if (isDef)
                continue;

            boolean isTrue = (rule.comparitor == '<') ? checkingPart < rule.num : checkingPart > rule.num;

            if (isTrue) {
                if (rule.res.equals("A") || rule.res.equals("R"))
                    resolved = true;
                else {
                    currentRules = rules.get(rule.res);
                    ruleIndex = 0;
                }
            } else {
                ruleIndex++;
            }

        }

        return rule.res.equals("A");
    }

    static class State {
        ArrayList<Instruction> ins;
        int index;
        boolean isAccepted;
        boolean succeed;
        State prev;

        public State(ArrayList<Instruction> i, int in, boolean a, boolean s, State p) {
            ins = i;
            index = in;
            isAccepted = a;
            succeed = s;
            prev = p;
        }

        public void GetNextState(HashMap<String, ArrayList<Instruction>> rules, Stack<State> toCheck) {
            // check the result in the instruction to decide if a path needs to be ignored
            // ('R') otherwise add the compare success value and fail value (if not default)
            String res = ins.get(index).res;
            if (res.equals("A"))
                toCheck.add(new State(ins, index, true, true, this));
            else if (!res.equals("R"))
                toCheck.add(new State(rules.get(res), 0, false, true, this));

            if (ins.get(index).var != 'D')
                toCheck.add(new State(ins, index + 1, false, false, this));
        }

        @Override
        public String toString() {
            return " (" + ins.get(index) + ") " + isAccepted;
        }
    }
}
