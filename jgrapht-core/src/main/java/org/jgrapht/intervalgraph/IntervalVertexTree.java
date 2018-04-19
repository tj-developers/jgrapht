package org.jgrapht.intervalgraph;

import org.jgrapht.graph.specifics.UndirectedEdgeContainer;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class IntervalVertexTree<V, E> implements IntervalGraphVertexContainer<V, E>, Serializable {

    private static final long serialVersionUID = 7768940080894764546L;

    private IntervalTree intervalTree;
    private LinkedHashMap<V, UndirectedEdgeContainer> vertexSet;

    public IntervalVertexTree() {
        this.intervalTree = new IntervalTree();
        this.vertexSet = new LinkedHashMap<>();
    }

    /**
     * Returns the whole vertex set of the graph.
     *
     * @return all vertices of the graph in a set
     */
    @Override
    public Set<V> getVertexSet() {
        return vertexSet.keySet();
    }

    /**
     * returns a list of all vertices with overlapping interval w.r.t <code>v</code>
     *
     * @param v the vertex with interval
     */
    @Override
    public List<V> getOverlappingIntervalVertices(V v) {
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
    public boolean put(V vertex, UndirectedEdgeContainer ec) {
        return false;
    }

    /**
     * Removes a vertex from the data structure if it is present.
     *
     * @param vertex the vertex to be removed
     * @return true if this data structure contained the specified element
     */
    @Override
    public boolean remove(V vertex) {
        return false;
    }
}
