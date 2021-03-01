
/* *****************************************************************************
 *  Name: Hongkai Yu
 *  Date: 6 Nov 2020
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class WordNet {

    private final SAP sapCalculator;

    private final String[] dictionary;
    private final Map<String, Set<Integer>> nouns;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException();
        }

        In synsetsIn = new In(synsets);
        In hypernymsIn = new In(hypernyms);

        String[] lines = synsetsIn.readAllLines();
        // number of vertexes
        int v = lines.length;

        // construct graph
        Digraph graph = new Digraph(v);

        while (hypernymsIn.hasNextLine()) {
            String[] ids = hypernymsIn.readLine().split(",");
            int fromId = Integer.parseInt(ids[0]);

            for (int i = 1; i < ids.length; i++) {
                int toId = Integer.parseInt(ids[i]);
                graph.addEdge(fromId, toId);
            }
        }

        DirectedCycle finder = new DirectedCycle(graph);
        if (finder.hasCycle()) {
            throw new IllegalArgumentException();
        }

        sapCalculator = new SAP(graph);

        // initialize dictionary and nouns, and check number of roots
        dictionary = new String[v];
        nouns = new HashMap<>();

        int numRoots = 0;
        for (int id = 0; id < v; id++) {
            if (graph.outdegree(id) == 0) {
                numRoots++;
                if (numRoots > 1) {
                    throw new IllegalArgumentException();
                }
            }

            dictionary[id] = lines[id].split(",")[1];
            for (String word : dictionary[id].split(" ")) {
                if (nouns.containsKey(word)) {
                    nouns.get(word).add(id);
                }
                else {
                    HashSet<Integer> ids = new HashSet<Integer>();
                    ids.add(id);
                    nouns.put(word, ids);
                }
            }
        }

    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nouns.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException();
        }
        return nouns.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        return sapCalculator.length(getNounIds(nounA), getNounIds(nounB));
    }

    private Iterable<Integer> getNounIds(String word) {
        if (!isNoun(word)) {
            throw new IllegalArgumentException();
        }

        return nouns.get(word);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        int idAncestor = sapCalculator.ancestor(getNounIds(nounA), getNounIds(nounB));

        return dictionary[idAncestor];
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet net = new WordNet("synsets.txt", "hypernyms.txt");
        while (!StdIn.isEmpty()) {
            String nounA = StdIn.readString();
            String nounB = StdIn.readString();
            int length = net.distance(nounA, nounB);
            String ancestor = net.sap(nounA, nounB);
            StdOut.printf("length = %d, ancestor = %s\n", length, ancestor);
        }
    }
}
