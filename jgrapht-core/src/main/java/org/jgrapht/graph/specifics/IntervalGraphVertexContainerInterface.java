package org.jgrapht.graph.specifics;

import org.jgrapht.graph.IntervalVertexInterface;

import java.util.List;
import java.util.Set;

/**
 * Interface of IntervalGraphVertexContainer
 *
 * @param <V> the vertex type
 * @param <E> the edge type
 *
 * @author Christoph Grüne (christophgruene)
 * @since Apr 26, 2018
 */
public interface IntervalGraphVertexContainerInterface<V extends IntervalVertexInterface, E> {

    /**
     * Returns the whole vertex set of the graph.
     *
     * @return all vertices of the graph in a set
     */
    Set<V> getVertexSet();

    /**
     * returns a list of all vertices with overlapping interval w.r.t <code>v</code>
     *
     * @param v the vertex with interval
     *
     * @return A list of overlapping intervals
     */
    List<V> getOverlappingIntervalVertices(V v);

    /**
     * Returns the edge container to the vertex
     *
     * @param vertex the vertex
     * @return the edge container to the vertex
     */
    UndirectedEdgeContainer<V, E> get(V vertex);

    /**
     * puts the given edge container to the data structure
     *
     * @param vertex the vertex
     * @param ec     the edge container
     *
     * @return An undirected edge container
     */
    UndirectedEdgeContainer<V, E> put(V vertex, UndirectedEdgeContainer<V, E> ec);

    /**
     * Removes a vertex from the data structure if it is present.
     *
     * @param vertex the vertex to be removed
     * @return true if this data structure contained the specified element
     */
    UndirectedEdgeContainer<V, E> remove(V vertex);
}
