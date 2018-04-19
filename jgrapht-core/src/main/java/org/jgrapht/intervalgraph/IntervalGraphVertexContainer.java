package org.jgrapht.intervalgraph;

import org.jgrapht.graph.specifics.UndirectedEdgeContainer;

import java.io.Serializable;
import java.util.Set;

public interface IntervalGraphVertexContainer<V, E> {

    /**
     * Returns the whole vertex set of the graph.
     *
     * @return all vertices of the graph in a set
     */
    Set<V> getVertexSet();

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
     * @param ec the edge container
     */
    void put(V vertex, UndirectedEdgeContainer<V, E> ec);
}
