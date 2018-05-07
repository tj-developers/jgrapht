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
