package org.jgrapht.util;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class SubSplitQueue {
    private int ownIndex;
    private SuperSplitQueue parent;

    public SubSplitQueue(int ownIndex, SuperSplitQueue parent) {
        this.ownIndex = ownIndex;
        this.parent = parent;
    }


    public int getOwnIndex() {
        return ownIndex;
    }


    /**
     * Returns the lowest element in the queue without side effects
     * Throws NoSuchElementException if empty
     * runs in constant time
     * @return the lowest element in the queue
     */
    public int peek() {
        return parent.peek(ownIndex);
    }


    /**
     * Returns and removes the lowest element in the queue
     * Throws NoSuchElementException if empty
     * Runs in constant time
     * @return the lowest element in the queue
     */
    public int poll() {
        return parent.poll(ownIndex);
    }


    public boolean isEmpty() {
        return parent.isEmpty(ownIndex);
    }


    /**
     * Removes the given element and keeps the data structure consistent
     * Runs in constant time
     * @param element the element to be removed
     */
    public void remove(int element) {
        parent.remove(element, ownIndex);
    }

    /**
     * Splits this queue in two: Elements in splitters are removed and transferred to a new queue,
     * the others stay.
     * Run time:
     * @param splitters HAVE TO BE SORTED
     * @return
     */
    public SubSplitQueue split(int[] splitters) {
        return parent.split(splitters, ownIndex);
    }
}
