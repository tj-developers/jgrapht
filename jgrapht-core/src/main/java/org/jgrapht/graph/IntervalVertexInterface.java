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
package org.jgrapht.graph;

import org.jgrapht.util.interval.Interval;

/**
 * Interface for IntervalVertex
 * This interface provides necessary getters for the container of vertices in an interval graph
 * as every vertex needs to have an interval.
 *
 * @param <V> the vertex
 * @param <T> the interval element
 *
 * @author Christoph Grüne (christophgruene)
 * @since Apr 26, 2018
 */
public interface IntervalVertexInterface<V, T extends Comparable<T>> {

    /**
     * Getter for <code>vertex</code>
     *
     * @return the vertex
     */
    V getVertex();

    /**
     * Getter for <code>interval</code>
     *
     * @return the interval
     */
    Interval<T> getInterval();
}
