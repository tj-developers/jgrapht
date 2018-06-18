package org.jgrapht.util;

import java.util.Iterator;
import java.util.Optional;

public class SubSplitQueue implements Iterable<Integer> {
    final private int ownIndex;

    final public SuperSplitQueue parent;

    private Optional<SubSplitQueue> prev;
    private Optional<SubSplitQueue> next;

    /**
     * Returns a SubSplitQueue with elements from 0 to universeSize - 1
     * @param universeSize
     * @return
     */
    public static SubSplitQueue subSplitQueueFactory(int universeSize) {
        return SuperSplitQueue.instantiate(universeSize);
    }

    /**
     * Returns a SubSplitQueue with elements from sortedElements.
     * SortedElements has to a subset of {0, ..., universeSize - 1}, sorted, and every entry should be unique
     * @param universeSize
     * @return
     */
    public static SubSplitQueue subSplitQueueFactory(int universeSize, int[] sortedElements) {
        return SuperSplitQueue.instantiate(universeSize, sortedElements);
    }

    /**
     *
     * @param ownIndex
     * @param parent
     */
    SubSplitQueue(int ownIndex, SuperSplitQueue parent) {
        this.ownIndex = ownIndex;
        this.parent = parent;
    }


    int getOwnIndex() {
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

    public boolean contains(int element) {
        return parent.contains(element, ownIndex);
    }

    @Override
    public Iterator<Integer> iterator() {
        return parent.iterator(ownIndex);
    }

    public int[] asArray() {
        return parent.asArray(ownIndex);
    }

    public int getSize() {
        return parent.getSize(ownIndex);
    }
}
