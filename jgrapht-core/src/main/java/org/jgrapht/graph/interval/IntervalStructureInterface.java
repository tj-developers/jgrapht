/*
 * (C) Copyright 2003-2018, by Christoph Grüne and Contributors.
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
package org.jgrapht.graph.interval;

import org.jgrapht.graph.interval.Interval;

import java.util.List;

/**
 * Interface of IntervalStructure
 * This interface is used for an implementation of an efficient data structure to maintain intervals.
 *
 * @param <T> the type of the interval
 *
 * @author Christoph Grüne (christophgruene)
 * @since Apr 26, 2018
 */
public interface IntervalStructureInterface<T extends Comparable<T>> {

    /**
     * Returns all intervals that overlap with the given <code>interval</code>
     *
     * @param interval the interval
     * @return all intervals that overlap with the given <code>interval</code>
     */
    List<Interval<T>> overlapsWith(Interval<T> interval);


    /**
     * Returns all intervals that overlap with the given <code>point</code>
     * @param point the point
     * @return all intervals that overlap with the given <code>point</code>
     */
    List<Interval<T>> overlapsWithPoint(T point);

    /**
     * adds an interval to the interval tree. It returns true iff the key has been added successfully.
     *
     * @param interval the interval
     *
     * @return true, if the key was not contained in the tree; false, otherwise
     */
    boolean add(Interval<T> interval);

    /**
     * removes an interval from the tree. It returns true iff the key has been removed successfully.
     *
     * @param interval the interval
     *
     * @return true, if the key was contained in the tree; false, otherwise
     */
    boolean remove(Interval<T> interval);
}
