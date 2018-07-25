/*
 * (C) Copyright 2018-2018, by Christoph Grüne and Contributors.
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

import java.io.Serializable;
import java.util.Objects;

/**
 * Implementation for IntervalVertexPair
 * This class implements the container class for vertices in an interval graph.
 *
 * @param <V> the vertex
 * @param <T> the interval element
 *
 * @author Christoph Grüne (christophgruene)
 * @since Apr 26, 2018
 */
public class IntervalVertexPair<V, T extends Comparable<T>> implements Serializable {
    private V vertex;
    private Interval<T> interval;

    private static final long serialVersionUID = 7632653463458053425L;

    /**
     * Constructor for an IntervalVertexPair (container class for vertices in interval graphs)
     *
     * @param vertex the vertex
     * @param interval the interval
     */
    private IntervalVertexPair(V vertex, Interval<T> interval) {
        this.vertex = vertex;
        this.interval = interval;
    }

    /**
     * Getter for <code>interval</code>
     *
     * @return the interval
     */
    public Interval<T> getInterval() {
        return interval;
    }

    /**
     * Getter for <code>vertex</code>
     *
     * @return the vertex
     */
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
        else if (!(o instanceof IntervalVertexPair))
            return false;

        @SuppressWarnings("unchecked") IntervalVertexPair<V, T> other = (IntervalVertexPair<V, T>) o;
        return Objects.equals(vertex, other.vertex) && Objects.equals(interval, other.interval);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(vertex, interval);
    }

    /**
     * Creates new IntervalVertexPair of elements pulling of the necessity to provide corresponding types of the
     * elements supplied.
     *
     * @param <V> the vertex type
     * @param <T> the interval element type
     * @param vertex the vertex
     * @param interval the interval
     * @return new pair
     */
    public static <V, T extends Comparable<T>> IntervalVertexPair<V, T> of(V vertex, Interval<T> interval) {
        return new IntervalVertexPair<>(vertex, interval);
    }
}
