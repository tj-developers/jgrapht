package org.jgrapht.intervalgraph;

import org.jgrapht.graph.specifics.UndirectedEdgeContainer;
import org.jgrapht.intervalgraph.interval.IntervalVertex;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public interface IntervalGraphVertexContainerInterface<V extends IntervalVertex, E> {

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
     */
    public List<V> getOverlappingIntervalVertices(V v);

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
     */
    public UndirectedEdgeContainer<V, E> put(V vertex, UndirectedEdgeContainer<V, E> ec);

    /**
     * Removes a vertex from the data structure if it is present.
     *
     * @param vertex the vertex to be removed
     * @return true if this data structure contained the specified element
     */
    public UndirectedEdgeContainer<V, E> remove(V vertex);
}