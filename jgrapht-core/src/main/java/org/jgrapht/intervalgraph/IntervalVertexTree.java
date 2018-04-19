package org.jgrapht.intervalgraph;

import org.jgrapht.graph.specifics.UndirectedEdgeContainer;

import java.io.Serializable;
import java.util.Set;

public class IntervalVertexTree<V, E> implements IntervalGraphVertexContainer<V, E>, Serializable {

    private static final long serialVersionUID = 7768940080894764546L;

    /**
     * Returns the whole vertex set of the graph.
     *
     * @return all vertices of the graph in a set
     */
    @Override
    public Set<V> getVertexSet() {
        return null;
    }

    /**
     * Returns the edge container to the vertex
     *
     * @param vertex the vertex
     * @return the edge container to the vertex
     */
    @Override
    public UndirectedEdgeContainer get(Object vertex) {
        return null;
    }

    /**
     * puts the given edge container to the data structure
     *
     * @param vertex the vertex
     * @param ec     the edge container
     */
    @Override
    public void put(Object vertex, UndirectedEdgeContainer ec) {

    }
}
