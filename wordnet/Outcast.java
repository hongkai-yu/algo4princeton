
/* *****************************************************************************
 *  Name: Hongkai Yu
 *  Date: 6 November 2020
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

    private final WordNet net;

    public Outcast(WordNet wordnet) {         // constructor takes a WordNet object
        net = wordnet;
    }

    public String outcast(String[] nouns) {  // given an array of WordNet nouns, return an outcast
        int maxDistance = 0;
        int maxDistanceIndex = 0;

        for (int i = 0; i < nouns.length; i++) {
            int distance = 0;
            for (String noun : nouns) {
                distance += net.distance(nouns[i], noun);
            }
            if (distance >= maxDistance) {
                maxDistance = distance;
                maxDistanceIndex = i;
            }
        }

        return nouns[maxDistanceIndex];
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
