
/* *****************************************************************************
 *  Name: Hongkai Yu
 *  Date: 10 May 2020
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {

    private Item[] q;       // queue elements
    private int n;          // number of elements on queue
    private int first;      // index of first element of queue
    private int last;       // index of next available slot

    private boolean[] holes; // true if there is a hole (already taken)


    // construct an empty randomized queue
    public RandomizedQueue() {
        q = (Item[]) new Object[2];
        n = 0;
        first = 0;
        last = 0;

        holes = new boolean[2];
        setNoHoles();
    }

    private void setNoHoles() {
        for (int i = 0; i < holes.length; i++)
            holes[i] = false;
    }


    // is the randomized queue empty?
    public boolean isEmpty() {
        return n == 0;
    }


    // return the number of items on the randomized queue
    public int size() {
        return n;
    }

    // resize the underlying array
    private void resize(int capacity) {
        assert capacity >= n;
        Item[] copy = (Item[]) new Object[capacity];

        int i = 0;
        for (int cursor = first; cursor < last; cursor++) {
            if (!holes[cursor]) {
                copy[i] = q[cursor];
                i++;
            }
        }
        q = copy;
        first = 0;
        last = n;

        holes = new boolean[capacity];
        setNoHoles();
    }


    public void enqueue(Item item) {
        // double size of array if necessary and recopy to front of array
        if (n == q.length) resize(2 * q.length);   // double size of array if necessary
        q[last++] = item;                        // add item
        // if (last == q.length) last = 0;          // wrap-around
        n++;
    }


    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Queue underflow");

        int index;
        do {
            index = StdRandom.uniform(last - first) + first;
        } while (holes[index]);

        Item item = q[index];
        q[index] = null;                            // to avoid loitering
        holes[index] = true;
        n--;
        if (index == first) first++;
        // if (first == q.length) first = 0;           // wrap-around
        // shrink size of array if necessary
        if (n > 0 && n == q.length / 4) resize(q.length / 2);
        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) throw new NoSuchElementException("Queue underflow");
        int index;
        do {
            index = StdRandom.uniform(last - first) + first;
        } while (holes[index]);
        return q[index];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new ArrayIterator();
    }

    // an iterator, doesn't implement remove() since it's optional
    private class ArrayIterator implements Iterator<Item> {
        private int i = 0;
        private boolean[] iterated;

        public ArrayIterator() {
            iterated = new boolean[q.length];
            for (int j = 0; j < iterated.length; j++)
                iterated[j] = false;
        }


        public boolean hasNext() {
            return i < n;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            int index;
            do {
                index = StdRandom.uniform(last - first) + first;

            } while (holes[index] || iterated[index]);
            i++;
            iterated[index] = true;
            return q[index];
        }
    }


    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<Character> queue = new RandomizedQueue<>();

        queue.enqueue('B');
        queue.enqueue('A');
        queue.enqueue('C');
        queue.enqueue('D');
        for (Character c : queue) {
            StdOut.println(c);
        }
        StdOut.println(queue.size());

        StdOut.println(queue.sample());
        StdOut.println(queue.dequeue());
        StdOut.println(queue.dequeue());
        StdOut.println(queue.dequeue());
        StdOut.println(queue.dequeue());
        StdOut.println(queue.size());

    }


}

