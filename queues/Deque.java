
/* *****************************************************************************
 *  Name: Hongkai Yu
 *  Date: 10 May 2020
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private Node<Item> first;
    private Node<Item> last;
    private int size;

    // construct an empty deque
    public Deque() {
        first = null;
        last = null;

        size = 0;
    }

    private class Node<Item> {
        private Item item;
        private Node<Item> next;
        private Node<Item> previous;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the deque
    public int size() {
        return size;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) throw new IllegalArgumentException();

        if (isEmpty()) {
            addOneFromEmpty(item);
        }
        else {
            Node<Item> oldFirst = first;
            first = new Node<Item>();
            first.item = item;
            first.next = oldFirst;
            first.previous = null;
            oldFirst.previous = first;
        }
        size++;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) throw new IllegalArgumentException();

        if (isEmpty()) {
            addOneFromEmpty(item);
        }
        else {
            Node<Item> oldLast = last;
            last = new Node<Item>();
            last.item = item;
            last.previous = oldLast;
            last.next = null;
            oldLast.next = last;
        }
        size++;

    }

    private void addOneFromEmpty(Item item) {
        Node<Item> node = new Node<Item>();
        node.item = item;
        node.next = null;
        node.previous = null;
        first = node;
        last = node;
    }


    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty()) throw new NoSuchElementException();

        if (size == 1) {
            return removeOneToEmpty();
        }
        else {
            Item item = first.item;
            first = first.next;
            first.previous = null;
            size--;
            return item;
        }

    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) throw new NoSuchElementException();
        if (size == 1) {
            return removeOneToEmpty();
        }
        else {
            Item item = last.item;
            last = last.previous;
            last.next = null;
            size--;
            return item;
        }
    }

    private Item removeOneToEmpty() {
        Item item = first.item;
        first = null;
        last = null;
        size = 0;

        return item;
    }


    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new LinkedIterator(first);
    }

    // an iterator, doesn't implement remove() since it's optional
    private class LinkedIterator implements Iterator<Item> {
        private Node<Item> current;

        public LinkedIterator(Node<Item> first) {
            current = first;
        }

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            current = current.next;
            return item;
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<Character> deque = new Deque<>();

        deque.addFirst('B');
        deque.addFirst('A');
        deque.addLast('C');
        deque.addLast('D');
        for (Character c : deque) {
            StdOut.println(c);
        }
        StdOut.println(deque.size);

        StdOut.println(deque.removeFirst());
        StdOut.println(deque.removeFirst());
        StdOut.println(deque.removeLast());
        StdOut.println(deque.removeLast());
        StdOut.println(deque.size);

    }

}
