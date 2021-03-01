
/* *****************************************************************************
 *  Name: Hongkai Yu
 *  Date: 23 Nov 2020
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class CircularSuffixArray {

    private final CircularSuffix[] suffixArray;
    private final String s;
    private final int[] indices;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();

        this.s = s;

        suffixArray = new CircularSuffix[s.length()];
        for (int i = 0; i < s.length(); i++) suffixArray[i] = new CircularSuffix(i);

        Integer[] indicesWrapper = new Integer[length()];
        for (int i = 0; i < length(); i++) indicesWrapper[i] = i;
        Arrays.sort(indicesWrapper, (o1, o2) -> suffixArray[o1].compareTo(suffixArray[o2]));

        indices = new int[length()]; // reduce memory
        for (int i = 0; i < length(); i++) indices[i] = indicesWrapper[i];
    }

    private class CircularSuffix implements Comparable<CircularSuffix> {

        private int shift;

        public CircularSuffix(int shift) {
            if (shift >= s.length()) shift %= s.length();

            this.shift = shift;
        }

        public char charAt(int index) {
            return s.charAt((index + shift) % length());
        }

        public int length() {
            return s.length();
        }

        public int compareTo(CircularSuffix other) {
            if (other == null || other.getClass() != getClass())
                throw new IllegalArgumentException();

            for (int i = 0; i < Math.min(this.length(), other.length()); i++) {
                if (this.charAt(i) < other.charAt(i)) return -1;
                else if (this.charAt(i) > other.charAt(i)) return 1;
            }

            return Integer.compare(this.length(), other.length());
        }

        public String toString() {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < length(); i++) result.append(this.charAt(i));
            return result.toString();
        }
    }

    // length of s
    public int length() {
        return suffixArray.length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= length()) throw new IllegalArgumentException();

        return indices[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        String s = "ABRACADABRA!";
        CircularSuffixArray array = new CircularSuffixArray(s);
        for (int i = 0; i < array.length(); i++) {
            StdOut.print(array.suffixArray[array.index(i)].toString());
            StdOut.print("    ");
            StdOut.println(array.index(i));
        }
    }

}

