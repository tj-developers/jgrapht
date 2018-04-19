package org.jgrapht.intervalgraph;

import org.jgrapht.graph.specifics.UndirectedEdgeContainer;
import org.jgrapht.intervalgraph.interval.Interval;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IntervalGraphVertexContainer<V extends Interval<T>, E, T extends Comparable<T>> implements IntervalGraphVertexContainerInterface<V, E>, Serializable {

    private static final long serialVersionUID = 7768940080894764546L;

    private IntervalTreeInterface<T> intervalTree;
    private Map<V, UndirectedEdgeContainer<V, E>> vertexMap;

    public IntervalGraphVertexContainer() {
        this.intervalTree = new IntervalTree<>();
        this.vertexMap = new LinkedHashMap<>();
    }

    /**
     * Returns the whole vertex set of the graph.
     *
     * @return all vertices of the graph in a set
     */
    @Override
    public Set<V> getVertexSet() {
        return vertexMap.keySet();
    }

    /**
     * returns a list of all vertices with overlapping interval w.r.t <code>v</code>
     *
     * @param v the vertex with interval
     */
    @Override
    public List<V> getOverlappingIntervalVertices(V v) {
        //TODO Daniel
        // TODO Das sollte eigentlich wie unten angegeben funktionieren?! Man kann mit ClassBasedVertexFactory gerade solche erstellen?!
        return intervalTree.overlapsWith(V);
    }

    /**
     * Returns the edge container to the vertex
     *
     * @param vertex the vertex
     * @return the edge container to the vertex
     */
    @Override
    public UndirectedEdgeContainer<V, E> get(V vertex) {
        return vertexMap.get(vertex);
    }

    /**
     * puts the given edge container to the data structure
     *
     * @param vertex the vertex
     * @param ec     the edge container
     */
    @Override
    public UndirectedEdgeContainer<V, E> put(V vertex, UndirectedEdgeContainer<V, E> ec) {
        intervalTree.add(vertex);
        return vertexMap.put(vertex, ec);
    }

    /**
     * Removes a vertex from the data structure if it is present.
     *
     * @param vertex the vertex to be removed
     * @return true if this data structure contained the specified element
     */
    @Override
    public UndirectedEdgeContainer<V, E> remove(V vertex) {
        intervalTree.remove(vertex);
        return vertexMap.remove(vertex);
    }
}
