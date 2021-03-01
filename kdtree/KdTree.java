/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;

public class KdTree {

    private static final boolean RED = true;
    private static final boolean BLUE = false;

    private Node initial;

    // construct an empty set of points
    public KdTree() {
        initial = null;
    }

    private class Node {
        Node left;
        Node right;
        Point2D point;
        int size;
        boolean colour;

        public Node(Point2D point, boolean colour) {
            this.point = point;
            this.colour = colour;
            left = null;
            right = null;
            size = 1;
        }

        public void updateSize() {
            size = size(left) + size(right) + 1;
        }

    }

    // is the set empty?
    public boolean isEmpty() {
        return initial == null;
    }

    // number of points in the set
    public int size() {
        return size(initial);
    }

    private int size(Node node) {
        if (node == null) return 0;
        return node.size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        initial = insert(p, initial, RED);
    }

    private Node insert(Point2D p, Node node, boolean colour) {
        if (node == null) {
            return new Node(p, colour);
        }

        if (node.point.equals(p)) return node; // if already contains, then do nothing

        if (node.colour == RED) {
            if (p.x() < node.point.x())
                node.left = insert(p, node.left, BLUE);
            else node.right = insert(p, node.right, BLUE);
        }
        else { // colour == BLUE;
            if (p.y() < node.point.y())
                node.left = insert(p, node.left, RED);
            else node.right = insert(p, node.right, RED);
        }

        node.updateSize();
        return node;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        return contains(p, initial);
    }


    private boolean contains(Point2D p, Node node) {
        if (node == null) return false;

        if (node.point.equals(p)) {
            return true;
        }

        if (node.colour == RED) {
            if (p.x() < node.point.x())
                return contains(p, node.left);
            else return contains(p, node.right);
        }
        else {  // colour == BLUE;
            if (p.y() < node.point.y())
                return contains(p, node.left);
            else return contains(p, node.right);
        }
    }


    // draw all points to standard draw
    public void draw() {
        draw(initial);
    }

    private void draw(Node node) {
        if (node == null) return;
        node.point.draw();
        draw(node.left);
        draw(node.right);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        Stack<Point2D> insides = new Stack<>();
        rangeSearch(rect, initial, insides);
        return insides;
    }

    private void rangeSearch(RectHV rect, Node node, Stack<Point2D> insides) {
        if (node == null) return;

        if (rect.contains(node.point)) {
            insides.push(node.point);
        }

        if (node.colour == RED) {
            if (rect.xmin() < node.point.x())
                rangeSearch(rect, node.left, insides);
            if (rect.xmax() >= node.point.x())
                rangeSearch(rect, node.right, insides);
        }
        else {  // colour == BLUE;
            if (rect.ymin() < node.point.y())
                rangeSearch(rect, node.left, insides);
            if (rect.ymax() >= node.point.y())
                rangeSearch(rect, node.right, insides);
        }
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (isEmpty()) return null;

        Point2D nearestPoint = initial.point;
        return nearest(p, initial, nearestPoint);
    }

    private Point2D nearest(Point2D p, Node node, Point2D nearestPoint) {
        if (node == null) return nearestPoint;

        double minDistance = nearestPoint.distanceTo(p);

        if (node.point.distanceTo(p) < minDistance) {
            nearestPoint = node.point;
        }

        if (node.colour == RED) {
            if (p.x() < node.point.x()) {
                nearestPoint = nearest(p, node.left, nearestPoint);
                if (node.point.x() - p.x() < minDistance) // search only if it's possible
                    nearestPoint = nearest(p, node.right, nearestPoint);
            }
            else {
                nearestPoint = nearest(p, node.right, nearestPoint);
                if (p.x() - node.point.x() < minDistance) // search only if it's possible
                    nearestPoint = nearest(p, node.left, nearestPoint);
            }
        }
        else {  // colour == BLUE;
            if (p.y() < node.point.y()) {
                nearestPoint = nearest(p, node.left, nearestPoint);
                if (node.point.y() - p.y() < minDistance) // search only if it's possible
                    nearestPoint = nearest(p, node.right, nearestPoint);
            }
            else {
                nearestPoint = nearest(p, node.right, nearestPoint);
                if (p.y() - node.point.y() < minDistance) // search only if it's possible
                    nearestPoint = nearest(p, node.left, nearestPoint);
            }
        }

        return nearestPoint;
    }


    // private double nearestSearch(Point2D p, Node node, Point2D nearest, double minDistance) {
    //     if (node == null) return minDistance;
    //
    //     double distance = node.point.distanceTo(p);
    //     if (distance < minDistance) {
    //         nearest = node.point;
    //         return distance;
    //     }
    //
    //     if (node.colour == RED) {
    //         if (p.x() < node.point.x()) {
    //             minDistance = nearestSearch(p, node.left, nearest, minDistance);
    //             if (node.point.x() - p.x() < minDistance) // search only if it's possible
    //                 minDistance = nearestSearch(p, node.right, nearest, minDistance);
    //         }
    //         else {
    //             minDistance = nearestSearch(p, node.right, nearest, minDistance);
    //             if (p.x() - node.point.x() < minDistance) // search only if it's possible
    //                 minDistance = nearestSearch(p, node.left, nearest, minDistance);
    //         }
    //     }
    //     else {  // colour == BLUE;
    //         if (p.y() < node.point.y()) {
    //             minDistance = nearestSearch(p, node.left, nearest, minDistance);
    //             if (node.point.y() - p.y() < minDistance) // search only if it's possible
    //                 minDistance = nearestSearch(p, node.right, nearest, minDistance);
    //         }
    //         else {
    //             minDistance = nearestSearch(p, node.right, nearest, minDistance);
    //             if (p.y() - node.point.y() < minDistance) // search only if it's possible
    //                 minDistance = nearestSearch(p, node.left, nearest, minDistance);
    //         }
    //     }
    //
    //     return minDistance;
    // }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        // no test
    }
}
