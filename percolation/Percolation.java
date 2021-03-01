/* *****************************************************************************
 *  Name:              Hongkai Yu
 *  Coursera User ID:
 *  Last modified:     10 May 2020
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private boolean[][] grid; // false is blocked
    private WeightedQuickUnionUF id;
    private final int up;
    private final int down;
    private int numberOfOpenSites;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {

        if (n <= 0) throw new IllegalArgumentException();

        grid = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = false;
            }
        }

        numberOfOpenSites = 0;

        id = new WeightedQuickUnionUF(n * n + 2);
        up = 0;
        down = n * n + 1;
    }

    private int rcToPos(int row, int col) {
        return (row - 1) * grid.length + col;
    }


    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        checkRange(row, col);

        if (isOpen(row, col)) return;

        grid[row - 1][col - 1] = true;
        numberOfOpenSites++;

        // do unions
        if (row == 1)
            id.union(rcToPos(row, col), up);
        if (row == grid.length)
            id.union(rcToPos(row, col), down);
        if (row != 1 && isOpen(row - 1, col))
            id.union(rcToPos(row, col), rcToPos(row - 1, col));
        if (row != grid.length && isOpen(row + 1, col))
            id.union(rcToPos(row, col), rcToPos(row + 1, col));
        if (col != 1 && isOpen(row, col - 1))
            id.union(rcToPos(row, col), rcToPos(row, col - 1));
        if (col != grid.length && isOpen(row, col + 1))
            id.union(rcToPos(row, col), rcToPos(row, col + 1));
    }


    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        checkRange(row, col);
        return grid[row - 1][col - 1];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        checkRange(row, col);
        return id.connected(rcToPos(row, col), up);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return numberOfOpenSites;
    }

    // does the system percolate?
    public boolean percolates() {
        return id.connected(0, down);

    }

    private void checkRange(int row, int col) {
        if (row <= 0 || row > grid.length || col <= 0 || col > grid.length)
            throw new IllegalArgumentException();
    }

    // test client (optional)
    public static void main(String[] args) {
        // no client
    }
}
