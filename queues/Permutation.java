/* *****************************************************************************
 *  Name:           Hongkai Yu
 *  Date:           10 May 2020
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Permutation {
    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);
        if (k <= 0) throw new IllegalArgumentException();
        RandomizedQueue<String> queue = new RandomizedQueue<String>();

        for (int i = 0; i < k; i++) {
            String s = StdIn.readString();
            queue.enqueue(s);
        }

        for (String s : queue) {
            StdOut.println(s);
        }

    }
}
