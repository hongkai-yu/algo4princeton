/* *****************************************************************************
 *  Name: Hongkai Yu
 *  Date: 7 November 2020
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DynamicRingBFS {

    private boolean[] marked;
    private int[] edgeTo;

    private int currentRadius;
    private Set<Integer> currentRing;
    private final Digraph graph;

    private ArrayList<Set<Integer>> cache;

    private DynamicRingBFS(Digraph graph) { // basic setup
        this.graph = graph;
        currentRadius = 0;
        marked = new boolean[graph.V()];
        edgeTo = new int[graph.V()];
        currentRing = new HashSet<>();

        cache = new ArrayList<>();
    }

    public DynamicRingBFS(Digraph graph, int s) {
        this(graph);

        validateVertex(s);

        currentRing.add(s);
        marked[s] = true;

        cache.add(currentRing);
    }

    public DynamicRingBFS(Digraph graph, Iterable<Integer> sources) {
        this(graph);

        validateVertices(sources);

        for (int s : sources) {
            currentRing.add(s);
            marked[s] = true;
        }

        cache.add(currentRing);
    }

    public Set<Integer> getRing(int radius) {
        if (radius < 0) {
            throw new IllegalArgumentException();
        }

        if (radius <= currentRadius) {
            return cache.get(radius);
        }
        else {
            if (isExpansionFinished()) {
                return new HashSet<>();
            }

            if (expand()) {
                return getRing(radius);
            }
            else {
                return new HashSet<>(); // empty set; signaling that there is no more to reach
            }
        }

    }

    private boolean expand() { // return value: whether the expansion is successful
        HashSet<Integer> nextRing = new HashSet<>();
        for (int v : currentRing) {
            for (int w : graph.adj(v)) {
                if (marked[w]) {
                    continue;
                }
                marked[w] = true;
                nextRing.add(w);
                edgeTo[w] = v;
            }
        }

        currentRing = nextRing;

        if (currentRing.isEmpty()) {
            return false;
        }
        else {
            currentRadius++;
            cache.add(currentRing);
            return true;
        }
    }

    public int getEdgeTo(int v) {
        return edgeTo[v];
    }

    public int getCurrentRadius() {
        return currentRadius;
    }

    public boolean isExpansionFinished() {
        return currentRing.isEmpty();
    }

    // validation copied from https://algs4.cs.princeton.edu/41graph/BreadthFirstPaths.java.html
    private void validateVertex(int v) {
        int length = graph.V();
        if (v < 0 || v >= length)
            throw new IllegalArgumentException(
                    "vertex " + v + " is not between 0 and " + (length - 1));
    }

    private void validateVertices(Iterable<Integer> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("argument is null");
        }
        for (Object v : vertices) {
            if (v == null) {
                throw new IllegalArgumentException("vertex is null");
            }
            validateVertex((int) v);
        }
    }
}
