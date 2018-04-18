package org.jgrapht.graph.specifics;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.EdgeSetFactory;
import org.jgrapht.graph.specifics.ArrayUnenforcedSetEdgeSetFactory;
import org.jgrapht.graph.specifics.Specifics;
import org.jgrapht.graph.specifics.UndirectedEdgeContainer;
import org.jgrapht.intervalgraph.IntervalGraph;
import sun.jvm.hotspot.utilities.IntervalTree;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class IntervalSpecifics<V, E> extends UndirectedSpecifics<V, E> implements Specifics<V, E> {

    //TODO implement interval specifics with IntervalTree

    protected IntervalGraph<V, E> abstractBaseGraph;
    protected IntervalTree<V, E> vertexIntervalTree; //TODO anpassen
    protected Map<V, UndirectedEdgeContainer<V, E>> vertexMapUndirected;
    protected EdgeSetFactory<V, E> edgeSetFactory;

    /**
     * Construct a new interval specifics.
     *
     * @param abstractBaseGraph the graph for which these specifics are for
     */
    public IntervalSpecifics(IntervalGraph<V, E> abstractBaseGraph) {
        super(abstractBaseGraph, new LinkedHashMap<>(), new ArrayUnenforcedSetEdgeSetFactory<>());
        this.vertexIntervalTree = new IntervalTree<>();
    }

    /**
     * Construct a new interval specifics.
     *
     * @param abstractBaseGraph the graph for which these specifics are for
     * @param edgeSetFactory factory for the creation of vertex edge sets
     */
    public IntervalSpecifics(IntervalGraph<V, E> abstractBaseGraph, EdgeSetFactory<V, E> edgeSetFactory) {
        super(abstractBaseGraph, new LinkedHashMap<>(), edgeSetFactory);
        this.vertexIntervalTree = new IntervalTree<>();
    }

    /**
     * Adds a vertex.
     *
     * @param vertex vertex to be added.
     */
    @Override
    public void addVertex(V vertex) {
        getEdgeContainer(vertex);
        vertexIntervalTree.add(vertex);
    }

    /**
     * Adds the specified edge to the edge containers of its source and target vertices.
     *
     * @param e the edge
     */
    @Override
    public void addEdgeToTouchingVertices(E e) {
        throw new IllegalArgumentException();
    }

    /**
     * Removes the specified edge from the edge containers of its source and target vertices.
     *
     * @param e the edge
     */
    @Override
    public void removeEdgeFromTouchingVertices(E e) {
        throw new IllegalArgumentException();
    }
}
