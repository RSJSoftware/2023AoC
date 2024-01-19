package dailyPuzzles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

import utils.Dir;
import utils.Grid;

public class Day23 {
    static record Coord(int row, int col) {
    }

    static record NodeConnection(Node n, int dis, Dir d) {
    }

    public static String Solve(Grid input, int part, boolean debug) {
        // find all nodes
        ArrayList<Node> nodes = FindNodes(input);

        if (part == 1)
            return One(input, nodes, debug);
        else if (part == 2)
            return Two(input, nodes, debug);
        else
            return (part + " is not a part");
    }

    public static String One(Grid input, ArrayList<Node> nodes, boolean debug) {
        long answer = 0;

        answer = getLongestPath(input, nodes, false);

        return answer + "";
    }

    public static String Two(Grid input, ArrayList<Node> nodes, boolean debug) {
        long answer = 0;

        answer = getLongestPath(input, nodes, true);

        return answer + "";
    }

    private static long getLongestPath(Grid input, ArrayList<Node> nodes, boolean ignoreSlopes) {
        long answer = 0;

        Node start = null;

        // find the starting node
        for (Node n : nodes) {
            if (n.isStart) {
                start = n;
                break;
            }
        }

        Stack<NodeState> path = new Stack<>();

        path.add(new NodeState(start, 0));

        // iterate through all possible paths
        while (!path.isEmpty()) {
            NodeState ns = path.pop();

            // if the end is reached, update the answer if necessary
            if (ns.node.isEnd) {
                answer = Math.max(ns.steps, answer);
                continue;
            }

            // add next nodes
            ns.AddSteps(input, path, ignoreSlopes);
        }

        return answer;
    }

    static ArrayList<Node> FindNodes(Grid input) {
        ArrayList<Node> nodes = new ArrayList<>();

        // hard code start and end nodes
        Node start = new Node(new Coord(0, 1));
        start.isStart = true;
        Node end = new Node(new Coord(input.getHeight() - 1, input.getLength() - 2));
        end.isEnd = true;
        nodes.add(start);
        nodes.add(end);

        Stack<Node> toCheck = new Stack<>();
        HashSet<Node> checked = new HashSet<>();

        toCheck.add(start);

        // check all nodes for connections
        while (!toCheck.isEmpty()) {
            Node checking = toCheck.pop();

            // if node has been checked, continue to the next one
            if (checked.contains(checking))
                continue;

            // cache checked node
            checked.add(checking);

            // check all directions for connections to other nodes
            checking.con[0] = findConnections(input, checking, Dir.NORTH, nodes);
            checking.con[1] = findConnections(input, checking, Dir.EAST, nodes);
            checking.con[2] = findConnections(input, checking, Dir.SOUTH, nodes);
            checking.con[3] = findConnections(input, checking, Dir.WEST, nodes);

            // add all connections to be checked
            for (int i = 0; i < 4; i++) {
                if (checking.con[i] != null)
                    toCheck.add(checking.con[i].n);
            }

        }

        return nodes;

    }

    private static NodeConnection findConnections(Grid input, Node start, Dir d, ArrayList<Node> nodes) {
        // if a connection in the current direction exists, return it
        switch (d) {
            case NORTH:
                if (start.con[0] != null)
                    return start.con[0];
                break;
            case EAST:
                if (start.con[1] != null)
                    return start.con[1];
                break;
            case SOUTH:
                if (start.con[2] != null)
                    return start.con[2];
                break;
            case WEST:
                if (start.con[3] != null)
                    return start.con[3];
                break;
            case NONE:
                System.out.println("ERROR in find connections: invalid direction");
        }

        // get the first coordinate in the direction, if it's out of bounds or a wall
        // (#) return null
        Coord c = new Coord(start.loc.row + d.rowOffset, start.loc.col + d.colOffset);
        if (c.row < 0 || c.row >= input.getHeight() || c.col < 0 || c.col >= input.getLength()
                || input.getData(c.row, c.col) == '#')
            return null;

        Stack<State> path = new Stack<>();
        Dir dir[] = { Dir.NORTH, Dir.EAST, Dir.SOUTH, Dir.WEST };
        State cur = null;
        int exists = -1;
        path.add(new State(c, 1, d));

        // iterate through paths until another node is found
        while (!path.isEmpty()) {
            cur = path.pop();

            // if the node exists in the node list already, record its index and break
            for (int i = 0; i < nodes.size(); i++) {
                Node n = nodes.get(i);
                if (n.loc.row == cur.loc.row && n.loc.col == cur.loc.col) {
                    exists = i;
                    break;
                }
            }

            if (exists > -1)
                break;

            // check surrouding tiles to see if there are more than 2 paths that are not
            // walls (#)
            int paths = 0;
            for (Dir di : dir) {
                Coord coord = new Coord(cur.loc.row + di.rowOffset, cur.loc.col + di.colOffset);
                if (input.getData(coord.row, coord.col) != '#')
                    paths++;
            }

            // if there are more than 2 paths, add this node to the node list, record its
            // index, and break
            if (paths > 2) {
                nodes.add(new Node(cur.loc));
                exists = nodes.size() - 1;
                break;
            }

            // get next step
            cur.addSteps(input, path);
        }

        // if a node was not found for some reason, return null
        if (exists == -1)
            return null;

        // set the found node's connection to the starting node
        Node finalNode = nodes.get(exists);

        NodeConnection nc = new NodeConnection(start, cur.steps, cur.dir.back());
        switch (nc.d) {
            case NORTH:
                finalNode.con[0] = nc;
                break;
            case EAST:
                finalNode.con[1] = nc;
                break;
            case SOUTH:
                finalNode.con[2] = nc;
                break;
            case WEST:
                finalNode.con[3] = nc;
                break;
            case NONE:
                break;
        }

        // return the starting node's connection to the found node
        return new NodeConnection(finalNode, cur.steps, cur.dir);
    }

    static class Node {
        Coord loc;
        NodeConnection[] con;
        boolean isEnd;
        boolean isStart;

        Node(Coord l) {
            loc = l;
            // 0 = NORTH, 1 = EAST, 2 = SOUTH, 3 = WEST
            con = new NodeConnection[4];
            con[0] = null;
            con[1] = null;
            con[2] = null;
            con[3] = null;
            isEnd = false;
            isStart = false;
        }

        @Override
        public String toString() {
            int size = 0;
            for (int i = 0; i < 4; i++) {
                if (con[i] != null)
                    size++;
            }
            return "Node: " + loc + " with " + size + " connections";
        }
    }

    static class NodeState {
        Node node;
        int steps;
        ArrayList<Node> pathRecord;

        NodeState(Node n, int s) {
            node = n;
            steps = s;

            pathRecord = new ArrayList<>();
            pathRecord.add(node);
        }

        NodeState(Node n, int s, ArrayList<Node> pr) {
            node = n;
            steps = s;
            pathRecord = new ArrayList<>();
            pathRecord.addAll(pr);
            pathRecord.add(node);
        }

        void AddSteps(Grid input, Stack<NodeState> path, boolean ignoreSlopes) {
            for (NodeConnection nc : node.con) {
                // if there are no connections, or the node has already been checked in the
                // current path, continue
                if (nc == null || pathRecord.contains(nc.n))
                    continue;

                // if slopes are not to be ignored, check the direction of the connection to
                // make sure the slope may be traveled
                if (!ignoreSlopes) {
                    Coord c = new Coord(node.loc.row + nc.d.rowOffset, node.loc.col + nc.d.colOffset);
                    char data = input.getData(c.row, c.col);

                    // if there is a slope, and it is not in the direction it needs to be in,
                    // continue
                    if (data != '.') {
                        switch (nc.d) {
                            case NORTH:
                                if (data != '^')
                                    continue;
                                break;
                            case EAST:
                                if (data != '>')
                                    continue;
                                break;
                            case SOUTH:
                                if (data != 'v')
                                    continue;
                                break;
                            case WEST:
                                if (data != '<')
                                    continue;
                                break;
                            case NONE:
                                continue;
                        }
                    }
                }

                // add the connected node, adding the distance between nodes to the total, and
                // record the path
                path.add(new NodeState(nc.n, steps + nc.dis, pathRecord));
            }
        }
    }

    static class State {
        Coord loc;
        int steps;
        Dir dir;
        ArrayList<Coord> pathRecord;

        State(Coord l, int s, Dir d) {
            loc = l;
            steps = s;
            dir = d;

            pathRecord = new ArrayList<Coord>();
            pathRecord.add(loc);
        }

        State(Coord l, int s, Dir d, ArrayList<Coord> pr) {
            loc = l;
            steps = s;
            dir = d;
            pathRecord = new ArrayList<Coord>();

            pathRecord.addAll(pr);
            pathRecord.add(loc);
        }

        void addSteps(Grid input, Stack<State> path) {
            // add a step in each direction
            addIndividualSteps(input, path, Dir.NORTH);
            addIndividualSteps(input, path, Dir.SOUTH);
            addIndividualSteps(input, path, Dir.EAST);
            addIndividualSteps(input, path, Dir.WEST);
        }

        void addIndividualSteps(Grid input, Stack<State> path, Dir d) {
            Coord newLoc = new Coord(loc.row + d.rowOffset, loc.col + d.colOffset);
            // if the direction is backwards, in the path record, or out of bounds, return
            if (newLoc.row < 0 || newLoc.row >= input.getHeight() || newLoc.col < 0
                    || newLoc.col >= input.getLength()
                    || d == dir.back() || pathRecord.contains(newLoc))
                return;

            // if the tile is a wall(#) return
            if (input.getData(newLoc.row, newLoc.col) == '#')
                return;

            // add new tile, increment step, and record new tile to record
            path.add(new State(newLoc, steps + 1, d, pathRecord));

        }

        @Override
        public String toString() {
            return loc + " at step: " + steps;
        }
    }

}
