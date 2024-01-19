package dailyPuzzles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Day25 {
    static record connection(Node n1, Node n2) {
    }

    public static String Solve(ArrayList<String> input, int part, boolean debug) {
        HashMap<Node, ArrayList<String>> connections = new HashMap<>();
        HashMap<String, Node> nodes = new HashMap<>();
        Pattern p = Pattern.compile("([a-zA-Z]+)");

        HashSet<Node> allNodes = new HashSet<>();

        // extract all nodes and lists of connections
        for (String s : input) {
            Matcher m = p.matcher(s);
            ArrayList<String> connectionString = new ArrayList<>();
            Node node = null;
            if (m.find())
                node = new Node(m.group());
            while (m.find())
                connectionString.add(m.group());

            allNodes.add(node);
            nodes.put(node.name, node);
            connections.put(node, connectionString);
        }

        // make connections, adding nodes that weren't in the front spot to the total
        // node list
        for (Map.Entry<String, Node> kvp : nodes.entrySet()) {
            for (String s : connections.get(kvp.getValue())) {
                if (nodes.containsKey(s))
                    kvp.getValue().connect(nodes.get(s));
                else {
                    boolean wasFound = false;
                    for (Node n : allNodes) {
                        if (s.equals(n.name)) {
                            wasFound = true;
                            kvp.getValue().connect(n);
                            break;
                        }
                    }

                    if (!wasFound) {
                        Node newNode = new Node(s);
                        allNodes.add(newNode);
                        kvp.getValue().connect(newNode);
                    }
                }
            }
        }

        if (part == 1)
            return One(nodes, allNodes, debug);
        // else if (part == 2)
        // return Two(input, debug);
        else
            return (part + " is not a part");
    }

    public static String One(HashMap<String, Node> nodes, HashSet<Node> allNodes, boolean debug) {
        long answer = -1;

        // make a list of all connections
        ArrayList<connection> allConnect = new ArrayList<>();
        for (Node s : allNodes) {
            if (!nodes.containsKey(s.name))
                continue;
            Node node = s;
            for (Node n : node.con) {
                boolean exists = false;
                for (connection c : allConnect) {
                    if ((c.n1 == node || c.n2 == node) && (c.n1 == n || c.n2 == n)) {
                        exists = true;
                        break;
                    }
                }
                if (exists)
                    continue;

                allConnect.add(new connection(node, n));
            }
        }

        // brute force--remove 3 connections at a time until there are 2 distinct
        // groups, reconnct if there are not
        outerLoop: for (int i = 0; i < allConnect.size() - 2; i++) {
            System.out.println("Checking: " + i + " - " + (i + 2) + "/" + allConnect.size());
            for (int j = i + 1; j < allConnect.size() - 1; j++) {
                for (int k = j + 1; k < allConnect.size(); k++) {
                    connection c1 = allConnect.get(i);
                    connection c2 = allConnect.get(j);
                    connection c3 = allConnect.get(k);
                    c1.n1.removeConnect(c1.n2);
                    c2.n1.removeConnect(c2.n2);
                    c3.n1.removeConnect(c3.n2);

                    int[] sizes = checkGroups(c1, c2, c3, allNodes);
                    if (sizes[0] > -1 && sizes[1] > -1) {
                        answer = sizes[0] * sizes[1];
                        System.out.println("Found breaks: ");
                        System.out.println(c1);
                        System.out.println(c2);
                        System.out.println(c3);
                        System.out.println(allNodes.size() + " " + sizes[0] + " " + sizes[1]);
                        break outerLoop;
                    }

                    c1.n1.connect(c1.n2);
                    c2.n1.connect(c2.n2);
                    c3.n1.connect(c3.n2);
                }
            }
        }

        return answer + "";
    }

    private static int[] checkGroups(connection c1, connection c2, connection c3, HashSet<Node> allNodes) {
        int[] ret = { -1, -1 };

        Stack<Node> group1 = new Stack<>();
        Stack<Node> group2 = new Stack<>();

        group1.add(c1.n1);
        group2.add(c1.n2);

        HashSet<Node> cache1 = new HashSet<>();
        HashSet<Node> cache2 = new HashSet<>();

        // trace the connections starting from the two nodes in connection 1
        while (!group1.isEmpty() || !group2.isEmpty()) {
            Node check1 = null;
            Node check2 = null;

            // if any of the connection nodes are found in the same traced group, return
            // immediately
            if ((check1 == c2.n1 && cache1.contains(c2.n2)) ||
                    (check1 == c2.n2 && cache1.contains(c2.n1)) ||
                    (check2 == c2.n1 && cache2.contains(c2.n2)) ||
                    (check2 == c2.n2 && cache2.contains(c2.n1)) ||
                    (check1 == c3.n1 && cache1.contains(c3.n2)) ||
                    (check1 == c3.n2 && cache1.contains(c3.n1)) ||
                    (check2 == c3.n1 && cache2.contains(c3.n2)) ||
                    (check2 == c3.n2 && cache2.contains(c3.n1)))
                return ret;

            if (!group1.isEmpty())
                check1 = group1.pop();
            if (!group2.isEmpty())
                check2 = group2.pop();

            // if adding next steps fail for any reason, return immediately
            if (!checkGroups(check1, group1, cache1, cache2))
                return ret;

            if (!checkGroups(check2, group2, cache2, cache1))
                return ret;
        }

        if ((cache1.size() + cache2.size()) != allNodes.size())
            return ret;

        // return the sizes of the two groups
        ret[0] = cache1.size();
        ret[1] = cache2.size();

        return ret;
    }

    private static boolean checkGroups(Node node, Stack<Node> group, HashSet<Node> cache1, HashSet<Node> cache2) {
        // if the connection already exists in the other cache, return false
        if (node != null && !cache1.contains(node)) {
            if (cache2.contains(node))
                return false;

            // add to cache
            cache1.add(node);

            // add all connections and back connections
            for (Node n : node.con) {
                group.add(n);
            }

            for (Node n : node.invCon) {
                group.add(n);
            }
        }

        return true;
    }

    static class Node {
        String name;
        ArrayList<Node> con;
        ArrayList<Node> invCon;

        Node(String n) {
            name = n;
            con = new ArrayList<>();
            invCon = new ArrayList<>();
        }

        void connect(Node node) {
            con.add(node);
            node.invCon.add(this);
        }

        void removeConnect(Node node) {
            con.remove(node);
            node.invCon.remove(this);
        }

        @Override
        public String toString() {
            String cons = "";
            for (Node n : con) {
                cons += n.name + " ";
            }

            cons += "(";
            for (Node n : invCon) {
                cons += n.name + " ";
            }
            cons += invCon.size() > 0 ? "\b)" : ")";
            return "Node: " + name + " : " + cons;
        }
    }

}
