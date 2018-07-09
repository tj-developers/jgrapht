/*
 * (C) Copyright 2003-2018, by Daniel Mock and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.util;

import java.util.Iterator;
import java.util.Optional;

/**
 * A queue on a bounded set of integers that is splittable. The order of the elements cannot be
 * changed after instanciation. An element cannot be added after instanciation.
 * 
 * @author Daniel Mock
 */
public class SubSplitQueue
    implements
    Iterable<Integer>
{
    final private int ownIndex;

    final public SuperSplitQueue parent;

    /**
     * Returns a SubSplitQueue with elements from 0 to universeSize - 1 Runs in O(universeSize)
     * 
     * @param universeSize the size of the universe
     * @return new SubSplitQueue
     */
    public static SubSplitQueue subSplitQueueFactory(int universeSize)
    {
        return SuperSplitQueue.instantiate(universeSize);
    }

    /**
     * Returns a SubSplitQueue with elements from sortedElements. SortedElements has to a subset of
     * {0, ..., universeSize - 1}, sorted by desired order, and every entry should be unique
     * 
     * @param universeSize the size of the universe
     * @return new SubSplitQueue
     */
    public static SubSplitQueue subSplitQueueFactory(int universeSize, int[] sortedElements)
    {
        return SuperSplitQueue.instantiate(universeSize, sortedElements);
    }

    /**
     * Returns a new SubSplitQueue initialized with its index and its parent SuperSplitQueue
     * 
     * @param ownIndex index for this queue
     * @param parent parent
     */
    SubSplitQueue(int ownIndex, SuperSplitQueue parent)
    {
        this.ownIndex = ownIndex;
        this.parent = parent;
    }

    /**
     * returns its own index
     * 
     * @return index
     */
    int getOwnIndex()
    {
        return ownIndex;
    }

    /**
     * Returns the lowest element in the queue without side effects Throws NoSuchElementException if
     * empty runs in constant time
     * 
     * @return the lowest element in the queue
     */
    public int peek()
    {
        return parent.peek(ownIndex);
    }

    /**
     * Returns and removes the lowest element in the queue Throws NoSuchElementException if empty
     * Runs in constant time
     * 
     * @return the lowest element in the queue
     */
    public int poll()
    {
        return parent.poll(ownIndex);
    }

    /**
     * Returns true iff the queue does not contain any elements. Runs in constant time.
     * 
     * @return whether it is empty
     */
    public boolean isEmpty()
    {
        return parent.isEmpty(ownIndex);
    }

    /**
     * Removes the given element and keeps the data structure consistent Runs in constant time
     * 
     * @param element the element to be removed
     */
    public void remove(int element)
    {
        parent.remove(element, ownIndex);
    }

    /**
     * Splits this queue in two: Elements in splitters are removed and transferred to a new queue,
     * the others stay. Run time: O(queue size)
     * 
     * @param splitters HAVE TO BE SORTED
     * @return
     */
    public SubSplitQueue split(int[] splitters)
    {
        return parent.split(splitters, ownIndex);
    }

    /**
     * Returns true iff the queue contains element. Runs in constant time.
     * 
     * @return whether it contains element
     */
    public boolean contains(int element)
    {
        return parent.contains(element, ownIndex);
    }

    /**
     * Iterates over the contained elements Every iteration takes constant time.
     * 
     * @return the iterator
     */
    @Override
    public Iterator<Integer> iterator()
    {
        return parent.iterator(ownIndex);
    }

    /**
     * Runs in O(queue size)
     * 
     * @return an array containing the elements of this queue
     */
    public int[] asArray()
    {
        return parent.asArray(ownIndex);
    }

    /**
     * Runs in constant time.
     * 
     * @return the amount of contained elements
     */
    public int getSize()
    {
        return parent.getSize(ownIndex);
    }
}