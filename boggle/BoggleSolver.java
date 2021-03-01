/* *****************************************************************************
 *  Name: Hongkai Yu
 *  Date: 17 Nov 2020
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;
import java.util.Set;

public class BoggleSolver {

    private final TrieST26<Integer> dictionary;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        this.dictionary = new TrieST26<>();
        for (String word : dictionary)
            if (word.length() >= 3)
                this.dictionary.put(word, pointOfWord(word));
    }

    private static int pointOfWord(String word) {
        int length = word.length();

        assert length >= 3;

        if (length == 3 || length == 4) {
            return 1;
        }
        else if (length == 5) {
            return 2;
        }
        else if (length == 6) {
            return 3;
        }
        else if (length == 7) {
            return 5;
        }
        else {
            return 11;
        }
    }

    // numberOfQu, not longer in use
    // private static int numberOfQu(String word) {
    //     int numberOfQu = 0;
    //     boolean previousIsQ = false;
    //     for (int i = 0; i < word.length(); i++) {
    //         switch (word.charAt(i)) {
    //             case 'Q':
    //                 previousIsQ = true;
    //                 break;
    //             case 'U':
    //                 if (previousIsQ) numberOfQu++;
    //                 previousIsQ = false;
    //                 break;
    //             default:
    //                 previousIsQ = false;
    //                 break;
    //         }
    //     }
    //
    //     return numberOfQu;
    // }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {

        Bag<Die> diceOfBoard = new Bag<>();

        for (int row = 0; row < board.rows(); row++)
            for (int col = 0; col < board.cols(); col++)
                diceOfBoard.add(new Die(row, col, board.getLetter(row, col)));

        for (Die die : diceOfBoard)
            for (Die otherDie : diceOfBoard)
                if (!die.equals(otherDie))
                    die.connectIfAdjacent(otherDie);

        HashSet<String> validWords = new HashSet<>();
        for (Die die : diceOfBoard) dfs(die, "", new HashSet<>(), validWords, dictionary.getRoot());

        return validWords;
    }

    private class Die {

        private final int row;
        private final int col;

        private final char letter;
        private final int hash;

        private Bag<Die> adjacentDice;

        public Die(int row, int col, char letter) {

            this.row = row;
            this.col = col;
            this.letter = letter;

            hash = row * 7777 + col;

            adjacentDice = new Bag<>();
        }

        public void connectIfAdjacent(Die other) {
            if (isAdjacent(other)) adjacentDice.add(other);
        }

        private boolean isAdjacent(Die other) {
            return Math.abs(this.row - other.row) <= 1
                    && Math.abs(this.col - other.col) <= 1;
        }

        public Iterable<Die> adjacentDice() {
            return adjacentDice;
        }

        public String getLetter() {
            if (letter == 'Q') return "QU";
            else return Character.toString(letter);
        }

        public int hashCode() {
            return hash;
        }

        public boolean equals(Object obj) {
            if (obj != null && obj.getClass() == getClass()) {
                Die other = (Die) obj;
                return this.row == other.row && this.col == other.col;
            }
            else return false;
        }
    }

    private void dfs(Die die, String currentWord, Set<Die> usedDice,
                     Set<String> validWords, TrieST26.Node currentNode) {

        if (currentNode == null) return;

        if (dictionary.contains(currentNode)) validWords.add(currentWord);

        // if (!dictionary.keysWithPrefix(currentWord).iterator().hasNext()) return;

        for (Die d : die.adjacentDice()) {
            if (!usedDice.contains(d)) {
                usedDice.add(d);
                dfs(d, currentWord + d.getLetter(), usedDice, validWords,
                    dictionary.deeper(currentNode, d.getLetter()));
                usedDice.remove(d);
            }
        }
    }


    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (dictionary.contains(word)) return dictionary.get(word);
        else return 0;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        int numberOfWords = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
            numberOfWords++;
        }
        StdOut.println("Score = " + score);
        StdOut.println("Number of Words = " + numberOfWords);
    }
}
