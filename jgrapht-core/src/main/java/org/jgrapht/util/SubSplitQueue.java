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

/**
 * A queue on a bounded set of integers that is splittable. The order of the elements cannot be
 * changed after instantiation. An element cannot be added after instantiation.
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
     * Maps the external elements to internal elements. The inverse mapping of toExternal.
     */
    private final int[] toInternal;

    /**
     * Maps the internal elements to the external elements. Is the same as sortedElements argument
     * in the static instantiation method. It equals the sortedElements array from the static
     * instantiation method
     */
    private final int[] toExternal;

    /**
     * Returns a SubSplitQueue with elements from 0 to universeSize - 1 Runs in O(universeSize) The
     * order is the natural order.
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
     * @param sortedElements the elements to be stored (subject to the above-mentioned restrictions)
     * @return new SubSplitQueue
     */
    public static SubSplitQueue subSplitQueueFactory(int universeSize, int[] sortedElements)
    {
        return SuperSplitQueue.instantiate(universeSize, sortedElements);
    }

    /**
     * Returns a new SubSplitQueue initialized with its index and its parent SuperSplitQueue Not to
     * be used externally.
     *
     * @param ownIndex index for this queue
     * @param parent parent
     */
    SubSplitQueue(int ownIndex, SuperSplitQueue parent, int[] toInternal, int[] toExternal)
    {
        this.ownIndex = ownIndex;
        this.parent = parent;
        this.toInternal = toInternal;
        this.toExternal = toExternal;
    }

    /**
     * Returns its own index.
     * 
     * @return index
     */
    int getOwnIndex()
    {
        return ownIndex;
    }

    /**
     * Returns the lowest element in the queue without side effects. Throws NoSuchElementException
     * if it is empty. Runs in constant time.
     * 
     * @return the lowest element in the queue
     */
    public int peek()
    {
        return toExternal[parent.peek(ownIndex)];
    }

    /**
     * Returns and removes the lowest element in the queue. Throws NoSuchElementException if it is
     * empty. Runs in constant time.
     * 
     * @return the lowest element in the queue
     */
    public int poll()
    {
        return toExternal[parent.poll(ownIndex)];
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
     * Removes the given element and keeps the data structure consistent. Runs in constant time.
     * 
     * @param element the element to be removed
     */
    public void remove(int element)
    {
        parent.remove(toInternal[element], ownIndex);
    }

    /**
     * Splits this queue in two: Elements in splitters are removed and transferred to a new queue,
     * the others stay. Run time: O(splitters size).
     * 
     * @param splitters The elements which have to be transferred to the new returned SubSplitQueue.
     *        They have to be sorted by the initial order.
     * @return a new SplitQueue holding the split off elements
     */
    public SubSplitQueue split(int[] splitters)
    {
        int[] internalSplitters = new int[splitters.length];
        for (int i = 0; i < splitters.length; i++) {
            internalSplitters[i] = toInternal[splitters[i]];
        }
        return parent.split(internalSplitters, ownIndex);
    }

    /**
     * Returns true iff the queue contains element. Runs in constant time.
     * 
     * @param element the element to be checked
     * @return whether it contains element
     */
    public boolean contains(int element)
    {
        return parent.contains(toInternal[element], ownIndex);
    }

    /**
     * Iterates over the contained elements. Every iteration takes constant time.
     * 
     * @return the iterator
     */
    @Override
    public Iterator<Integer> iterator()
    {
        return new Iterator<Integer>()
        {
            @Override
            public boolean hasNext()
            {
                return parent.iterator(ownIndex).hasNext();
            }

            @Override
            public Integer next()
            {
                return toExternal[parent.iterator(ownIndex).next()];
            }
        };
    }

    /**
     * Returns the elements contained in this object as Array, ordered by the initial order. Runs in
     * O(queue size).
     * 
     * @return an array containing the elements of this queue
     */
    public int[] asArray()
    {
        int[] result = parent.asArray(ownIndex);
        for (int i = 0; i < result.length; i++) {
            result[i] = toExternal[result[i]];
        }
        return result;
    }

    /**
     * Returns the number of elements in this object. Runs in constant time.
     * 
     * @return the amount of contained elements
     */
    public int getSize()
    {
        return parent.getSize(ownIndex);
    }
}
