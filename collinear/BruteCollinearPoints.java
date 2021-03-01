
/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinkedQueue;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.Comparator;

public class BruteCollinearPoints {

    private final Point[] points;
    private int numberOfSegments;
    private final LinkedQueue<LineSegment> segmentsQueue;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        if (points == null)
            throw new IllegalArgumentException();

        for (int i = 0; i < points.length; i++) {
            if (points[i] == null)
                throw new IllegalArgumentException();
            for (int j = i + 1; j < points.length; j++) {
                if (points[i].compareTo(points[j]) == 0)
                    throw new IllegalArgumentException();
            }
        }

        this.points = points.clone();
        Arrays.sort(this.points);

        segmentsQueue = new LinkedQueue<LineSegment>();
        findNumberOfSegments();
    }

    private void findNumberOfSegments() {
        numberOfSegments = 0;
        for (int i = 0; i < points.length; i++) {
            Point p = points[i];
            Comparator<Point> comparator = p.slopeOrder();
            outerloop:
            for (int j = i + 1; j < points.length; j++) {
                Point q = points[j];
                for (int k = j + 1; k < points.length; k++) {
                    Point r = points[k];
                    if (comparator.compare(q, r) == 0) {
                        for (int m = k + 1; m < points.length; m++) {
                            Point s = points[m];
                            if (comparator.compare(r, s) == 0) {
                                numberOfSegments++;
                                // endpoints[i] = s;
                                segmentsQueue.enqueue(new LineSegment(p, s));
                                break outerloop;
                            }
                        }
                    }
                }
            }
        }
    }

    // the number of line segments
    public int numberOfSegments() {
        return numberOfSegments;
    }

    // the line segments
    public LineSegment[] segments() {
        LineSegment[] segments = new LineSegment[numberOfSegments];

        int i = 0;
        for (LineSegment segment : segmentsQueue) {
            segments[i] = segment;
            i++;
        }

        return segments;
    }


    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
