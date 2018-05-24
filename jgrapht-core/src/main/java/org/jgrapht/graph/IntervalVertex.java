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

import java.io.Serializable;
import java.util.Objects;

/**
 * Implementation for IntervalVertex
 * This class implements the container class for vertices in an interval graph.
 *
 * @param <V> the vertex
 * @param <T> the interval element
 *
 * @author Christoph Grüne (christophgruene)
 * @since Apr 26, 2018
 */
public class IntervalVertex<V, T extends Comparable<T>> implements IntervalVertexInterface, Serializable {
    private V vertex;
    private Interval<T> interval;

    private static final long serialVersionUID = 7632653463458053425L;

    /**
     * Constructor for an IntervalVertex (container class for vertices in interval graphs)
     *
     * @param vertex the vertex
     * @param interval the interval
     */
    private IntervalVertex(V vertex, Interval<T> interval) {
        this.vertex = vertex;
        this.interval = interval;
    }

    /**
     * Getter for <code>interval</code>
     *
     * @return the interval
     */
    @Override
    public Interval<T> getInterval() {
        return interval;
    }

    /**
     * Getter for <code>vertex</code>
     *
     * @return the vertex
     */
    @Override
    public V getVertex() {
        return vertex;
    }

    @Override
    public String toString()
    {
        return "(" + vertex + "," + interval + ")";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        else if (!(o instanceof IntervalVertex))
            return false;

        @SuppressWarnings("unchecked") IntervalVertex<V, T> other = (IntervalVertex<V, T>) o;
        return Objects.equals(vertex, other.vertex) && Objects.equals(interval, other.interval);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(vertex, interval);
    }

    /**
     * Creates new IntervalVertex of elements pulling of the necessity to provide corresponding types of the
     * elements supplied.
     *
     * @param <V> the vertex type
     * @param <T> the interval element type
     * @param vertex the vertex
     * @param interval the interval
     * @return new pair
     */
    public static <V, T extends Comparable<T>> IntervalVertex<V, T> of(V vertex, Interval<T> interval) {
        return new IntervalVertex<>(vertex, interval);
    }
}
