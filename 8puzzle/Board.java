
/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.Objects;

public class Board {

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)

    private static final int BLANK = 0;

    private int[][] tiles;
    private final int dimension;

    private Position blankPosition;

    public Board(int[][] tiles) {
        dimension = tiles.length;
        this.tiles = new int[dimension][dimension];

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                this.tiles[i][j] = tiles[i][j];
                if (tiles[i][j] == BLANK) blankPosition = new Position(i, j);
            }
        }
    }

    // string representation of this board
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(dimension);
        s.append('\n');
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                s.append(' ');
                s.append(tiles[i][j]);
            }
            s.append('\n');
        }
        return s.toString();
    }

    // board dimension n
    public int dimension() {
        return dimension;
    }

    private Board duplicate() {
        return new Board(tiles);
    }

    private class Position {
        public final int row;
        public final int col;

        public Position(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public Queue<Position> neighborPositions() {
            Queue<Position> queue = new Queue<>();
            if (row != 0) queue.enqueue(new Position(row - 1, col));
            if (row != dimension - 1) queue.enqueue(new Position(row + 1, col));
            if (col != 0) queue.enqueue(new Position(row, col - 1));
            if (col != dimension - 1) queue.enqueue(new Position(row, col + 1));
            return queue;
        }

        public int hashCode() {
            return Objects.hash(row, col);
        }

        public String toString() {
            return "Position{" +
                    "row=" + row +
                    ", col=" + col +
                    '}';
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return row == position.row &&
                    col == position.col;
        }
    }

    private Position goalPosition(int label) {
        int row;
        int col;
        if (label == BLANK) {
            row = dimension - 1;
            col = dimension - 1;
        }
        else {
            row = (label - 1) / dimension;
            col = (label - 1) % dimension;
        }

        return new Position(row, col);
    }

    // number of tiles out of place
    public int hamming() {
        int result = 0;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                int label = tiles[i][j];
                if (label == BLANK) continue;
                Position goal = goalPosition(label);
                if (i != goal.row || j != goal.col) result++;
            }
        }
        return result;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int result = 0;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                int label = tiles[i][j];
                if (label == BLANK) continue;
                Position goal = goalPosition(label);
                result += Math.abs(i - goal.row) + Math.abs(j - goal.col);
            }
        }
        return result;
    }

    // is this board the goal board?
    public boolean isGoal() {
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                int label = tiles[i][j];
                Position goal = goalPosition(label);
                if (i != goal.row || j != goal.col) return false;
            }
        }
        return true;
    }

    // does this board equal y?
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return dimension == board.dimension &&
                Arrays.equals(tiles, board.tiles);
    }


    private Board swap(Position p1, Position p2) {
        int tmp = tiles[p1.row][p1.col];
        tiles[p1.row][p1.col] = tiles[p2.row][p2.col];
        tiles[p2.row][p2.col] = tmp;

        if (p1.equals(blankPosition)) blankPosition = p2;
        else if (p2.equals(blankPosition)) blankPosition = p1;

        return this;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Queue<Board> boards = new Queue<>();
        for (Position p : blankPosition.neighborPositions()) {
            boards.enqueue(duplicate().swap(blankPosition, p));
        }
        return boards;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        if (dimension == 1) throw new UnsupportedOperationException();
        Position p1 = new Position(0, 0);
        Position p2 = new Position(0, 1);
        Position p3 = new Position(1, 0);
        if (p1.equals(blankPosition)) return duplicate().swap(p2, p3);
        else if (p2.equals(blankPosition)) return duplicate().swap(p1, p3);
        else return duplicate().swap(p1, p2);
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        Board board = new Board(new int[][] { { 8, 1, 3 }, { 4, 0, 2 }, { 7, 6, 5 } });
        for (Board b : board.neighbors()) {
            StdOut.println("----------------");
            StdOut.println(b);
            StdOut.println(b.blankPosition);
            for (Board bb : b.neighbors()) {
                StdOut.println(bb);
            }
        }
    }
}
