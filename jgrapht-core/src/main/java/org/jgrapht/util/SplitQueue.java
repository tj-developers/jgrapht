package org.jgrapht.util;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * This queue uses the natural order on the natural numbers.
 */
public class SplitQueue {
    /**
     * isContained[i] == true IFF i is contained in the Queue
     */
    private boolean[] isContained;

    /**
     * previous[i] = j IFF i and j are in the queue and j is the direct predecessor of i
     *     or -1 if it is not contained in the queue or i is the first element
     */
    private int[] previous;

    /**
     * next[i] = j IFF IFF i and j are in the queue and j is the direct successor of i
     *      or -1 if i is not contained in the queue or i is the last element
     */
    private int[] next;

    /**
     * the first element of the queue
     * Equals -1 IFF queue is empty
     */
    private int first;

    /**
     * the last element of the queue
     * Equals -1 IFF queue is empty
     */
    private int last;

    private int universeSize;

    /**
     * Creates the SplitQueue containing the elements from input.
     * Runs in linear time (in the size of the input/queue, not in universeSize)
     * @param input The elements added to the queue. They have to be sorted.
     * @param universeSize The size of the universe, not of the queue/input
     */
    public SplitQueue(int[] input, int universeSize) {
        this(universeSize);

        if (input == null || input.length == 0) {
            throw new IllegalArgumentException("The input array has to be not null and not empty.");
        }
        if (input[0] < 0) {
            throw new IllegalArgumentException("The input array has to be non-negative");
        }
        // TODO check if it is sorted

        // add input
        for (int i: input) {
            addLast(i);
        }

        // handle first and last element
//        first = input[0];
//        last = input[input.length - 1];
//        previous[first] = -1;
        // next[last] is implicitly -1
    }

    public SplitQueue(int universeSize) {
        isContained = new boolean[universeSize];
        previous = new int[universeSize];
        next = new int[universeSize];
        this.universeSize = universeSize;

        // initialize arrays with -1
        for (int i = 0; i < universeSize; i++) {
            previous[i] = -1;
            next[i] = -1;
        }

        // isContained is already correctly initialized with false
    }

    /**
     * Returns and removes the lowest element in the queue
     * Throws NoSuchElementException if empty
     * Runs in constant time
     * @return the lowest element in the queue
     */
    public int poll() {
        int result = peek();
        remove(result);
        return result;
    }

    /**
     * Returns the lowest element in the queue without side effects
     * Throws NoSuchElementException if empty
     * runs in constant time
     * @return the lowest element in the queue
     */
    public int peek() {
        if(isEmpty()) {
            throw new NoSuchElementException();
        }
        return first;
    }


    public boolean isEmpty() {
        return first == -1;
    }

    /**
     * Removes the given element and keeps the data structure consistent
     * Runs in constant time
     * @param i the element to be removed
     */
    public void remove(int i) {
        inputChecker(i);
        if (!isContained[i]) {
            return;
        }
        // for h <-> i <-> j, where h = previous[i] and j = next[i]
        // we want h <-> j

        // set h -> j
        if (previous[i] != -1) { // nothing to update if it is the first element
            next[previous[i]] = next[i];
        }

        // set h <- j
        if (next[i] != -1) { // nothing to update if it is the last element
            previous[next[i]] = previous[i];
        }
        // reset i's entries
        isContained[i] = false;
        previous[i] = -1;
        next[i] = -1;
    }


    /**
     * Adds the element at the beginning of the queue.
     * If it is already contained or if it is bigger than some element in the queue,
     * the method throws an IllegalArgumentException
     * Runs in constant time
     * @param i
     */
    public void addFirst(int i) {
        inputChecker(i);

        if (i >= first) {
            throw new IllegalArgumentException("Input cannot be the first entry in the queue");
        }

        isContained[i] = true;
        next[i] = first;
        if (first != -1) {
            previous[first] = i;
        }
        first = i;

        if (last == -1) {
            last = i;
        }
    }


    /**
     * Adds the element at the end of the queue.
     * If it is already contained or if it is smaller than some element in the queue,
     * the method throws an IllegalArgumentException
     * Runs in constant time
     * @param i
     */
    private void addLast(int i) {
        inputChecker(i);

        if (i <= last) {
            throw new IllegalArgumentException("Input cannot be the last entry in the queue");
        }

        isContained[i] = true;
        // adapt "pointers, last <-> i
        previous[i] = last;
        if(last != -1) {
            next[last] = i;
        }
        last = i;

        if (first == -1) {
            first = i;
        }
    }


    private void inputChecker(int i) {
        if (i < 0 || i >= universeSize) {
            throw new IllegalArgumentException("Input not suitable for this queue in general");
        }
    }


    /**
     * Splits this queue in two: Elements in splitters are removed and transferred to a new queue,
     * the others stay.
     * Run time:
     * @param splitters have to be sorted!!!
     * @return
     */
    public LinkedList<Integer> split(int[] splitters) {
        LinkedList<Integer> out = new LinkedList<>();

        for (int i: splitters) {
            if(isContained[i]) {
                out.addLast(i);
                remove(i);
            }
        }

        return out;
    }

}
