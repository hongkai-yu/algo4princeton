
/* *****************************************************************************
 *  Name: Hongkai Yu
 *  Date: 23 Nov 2020
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

    private static final int R = 256;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        CharIndexSequence sequence = new CharIndexSequence();
        while (!BinaryStdIn.isEmpty())
            BinaryStdOut.write(sequence.popIndex(BinaryStdIn.readChar()));
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        IndexCharSequence sequence = new IndexCharSequence();
        while (!BinaryStdIn.isEmpty()) BinaryStdOut.write(sequence.popChar(BinaryStdIn.readChar()));
        BinaryStdOut.close();
    }

    private static class CharIndexSequence {
        // char[i] means the index for (char) i
        private char[] indices;

        public CharIndexSequence() {
            indices = new char[R];
            for (char i = 0; i < R; i++) indices[i] = i;
        }

        // move to the front and return the index
        public char popIndex(char c) {
            char index = indices[c];

            // index + 1 for all smaller indices
            for (char i = 0, numberOfUpdates = 0; i < R && numberOfUpdates < index; i++)
                if (indices[i] < index) {
                    indices[i]++;
                    numberOfUpdates++;
                }
            indices[c] = 0;
            return index;
        }
    }

    private static class IndexCharSequence {
        // char[i] means the char for index i
        private char[] chars;

        public IndexCharSequence() {
            chars = new char[R];
            for (char i = 0; i < R; i++) chars[i] = i;
        }

        // move to the front and return the char
        public char popChar(char index) {
            char c = chars[index];

            // index + 1 for all smaller indices
            for (char i = index; i > 0; i--) chars[i] = chars[i - 1];
            chars[0] = c;
            return c;
        }
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
    }

}

