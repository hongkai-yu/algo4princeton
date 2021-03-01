/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinkedQueue;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.Comparator;

public class FastCollinearPointsLoneline {
    private Point[] points;
    private int numberOfSegments;
    private LinkedQueue<LineSegment> segmentsQueue;
    private Bag<LongLineSegment> longLineSegments;

    // finds all line segments containing 4 points
    public FastCollinearPointsLoneline(Point[] points) {
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

        longLineSegments = new Bag<>();
        findNumberOfSegments();
    }

    private void findNumberOfSegments() {
        numberOfSegments = 0;
        for (int i = 0; i < points.length; i++) {
            Point p = points[i];
            Comparator<Point> comparator = p.slopeOrder();

            Point[] remainingPoints = Arrays.copyOfRange(points, i + 1, points.length);
            Arrays.sort(remainingPoints, comparator);
            int remainingPointsLength = points.length - (i + 1);

            int recorder = 1;
            for (int j = 1; j < remainingPointsLength; j++) {
                if (comparator.compare(remainingPoints[j], (remainingPoints[j - 1])) == 0)
                    recorder++;
                else {
                    if (recorder >= 3) {
                        LongLineSegment line = new LongLineSegment(p);
                        for (int k = j; k > j - recorder; k--) {
                            line.add(remainingPoints[k]);
                        }
                        boolean isSubset = false;
                        for (LongLineSegment longLine : longLineSegments) {
                            if (longLine.contains(line)) {
                                isSubset = true;
                            }
                        }
                        if (!isSubset) {
                            longLineSegments.add(line);
                            numberOfSegments++;
                        }
                    }
                    recorder = 1;
                }
            }

            if (recorder >= 3) {
                LongLineSegment line = new LongLineSegment(p);
                int j = remainingPointsLength - 1;
                for (int k = j; k > j - recorder; k--) {
                    line.add(remainingPoints[k]);
                }
                boolean isSubset = false;
                for (LongLineSegment longLine : longLineSegments) {
                    if (longLine.contains(line)) {
                        isSubset = true;
                    }
                }
                if (!isSubset) {
                    longLineSegments.add(line);
                    numberOfSegments++;
                }
            }
        }
    }

    private class LongLineSegment {
        private LinkedQueue<Point> points;
        private Point origin;

        public LongLineSegment(Point origin) {
            points = new LinkedQueue<Point>();
            this.origin = origin;
        }

        public void add(Point point) {
            points.enqueue(point);
        }

        public boolean contains(LongLineSegment that) {
            if (!that.getEnd().equals(getEnd())) {
                return false;
            }


            for (Point p : points) {
                if (that.getOrigin().equals(p)) {
                    return true;
                }
            }
            return false;
        }

        public Point getOrigin() {
            return origin;
        }

        public Point getEnd() {
            return points.peek();
        }

        public LineSegment deliverSegment() {
            return new LineSegment(getOrigin(), getEnd());
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
        for (LongLineSegment longLine : longLineSegments) {
            segments[i] = longLine.deliverSegment();
            i++;
        }
        return segments.clone();
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
        FastCollinearPointsLoneline collinear = new FastCollinearPointsLoneline(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
