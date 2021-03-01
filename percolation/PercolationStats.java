
/* *****************************************************************************
 *  Name:              Hongkai Yu
 *  Coursera User ID:
 *  Last modified:     10 May 2020
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    private static final double CONFIDENCE_95 = 1.96;

    private final double mean;
    private final double stddev;
    private final double confidenceHi;
    private final double confidenceLo;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) throw new IllegalArgumentException();

        double[] p = new double[trials];
        for (int i = 0; i < trials; i++) {
            Percolation perc = new Percolation(n);
            while (!perc.percolates())
                randomOpen(perc, n);
            p[i] = perc.numberOfOpenSites() * 1.0 / (n * n);
        }

        mean = StdStats.mean(p);
        stddev = StdStats.stddev(p);
        confidenceLo = mean - CONFIDENCE_95 * stddev / Math.sqrt(p.length);
        confidenceHi = mean + CONFIDENCE_95 * stddev / Math.sqrt(p.length);
    }


    private void randomOpen(Percolation perc, int n) {
        int row;
        int col;
        do {
            row = StdRandom.uniform(n) + 1;
            col = StdRandom.uniform(n) + 1;
        } while (perc.isOpen(row, col));

        perc.open(row, col);
    }

    // sample mean of percolation threshold
    public double mean() {
        return mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return stddev;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return confidenceLo;
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return confidenceHi;
    }


    // test client (see below)
    public static void main(String[] args) {

        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);
        PercolationStats stats = new PercolationStats(n, trials);

        StdOut.print("Mean:         = ");
        StdOut.println(stats.mean());
        StdOut.print("Stddev:       = ");
        StdOut.println(stats.stddev());
        StdOut.print("ConfidenceLo: = ");
        StdOut.println(stats.confidenceLo());
        StdOut.print("ConfidenceHi: = ");
        StdOut.println(stats.confidenceHi());
    }
}
