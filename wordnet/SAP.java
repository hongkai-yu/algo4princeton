/* *****************************************************************************
 *  Name: Hongkai Yu
 *  Date: 6 November 2020
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;
import java.util.Set;

public class SAP {

    private final Digraph graph;

    private Iterable<Integer> cacheV;
    private Iterable<Integer> cacheW;
    private AncestorLength cacheResult;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        graph = new Digraph(G);
    }


    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        Set<Integer> setV = new HashSet<>();
        setV.add(v);

        Set<Integer> setW = new HashSet<>();
        setW.add(w);

        return length(setV, setW);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        Set<Integer> setV = new HashSet<>();
        setV.add(v);

        Set<Integer> setW = new HashSet<>();
        setW.add(w);

        return ancestor(setV, setW);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        calculate(v, w);
        return cacheResult.getLength();
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        calculate(v, w);
        return cacheResult.getAncestor();
    }

    private void calculate(Iterable<Integer> v, Iterable<Integer> w) {

        if (isCache(v, w)) { // just calculated
            return;
        }

        cacheV = v;
        cacheW = w;

        DynamicRingBFS bfsV = new DynamicRingBFS(graph, v);
        DynamicRingBFS bfsW = new DynamicRingBFS(graph, w);

        for (int length = 0; !bfsV.isExpansionFinished()
                || !bfsW.isExpansionFinished()
                || length <= bfsV.getCurrentRadius() + bfsW.getCurrentRadius();
             length++) {
            for (int radiusV = 0; radiusV <= length; radiusV++) {
                int radiusW = length - radiusV;

                // Set<Integer> intersection = bfsV.getRing(radiusV);
                // intersection.retainAll(bfsW.getRing(radiusW));

                Set<Integer> ringV = bfsV.getRing(radiusV);
                Set<Integer> ringW = bfsW.getRing(radiusW);

                Set<Integer> intersection = new HashSet<>(ringV);
                intersection.retainAll(ringW);

                if (!intersection.isEmpty()) {
                    int ancestor = intersection.iterator().next(); // pick an ancestor
                    cacheResult = new AncestorLength(ancestor, length);
                    return;
                }

            }
        }
        // didn't find any solution
        cacheResult = new AncestorLength(-1, -1);
    }

    private boolean isCache(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException();
        }
        return (v.equals(cacheV) && w.equals(cacheW))
                || (v.equals(cacheW) && w.equals(cacheV));

    }

    private class AncestorLength {
        private final int ancestor;
        private final int length;

        public AncestorLength(int ancestor, int length) {
            this.ancestor = ancestor;
            this.length = length;
        }

        public int getLength() {
            return length;
        }

        public int getAncestor() {
            return ancestor;
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}


