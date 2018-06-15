package org.jgrapht.util;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class SuperSplitQueue {
    /**
     * containedIn[i] == k IFF i is contained in queue k
     * init with 0
     */
    private int[] containedIn;

    /**
     * previous[i] = j IFF i and j are in the same queue and j is the direct predecessor of i
     *     or -1 if it is not contained in any queue or i is the first element in a queue
     */
    private int[] previous;

    /**
     * next[i] = j IFF IFF i and j are in the same queue and j is the direct successor of i
     *      or -1 if i is not contained in the queue or i is the last element in a queue
     */
    private int[] next;

    private ArrayList<Integer> firstOfQ;

    private ArrayList<Integer> lastOfQ;

    final private int universeSize;

    private int amountQueues;

    public SuperSplitQueue(int universeSize) {
        this.universeSize = universeSize;

        containedIn = new int[universeSize];
        previous = new int[universeSize];
        next = new int[universeSize];

        amountQueues = 1;

        firstOfQ = new ArrayList<>();
        lastOfQ = new ArrayList<>();
        firstOfQ.set(0, 0);
        lastOfQ.set(0, universeSize - 1);

        // initialize arrays with -1
        for (int i = 0; i < universeSize; i++) {
            previous[i] = -1;
            next[i] = -1;
            // containedIn init with 0
        }
    }


    private SubSplitQueue getNewSubSplitQueue() {
        SubSplitQueue result = new SubSplitQueue(amountQueues, this);

        firstOfQ.add(amountQueues);
        lastOfQ.add(amountQueues);

        return result;
    }


    public int peek(int queueIndex) {
        if(isEmpty(queueIndex)) {
            throw new NoSuchElementException();
        }
        return firstOfQ.get(queueIndex);
    }


    public int poll(int queueIndex) {
        int result = peek(queueIndex);
        remove(result, queueIndex);
        return result;
    }


    public boolean isEmpty(int queueIndex) {
        return firstOfQ.get(queueIndex) == -1;
    }


    /**
     * Removes the given element and keeps the data structure consistent
     * Runs in constant time
     * @param element the element to be removed
     */
    public void remove(int element, int queueIndex) {
        inputChecker(element, queueIndex);
        if (containedIn[element] != queueIndex) {
            throw new NoSuchElementException("Element not in specified queue");
        }
        // for h <-> element <-> j, where h = previous[element] and j = next[element]
        // we want h <-> j

        // set h -> j
        if (previous[element] != -1) { // nothing to update if it is the first element
            next[previous[element]] = next[element];
        }

        // set h <- j
        if (next[element] != -1) { // nothing to update if it is the last element
            previous[next[element]] = previous[element];
        }
        // reset element's entries
        containedIn[element] = -1;
        previous[element] = -1;
        next[element] = -1;
    }


    /**
     * Splits this queue in two: Elements in splitters are removed and transferred to a new queue,
     * the others stay.
     * Run time:
     * @param splitters HAVE TO BE SORTED
     * @return the index of the new queue
     */
    public SubSplitQueue split(int[] splitters, int queue) {
        SubSplitQueue newQueue = getNewSubSplitQueue();

        for (int i: splitters) {
            if(containedIn[i] == queue) {
                moveElementTo(i, newQueue.getOwnIndex());
            }
        }

        return newQueue;
    }

    public void addLast(int element, int queueIndex) {
        inputChecker(element, queueIndex);
        if (containedIn[element] != -1) {
            throw new IllegalArgumentException();
        }

        int last = lastOfQ.get(queueIndex);
        int first = firstOfQ.get(queueIndex);

        if (element <= last) {
            throw new IllegalArgumentException("Input cannot be the last entry in the queue");
        }

        containedIn[element] = queueIndex;
        // adapt "pointers, last <-> i
        previous[element] = last;
        if(last != -1) {
            next[last] = element;
        }
        last = element;

        if (first == -1) {
            first = element;
        }

        lastOfQ.set(queueIndex, last);
        firstOfQ.set(queueIndex, first);
    }


    private void moveElementTo(int element, int queueIndex) {
        remove(element, queueIndex);
        addLast(element, queueIndex);
    }



    private void inputChecker(int element, int queueIndex) {
        if (element < 0 || element >= universeSize) {
            throw new IllegalArgumentException("Element not suitable for this queue in general");
        }
        if (queueIndex >= amountQueues) {
            throw new IllegalArgumentException("Queue does not exist");
        }
    }
}
