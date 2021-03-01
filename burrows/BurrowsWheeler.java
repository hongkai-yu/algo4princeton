
/* *****************************************************************************
 *  Name: Hongkai Yu
 *  Date: 23 Nov 2020
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.Arrays;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String s = BinaryStdIn.readString();

        CircularSuffixArray suffixArray = new CircularSuffixArray(s);
        for (int i = 0; i < suffixArray.length(); i++)
            if (suffixArray.index(i) == 0) {
                BinaryStdOut.write(i);
                break;
            }
        for (int i = 0; i < suffixArray.length(); i++)
            BinaryStdOut.write(s.charAt((s.length() - 1 + suffixArray.index(i)) % s.length()));

        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String t = BinaryStdIn.readString();

        char[] last = t.toCharArray();
        char[] begin = t.toCharArray();
        Arrays.sort(begin);

        // construct next
        Integer[] next = new Integer[t.length()];
        for (int i = 0; i < t.length(); i++) next[i] = i;
        Arrays.sort(next, (o1, o2) -> Character.compare(last[o1], last[o2]));

        // use next
        int i = first;
        for (int j = 0; j < t.length(); j++) {
            BinaryStdOut.write(begin[i]);
            i = next[i];
        }

        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) transform();
        else if (args[0].equals("+")) inverseTransform();
    }

}

