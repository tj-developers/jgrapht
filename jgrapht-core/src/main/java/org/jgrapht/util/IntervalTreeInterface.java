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
package org.jgrapht.util;

import org.jgrapht.util.interval.Interval;

import java.util.List;

/**
 * Interface of IntervalTree
 * This is the interface to an interval tree. It maintains intervals in order to find intersecting intervals and
 * included points in O(log n + k), where n is the number of intervals and k the output size,
 * i.e. the number of output intervals.
 *
 * @param <T> The type of the interval
 * @param <K> The key for the search tree
 * @param <NodeValue> The node value type
 *
 * @author Christoph Grüne (christophgruene)
 * @since Apr 26, 2018
 */
public interface IntervalTreeInterface< T extends Comparable<T>, K extends Comparable<K>, NodeValue extends IntervalTreeNodeValue<Interval<T>, T>> extends BinarySearchTree<K, NodeValue> {

    /**
     * Returns all intervals of all vertices that intersect with the given <code>interval</code>
     *
     * @param interval the interval
     * @return all intersecting intervals of vertices in the graph
     */
    List<Interval<T>> overlapsWith(Interval<T> interval);

    /**
     * Returns all intervals of all vertices that include the given <code>point</code>
     *
     * @param point the point
     * @return all including intervals of vertices in the graph
     */
    List<Interval<T>> overlapsWith(T point);

}
