package dailyPuzzles;

import java.util.Collection;
import java.util.HashMap;
import java.util.PriorityQueue;

import utils.Dir;
import utils.Grid;

public class Day17 {
    private record PathNode(int row, int col, Dir dir, int steps) {

    }

    public static String Solve(Grid input, int part, boolean debug) {

        if (part == 1)
            return One(input, debug);
        else if (part == 2)
            return Two(input, debug);
        else
            return (part + " is not a part");
    }

    public static String One(Grid input, boolean debug) {
        long answer = FindShortestPath(input, 0, 2);

        return answer + "";
    }

    public static String Two(Grid input, boolean debug) {
        long answer = FindShortestPath(input, 3, 9);

        return answer + "";
    }

    private static long FindShortestPath(Grid input, int minSteps, int maxSteps) {
        int nRow = input.getHeight();
        int nCol = input.getLength();

        // initaialize the queue with east and south steps, treating each as a turn
        HashMap<PathNode, Integer> visited = new HashMap<>();
        PriorityQueue<State> queue = new PriorityQueue<>();
        queue.add(new State(new PathNode(0, 1, Dir.EAST, 0), Character.getNumericValue(input.getData(0, 1)), null));
        queue.add(new State(new PathNode(1, 0, Dir.SOUTH, 0), Character.getNumericValue(input.getData(1, 0)), null));
        State currentState = null;
        while (!queue.isEmpty()) {
            currentState = queue.remove();

            // if the node was already visited, continue
            if (visited.containsKey(currentState.node)) {
                continue;
            }

            // cache the current node and cost
            visited.put(currentState.node, currentState.cost);

            // if the node is the target node, return the cost
            if (currentState.node.row == nRow - 1 && currentState.node.col == nCol - 1
                    && currentState.node.steps < maxSteps && currentState.node.steps >= minSteps) {

                return currentState.cost;
            }

            // add all possible moves to the queue
            currentState.AddMoves(queue, input, minSteps, maxSteps);
        }

        return -1;
    }

    static class State implements Comparable<State> {
        PathNode node;
        int cost;
        State prevState;

        public State(PathNode n, int c, State p) {
            node = n;
            cost = c;
            prevState = p;
        }

        @Override
        public int compareTo(State o) {
            // compare costs first, then whichever node has fewer consecutive steps, then
            // whichever is closer to the goal
            int comp = cost - o.cost;
            if (comp == 0 && node.dir == o.node.dir) {
                comp = node.steps - o.node.steps;
            }

            if (comp == 0) {
                comp = (node.col == o.node.col) ? Integer.compare(node.row, o.node.row)
                        : Integer.compare(node.col, o.node.col);
            }
            return comp;
        }

        public void FindAllMoves(Collection<State> queue, Dir d, Grid input) {
            // find moves using offset of given direction
            int row = node.row + d.rowOffset;
            int col = node.col + d.colOffset;

            // only add if in bounds
            if (row >= 0 && row < input.getHeight() && col >= 0 && col < input.getLength()) {
                // update the step value, resetting to 0 if turned
                int steps = (node.dir == d) ? (node.steps + 1) : 0;

                // add the cost of the new node
                int newCost = cost + Character.getNumericValue(input.getData(row, col));

                // add to the queue
                queue.add(new State(new PathNode(row, col, d, steps), newCost, this));
            }
        }

        public void AddMoves(Collection<State> queue, Grid input, int minSteps, int maxSteps) {
            // if the node is within max steps, go forward
            if (node.steps < maxSteps)
                FindAllMoves(queue, node.dir, input);

            // if the node is within minimum steps, turn
            if (node.steps >= minSteps) {
                FindAllMoves(queue, node.dir.left(), input);
                FindAllMoves(queue, node.dir.right(), input);
            }
        }

        @Override
        public String toString() {
            return node.row + "," + node.col + " steps: " + node.steps + " dir: " + node.dir + " current cost: " + cost;
        }

    }
}
