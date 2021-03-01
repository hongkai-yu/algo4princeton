
/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {

    private boolean isSolvable;
    private int moves;
    private Stack<Board> solution;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException();

        MinPQ<Node> searchNodes = new MinPQ<>();
        searchNodes.insert(new Node(initial));

        MinPQ<Node> searchNodesTwin = new MinPQ<>();
        searchNodesTwin.insert(new Node(initial.twin()));

        while (true) {
            Node minNode = searchNodes.delMin();
            if (minNode.board.isGoal()) {
                solved(minNode);
                break;
            }

            for (Board b : minNode.board.neighbors()) {
                if (minNode.previous != null && b.equals(minNode.previous.board)) continue;
                searchNodes.insert(new Node(b, minNode));
            }

            Node minNodeTwin = searchNodesTwin.delMin();
            if (minNodeTwin.board.isGoal()) {
                isSolvable = false;
                break;
            }

            for (Board b : minNodeTwin.board.neighbors()) {
                if (minNodeTwin.previous != null && b.equals(minNodeTwin.previous.board)) continue;
                searchNodesTwin.insert(new Node(b, minNodeTwin));
            }
        }
    }

    private void solved(Node solvedNode) {
        isSolvable = true;
        moves = solvedNode.moves;
        solution = new Stack<>();
        Node node = solvedNode;
        do {
            solution.push(node.board);
            node = node.previous;
        } while (node != null);
    }

    private class Node implements Comparable<Node> {
        final Board board;
        final int moves;
        final Node previous;
        final int priority;

        public Node(Board initial) {
            this.board = initial;
            this.previous = null;
            this.moves = 0;
            priority = board.manhattan() + moves;
        }

        public Node(Board board, Node previous) {
            this.board = board;
            this.previous = previous;
            this.moves = previous.moves + 1;
            priority = board.manhattan() + moves;
        }

        public int compareTo(Node o) {
            return priority - o.priority;
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return isSolvable;
    }

    // min number of moves to solve initial board
    public int moves() {
        if (!isSolvable) return -1;
        return moves;
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        return solution;
    }

    // test client (see below)
    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}
